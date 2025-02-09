package ru.itis.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.itis.data.entities.BookEntity

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBook(book : BookEntity)

    @Query("SELECT * FROM books WHERE :userId=user_id")
    suspend fun getBooks(userId: String) : MutableList<BookEntity>

    @Query("DELETE FROM books WHERE :bookId=book_id")
    suspend fun deleteBook(bookId: String)

    @Query("SELECT * FROM books WHERE :bookId=book_id")
    suspend fun getBook(bookId: String) : BookEntity

    @Query("UPDATE books SET review = :newReview WHERE :bookId=book_id")
    suspend fun setReview(bookId: String, newReview: String)

    @Query("UPDATE books SET rating = :newRating WHERE :bookId=book_id")
    suspend fun setRating(bookId: String, newRating: Float)

    @Query("UPDATE books SET book_image = :newUri WHERE :bookId=book_id")
    suspend fun setImage(bookId: String, newUri: String)
}