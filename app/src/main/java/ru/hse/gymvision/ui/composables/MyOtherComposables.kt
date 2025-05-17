package ru.hse.gymvision.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import ru.hse.gymvision.R
import ru.hse.gymvision.ui.theme.GymVisionTheme

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
    enabled: Boolean = true,
    errorText: String? = null,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier.padding(8.dp),
        value = value,
        label = { Text(label) },
        isError = isError,
        enabled = enabled,
        onValueChange = onValueChange,
        supportingText = { errorText?.let { Text(it) } }
    )
}

@Composable
fun MyPasswordField(
    value: String,
    label: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    onIconClick: () -> Unit,
    passwordVisibility: Boolean,
    errorText: String? = null
) {
    TextField(
        modifier = Modifier.padding(8.dp),
        value = value,
        label = { Text(label) },
        isError = isError,
        supportingText = { errorText?.let { Text(it) } },
        onValueChange = onValueChange,
        trailingIcon = {
            IconButton(onClick = onIconClick) {
                Icon(
                    painterResource(id =
                    if (passwordVisibility) R.drawable.ic_visibility
                    else R.drawable.ic_visibility_off),
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
fun MyAlertDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    confirmButtonText: String = "OK",
    onDismissRequest: () -> Unit = onConfirm,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
            ) {
                Text(text = confirmButtonText)
            }
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        titleContentColor = MaterialTheme.colorScheme.error,
        textContentColor = MaterialTheme.colorScheme.onErrorContainer,
    )
}

@Composable
fun MyPopup(
    header: String,
    text: String,
    onDismissRequest: () -> Unit
) {
    Popup(
        alignment = Alignment.TopCenter,
        properties = PopupProperties(),
        onDismissRequest = onDismissRequest
    ) {
        Box(
            Modifier
                .size(300.dp, 100.dp)
                .padding(top = 5.dp)
                .background(
                    MaterialTheme.colorScheme.secondary,
                    RoundedCornerShape(10.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = header,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun MyAlertDialogLightPreview() {
    GymVisionTheme {
        MyAlertDialog(
            title = "Title",
            text = "Text",
            onConfirm = {}
        )
    }
}

@Preview
@Composable
fun MyAlertDialogDarkPreview() {
    GymVisionTheme(darkTheme = true) {
        MyAlertDialog(
            title = "Title",
            text = "Text",
            onConfirm = {}
        )
    }
}