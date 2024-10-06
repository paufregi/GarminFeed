package paufregi.garminfeed.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalMaterial3Api
fun StatusInfo(
    icon: ImageVector,
    iconTint: Color,
    text: String,
    onClick: () -> Unit = {},
    contentPadding: PaddingValues
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconTint,
            modifier = Modifier.scale(2.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = text, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(modifier = Modifier.height(50.dp))
        Button(text = "Done", onClick = onClick)
    }
}

object StatusInfo {
    @Composable
    @ExperimentalMaterial3Api
    fun Failure(
        onClick: () -> Unit,
        contentPadding: PaddingValues
    ) {
        StatusInfo(
            icon = Icons.Default.WarningAmber,
            iconTint = Color.Red,
            text = "Sync failed",
            onClick = onClick,
            contentPadding = contentPadding
        )
    }

    @Composable
    @ExperimentalMaterial3Api
    fun Success(
        onClick: () -> Unit,
        contentPadding: PaddingValues
    ) {
        StatusInfo(
            icon = Icons.Default.CheckCircleOutline,
            iconTint = Color.Green,
            text = "Sync succeeded",
            onClick = onClick,
            contentPadding = contentPadding
        )
    }

    @Composable
    @ExperimentalMaterial3Api
    fun Unknown(
        onClick: () -> Unit,
        contentPadding: PaddingValues
    ) {
        StatusInfo(
            icon = Icons.Default.WarningAmber,
            iconTint = Color.DarkGray,
            text = "Don't know what to do",
            onClick = onClick,
            contentPadding = contentPadding
        )
    }
}