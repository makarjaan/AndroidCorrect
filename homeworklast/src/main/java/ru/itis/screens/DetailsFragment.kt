package ru.itis.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Surface
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.itis.base.BaseFragment
import ru.itis.base.MainActivity
import ru.itis.data.entities.BookEntity
import ru.itis.di.ServiceLocator
import ru.itis.homeworklast.R
import ru.itis.homeworklast.databinding.FragmentDetailsBinding
import ru.itis.ui.DetailsPage
import ru.itis.util.Keys

class DetailsFragment(
    val userName: String,
    val bookId: String
) : BaseFragment(R.layout.fragment_details) {

    private var viewBinding: FragmentDetailsBinding? = null
    private var bookRepository = ServiceLocator.getBookRepository()
    private var book: BookEntity? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentDetailsBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    book = bookRepository.getBook(bookId)
                }
            }
            result.onSuccess {
                initView()
            }.onFailure { ex ->
                Log.e(Keys.ERROR_MESSAGE, "${resources.getString(R.string.load_er)} ${ex.message}", ex)
            }
        }
    }

    private fun initView() {
        viewBinding?.composeContainerId?.setContent {
            Surface {
                book?.let {
                    DetailsPage(
                        userName = userName,
                        onLogoutClick = { MainPageFragment().onLogoutClick() },
                        book = it,
                        onReviewUpdated = ::onReviewUpdated,
                        onRatingUpdated = ::onRatingUpdated,
                        onImageUrlUpdated = ::onImageUrlUpdated,
                        onSaveClick = ::onSaveClick
                    )
                }
            }
        }
    }

    private fun onReviewUpdated(newReview: String) {
        Log.d("TEST-TAG", "$bookId $newReview")
        lifecycleScope.launch {
            runCatching {
                bookRepository.setReview(bookId, newReview)
            }.onFailure { ex ->
                Log.e(Keys.ERROR_MESSAGE, "${resources.getString(R.string.revie_err)} ${ex.message}", ex)
            }
        }
    }

    private fun onRatingUpdated(newRating: Float) {
        lifecycleScope.launch {
            runCatching {
                bookRepository.setRating(bookId, newRating)
            }.onFailure { ex ->
                Log.e(Keys.ERROR_MESSAGE, "${resources.getString(R.string.rating_err)} ${ex.message}", ex)
            }
        }
    }

    private fun onImageUrlUpdated(newUrl: String) {
        lifecycleScope.launch {
            kotlin.runCatching {
                bookRepository.setImage(bookId, newUrl)
            }.onFailure { ex ->
                Log.e(Keys.ERROR_MESSAGE, "${resources.getString(R.string.image_err)} ${ex.message}", ex)
            }
        }
    }

    private fun onSaveClick() {
        val newFragment = MainPageFragment()
        newFragment.setList()
        (requireActivity() as? MainActivity)?.addFragment(newFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding = null
        composeView = null
    }
}