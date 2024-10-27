package paufregi.garminfeed.data.api.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.garminfeed.core.models.ActivityType as CoreActivityType

class ActivityTypeTest {

    @Test
    fun `To Core activity - running`() {
        val activityType = ActivityType(typeId = 1, typeKey = "running")

        assertThat(activityType.toCore()).isEqualTo(CoreActivityType.Running)
    }

    @Test
    fun `To Core activity - cycling`() {
        val activityType = ActivityType(typeId = 10, typeKey = "cycling")

        assertThat(activityType.toCore()).isEqualTo(CoreActivityType.Cycling)
    }

    @Test
    fun `To Core activity - unknown`() {
        val activityType = ActivityType(typeId = 13, typeKey = "strength_training")

        assertThat(activityType.toCore()).isEqualTo(CoreActivityType.Unknown)
    }

    @Test
    fun `To string`() {
        val activityType = ActivityType(typeId = 1, typeKey = "running")

        assertThat(activityType.toString()).isEqualTo("ActivityType(typeId: 1, typeKey: running)")
    }
}
