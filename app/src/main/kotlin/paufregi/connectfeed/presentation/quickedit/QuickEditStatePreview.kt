package paufregi.connectfeed.presentation.quickedit

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

class QuickEditStatePreview : PreviewParameterProvider<QuickEditState> {
    override val values = sequenceOf(
        QuickEditState(ProcessState.Processing),
        QuickEditState(ProcessState.Idle),
        QuickEditState(
            selectedActivity = Activity(1, "activity", ActivityType.Running),
            selectedProfile = Profile("name", true, EventType.transportation, ActivityType.Running, Course.commuteHome, 1)
        ),
        QuickEditState(
            selectedActivity = Activity(1, "activity", ActivityType.Cycling),
            selectedProfile = Profile("name", true, EventType.transportation, ActivityType.Cycling, Course.commuteHome, 1),
            selectedFeel = 50f,
            selectedEffort = 50f,
        )
    )
}
