package paufregi.garminfeed.garmin.data

import androidx.annotation.Keep

@Keep
data class Oauth1(
    val token: String,
    val secret: String,
) {
    override fun toString(): String {
        return "Oauth1(token: $token, secret: $secret)"
    }

    fun isValid(): Boolean {
        return token.isNotBlank() && secret.isNotBlank()
    }
}


