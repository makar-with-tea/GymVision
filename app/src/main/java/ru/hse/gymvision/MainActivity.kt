package ru.hse.gymvision

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ru.hse.gymvision.domain.exampledata.gymSchemeExample
import ru.hse.gymvision.ui.navigation.MainView
import ru.hse.gymvision.ui.BitmapHelper
import ru.hse.gymvision.ui.theme.GymVisionTheme

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
                    MainView()
                }
            }
        }
        // todo: убрать, когда появится бэкенд
        gymSchemeExample.image = BitmapHelper.bitmapToByteArray(
            BitmapFactory.decodeResource(resources, R.drawable.im_gym_scheme)
        )!!
    }
}
