package paufregi.garminfeed.core.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.util.Date
import java.util.Locale

class FormatterTest {

    @Test
    fun `Formatter date time for filename`() {
        val date = Instant.ofEpochMilli(1704057630000) // 2024-01-01 10:20:30
        assertThat(Formatter.dateTimeForFilename.format(date)).isEqualTo("20240101_102030")
    }

    @Test
    fun `Formatter date time for importer`() {
        val date = Date.from(Instant.ofEpochMilli(1704057630000)) // 2024-01-01 10:20:30
        val result = Formatter.dateTimeForImport(Locale.getDefault()).parse("2024-01-01 10:20:30")
        assertThat(result).isEqualTo(date)
    }
}