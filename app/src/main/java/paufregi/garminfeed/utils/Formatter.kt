package paufregi.garminfeed.utils

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatter {
    val date = { locale: Locale -> SimpleDateFormat("dd MMM yyyy", locale) }

    val dayOfWeek = { locale: Locale -> SimpleDateFormat("EEEE", locale) }

    val dateTimeForFilename: DateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyyMMdd_kkmmss")
        .withZone(ZoneId.systemDefault())

    val dateTimeForImport = { locale: Locale -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale) }

    val decimal = DecimalFormat("#.00")
}