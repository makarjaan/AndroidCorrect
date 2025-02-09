package ru.itis.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.itis.data.entities.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users WHERE :email=email")
    suspend fun isUserInDatabase(email: String): Int

    @Query("SELECT id FROM users WHERE :email=email")
    suspend fun getUserIdByEmail(email: String): String

    @Query("SELECT * FROM users WHERE :id=id")
    suspend fun getUserById(id: String): UserEntity?
}
