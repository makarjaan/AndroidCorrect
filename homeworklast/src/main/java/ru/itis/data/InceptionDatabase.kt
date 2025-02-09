package ru.itis.data

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.itis.data.dao.BookDao
import ru.itis.data.dao.UserDao
import ru.itis.data.entities.BookEntity
import ru.itis.data.entities.UserEntity

@Database(
    entities = [UserEntity::class, BookEntity::class],
    version = 2
)

abstract class InceptionDatabase : RoomDatabase() {
    abstract val userDao: UserDao
    abstract val bookDao: BookDao

    companion object {
        const val DB_LOG_KEY = "InceptionDB"
    }
}