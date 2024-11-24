package paufregi.connectfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.connectfeed.core.models.ActivityType as CoreActivityType

@Keep
@Serializable
data class ActivityType(
    @SerializedName("typeId")
    val id: Long,
    @SerializedName("typeKey")
    val key: String
) {
    fun toCore(): CoreActivityType = when (this.id) {
        1L -> CoreActivityType.Running
        6L -> CoreActivityType.TrailRunning
        10L -> CoreActivityType.Cycling
        13L -> CoreActivityType.Strength
        else -> CoreActivityType.Unknown
    }
}