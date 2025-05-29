package ru.hse.gymvision.ui.composables

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.hse.gymvision.ui.theme.GymVisionTheme

@Composable
fun AccountButton(
    textId: Int,
    iconId: Int,
    onClick: () -> Unit,
    contentDescription: String? = null,
    isDangerous: Boolean = false
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .height(50.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(start=8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isDangerous)
                        MaterialTheme.colorScheme.errorContainer.copy(0.6f)
                    else 
                        MaterialTheme.colorScheme.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = contentDescription,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }

        Text(
            text = stringResource(id = textId),
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            color = if (isDangerous)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountButtonPreview() {
    GymVisionTheme {
        AccountButton(
            textId = ru.hse.gymvision.R.string.change_email,
            iconId = ru.hse.gymvision.R.drawable.ic_mail,
            onClick = {},
            contentDescription = "Account Button",
            isDangerous = false
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0x00000000)
@Composable
fun AccountButtonDangerousPreview() {
    GymVisionTheme(true) {
        AccountButton(
            textId = ru.hse.gymvision.R.string.delete_account,
            iconId = ru.hse.gymvision.R.drawable.ic_delete,
            onClick = {},
            contentDescription = "Dangerous Account Button",
            isDangerous = true
        )
    }
}