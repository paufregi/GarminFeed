package paufregi.connectfeed.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterial3Api
fun SimpleScaffold(
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connect Feed") },
                actions = {
                    Button(
                        icon = Icons.Filled.Menu,
                        onClick = { },
                        enabled = false
                    )
                }
            )
        }
    ) {
        content(it)
    }
}