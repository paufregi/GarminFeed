package paufregi.garminfeed.data.api.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.garminfeed.core.models.Activity as CoreActivity
import paufregi.garminfeed.core.models.ActivityType as CoreActivityType

class ActivityTest {

    @Test
    fun `To Core activity`() {
        val activity = Activity(
            id = 1,
            name = "name",
            type = ActivityType(id = 1, key = "running")
        )

        val coreActivity = CoreActivity(
            id = 1,
            name = "name",
            type = CoreActivityType.Running
        )

        assertThat(activity.toCore()).isEqualTo(coreActivity)
    }
}
