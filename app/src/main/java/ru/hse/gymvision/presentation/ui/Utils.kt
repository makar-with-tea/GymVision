package ru.hse.gymvision.presentation.ui

import android.content.Context
import android.content.SharedPreferences

enum class BottomNavScreen {
    HOME, GYM_SCHEME, PROFILE, NONE
}

class PreferencesHelper(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("gym_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_GYM_ID = "current_gym_id"
    }

    fun saveCurGymId(gymId: Int) {
        preferences.edit().putInt(KEY_CURRENT_GYM_ID, gymId).apply()
    }

    fun getCurGymId(): Int {
        return preferences.getInt(KEY_CURRENT_GYM_ID, -1)
    }
}