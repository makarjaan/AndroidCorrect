package ru.itis.screens

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.itis.base.BaseFragment
import ru.itis.base.MainActivity
import ru.itis.base.MainActivity.Companion.USER_ID_TAG
import ru.itis.data.entities.BookEntity
import ru.itis.data.entities.UserEntity
import ru.itis.di.ServiceLocator
import ru.itis.homeworklast.R
import ru.itis.homeworklast.databinding.FragmentMainPageBinding
import ru.itis.ui.ComposeListSample


class MainPageFragment : BaseFragment(R.layout.fragment_main_page) {

    private var viewBinding: FragmentMainPageBinding? = null
    private var userRepository = ServiceLocator.getUserRepository()
    private var bookRepository = ServiceLocator.getBookRepository()
    private var userId: String? = null
    var user: UserEntity? = null
    private var booksList: MutableList<BookEntity> = mutableListOf()
    var pref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentMainPageBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        userId = pref?.getString(USER_ID_TAG, null)
        lifecycleScope.launch {
            runCatching {
                userId?.let { id ->
                    withContext(Dispatchers.IO) {
                        user = userRepository.getUserById(id)
                        booksList = bookRepository.getBooksByUserId(id)
                    }
                } ?: IllegalStateException(resources.getString(R.string.id_err))
            }.onSuccess {
                initView()
            }.onFailure { ex ->
                when (ex) {
                    is IllegalStateException -> {
                        Log.e(MainActivity.ERROR_TAG, ex.message, ex)
                    }
                    else -> {
                        Log.e(MainActivity.ERROR_TAG, "${resources.getString(R.string.load_er)} ${ex.message}", ex)
                        Toast.makeText(context, R.string.data_load_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


    private fun initView() {
        setList()
        viewBinding?.run {
            floatBtn.setOnClickListener {
                val dialog = BottomSheetFragment(userId).apply {
                    isCancelable
                }
                dialog.show(childFragmentManager, BottomSheetFragment.TAG)
            }
        }
    }

    fun onLogoutClick() {
        pref?.edit()?.apply {
            putString(USER_ID_TAG, null)
            commit()
        }
        (requireActivity() as? MainActivity)?.replaceFragment(
            AuthorizationFragment.TAG
        )
    }

    fun loadBooks() {
        lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    userId?.let { id ->
                        booksList = bookRepository.getBooksByUserId(id)
                    } ?: throw IllegalStateException(resources.getString(R.string.id_err))
                }
            }.onFailure { ex ->
                Log.e(MainActivity.ERROR_TAG, "${resources.getString(R.string.load_er)} ${ex.message}", ex)
                Toast.makeText(context, R.string.data_load_error, Toast.LENGTH_SHORT).show()
            }
            setList()
        }
    }

    fun setList() {
        viewBinding?.composeContainerId?.setContent {
            val gridState = rememberLazyGridState()
            Column {
                user?.userName?.let {
                    ComposeListSample(
                        listState = gridState,
                        items = booksList,
                        userName = it,
                        onLogoutClick = ::onLogoutClick,
                        onClick = ::onClick,
                        onLongClick = ::onLongClick
                    )
                }
                if (booksList.isEmpty()) {
                    Text(
                        text = resources.getString(R.string.info),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.body1,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    fun onClick(book: BookEntity) {
        user?.userName?.let { userName ->
            (requireActivity() as? MainActivity)?.addFragment(
                DetailsFragment(userName = userName, bookId = book.bookId))
        }
    }


    fun onLongClick(book: BookEntity) {
        lifecycleScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    bookRepository.deleteBookByBookId(bookId = book.bookId)
                }
            }.onSuccess {
                loadBooks()
            }.onFailure { ex ->
                Log.e(MainActivity.ERROR_TAG, "${resources.getString(R.string.delete_error)} ${ex.message}", ex)
                Toast.makeText(context, R.string.book_delete_error, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
        viewBinding = null
    }

    companion object {
        const val TAG = "MainPageFragment"
    }
}