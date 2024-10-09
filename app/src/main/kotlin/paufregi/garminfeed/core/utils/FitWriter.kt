package paufregi.garminfeed.core.utils

import com.garmin.fit.DateTime
import com.garmin.fit.FileEncoder
import com.garmin.fit.FileIdMesg
import com.garmin.fit.Fit
import com.garmin.fit.Manufacturer
import com.garmin.fit.WeightScaleMesg
import paufregi.garminfeed.core.models.Weight
import java.io.File

object FitWriter {
    fun weights(file: File, weights: List<Weight>) {
        val encoder = FileEncoder(file, Fit.ProtocolVersion.V1_0)

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
    }
}