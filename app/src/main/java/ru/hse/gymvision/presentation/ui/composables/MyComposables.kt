package ru.hse.gymvision.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hse.gymvision.R

@Composable
fun MyTitle(
    text: String
) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
}

@Composable
fun MyTextField(
    value: String,
    label: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier.padding(8.dp),
        value = value,
        label = { Text(label) },
        isError = isError,
        onValueChange = onValueChange
    )
}

@Composable
fun MyPasswordField(
    value: String,
    label: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    onIconClick: () -> Unit,
    passwordVisibility: Boolean
) {
    TextField(
        modifier = Modifier.padding(8.dp),
        value = value,
        label = { Text(label) },
        isError = isError,
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = onIconClick) {
                Icon(
                    painterResource(id = if (passwordVisibility) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun LoadingBlock() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun LoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}