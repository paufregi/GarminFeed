package paufregi.connectfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.connectfeed.core.models.ActivityType as CoreActivityType

@Keep
@Serializable
data class Summary(
    @SerializedName("waterConsumed")
    val water: Int?,
    @SerializedName("directWorkoutFeel")
    val feel: Float?, // 0.0-25.0-50.0-75.0-100.0
    @SerializedName("directWorkoutRpe")
    val effort: Float?, // 10.0-100.0 (step 10.0)
)