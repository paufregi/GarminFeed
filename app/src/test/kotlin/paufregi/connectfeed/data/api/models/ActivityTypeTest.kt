package paufregi.connectfeed.data.api.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.connectfeed.core.models.ActivityType as CoreActivityType

class ActivityTypeTest {

    @Test
    fun `To Core activity - running`() {
        val activityType = ActivityType(id = 1, key = "running")

        assertThat(activityType.toCore()).isEqualTo(CoreActivityType.Running)
    }

    @Test
    fun `To Core activity - cycling`() {
        val activityType = ActivityType(id = 10, key = "cycling")

        assertThat(activityType.toCore()).isEqualTo(CoreActivityType.Cycling)
    }

    @Test
    fun `To Core activity - unknown`() {
        val activityType = ActivityType(id = 13, key = "strength_training")

        assertThat(activityType.toCore()).isEqualTo(CoreActivityType.Unknown)
    }
}