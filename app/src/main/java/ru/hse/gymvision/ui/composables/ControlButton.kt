package ru.hse.gymvision.ui.composables

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

@Composable
fun ControlButtonSimple(iconId: Int,
                  contentDescription: String,
                  alignment: Alignment,
                  onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp), contentAlignment = alignment
    ) {
        IconButton(modifier = Modifier.size(64.dp), onClick = onClick) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconId),
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(50.dp),
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ControlButton(
    iconId: Int,
    contentDescription: String,
    alignment: Alignment,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    val isPressed = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = alignment
    ) {
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(64.dp)
                .pointerInteropFilter { event ->
                    when (event.action) {
                        android.view.MotionEvent.ACTION_DOWN -> {
                            if (!isPressed.value) {
                                isPressed.value = true
                                onPress()
                                Log.d("marathinks", "Button pressed")
                            }
                            true
                        }

                        android.view.MotionEvent.ACTION_UP,
                        android.view.MotionEvent.ACTION_CANCEL -> {
                            if (isPressed.value) {
                                isPressed.value = false
                                onRelease()
                                Log.d("marathinks", "Button released")
                            }
                            true
                        }

                        else -> false // Не обрабатываем другие события
                    }
                },
            interactionSource = remember { MutableInteractionSource() }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconId),
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(50.dp),
            )
        }
    }
}
