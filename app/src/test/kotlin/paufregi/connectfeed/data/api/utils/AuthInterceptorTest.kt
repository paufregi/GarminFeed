package paufregi.connectfeed.data.api.utils

import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.createOAuth2
import paufregi.connectfeed.data.api.GarminConnectOAuth1
import paufregi.connectfeed.data.api.GarminConnectOAuth2
import paufregi.connectfeed.data.api.GarminSSO
import paufregi.connectfeed.data.api.Garth
import paufregi.connectfeed.data.api.models.CSRF
import paufregi.connectfeed.data.api.models.OAuth1
import paufregi.connectfeed.data.api.models.OAuthConsumer
import paufregi.connectfeed.data.api.models.Ticket
import paufregi.connectfeed.data.database.GarminDao
import paufregi.connectfeed.data.database.entities.CredentialEntity
import paufregi.connectfeed.data.datastore.UserDataStore
import paufregi.connectfeed.tomorrow
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class AuthInterceptorTest {

    private lateinit var auth: AuthInterceptor
    private lateinit var api: TestApi

    private val garth = mockk<Garth>()
    private val connectOAuth1 = mockk<GarminConnectOAuth1>()
    private val connectOAuth2 = mockk<GarminConnectOAuth2>()
    private val garminSSO = mockk<GarminSSO>()
    private val userDataStore = mockk<UserDataStore>()

    private val server = MockWebServer()

    interface TestApi {
        @GET("/test")
        suspend fun test(): Response<Unit>
    }

    @Before
    fun setup() {
        server.start()

        auth = AuthInterceptor(
            garth = garth,
            garminSSO = garminSSO,
            userDataStore = userDataStore,
            createConnectOAuth1 = {_ -> connectOAuth1},
            createConnectOAuth2 = {_, _ -> connectOAuth2},
        )
        server.enqueue(MockResponse().setResponseCode(200))
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

        every { userDataStore.getOauth2() } returns flowOf(oauth2)

        api.test()

        verify { userDataStore.getOauth2() }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)

        val req = server.takeRequest()
        assertThat(req.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
    }

    @Test
    fun `Authenticate - all cached`() = runTest {
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")
        val oauth2 = createOAuth2(tomorrow)

        every { userDataStore.getOAuthConsumer() } returns flowOf(consumer)
        every { userDataStore.getOauth1() } returns flowOf(oauth1)
        every { userDataStore.getOauth2() } returns flowOf(null)
        coEvery { userDataStore.saveOAuth2(any()) } returns Unit
        coEvery { connectOAuth2.getOauth2() } returns Response.success(oauth2)

        api.test()

        val apiReq = server.takeRequest()
        assertThat(apiReq.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
        verify {
            userDataStore.getOAuthConsumer()
            userDataStore.getOauth1()
            userDataStore.getOauth2()
        }
        coVerify {
            userDataStore.saveOAuth2(oauth2)
            connectOAuth2.getOauth2()
        }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)
    }

    @Test
    fun `Authenticate - no cached`() = runTest {
        val cred = Credential("user", "pass")
        val csrf = CSRF("csrf")
        val ticket = Ticket("ticket")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")
        val oauth2 = createOAuth2(tomorrow)

        every { userDataStore.getCredential() } returns flowOf(cred)
        every { userDataStore.getOAuthConsumer() } returns flowOf(null)
        every { userDataStore.getOauth1() } returns flowOf(null)
        every { userDataStore.getOauth2() } returns flowOf(null)
        coEvery { userDataStore.saveOAuthConsumer(any()) } returns Unit
        coEvery { userDataStore.saveOAuth1(any()) } returns Unit
        coEvery { userDataStore.saveOAuth2(any()) } returns Unit
        coEvery { garth.getOAuthConsumer() } returns Response.success(consumer)
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.success(ticket)
        coEvery { connectOAuth1.getOauth1(any()) } returns Response.success(oauth1)
        coEvery { connectOAuth2.getOauth2() } returns Response.success(oauth2)

        api.test()

        val apiReq = server.takeRequest()
        assertThat(apiReq.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
        verify {
            userDataStore.getCredential()
            userDataStore.getOAuthConsumer()
            userDataStore.getOauth1()
            userDataStore.getOauth2()
        }
        coVerify {
            userDataStore.saveOAuthConsumer(consumer)
            userDataStore.saveOAuth1(oauth1)
            userDataStore.saveOAuth2(oauth2)
            garth.getOAuthConsumer()
            garminSSO.getCSRF()
            garminSSO.login(cred.username, cred.password, csrf)
            connectOAuth1.getOauth1(ticket)
            connectOAuth2.getOauth2()
        }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)
    }

    @Test
    fun `Authenticate - failure get consumer`() = runTest {
        every { userDataStore.getOAuthConsumer() } returns flowOf(null)
        every { userDataStore.getOauth2() } returns flowOf(null)
        coEvery { garth.getOAuthConsumer() } returns Response.error(400, "error".toResponseBody())

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            userDataStore.getOAuthConsumer()
            userDataStore.getOauth2()
        }
        coVerify {
            garth.getOAuthConsumer()
            garth.getOAuthConsumer()
        }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)
    }


    @Test
    fun `Authenticate - failure no credentials`() = runTest {
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { userDataStore.getCredential() } returns flowOf(null)
        every { userDataStore.getOAuthConsumer() } returns flowOf(consumer)
        every { userDataStore.getOauth1() } returns flowOf(null)
        every { userDataStore.getOauth2() } returns flowOf(null)

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            userDataStore.getOAuthConsumer()
            userDataStore.getOauth1()
            userDataStore.getOauth2()
        }
        coVerify { userDataStore.getCredential() }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)
    }

    @Test
    fun `Authenticate - failure no csrf`() = runTest {
        val cred = Credential(username = "user", password = "pass")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { userDataStore.getCredential() } returns flowOf(cred)
        every { userDataStore.getOAuthConsumer() } returns flowOf(consumer)
        every { userDataStore.getOauth1() } returns flowOf(null)
        every { userDataStore.getOauth2() } returns flowOf(null)
        coEvery { garminSSO.getCSRF() } returns Response.error(400, "error".toResponseBody())

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            userDataStore.getCredential()
            userDataStore.getOAuthConsumer()
            userDataStore.getOauth1()
            userDataStore.getOauth2()
        }
        coVerify {
            garminSSO.getCSRF()
        }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)
    }

    @Test
    fun `Authenticate - failure login`() = runTest {
        val cred = Credential(username = "user", password = "pass")
        val csrf = CSRF("csrf")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { userDataStore.getCredential() } returns flowOf(cred)
        every { userDataStore.getOAuthConsumer() } returns flowOf(consumer)
        every { userDataStore.getOauth1() } returns flowOf(null)
        every { userDataStore.getOauth2() } returns flowOf(null)
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.error(400, "error".toResponseBody())

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            userDataStore.getCredential()
            userDataStore.getOAuthConsumer()
            userDataStore.getOauth1()
            userDataStore.getOauth2()
        }
        coVerify {
            garminSSO.getCSRF()
            garminSSO.login(cred.username, cred.password, csrf)
        }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)
    }

    @Test
    fun `Authenticate - failure OAuth1`() = runTest {
        val cred = Credential(username = "user", password = "pass")
        val csrf = CSRF("csrf")
        val ticket = Ticket("ticket")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { userDataStore.getCredential() } returns flowOf(cred)
        every { userDataStore.getOAuthConsumer() } returns flowOf(consumer)
        every { userDataStore.getOauth1() } returns flowOf(null)
        every { userDataStore.getOauth2() } returns flowOf(null)
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.success(ticket)
        coEvery { connectOAuth1.getOauth1(any()) } returns Response.error(400, "error".toResponseBody())

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            userDataStore.getCredential()
            userDataStore.getOAuthConsumer()
            userDataStore.getOauth1()
            userDataStore.getOauth2()
        }
        coVerify {
            garminSSO.getCSRF()
            garminSSO.login(cred.username, cred.password, csrf)
            connectOAuth1.getOauth1(ticket)
        }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)
    }

    @Test
    fun `Authenticate - failure OAuth2`() = runTest {
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")

        every { userDataStore.getOAuthConsumer() } returns flowOf(consumer)
        every { userDataStore.getOauth1() } returns flowOf(oauth1)
        every { userDataStore.getOauth2() } returns flowOf(null)
        coEvery { connectOAuth2.getOauth2() } returns Response.error(400, "error".toResponseBody())

        val res = api.test()

        assertThat(res.isSuccessful).isFalse()
        verify {
            userDataStore.getOAuthConsumer()
            userDataStore.getOauth1()
            userDataStore.getOauth2()
        }
        coVerify { connectOAuth2.getOauth2() }
        confirmVerified(garth, garminSSO, userDataStore, connectOAuth1, connectOAuth2)
    }
}