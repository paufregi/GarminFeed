package paufregi.garminfeed.garmin.data

import androidx.annotation.Keep
import com.auth0.jwt.JWT
import com.google.gson.annotations.SerializedName
import java.util.Date

@Keep
data class Oauth2(
    @SerializedName("scope")
    val scope: String,
    @SerializedName("jti")
    val jti: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Long,
    @SerializedName("refresh_token_expires_in")
    val refreshTokenExpiresIn: Long,
) {
    fun isExpired(): Boolean {
        return accessToken.isBlank() || JWT.decode(accessToken).expiresAt.before(Date())
    }

    override fun toString(): String {
        return "Oauth2(scope: $scope, jti: $jti, accessToken: $accessToken, tokenType: $tokenType, refreshToken: $refreshToken, expiresIn: $expiresIn, refreshTokenExpiresIn: $refreshTokenExpiresIn)"
    }
}


