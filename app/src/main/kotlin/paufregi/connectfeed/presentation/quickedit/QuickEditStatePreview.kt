package paufregi.connectfeed.presentation.quickedit

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.presentation.ui.components.ProcessState

class QuickEditStatePreview : PreviewParameterProvider<QuickEditState> {
    override val values = sequenceOf(
        QuickEditState(ProcessState.Processing),
        QuickEditState(ProcessState.Success("Activity updates")),
        QuickEditState(ProcessState.Failure("Failed to update activity")),
        QuickEditState(
            activity = Activity(id = 1, name = "activity", type = ActivityType.Running),
            profile = Profile(name = "name", activityType = ActivityType.Running)
        ),
        QuickEditState(
            activity = Activity(id = 2, name = "activity", type = ActivityType.Cycling),
            profile = Profile(name = "name", activityType = ActivityType.Cycling),
            feel = 50f,
            effort = 50f,
        )
    )
}
