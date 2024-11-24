package paufregi.connectfeed.presentation.quickedit

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Profile

class QuickEditStatePreview : PreviewParameterProvider<QuickEditState> {
    override val values = sequenceOf(
        QuickEditState(ProcessState.Processing),
        QuickEditState(ProcessState.Idle),
        QuickEditState(
            selectedActivity = Activity(id = 1, name = "activity", type = ActivityType.Running),
            selectedProfile = Profile(name = "name", activityType = ActivityType.Running)
        ),
        QuickEditState(
            selectedActivity = Activity(id = 2, name = "activity", type = ActivityType.Cycling),
            selectedProfile = Profile(name = "name", activityType = ActivityType.Cycling),
            selectedFeel = 50f,
            selectedEffort = 50f,
        )
    )
}
