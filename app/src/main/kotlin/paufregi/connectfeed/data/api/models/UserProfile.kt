package paufregi.connectfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.connectfeed.core.models.User as CoreUserProfile

@Keep
@Serializable
data class UserProfile(
    @SerializedName("fullName")
    val name: String,

    @SerializedName("profileImageUrlLarge")
    val avatarUrl: String,
) {
    fun toCore(): CoreUserProfile = CoreUserProfile(
        name = name,
        avatarUrl = avatarUrl
    )
}