package paufregi.garminfeed.data.api

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.data.api.models.OAuthConsumer
import java.net.HttpURLConnection

class GarthTest {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: Garth

    @Before
    fun setUp() {
        server.start()
        api = Garth.client(server.url("/").toString())
        print(server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get OAuth consumer`() = runTest{
        val response = MockResponse()
            .setResponseCode(200)
            .setBody("""{"consumer_key":"KEY","consumer_secret":"SECRET"}""")
        server.enqueue(response)

        val res = api.getOAuthConsumer()

        val request = server.takeRequest()

        assertThat(request.method).isEqualTo("GET")
        assertThat(request.requestUrl?.toUrl()?.path).isEqualTo("/oauth_consumer.json")
        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(OAuthConsumer(key="KEY", secret="SECRET"))
    }

    @Test
    fun `Get OAuth consumer - failure`() = runTest{
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST)
        server.enqueue(response)

        val res = api.getOAuthConsumer()

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.body()).isNull()
    }
}