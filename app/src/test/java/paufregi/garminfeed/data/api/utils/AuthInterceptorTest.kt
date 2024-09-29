package paufregi.garminfeed.data.api.utils

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.data.datastore.TokenManager
import paufregi.garminfeed.data.repository.GarminAuthRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.verify
import io.mockk.verifyAll
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import paufregi.garminfeed.data.api.models.ApiResponse
import paufregi.garminfeed.createOAuth2
import paufregi.garminfeed.tomorrow
import paufregi.garminfeed.yesterday
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.HttpURLConnection

class AuthInterceptorTest {

    private lateinit var auth: AuthInterceptor
    private lateinit var api: TestApi

    private val server = MockWebServer()
    private val authRepo = mockk<GarminAuthRepository>()
    private val tokenManager = mockk<TokenManager>()

    interface TestApi {
        @GET("/test")
        suspend fun test(): retrofit2.Response<Unit>
    }

    @Before
    fun setUp() {
        auth = AuthInterceptor(authRepo, tokenManager)
        server.start()
        server.enqueue(MockResponse().setResponseCode(HttpURLConnection.HTTP_OK))
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(auth).build())
            .build()
            .create(TestApi::class.java)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        server.shutdown()
    }

    @Test
    fun `Use cachedOAuth2`() = runTest {
        val oauth2 = createOAuth2(tomorrow)

        every { tokenManager.getOauth2() } returns flow { emit(oauth2) }

        api.test()

        verify { tokenManager.getOauth2() }
        confirmVerified(authRepo, tokenManager)

        val req = server.takeRequest()
        assertThat(req.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
    }

    @Test
    fun `No cachedOAuth2`() = runTest {
        val oauth2 = createOAuth2(tomorrow)

        every { tokenManager.getOauth2() } returns  flow { emit(null) }
        coEvery { authRepo.authenticate() } returns ApiResponse.Success(oauth2)

        api.test()

        verify { tokenManager.getOauth2() }
        coVerify { authRepo.authenticate() }
        confirmVerified(authRepo, tokenManager)

        val req = server.takeRequest()
        assertThat(req.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
    }

    @Test
    fun `Invalid cachedOAuth2`() = runTest{
        val invalidOauth2 = createOAuth2(yesterday)
        val validOauth2 = createOAuth2(tomorrow)

        every { tokenManager.getOauth2() } returns  flow { emit(invalidOauth2) }
        coEvery { authRepo.authenticate() } returns ApiResponse.Success(validOauth2)

        api.test()

        verify { tokenManager.getOauth2() }
        coVerify { authRepo.authenticate() }
        confirmVerified(authRepo, tokenManager)

        val req = server.takeRequest()
        assertThat(req.headers["Authorization"]).isEqualTo("Bearer ${validOauth2.accessToken}")
    }

    @Test
    fun `Auth failed`() = runTest{
        every { tokenManager.getOauth2() } returns  flow { emit(null) }
        coEvery { authRepo.authenticate() } returns ApiResponse.Failure("error")

        val res = api.test()

        verifyAll { tokenManager.getOauth2() }
        coVerify { authRepo.authenticate() }
        confirmVerified(authRepo, tokenManager)

        assertThat(res.isSuccessful).isFalse()
        assertThat(res.code()).isEqualTo(401)
        assertThat(res.message()).isEqualTo("Auth failed")
    }
}