package ru.itis.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.itis.data.dao.UserDao
import ru.itis.data.entities.UserEntity

class UserRepository(
    private val userDao: UserDao,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun saveUser(user: UserEntity) {
        return withContext(ioDispatcher) {
            userDao.saveUser(user = user)
        }
    }

    suspend fun userInDataBase(email: String): Int {
        return withContext(ioDispatcher) {
            userDao.isUserInDatabase(email = email)
        }
    }

    suspend fun getUserIdByEmail(email: String): String {
        return withContext(ioDispatcher) {
            userDao.getUserIdByEmail(email)
        }
    }

    suspend fun getUserById(id: String): UserEntity {
        return withContext(ioDispatcher) {
            userDao.getUserById(id = id)
                ?: throw IllegalStateException("User with given id not found")
        }
    }
}