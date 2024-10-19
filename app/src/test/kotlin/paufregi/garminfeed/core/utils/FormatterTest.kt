package paufregi.garminfeed.core.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class FormatterTest {

    val zoneId = ZoneId.of("Pacific/Auckland")

    @Test
    fun `Formatter date time for filename`() {
        val date = Instant.ofEpochMilli(1704057630000) // 2024-01-01 10:20:30
        val result = Formatter.dateTimeForFilename(zoneId).format(date)
        assertThat(result).isEqualTo("20240101_102030")
    }

    @Test
    fun `Formatter date time for importer`() {
        val date = Date.from(Instant.ofEpochMilli(1704057630000))
        val result = Formatter.dateTimeForImport(Locale.ENGLISH).parse("2024-01-01 10:20:30")
        assertThat(result).isEqualTo(date)
    }
}