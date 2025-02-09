package ru.itis.data.migrations

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.itis.data.InceptionDatabase

class Migration_1_2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `books`" +
                        " (`book_id` TEXT NOT NULL, `user_id` TEXT NOT NULL," +
                        " `title` TEXT NOT NULL, `author` TEXT NOT NULL, " +
                        "`genre` TEXT NOT NULL, `rating` REAL NOT NULL, " +
                        "`review` TEXT, `book_image` TEXT," +
                        " PRIMARY KEY(`book_id`)," +
                        " FOREIGN KEY(`user_id`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
            )
        } catch (ex: Exception) {
            Log.d(InceptionDatabase.DB_LOG_KEY, "Error while 1_2 migration: ${ex.message}")
        }
    }
}