package paufregi.garminfeed.presentation.quickedit

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.ActivityType
import paufregi.garminfeed.core.models.Course
import paufregi.garminfeed.core.models.EventType
import paufregi.garminfeed.core.models.Profile

class QuickEditStatePreview : PreviewParameterProvider<QuickEditState> {
    override val values = sequenceOf(
        QuickEditState(true),
        QuickEditState(false),
        QuickEditState(
            selectedActivity = Activity(1, "activity", ActivityType.Running),
            selectedProfile = Profile("name", EventType.transportation, ActivityType.Running, Course.commuteHome, 1)
        ),
        QuickEditState(
            selectedActivity = Activity(1, "activity", ActivityType.Cycling),
            selectedProfile = Profile("name", EventType.transportation, ActivityType.Cycling, Course.commuteHome, 1),
            selectedFeel = 50f,
            selectedEffort = 50f,
        )
    )
}
