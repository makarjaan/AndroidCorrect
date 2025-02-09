package ru.itis.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.itis.data.dao.BookDao
import ru.itis.data.entities.BookEntity

class BookRepository(
    private val bookDao: BookDao,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun saveBook(book: BookEntity) {
        return withContext(ioDispatcher) {
            bookDao.saveBook(book = book)
        }
    }

    suspend fun getBooksByUserId(userId: String) : MutableList<BookEntity> {
        return withContext(ioDispatcher) {
            bookDao.getBooks(userId)
        }
    }

    suspend fun deleteBookByBookId(bookId: String) {
        return withContext(ioDispatcher) {
            bookDao.deleteBook(bookId)
        }
    }

    suspend fun getBook(bookId: String) : BookEntity {
        return withContext(ioDispatcher) {
            bookDao.getBook(bookId)
        }
    }

    suspend fun setReview(bookId: String, newReview: String) {
        return withContext(ioDispatcher) {
            bookDao.setReview(bookId, newReview)
        }
    }

    suspend fun setRating(bookId: String, newRating: Float) {
        return withContext(ioDispatcher) {
            bookDao.setRating(bookId, newRating)
        }
    }

    suspend fun setImage(bookId: String, newUri: String) {
        return withContext(ioDispatcher) {
            bookDao.setImage(bookId, newUri)
        }
    }


}