package paufregi.garminfeed.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import paufregi.garminfeed.models.Weight
import java.time.Instant
import java.time.Period
import java.util.Date

class WeightsPreview : PreviewParameterProvider<List<Weight>> {
    override val values = sequenceOf(
        listOf(
            Weight(
                timestamp = Date.from(Instant.now()),
                weight = 71.90f,
                bmi = 21.7f,
                fat = 20.9f,
                visceralFat = 6,
                water = 57.8f,
                muscle = 53.8f,
                bone = 2.88f,
                basalMet = 1614f,
                metabolicAge = 32
            ),
            Weight(
                timestamp = Date.from(Instant.now().minus(Period.ofDays(1))),
                weight = 71.50f,
                bmi = 21.5f,
                fat = 20.7f,
                visceralFat = 6,
                water = 58.1f,
                muscle = 54.5f,
                bone = 2.88f,
                basalMet = 1616f,
                metabolicAge = 32
            )
        ),
    )
}