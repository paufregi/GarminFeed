package paufregi.connectfeed.presentation.profiles

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

class ProfilesStatePreview : PreviewParameterProvider<ProfilesState> {
    override val values = sequenceOf(
        ProfilesState(profiles = emptyList()),
        ProfilesState(
            profiles = listOf(
                Profile(
                    name = "Commute to home",
                    rename = true,
                    eventType = EventType.transportation,
                    activityType = ActivityType.Cycling,
                    course = Course.commuteHome,
                    water = 550),
                Profile(
                    name = "Commute to work",
                    rename = true,
                    eventType = EventType.transportation,
                    activityType = ActivityType.Cycling,
                    course = Course.commuteWork,
                    water = 550),
                Profile(
                    name = "Ponsonby/Westhaven",
                    rename = true,
                    eventType = EventType.training,
                    activityType = ActivityType.Running,
                    course = Course.ponsonbyWesthaven),
                Profile(
                    name = "Auckland CBD",
                    rename = true,
                    eventType = EventType.training,
                    activityType = ActivityType.Running,
                    course = Course.aucklandCBD),
            )
        )
    )
}