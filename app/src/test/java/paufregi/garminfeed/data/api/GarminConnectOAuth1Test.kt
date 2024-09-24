package paufregi.garminfeed.data.api

import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.api.models.Ticket
import java.net.HttpURLConnection

class GarminConnectOAuth1Test {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: GarminConnectOAuth1
    private val consumer = OAuthConsumer("KEY", "SECRET")

    @Before
    fun setUp() {
        server.start()
        api = GarminConnectOAuth1.client(consumer, server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get OAuth token`() = runTest{
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""{"token":"TOKEN","secret":"SECRET"}""")
        server.enqueue(response)

        val ticket = Ticket("TICKET")
        val res = api.getOauthToken(ticket)

        val request = server.takeRequest()

        assertThat(request.method).isEqualTo("GET")
        assertThat(request.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/preauthorized")
        assertThat(request.requestUrl?.queryParameterValues("token")).isEqualTo("TOKEN")
        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(OAuth1(token="TOKEN", secret="SECRET"))
    }

    @Test
    fun `Get OAuth consumer - failure`() = runTest{
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        server.enqueue(response)

        val ticket = Ticket("TICKET")
        val res = api.getOauthToken(ticket)

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.body()).isNull()
    }
}