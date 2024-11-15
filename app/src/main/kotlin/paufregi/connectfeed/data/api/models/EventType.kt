package paufregi.connectfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class EventType(
    @SerializedName("typeId")
    val typeId: Long,
    @SerializedName("typeKey")
    val typeKey: String,
)