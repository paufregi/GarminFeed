package paufregi.connectfeed.data.database.coverters

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.connectfeed.core.models.ActivityType

class ActivityTypeConverterTest {

    private val converter = ActivityTypeConverter()

    @Test
    fun `To Activity type - any`() {
        val result = converter.fromString("any")

        assertThat(result).isEqualTo(ActivityType.Any)
    }

    @Test
    fun `To Activity type - running`() {
        val result = converter.fromString("running")

        assertThat(result).isEqualTo(ActivityType.Running)
    }

    @Test
    fun `To Activity type - trail running`() {
        val result = converter.fromString("trail running")

        assertThat(result).isEqualTo(ActivityType.TrailRunning)
    }

    @Test
    fun `To Activity type - trail_running`() {
        val result = converter.fromString("trail_running")

        assertThat(result).isEqualTo(ActivityType.TrailRunning)
    }

    @Test
    fun `To Activity type - cycling`() {
        val result = converter.fromString("cycling")

        assertThat(result).isEqualTo(ActivityType.Cycling)
    }

    @Test
    fun `To Activity type - strength`() {
        val result = converter.fromString("strength")

        assertThat(result).isEqualTo(ActivityType.Strength)
    }

    @Test
    fun `To Activity type - strength_training`() {
        val result = converter.fromString("strength_training")

        assertThat(result).isEqualTo(ActivityType.Strength)
    }

    @Test
    fun `To Activity type - unknown`() {
        val result = converter.fromString("nope")

        assertThat(result).isEqualTo(ActivityType.Unknown)
    }
}