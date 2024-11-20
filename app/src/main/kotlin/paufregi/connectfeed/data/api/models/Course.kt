package paufregi.connectfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.connectfeed.core.models.Course as CoreCourse

@Keep
@Serializable
data class Course(
    @SerializedName("courseId")
    val id: Long,
    @SerializedName("courseName")
    val name: String,
    @SerializedName("activityType")
    val type: ActivityType
) {
    fun toCore(): CoreCourse =
        CoreCourse(
            id = this.id,
            name = this.name,
            type = this.type.toCore()
        )
}