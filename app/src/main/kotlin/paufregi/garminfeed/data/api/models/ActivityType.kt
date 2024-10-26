package paufregi.garminfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.garminfeed.core.models.ActivityType as CoreActivityType

@Keep
@Serializable
data class ActivityType(
    @SerializedName("typeId")
    val typeId: Long,
    @SerializedName("typeKey")
    val typeKey: String
) {
    fun toCore(): CoreActivityType = when (this.typeId) {
        1L -> CoreActivityType.Running
        10L -> CoreActivityType.Cycling
        else -> CoreActivityType.Unknown
    }

    override fun toString(): String {
        return "ActivityType(typeId: $typeId, typeKey: $typeKey)"
    }
}