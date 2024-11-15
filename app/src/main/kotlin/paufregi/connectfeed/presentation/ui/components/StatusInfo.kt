package paufregi.connectfeed.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Timelapse
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp

sealed class StatusInfoType(
    val icon: ImageVector,
    val color: Color
){
    object Success: StatusInfoType(Icons.Default.CheckCircleOutline, Color.Green)
    object Failure: StatusInfoType(Icons.Default.WarningAmber, Color.Red)
    object Waiting: StatusInfoType(Icons.Default.Timelapse, Color.Cyan)
    object Unknown: StatusInfoType(Icons.Default.WarningAmber, Color.DarkGray)
}

@Preview
@Composable
@ExperimentalMaterial3Api
fun StatusInfo(
    @PreviewParameter(StatusInfoTypePreview ::class) type: StatusInfoType,
    text: String = "",
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues()
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Icon(
            imageVector = type.icon,
            contentDescription = text,
            tint = type.color,
            modifier = Modifier.scale(2.5f)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = text, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(modifier = Modifier.height(50.dp))
        Button(text = "Done", onClick = onClick)
    }
}

private class StatusInfoTypePreview : PreviewParameterProvider<StatusInfoType> {
    override val values = sequenceOf(
        StatusInfoType.Success,
        StatusInfoType.Failure,
        StatusInfoType.Waiting,
        StatusInfoType.Unknown,
    )
}
