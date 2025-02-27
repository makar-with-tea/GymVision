package ru.hse.gymvision.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ru.hse.gymvision.R
import ru.hse.gymvision.domain.exampledata.gymSchemeExample
import ru.hse.gymvision.presentation.ui.BitmapHelper
import ru.hse.gymvision.presentation.ui.MainView
import ru.hse.gymvision.presentation.ui.theme.GymVisionTheme
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymVisionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(this)
                }
            }
        }
        // todo: убрать, когда появится бэкенд
        gymSchemeExample.image = BitmapHelper.bitmapToByteArray(
            BitmapFactory.decodeResource(resources, R.drawable.im_gym_scheme)
        )!!
    }
}
