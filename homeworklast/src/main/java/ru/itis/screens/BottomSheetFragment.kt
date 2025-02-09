package ru.itis.screens

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import ru.itis.base.MainActivity
import ru.itis.data.entities.BookEntity
import ru.itis.di.ServiceLocator
import ru.itis.homeworklast.R
import ru.itis.homeworklast.databinding.DialigBottomSheetBinding
import java.util.UUID

class BottomSheetFragment(
    val userId: String? = null
) : BottomSheetDialogFragment(R.layout.dialig_bottom_sheet) {

    private var viewBinding : DialigBottomSheetBinding? = null
    private var bookRepository = ServiceLocator.getBookRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DialigBottomSheetBinding.inflate(layoutInflater)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        viewBinding?.run {
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.planets_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    planetsSpinner.adapter = adapter
            }
            planetsSpinner.setSelection(0, false)


            sendBookBtn.setOnClickListener {
                val title = titleEt.text.toString()
                val author = authorEt.text.toString()
                val genre = planetsSpinner.selectedItem.toString()
                val ratingValue = ratingBar.rating
                val review = reviewEt.text.toString()
                val url = urlEt.text.toString()

                if (title.isEmpty()) {
                    titleInput.error = resources.getString(R.string.bottom_title)
                }

                if (author.isEmpty()) {
                    authorInput.error = resources.getString(R.string.botton_author)
                }

                if (genre == resources.getString(R.string.choose_genre)) {
                    Toast.makeText(context, R.string.choose_genre, Toast.LENGTH_SHORT).show()
                }

                if (title.isNotEmpty() && author.isNotEmpty() && genre != resources.getString(R.string.choose_genre)) {
                    val book = userId?.let {
                        BookEntity(
                            bookId = UUID.randomUUID().toString(),
                            userId = it,
                            title = title,
                            author = author,
                            genre = genre,
                            rating = ratingValue,
                            review = review,
                            image = url
                        )
                    }
                    lifecycleScope.launch {
                        runCatching {
                            book?.let { bookRepository.saveBook(it) }
                            (parentFragment as? MainPageFragment)?.loadBooks()
                        }.onFailure { ex ->
                            Log.e(MainActivity.ERROR_TAG, "${resources.getString(R.string.save_err)} ${ex.message}", ex)
                            Toast.makeText(context, R.string.book_save_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                    dismiss()
                }
            }
        }
    }

    companion object {
        const val TAG = "bottom_sheet"
    }
}