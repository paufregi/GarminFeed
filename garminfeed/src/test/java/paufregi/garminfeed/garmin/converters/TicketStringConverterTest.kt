package paufregi.garminfeed.garmin.converters

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import paufregi.garminfeed.garmin.data.CSRF

class CSRFStringConverterTest {

    private val converter = CSRFStringConverter()

    @Test
    fun `Convert CSRF`() {
        val result = converter.convert(CSRF("test"))

        assertThat(result).isEqualTo("test")
    }
}
