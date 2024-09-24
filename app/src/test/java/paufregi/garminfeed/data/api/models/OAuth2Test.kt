package paufregi.garminfeed.data.api.models

import org.junit.Test
import com.google.common.truth.Truth.assertThat
import java.util.Date

class OAuth2Test {

    @Test
    fun `Valid access token`() {
        val oauth2 = OAuth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NCIsIm5hbWUiOiJQYXVsIEVsbGlzIiwiZXhwIjoxNzA0MDI0MDAwLCJpYXQiOjE3MDQwMjQwMDB9.BAAoEhz3DEQfSe77n1BtDZEYX-e3_2_lfGIgx-QXEew", // ExpiresAt: 2024-01-01T00:00
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEN",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704024000
        )

        val date = Date(1672488000) // 2023-01-01T00:00

        assertThat(oauth2.isExpired(date)).isFalse()
    }

    @Test
    fun `Expired access token`() {
        val oauth2 = OAuth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NCIsIm5hbWUiOiJQYXVsIEVsbGlzIiwiZXhwIjoxNzA0MDI0MDAwLCJpYXQiOjE3MDQwMjQwMDB9.BAAoEhz3DEQfSe77n1BtDZEYX-e3_2_lfGIgx-QXEew", // ExpiresAt: 2024-01-01T00:00
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEN",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704024000
        )

        val date = Date(1722430800000) // 2024-08-01T00:00

        assertThat(oauth2.isExpired(date)).isTrue()
    }

    @Test
    fun `Empty access token Oauth2`() {
        val oauth2 = OAuth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "",
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEN",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704024000
        )

        val date = Date(1722430800000) // 2024-08-01T00:00

        assertThat(oauth2.isExpired(date)).isTrue()
    }

    @Test
    fun `To string` () {
        val oauth2 = OAuth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "ACCESS",
            tokenType = "TYPE",
            refreshToken = "REFRESH",
            expiresIn = 1,
            refreshTokenExpiresIn = 1
        )

        assertThat(oauth2.toString()).isEqualTo("Oauth2(scope: SCOPE, jti: JTI, accessToken: ACCESS, tokenType: TYPE, refreshToken: REFRESH, expiresIn: 1, refreshTokenExpiresIn: 1)")
    }

}