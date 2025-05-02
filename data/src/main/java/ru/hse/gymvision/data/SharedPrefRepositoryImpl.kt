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
        Log.d("SharedPrefRepository", "saveGymId: $gymId")
        preferences.edit {
            remove("camera_ids") // при выборе нового зала удаляем старые камеры
            remove("camera_links")
            putInt("gym_id", gymId)
        }
    }

    override suspend fun getGymId(): Int {
        return preferences.getInt("gym_id", -1).also {
            Log.d("SharedPrefRepository", "getGymId: $it")
        }
    }

    override suspend fun saveUser(username: String) {
        Log.d("SharedPrefRepository", "saveUser: $username")
        preferences.edit { putString("login", username) }
    }

    override suspend fun getUser(): String? {
        return preferences.getString("login", null).also {
            Log.d("SharedPrefRepository", "getUser: $it")
        }
    }

    override suspend fun clearInfo() {
        Log.d("SharedPrefRepository", "clearInfo")
        preferences.edit { clear() }
    }

    override suspend fun saveCameras(cameraIds: List<Int>, cameraLinks: List<String>) {
        Log.d("SharedPrefRepository", "saveCameras: $cameraIds")
        preferences.edit { putString("camera_ids", cameraIds.joinToString(",")) }
        preferences.edit { putString("camera_links", cameraLinks.joinToString(",")) }
    }

    override suspend fun getCameraIds(): List<Int> {
        return preferences.getString("camera_ids", null)?.split(",")?.map { it.toInt() } ?: emptyList<Int>().also {
            Log.d("SharedPrefRepository", "getCameraIds: $it")
        }
    }

    override suspend fun getCameraLinks(): List<String> {
        return preferences.getString("camera_links", null)?.split(",") ?: emptyList<String>().also {
            Log.d("SharedPrefRepository", "getCameraLinks: $it")
        }
    }
}