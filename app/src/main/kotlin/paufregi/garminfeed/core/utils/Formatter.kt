package paufregi.garminfeed.core.utils

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatter {
    val dateTimeForFilename: DateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyyMMdd_hhmmss")
        .withZone(ZoneId.systemDefault())

    val dateTimeForImport = { locale: Locale -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale) }
}