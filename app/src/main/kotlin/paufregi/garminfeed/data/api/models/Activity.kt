package paufregi.garminfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.garminfeed.core.models.Activity as CoreActivity

@Keep
@Serializable
data class Activity(
    @SerializedName("activityId")
    val id: Long,
    @SerializedName("activityName")
    val name: String,
    @SerializedName("activityType")
    val type: ActivityType
) {
    fun toCore(): CoreActivity =
        CoreActivity(
            id = this.id,
            name = this.name,
            type = this.type.toCore()
        )
}