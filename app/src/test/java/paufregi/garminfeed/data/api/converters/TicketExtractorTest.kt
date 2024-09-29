package paufregi.garminfeed.data.api.converters

import com.google.common.truth.Truth.assertThat

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import paufregi.garminfeed.data.api.models.Ticket
import paufregi.garminfeed.htmlForTicket

class TicketExtractorTest {

    private val converter = TicketExtractor()

    private val mediaType = "text/html; charset=UTF-8"

    @Test
    fun `Extract Ticket`() {
        val responseBody = htmlForTicket.toResponseBody(mediaType.toMediaType())

        assertThat(converter.convert(responseBody)).isEqualTo(Ticket("TEST_TICKET_VALUE"))
    }

    @Test
    fun `No Ticket`() {
        val responseBody = "".toResponseBody(mediaType.toMediaType())

                assertThat(converter.convert(responseBody)).isEqualTo(Ticket(""))
    }
}