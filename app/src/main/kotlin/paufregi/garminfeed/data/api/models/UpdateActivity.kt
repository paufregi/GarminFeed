package paufregi.garminfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class UpdateActivity(
    @SerializedName("activityId")
    val id: Long,
    @SerializedName("activityName")
    val name: String,
    @SerializedName("eventTypeDTO")
    val eventType: EventType,
    @SerializedName("metadataDTO")
    val metadata: Metadata?,
    @SerializedName("summaryDTO")
    val summary: Summary?
)