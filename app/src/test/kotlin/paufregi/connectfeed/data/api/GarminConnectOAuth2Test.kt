package paufregi.connectfeed.data.api

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.data.api.models.OAuth1
import paufregi.connectfeed.data.api.models.OAuth2
import paufregi.connectfeed.data.api.models.OAuthConsumer

class GarminConnectOAuth2Test {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: GarminConnectOAuth2
    private val consumer = OAuthConsumer("KEY", "SECRET")
    private val oauth = OAuth1("TOKEN", "SECRET")

    @Before
    fun setup() {
        server.start()
        api = GarminConnectOAuth2.client(consumer, oauth, server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get OAuth2 token`() = runTest {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody("""{"scope": "SCOPE","jti": "JTI","access_token": "ACCESS_TOKEN","token_type": "TOKEN_TYPE","refresh_token": "REFRESH","expires_in": 1704020400,"refresh_token_expires_in": 1704020400}""")
        server.enqueue(response)

        val res = api.getOauth2()

        val request = server.takeRequest()
        val expected = OAuth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "ACCESS_TOKEN",
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704020400
        )

        assertThat(request.method).isEqualTo("POST")
        assertThat(request.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/exchange/user/2.0")
        assertThat(request.headers["authorization"]).contains("OAuth")
        assertThat(request.headers["authorization"]).contains("""oauth_consumer_key="KEY"""")
        assertThat(request.headers["authorization"]).contains("""oauth_token="TOKEN"""")
        assertThat(request.headers["authorization"]).contains("""oauth_signature_method="HMAC-SHA1"""")
        assertThat(request.headers["authorization"]).contains("""oauth_signature""")
        assertThat(request.headers["authorization"]).contains("""oauth_version="1.0"""")
        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(expected)
    }

    @Test
    fun `Get OAuth2 token - failure`() = runTest {
        val response = MockResponse()
            .setResponseCode(400)
        server.enqueue(response)

        val res = api.getOauth2()

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.body()).isNull()
    }
}