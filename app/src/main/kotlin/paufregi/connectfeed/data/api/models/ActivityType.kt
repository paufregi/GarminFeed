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
        10L -> CoreActivityType.Cycling
        else -> CoreActivityType.Unknown
    }

    fun fromCore(type: CoreActivityType): ActivityType = when (type) {
        CoreActivityType.Running -> ActivityType(1L, "running")
        CoreActivityType.Cycling -> ActivityType(10L, "cycling")
        else -> ActivityType(0L, "unknown")
    }
}