package paufregi.garminfeed.models

import java.util.Date

data class Weight(
    val timestamp: Date,
    val weight: Float,
    val bmi: Float,
    val fat: Float,
    val visceralFat: Short,
    val water: Float,
    val muscle: Float,
    val bone: Float,
    val basalMet: Float,
    val metabolicAge: Short,
)

