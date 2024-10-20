package paufregi.garminfeed.data.api

import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.data.api.utils.AuthInterceptor
import java.io.File

class GarminConnectTest {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: GarminConnect
    private val authInterceptor = mockk<AuthInterceptor>()

    val chain = slot<Interceptor.Chain>()

    private val testFile = File.createTempFile("test", "test")
    private val fitFile = MultipartBody.Part.createFormData("fit", testFile.name, testFile.asRequestBody())

    @Before
    fun setup() {
        server.start()
        api = GarminConnect.client(authInterceptor, server.url("/").toString())
        every {
            authInterceptor.intercept(capture(chain))
        } answers {
            chain.captured.proceed(chain.captured.request())
        }
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
        assertThat(res.isSuccessful).isTrue()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Upload file - failure`() = runTest{
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)

        val res = api.uploadFile(fitFile)

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }
}