package paufregi.garminfeed.data.api

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.data.api.models.CSRF
import paufregi.garminfeed.data.api.models.Ticket
import paufregi.garminfeed.htmlForCSRF
import paufregi.garminfeed.htmlForTicket

class GarminSSOTest {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: GarminSSO

    @Before
    fun setup() {
        server.start()
        api = GarminSSO.client(server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `Get CSRF`() = runTest {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(htmlForCSRF)
        server.enqueue(response)

        val res = api.getCSRF()

        val request = server.takeRequest()

        assertThat(request.method).isEqualTo("GET")
        assertThat(request.requestUrl?.toUrl()?.path.toString()).isEqualTo("/sso/signin")
        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(CSRF("TEST_CSRF_VALUE"))
    }

    @Test
    fun `Get CSRF - failure`() = runTest {
        val response = MockResponse()
            .setResponseCode(400)
        server.enqueue(response)

        val res = api.getCSRF()

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.body()).isNull()
    }

    @Test
    fun `Get Ticket`() = runTest {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(htmlForTicket)
        server.enqueue(response)

        val res = api.login(username = "user", password = "pass", csrf = CSRF("csrf"))

        val request = server.takeRequest()

        assertThat(request.method).isEqualTo("POST")
        assertThat(request.requestUrl?.toUrl()?.path).isEqualTo("/sso/signin")
        assertThat(request.body.toString()).isEqualTo("[text=username=user&password=pass&_csrf=csrf&embed=true]")
        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(Ticket("TEST_TICKET_VALUE"))
    }

    @Test
    fun `Get Ticket - failure`() = runTest {
        val response = MockResponse()
            .setResponseCode(400)
        server.enqueue(response)

        val res = api.login(username = "user", password = "pass", csrf = CSRF("csrf"))

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.body()).isNull()
    }
}