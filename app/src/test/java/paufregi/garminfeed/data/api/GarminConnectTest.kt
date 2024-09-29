package paufregi.garminfeed.data.api

import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.mockwebserver.MockResponse
import paufregi.garminfeed.createOAuth2
import paufregi.garminfeed.tomorrow
import paufregi.garminfeed.data.datastore.TokenManager
import paufregi.garminfeed.data.repository.GarminAuthRepository
import java.io.File

class GarminConnectTest {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: GarminConnect
    private val authRepo = mockk<GarminAuthRepository>()
    private val tokenManager = mockk<TokenManager>()

    private val testFile = File.createTempFile("test", "test")
    private val fitFile = MultipartBody.Part.createFormData("fit", testFile.name, testFile.asRequestBody())
    private val oAuth2 = createOAuth2(tomorrow)

    @Before
    fun setUp() {
        server.start()
        api = GarminConnect.client(authRepo, tokenManager, server.url("/").toString())
        every { tokenManager.getOauth2() } returns flow { emit(oAuth2) }
    }

    @After
    fun tearDown() {
        clearAllMocks()
        server.shutdown()
    }

    @Test
    fun `Upload file`() = runTest{
        val response = MockResponse().setResponseCode(200)
        server.enqueue(response)

        val res = api.uploadFile(fitFile)

        val request = server.takeRequest()
        assertThat(request.method).isEqualTo("POST")
        assertThat(request.requestUrl?.toUrl()?.path).isEqualTo("/upload-service/upload")
        assertThat(request.headers["authorization"]).isEqualTo("Bearer ${oAuth2.accessToken}")
        assertThat(res.isSuccessful).isTrue()
        verify { tokenManager.getOauth2() }
        confirmVerified(authRepo, tokenManager)
    }

    @Test
    fun `Upload file - failure`() = runTest{
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)

        val res = api.uploadFile(fitFile)

        assertThat(res.isSuccessful).isFalse()
        verify { tokenManager.getOauth2() }
        confirmVerified(authRepo, tokenManager)
    }
}