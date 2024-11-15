package paufregi.connectfeed.data.api.converters

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.connectfeed.data.api.models.Ticket

class TicketStringConverterTest {

    private val converter = TicketStringConverter()

    @Test
    fun `Convert Ticket to string`() {
        val result = converter.convert(Ticket("test"))

        assertThat(result).isEqualTo("test")
    }
}
