package ru.itis.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ])
data class BookEntity (
    @PrimaryKey
    @ColumnInfo(name = "book_id")
    val bookId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "genre")
    val genre: String,
    @ColumnInfo(name = "rating")
    val rating: Float,
    @ColumnInfo(name = "review")
    val review: String? = null,
    @ColumnInfo(name = "book_image")
    val image: String? = null
)