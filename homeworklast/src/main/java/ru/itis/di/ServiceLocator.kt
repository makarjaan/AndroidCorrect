package ru.itis.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import kotlinx.coroutines.Dispatchers
import ru.itis.data.InceptionDatabase
import ru.itis.data.migrations.Migration_1_2
import ru.itis.data.repository.BookRepository
import ru.itis.data.repository.UserRepository

object ServiceLocator {

    private const val DATABASE_NAME = "InceptionDB"

    private var dbInstance: InceptionDatabase? = null

    private var userRepository: UserRepository? = null

    private var bookRepository: BookRepository? = null

    private fun initDatabase(ctx: Context) {
        dbInstance = Room.databaseBuilder(ctx, InceptionDatabase::class.java, DATABASE_NAME)
            .addMigrations(
                Migration_1_2()
            )
            .build()
    }

    fun initDataLayerDependencies(ctx: Context) {
        if (dbInstance == null) {
            initDatabase(ctx)
            dbInstance?.let {
                userRepository = UserRepository(
                    userDao = it.userDao,
                    ioDispatcher = Dispatchers.IO
                )
                bookRepository = BookRepository (
                    bookDao = it.bookDao,
                    ioDispatcher = Dispatchers.IO
                )
            }
        }
    }

    fun getUserRepository(): UserRepository =
        userRepository ?: throw IllegalStateException("User repository not initialized")

    fun getBookRepository(): BookRepository =
        bookRepository ?: throw IllegalStateException("Book repository not initialized")
}