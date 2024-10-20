package paufregi.garminfeed.core.utils

import com.google.common.truth.Truth.assertThat
import org.apache.commons.io.IOUtils
import org.junit.Test
import paufregi.garminfeed.core.models.Weight
import java.time.Instant
import java.util.Date
import java.util.Locale


class RenphoReaderTest {

    @Test
    fun `Convert Renpho CSV to list of weights`() {
        Locale.setDefault(Locale.ENGLISH)
        val csvText = """
            Time of Measurement,Weight(kg),BMI,Body Fat(%),Fat-Free Mass(kg),Subcutaneous Fat(%),Visceral Fat,Body Water(%),Skeletal Muscle(%),Muscle Mass(kg),Bone Mass(kg),Protein(%),BMR(kcal),Metabolic Age,Optimal weight(kg),Target to optimal weight(kg),Target to optimal fat mass(kg),Target to optimal muscle mass(kg),Body Type,Remarks
            2024-01-01 10:20:30,76.15,23.8,23.2,58.48,20.9,7.0,55.4,49.5,55.59,2.89,17.5,1618,35,,,,,,
        """.trimIndent()

        val stubInputStream = IOUtils.toInputStream(csvText, "UTF-8")

        val expected = Weight(
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

        val result = RenphoReader.read(stubInputStream)

        assertThat(result).containsExactly(expected)
    }
}