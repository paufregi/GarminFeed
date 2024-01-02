package paufregi.garminfeed.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.DialogProperties

@Composable
@ExperimentalMaterial3Api
fun LoadingDialog() {
    AlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}