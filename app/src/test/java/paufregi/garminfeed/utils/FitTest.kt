package paufregi.garminfeed.utils

import com.garmin.fit.DateTime
import com.garmin.fit.MesgBroadcaster
import com.garmin.fit.WeightScaleMesgListener
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import paufregi.garminfeed.models.Weight
import java.io.File
import java.time.Instant
import java.util.Date

class FitTest {

    @Test
    fun `Generate Fit file from list of weight`() {
        val weight = Weight(
            timestamp = Date.from(Instant.ofEpochMilli(1704057630000)),
            weight = 76.15f,
            bmi = 23.8f,
            fat = 23.2f,
            visceralFat = 7,
            water = 55.4f,
            muscle = 55.59f,
            bone = 2.89f,
            basalMet = 1618f,
            metabolicAge = 35,
        )
        val file = File.createTempFile("garmin-feed", "test")
        file.deleteOnExit()

        Fit.weight(file, listOf(weight))

        val msgBroadcaster = MesgBroadcaster()

        msgBroadcaster.addListener(WeightScaleMesgListener { it ->
            assertThat(it.timestamp).isEquivalentAccordingToCompareTo(DateTime(weight.timestamp))
            assertThat(it.weight).isEqualTo(weight.weight)
            assertThat(it.bmi).isEqualTo(weight.bmi)
            assertThat(it.percentFat).isEqualTo(weight.fat)
            assertThat(it.visceralFatRating).isEqualTo(weight.visceralFat)
            assertThat(it.percentHydration).isEqualTo(weight.water)
            assertThat(it.muscleMass).isEqualTo(weight.muscle)
            assertThat(it.boneMass).isEqualTo(weight.bone)
            assertThat(it.basalMet).isEqualTo(weight.basalMet)
            assertThat(it.metabolicAge).isEqualTo(weight.metabolicAge)
        })

        msgBroadcaster.run(file.inputStream())
    }
}