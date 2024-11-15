package paufregi.connectfeed.core.utils

import org.apache.commons.csv.CSVFormat
import paufregi.connectfeed.core.models.Weight
import java.io.InputStream
import java.util.Locale

object RenphoReader {
    fun read(inputStream: InputStream): List<Weight> {
        return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
            setIgnoreSurroundingSpaces(true)
        }.build().parse(inputStream.reader())
            .drop(1)
            .mapNotNull { record ->
                val timestamp = Formatter.dateTimeForImport(Locale.getDefault()).parse(record[0])
                timestamp?.let {
                    Weight(
                        timestamp = it,
                        weight = record[1].toFloat(),
                        bmi = record[2].toFloat(),
                        fat = record[3].toFloat(),
                        visceralFat = record[6].toFloat().toInt().toShort(),
                        water = record[7].toFloat(),
                        muscle = record[9].toFloat(),
                        bone = record[10].toFloat(),
                        basalMet = record[12].toFloat(),
                        metabolicAge = record[13].toShort(),
                    )
                }
            }
    }
}