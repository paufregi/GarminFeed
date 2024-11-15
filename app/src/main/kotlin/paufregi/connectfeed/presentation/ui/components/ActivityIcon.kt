package paufregi.connectfeed.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.ActivityType

@Preview
@Composable
@ExperimentalMaterial3Api
fun ActivityIcon(
    @PreviewParameter(ActivityTypePreview ::class) activityType: ActivityType?
) {
    when (activityType) {
        is ActivityType.Running -> Icon(Icons.AutoMirrored.Default.DirectionsRun, null)
        is ActivityType.Cycling -> Icon(Icons.AutoMirrored.Default.DirectionsBike, null)
        else -> null
    }
}

private class ActivityTypePreview : PreviewParameterProvider<ActivityType?> {
    override val values = sequenceOf(
        ActivityType.Running,
        ActivityType.Cycling,
        ActivityType.Unknown,
        null
    )
}