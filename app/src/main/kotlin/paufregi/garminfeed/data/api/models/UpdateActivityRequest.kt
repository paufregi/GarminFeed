package paufregi.garminfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UpdateActivityRequest(
    @SerializedName("activityId")
    val activityId: Long,
    @SerializedName("activityName")
    val activityName: String,
    @SerializedName("eventTypeDTO")
    val eventTypeDTO: EventType,
    @SerializedName("metadataDTO")
    val metadataDTO: Metadata,
    @SerializedName("summaryDTO")
    val summaryDTO: Summary
)
