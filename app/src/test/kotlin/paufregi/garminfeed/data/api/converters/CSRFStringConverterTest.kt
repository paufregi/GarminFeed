package paufregi.garminfeed.data.api.converters

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.garminfeed.data.api.models.CSRF

class CSRFStringConverterTest {

    private val converter = CSRFStringConverter()

    @Test
    fun `Convert CSRF to string`() {
        val result = converter.convert(CSRF("test"))

        assertThat(result).isEqualTo("test")
    }
}
