package paufregi.garminfeed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@Preview
@Composable
@ExperimentalMaterial3Api
fun SuccessDialog(
    onClick: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircleOutline,
                contentDescription = "Sync succeeded",
                tint = Color.Green,
                modifier = Modifier.scale(2f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Sync failed")
            Spacer(modifier = Modifier.height(50.dp))
            Button(text = "Done", onClick = onClick)
        }
    }
}