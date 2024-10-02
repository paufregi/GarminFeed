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
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.verify
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import paufregi.garminfeed.createOAuth2
import paufregi.garminfeed.data.api.GarminSSO
import paufregi.garminfeed.data.api.Garth
import paufregi.garminfeed.data.api.models.CSRF
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.api.models.Ticket
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.tomorrow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.HttpURLConnection

class AuthInterceptorTest {

    private lateinit var auth: AuthInterceptor
    private lateinit var api: TestApi

    private val garminDao = mockk<GarminDao>()
    private val garth = mockk<Garth>()
    private val garminSSO = mockk<GarminSSO>()
    private val tokenManager = mockk<TokenManager>()

    private val server = MockWebServer()
    private val garminOAuth1Server = MockWebServer()
    private val garminOAuth2Server = MockWebServer()

    interface TestApi {
        @GET("/test")
        suspend fun test(): retrofit2.Response<Unit>
    }

    @Before
    fun setUp() {
        server.start()
        garminOAuth1Server.start()
        garminOAuth2Server.start()
        auth = AuthInterceptor(
            garminDao,
            garth,
            garminSSO,
            tokenManager,
            garminOAuth1Server.url("/").toString(),
            garminOAuth2Server.url("/").toString()
        )
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
        garminOAuth1Server.shutdown()
        garminOAuth2Server.shutdown()
    }

    @Test
    fun `Use cachedOAuth2`() = runTest {
        val oauth2 = createOAuth2(tomorrow)

        every { tokenManager.getOauth2() } returns flow { emit(oauth2) }

        api.test()

        verify { tokenManager.getOauth2() }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)

        val req = server.takeRequest()
        assertThat(req.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
    }

    @Test
    fun `Authenticate - all cached`() = runTest {
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")
        val oauth2 = createOAuth2(tomorrow)
        val jsonOAuth2 = """{"scope": "${oauth2.scope}","jti": "${oauth2.jti}","access_token": "${oauth2.accessToken}","token_type": "${oauth2.tokenType}","refresh_token": "${oauth2.refreshToken}","expires_in": ${oauth2.expiresIn},"refresh_token_expires_in": ${oauth2.refreshTokenExpiresIn}}"""

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(oauth1) }
        every { tokenManager.getOauth2() } returns flow { emit(null) }
        coEvery { tokenManager.saveOAuth2(any()) } returns Unit

        val response = MockResponse().setResponseCode(200).setBody(jsonOAuth2)
        garminOAuth2Server.enqueue(response)

        api.test()

        val apiReq = server.takeRequest()
        val oauthReq = garminOAuth2Server.takeRequest()
        assertThat(apiReq.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
        assertThat(oauthReq.method).isEqualTo("POST")
        assertThat(oauthReq.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/exchange/user/2.0")
        assertThat(oauthReq.headers["authorization"]).contains("OAuth")
        assertThat(oauthReq.headers["authorization"]).contains("""oauth_consumer_key="CONSUMER_KEY"""")
        assertThat(oauthReq.headers["authorization"]).contains("""oauth_token="OAUTH_TOKEN"""")
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
            tokenManager.getOauth2()
        }
        coVerify { tokenManager.saveOAuth2(oauth2) }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)
    }

    @Test
    fun `Authenticate - no cached`() = runTest {
        val creds = Credentials(username = "user", password = "pass")
        val csrf = CSRF("csrf")
        val ticket = Ticket("ticket")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")
        val oauth2 = createOAuth2(tomorrow)
        val oauth2Body = """{"scope": "${oauth2.scope}","jti": "${oauth2.jti}","access_token": "${oauth2.accessToken}","token_type": "${oauth2.tokenType}","refresh_token": "${oauth2.refreshToken}","expires_in": ${oauth2.expiresIn},"refresh_token_expires_in": ${oauth2.refreshTokenExpiresIn}}"""

        every { tokenManager.getOAuthConsumer() } returns flow { emit(null) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        every { tokenManager.getOauth2() } returns flow { emit(null) }
        coEvery { tokenManager.saveOAuthConsumer(any()) } returns Unit
        coEvery { tokenManager.saveOAuth1(any()) } returns Unit
        coEvery { tokenManager.saveOAuth2(any()) } returns Unit
        coEvery { garth.getOAuthConsumer() } returns Response.success(consumer)
        coEvery { garminDao.getCredentials() } returns creds
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.success(ticket)

        val oauth1Response = MockResponse().setResponseCode(200).setBody("oauth_token=${oauth1.token}&oauth_token_secret=${oauth1.secret}")
        garminOAuth1Server.enqueue(oauth1Response)

        val oauth2Response = MockResponse().setResponseCode(200).setBody(oauth2Body)
        garminOAuth2Server.enqueue(oauth2Response)

        api.test()

        val apiReq = server.takeRequest()
        val oauth1Req = garminOAuth1Server.takeRequest()
        val oauth2Req = garminOAuth2Server.takeRequest()
        assertThat(apiReq.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
        assertThat(oauth1Req.method).isEqualTo("GET")
        assertThat(oauth1Req.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/preauthorized")
        assertThat(oauth1Req.headers["authorization"]).contains("OAuth")
        assertThat(oauth1Req.headers["authorization"]).contains("""oauth_consumer_key="${consumer.key}"""")
        assertThat(oauth2Req.method).isEqualTo("POST")
        assertThat(oauth2Req.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/exchange/user/2.0")
        assertThat(oauth2Req.headers["authorization"]).contains("OAuth")
        assertThat(oauth2Req.headers["authorization"]).contains("""oauth_consumer_key="${consumer.key}"""")
        assertThat(oauth2Req.headers["authorization"]).contains("""oauth_token="${oauth1.token}"""")
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
            tokenManager.getOauth2()
        }
        coVerify {
            tokenManager.saveOAuthConsumer(consumer)
            tokenManager.saveOAuth1(oauth1)
            tokenManager.saveOAuth2(oauth2)
            garth.getOAuthConsumer()
            garminDao.getCredentials()
            garminSSO.getCSRF()
            garminSSO.login(creds.username, creds.password, csrf)
        }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)
    }

    @Test
    fun `Authenticate - failure get consumer`() = runTest {
        every { tokenManager.getOAuthConsumer() } returns flow { emit(null) }
        every { tokenManager.getOauth2() } returns flow { emit(null) }
        coEvery { garth.getOAuthConsumer() } returns Response.error(400, "error".toResponseBody())

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth2()
        }
        coVerify {
            garth.getOAuthConsumer()
            garth.getOAuthConsumer()
        }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)
    }


    @Test
    fun `Authenticate - failure no credentials`() = runTest {
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        every { tokenManager.getOauth2() } returns flow { emit(null) }
        coEvery { garminDao.getCredentials() } returns null

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
            tokenManager.getOauth2()
        }
        coVerify { garminDao.getCredentials() }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)
    }

    @Test
    fun `Authenticate - failure no csrf`() = runTest {
        val creds = Credentials(username = "user", password = "pass")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        every { tokenManager.getOauth2() } returns flow { emit(null) }
        coEvery { garminDao.getCredentials() } returns creds
        coEvery { garminSSO.getCSRF() } returns Response.error(400, "error".toResponseBody())

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
            tokenManager.getOauth2()
        }
        coVerify {
            garminDao.getCredentials()
            garminSSO.getCSRF()
        }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)
    }

    @Test
    fun `Authenticate - failure login`() = runTest {
        val creds = Credentials(username = "user", password = "pass")
        val csrf = CSRF("csrf")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        every { tokenManager.getOauth2() } returns flow { emit(null) }
        coEvery { garminDao.getCredentials() } returns creds
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.error(400, "error".toResponseBody())

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
            tokenManager.getOauth2()
        }
        coVerify {
            garminDao.getCredentials()
            garminSSO.getCSRF()
            garminSSO.login(creds.username, creds.password, csrf)
        }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)
    }

    @Test
    fun `Authenticate - failure OAuth1`() = runTest {
        val creds = Credentials(username = "user", password = "pass")
        val csrf = CSRF("csrf")
        val ticket = Ticket("ticket")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        every { tokenManager.getOauth2() } returns flow { emit(null) }
        coEvery { garminDao.getCredentials() } returns creds
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.success(ticket)

        val oauth1Response = MockResponse().setResponseCode(400)
        garminOAuth1Server.enqueue(oauth1Response)

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
            tokenManager.getOauth2()
        }
        coVerify {
            garminDao.getCredentials()
            garminSSO.getCSRF()
            garminSSO.login(creds.username, creds.password, csrf)
        }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)
    }

    @Test
    fun `Authenticate - failure OAuth2`() = runTest {
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(oauth1) }
        every { tokenManager.getOauth2() } returns flow { emit(null) }

        val response = MockResponse().setResponseCode(400)
        garminOAuth2Server.enqueue(response)

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
            tokenManager.getOauth2()
        }
        confirmVerified(garminDao, garth, garminSSO, tokenManager)
    }
}