package ru.hse.gymvision.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

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

class BitmapHelper {
    companion object {
        fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream) ?: return null
            return stream.toByteArray()
        }

        fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
            byteArray ?: return null
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }
}