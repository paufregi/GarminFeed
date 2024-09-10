package paufregi.garminfeed.garmin.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Oauth1Consumer(
    @SerializedName("consumer_key")
    val key: String,
    @SerializedName("consumer_secret")
    val secret: String
) {
    override fun toString(): String {
        return "OauthConsumer(key: $key, secret: $secret)"
    }
}
