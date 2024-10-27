package paufregi.garminfeed.data.api.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.garminfeed.core.models.Activity as CoreActivity
import paufregi.garminfeed.core.models.ActivityType as CoreActivityType

class ActivityTest {

    @Test
    fun `To Core activity`() {
        val activity = Activity(
            activityId = 1,
            activityName = "name",
            activityType = ActivityType(typeId = 1, typeKey = "running")
        )

        val coreActivity = CoreActivity(
            id = 1,
            name = "name",
            type = CoreActivityType.Running
        )

        assertThat(activity.toCore()).isEqualTo(coreActivity)
    }

    @Test
    fun `To string`() {
        val activity = Activity(
            activityId = 1,
            activityName = "name",
            activityType = ActivityType(typeId = 1, typeKey = "running")
        )

        assertThat(activity.toString()).isEqualTo("Activity(activityId: 1, activityName: name, activityType: ActivityType(typeId: 1, typeKey: running))")
    }
}
