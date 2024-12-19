package paufregi.connectfeed.presentation.profile

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.presentation.ui.models.ProcessState

class ProfileStatePreview : PreviewParameterProvider<ProfileState> {
    override val values = sequenceOf(
        ProfileState(process = ProcessState.Processing),
        ProfileState(process = ProcessState.Success("Profile saved")),
        ProfileState(process = ProcessState.Failure("Error saving profile")),
        ProfileState(
            profile = Profile(
                name = "Profile running",
                activityType = ActivityType.Running,
                eventType = EventType(1, "EventType 1"),
                course = Course(1, "Course 1", ActivityType.Running),
                water = 200,
                rename = true,
                customWater = true,
                feelAndEffort = true
            ),
        ),
        ProfileState(
            profile = Profile(
                name = "Profile any",
                activityType = ActivityType.Any,
                eventType = null,
                course = null,
                water = 200,
                rename = false,
            ),
        ),
        ProfileState(
            profile = Profile(
                name = "Profile strength",
                activityType = ActivityType.Strength,
                eventType = null,
                course = null,
                water = 200,
                rename = false,
            ),
        ),

    )
}