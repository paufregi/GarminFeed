package paufregi.connectfeed.data.api.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.connectfeed.core.models.Activity as CoreActivity

@Keep
@Serializable
data class UserProfile(
    @SerializedName("firstName")
    val firstName: String,
)