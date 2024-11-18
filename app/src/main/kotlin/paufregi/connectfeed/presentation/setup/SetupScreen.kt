package paufregi.connectfeed.presentation.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
@ExperimentalMaterial3Api
internal fun SetupScreen(paddingValues: PaddingValues = PaddingValues()) {
    Scaffold(
        modifier = Modifier.padding(paddingValues),
    ) { pv ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Please setup your credential",
                tint = Color.Red,
                modifier = Modifier
                    .scale(3f)
                    .padding(30.dp)
            )
            Text(text = "Please setup your credential")
        }
    }
}
