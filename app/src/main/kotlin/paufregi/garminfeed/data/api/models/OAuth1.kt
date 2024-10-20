package paufregi.garminfeed.data.api.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class OAuth1(
    val token: String,
    val secret: String,
) {
    fun isValid(): Boolean {
        return token.isNotBlank() && secret.isNotBlank()
    }

    override fun toString(): String {
        return "Oauth1(token: $token, secret: $secret)"
    }
}


