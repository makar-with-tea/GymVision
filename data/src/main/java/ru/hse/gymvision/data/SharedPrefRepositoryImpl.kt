package ru.hse.gymvision.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import ru.hse.gymvision.domain.repos.SharedPrefRepository
import androidx.core.content.edit

class SharedPrefRepositoryImpl(
    context: Context
): SharedPrefRepository {

    private val preferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    override suspend fun saveGymId(gymId: Int) {
        preferences.edit { putInt("gym_id", gymId) }
    }

    override suspend fun getGymId(): Int {
        return preferences.getInt("gym_id", -1)
    }

    override suspend fun saveUser(username: String) {
        Log.d("SharedPrefRepository", "saveUser: $username")
        preferences.edit { putString("login", username) }
    }

    override suspend fun getUser(): String? {
        Log.d("SharedPrefRepository", "getUser: ${preferences.getString("login", null)}")
        return preferences.getString("login", null)
    }

    override suspend fun clearInfo() {
        Log.d("SharedPrefRepository", "clearInfo")
        preferences.edit { clear() }
    }

}