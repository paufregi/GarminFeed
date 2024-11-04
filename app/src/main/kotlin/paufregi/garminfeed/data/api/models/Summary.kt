package paufregi.garminfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.garminfeed.core.models.ActivityType as CoreActivityType

@Keep
@Serializable
data class Summary(
    @SerializedName("waterConsumed")
    val waterConsumed: Int,
    @SerializedName("directWorkoutFeel")
    val feel: Int, // 0-25-50-75-100
    @SerializedName("directWorkoutRpe")
    val effort: Int, // 10-100 (step 10)
)