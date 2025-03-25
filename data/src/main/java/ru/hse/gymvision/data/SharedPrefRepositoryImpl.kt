package ru.hse.gymvision.data

import android.content.Context
import android.content.SharedPreferences
import ru.hse.gymvision.domain.repos.SharedPrefRepository

class SharedPrefRepositoryImpl(
    context: Context
): SharedPrefRepository {

    private val preferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    override suspend fun saveGymId(gymId: Int) {
        preferences.edit().putInt("gym_id", gymId).apply()
    }

    override suspend fun getGymId(): Int {
        return preferences.getInt("gym_id", -1)
    }

    override suspend fun saveUser(username: String) {
        preferences.edit().putString("login", username).apply()
    }

    override suspend fun getUser(): String? {
        return preferences.getString("login", null)
    }

    override suspend fun clearInfo() {
        preferences.edit().clear().apply()
    }

}