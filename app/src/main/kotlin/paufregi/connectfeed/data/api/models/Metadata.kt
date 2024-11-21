package paufregi.connectfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.connectfeed.core.models.ActivityType as CoreActivityType

@Keep
@Serializable
data class Metadata(
    @SerializedName("associatedCourseId")
    val courseId: Long?,
)