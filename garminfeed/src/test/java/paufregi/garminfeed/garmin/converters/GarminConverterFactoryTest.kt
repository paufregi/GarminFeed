package paufregi.garminfeed.garmin.converters

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk

import org.junit.jupiter.api.Test
import paufregi.garminfeed.garmin.data.CSRF
import paufregi.garminfeed.garmin.data.Ticket
import retrofit2.Retrofit

class GarminConverterFactoryTest {

    @Test
    fun `CSRF string converter`() {
        val result = GarminConverterFactory()
            .stringConverter(CSRF::class.java, arrayOf(), mockk<Retrofit>() )

        assertThat(result).isInstanceOf(CSRFStringConverter::class.java)
    }

    @Test
    fun `Ticket string converter`() {
        val result = GarminConverterFactory()
            .stringConverter(Ticket::class.java, arrayOf(), mockk<Retrofit>() )

        assertThat(result).isInstanceOf(TicketStringConverter::class.java)
    }

    @Test
    fun `Unsupported string converter`() {
        val result = GarminConverterFactory()
            .stringConverter(String::class.java, arrayOf(), mockk<Retrofit>() )

        assertThat(result).isNull()
    }
}