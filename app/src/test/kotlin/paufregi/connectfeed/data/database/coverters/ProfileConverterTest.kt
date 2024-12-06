package paufregi.connectfeed.data.database.coverters

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.data.database.entities.ProfileEntity
import paufregi.connectfeed.core.models.Profile

class ProfileConverterTest {

    val profile = Profile(
        id = 1,
        name = "profile",
        activityType = ActivityType.Cycling,
        eventType = EventType(id = 1, name = "event 1"),
        course = Course(id = 1, name = "course 1", type = ActivityType.Cycling),
        water = 550,
        rename = true,
        customWater = true,
        feelAndEffort = true
    )

    val entityProfile = ProfileEntity(
        id = 1,
        name = "profile",
        activityType = ActivityType.Cycling,
        eventType = EventType(id = 1, name = "event 1"),
        course = Course(id = 1, name = "course 1", type = ActivityType.Cycling),
        water = 550,
        rename = true,
        customWater = true,
        feelAndEffort = true
    )

    @Test
    fun `Profile to entity`() {
        val result = profile.toEntity()

        assertThat(result).isEqualTo(entityProfile)
    }

    @Test
    fun `Entity profile to profile`() {
        val result = entityProfile.toCore()

        assertThat(result).isEqualTo(profile)
    }
}
