package paufregi.garminfeed.utils

import android.content.Context
import com.garmin.fit.DateTime
import com.garmin.fit.FileEncoder
import com.garmin.fit.FileIdMesg
import com.garmin.fit.Fit
import com.garmin.fit.Manufacturer
import com.garmin.fit.WeightScaleMesg
import paufregi.garminfeed.models.Weight
import java.io.File
import java.time.Instant

object Fit {
    fun weight(context: Context, weights: List<Weight>): File {
        val filename = "ws_${Formatter.dateTimeForFilename.format(Instant.now())}.fit"
        val file = File(context.cacheDir, filename)
        val encoder = FileEncoder(File(context.cacheDir, filename), Fit.ProtocolVersion.V1_0)

        val fileIdMesg = FileIdMesg()
        fileIdMesg.type = com.garmin.fit.File.WEIGHT
        fileIdMesg.manufacturer = Manufacturer.GARMIN
        fileIdMesg.product = 1
        fileIdMesg.serialNumber = 1L
        encoder.write(fileIdMesg)

        weights.forEach { weight ->
            val weightMesg = WeightScaleMesg()
            weightMesg.timestamp = DateTime(weight.timestamp)
            weightMesg.weight = weight.weight
            weightMesg.bmi = weight.bmi
            weightMesg.percentFat = weight.fat
            weightMesg.visceralFatRating = weight.visceralFat
            weightMesg.percentHydration = weight.water
            weightMesg.muscleMass = weight.muscle
            weightMesg.boneMass = weight.bone
            weightMesg.basalMet = weight.basalMet
            weightMesg.metabolicAge = weight.metabolicAge
            encoder.write(weightMesg)
        }

        encoder.close()
        return file
    }
}