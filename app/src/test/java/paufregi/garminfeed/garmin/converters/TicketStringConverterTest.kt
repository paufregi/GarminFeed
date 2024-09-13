package paufregi.garminfeed.garmin.converters

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.garminfeed.garmin.data.Ticket

class TicketStringConverterTest {

    private val converter = TicketStringConverter()

    @Test
    fun `Convert Ticket to string`() {
        val result = converter.convert(Ticket("test"))

        assertThat(result).isEqualTo("test")
    }
}
