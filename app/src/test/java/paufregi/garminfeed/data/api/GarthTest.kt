package paufregi.garminfeed.data.api

import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.MockResponse
import paufregi.garminfeed.data.api.models.OAuthConsumer
import java.net.HttpURLConnection

class GarthTest {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: Garth

    @Before
    fun setUp() {
        server.start()
        api = Garth.client(server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get OAuth consumer`() = runTest{
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""{"consumer_key":"KEY","consumer_secret":"SECRET"}""")
        server.enqueue(response)

        val res = api.getOAuthConsumer()

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