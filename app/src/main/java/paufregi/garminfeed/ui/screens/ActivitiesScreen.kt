package paufregi.garminfeed.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
@ExperimentalMaterial3Api
fun ActivitiesScreen(
    nav: NavController = rememberNavController(),
) {
    Scaffold {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Setup credentials",
                tint = Color.Red,
                modifier = Modifier
                    .scale(3f)
                    .padding(30.dp)
            )
            Text(text = "TO DO")
            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}
