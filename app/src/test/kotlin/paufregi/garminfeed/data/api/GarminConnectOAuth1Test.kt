package paufregi.garminfeed.data.api

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.api.models.Ticket

class GarminConnectOAuth1Test {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: GarminConnectOAuth1
    private val consumer = OAuthConsumer("KEY", "SECRET")

    @Before
    fun setup() {
        server.start()
        api = GarminConnectOAuth1.client(consumer, server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get OAuth1`() = runTest {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody("oauth_token=TOKEN&oauth_token_secret=SECRET")
        server.enqueue(response)

        val ticket = Ticket("TICKET")
        val res = api.getOauth1(ticket)

        val request = server.takeRequest()

        assertThat(request.method).isEqualTo("GET")
        assertThat(request.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/preauthorized")
        assertThat(request.requestUrl?.queryParameterValues("ticket")).isEqualTo(listOf("TICKET"))
        assertThat(request.headers["authorization"]).contains("OAuth")
        assertThat(request.headers["authorization"]).contains("""oauth_consumer_key="KEY"""")
        assertThat(request.headers["authorization"]).contains("""oauth_signature_method="HMAC-SHA1"""")
        assertThat(request.headers["authorization"]).contains("""oauth_signature""")
        assertThat(request.headers["authorization"]).contains("""oauth_version="1.0"""")
        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(OAuth1(token="TOKEN", secret="SECRET"))

    }

    @Test
    fun `Get OAuth1 - failure`() = runTest {
        val response = MockResponse()
            .setResponseCode(400)
        server.enqueue(response)

        val ticket = Ticket("TICKET")
        val res = api.getOauth1(ticket)

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.body()).isNull()
    }
}