package paufregi.connectfeed.data.api.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.connectfeed.core.models.EventType as CoreEventType

class EventTypeTest {

    @Test
    fun `To Core event type`() {
        val eventType = EventType(id = 1, key = "event")
        val coreEventType = CoreEventType(id = 1, name = "event")


        assertThat(eventType.toCore()).isEqualTo(coreEventType)
    }

    @Test
    fun `To Core event type - no id`() {
        val eventType = EventType(id = null, key = "trail_running")

        assertThat(eventType.toCore()).isNull()
    }

    @Test
    fun `To Core event type - no name`() {
        val eventType = EventType(id = 1, key = null)

        assertThat(eventType.toCore()).isNull()
    }
}
