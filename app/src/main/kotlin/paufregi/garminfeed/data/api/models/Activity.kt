package paufregi.garminfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.garminfeed.core.models.Activity as CoreActivity

@Keep
@Serializable
data class Activity(
    @SerializedName("activityId")
    val activityId: Long,
    @SerializedName("activityName")
    val activityName: String,
    @SerializedName("activityType")
    val activityType: ActivityType
) {
    fun toCore(): CoreActivity =
        CoreActivity(
            id = this.activityId,
            name = this.activityName,
            type = this.activityType.toCore()
        )

    override fun toString(): String {
        return "Activity(activityId: $activityId, activityName: $activityName, activityType: $activityType)"
    }
}