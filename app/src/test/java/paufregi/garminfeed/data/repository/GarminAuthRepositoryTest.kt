package paufregi.garminfeed.data.repository

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.clearStaticMockk
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.createOAuth2
import paufregi.garminfeed.data.api.GarminSSO
import paufregi.garminfeed.data.api.Garth
import paufregi.garminfeed.data.api.models.ApiResponse
import paufregi.garminfeed.data.api.models.CSRF
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuth2
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.api.models.Ticket
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.data.datastore.TokenManager
import paufregi.garminfeed.tomorrow
import retrofit2.Response

class GarminAuthRepositoryTest {

    private lateinit var repo: GarminAuthRepository

    private val garminOAuth1Server = MockWebServer()
    private val garminOAuth2Server = MockWebServer()

    private val garminDao = mockk<GarminDao>()
    private val garminSSO = mockk<GarminSSO>()
    private val garth = mockk<Garth>()
    private val tokenManager  = mockk<TokenManager>()

    @Before
    fun setUp(){
        garminOAuth1Server.start()
        garminOAuth2Server.start()
        repo = GarminAuthRepository(
            garminDao,
            garminSSO,
            garth,
            tokenManager,
            garminOAuth1Server.url("/").toString(),
            garminOAuth2Server.url("/").toString()
        )
    }

    @After
    fun tearDown(){
        clearAllMocks()
        clearStaticMockk(Log::class)
        garminOAuth1Server.shutdown()
        garminOAuth2Server.shutdown()
    }

    @Test
    fun `Authenticate - all cached`() = runTest{
        val oAuth2 = createOAuth2(tomorrow)
        val jsonOAuth2 = """{"scope": "${oAuth2.scope}","jti": "${oAuth2.jti}","access_token": "${oAuth2.accessToken}","token_type": "${oAuth2.tokenType}","refresh_token": "${oAuth2.refreshToken}","expires_in": ${oAuth2.expiresIn},"refresh_token_expires_in": ${oAuth2.refreshTokenExpiresIn}}"""
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(oauth1) }
        coEvery { tokenManager.saveOAuth2(any()) } returns Unit

        val response = MockResponse().setResponseCode(200).setBody(jsonOAuth2)
        garminOAuth2Server.enqueue(response)

        val res = repo.authenticate()

        val req = garminOAuth2Server.takeRequest()
        assertThat(res.isSuccessful).isTrue()
        res as ApiResponse.Success<OAuth2>
        assertThat(res.data).isEqualTo(oAuth2)
        assertThat(req.method).isEqualTo("POST")
        assertThat(req.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/exchange/user/2.0")
        assertThat(req.headers["authorization"]).contains("OAuth")
        assertThat(req.headers["authorization"]).contains("""oauth_consumer_key="CONSUMER_KEY"""")
        assertThat(req.headers["authorization"]).contains("""oauth_token="OAUTH_TOKEN"""")
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
        }
        coVerify { tokenManager.saveOAuth2(oAuth2) }
        confirmVerified(garminDao, garminSSO, garth, tokenManager)
    }

    @Test
    fun `Authenticate - no cache`() = runTest{
        val creds = Credentials(username = "user", password = "pass")
        val csrf = CSRF("csrf")
        val ticket = Ticket("ticket")
        val oAuth2 = createOAuth2(tomorrow)
        val jsonOAuth2 = """{"scope": "${oAuth2.scope}","jti": "${oAuth2.jti}","access_token": "${oAuth2.accessToken}","token_type": "${oAuth2.tokenType}","refresh_token": "${oAuth2.refreshToken}","expires_in": ${oAuth2.expiresIn},"refresh_token_expires_in": ${oAuth2.refreshTokenExpiresIn}}"""
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oAuth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(null) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        coEvery { garth.getOAuthConsumer() } returns Response.success(consumer)
        coEvery { tokenManager.saveOAuthConsumer(any()) } returns Unit
        coEvery { tokenManager.saveOAuth1(any()) } returns Unit
        coEvery { tokenManager.saveOAuth2(any()) } returns Unit
        coEvery { garminDao.getCredentials() } returns creds
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.success(ticket)

        val oauth1Response = MockResponse().setResponseCode(200).setBody("oauth_token=${oAuth1.token}&oauth_token_secret=${oAuth1.secret}")
        garminOAuth1Server.enqueue(oauth1Response)

        val oauth2Response = MockResponse().setResponseCode(200).setBody(jsonOAuth2)
        garminOAuth2Server.enqueue(oauth2Response)

        val res = repo.authenticate()

        val oauth1Req = garminOAuth1Server.takeRequest()
        val oauth2Req = garminOAuth2Server.takeRequest()
        assertThat(res.isSuccessful).isTrue()
        res as ApiResponse.Success<OAuth2>
        assertThat(res.data).isEqualTo(oAuth2)
        assertThat(oauth1Req.method).isEqualTo("GET")
        assertThat(oauth1Req.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/preauthorized")
        assertThat(oauth1Req.headers["authorization"]).contains("OAuth")
        assertThat(oauth1Req.headers["authorization"]).contains("""oauth_consumer_key="${consumer.key}"""")
        assertThat(oauth2Req.method).isEqualTo("POST")
        assertThat(oauth2Req.requestUrl?.toUrl()?.path).isEqualTo("/oauth-service/oauth/exchange/user/2.0")
        assertThat(oauth2Req.headers["authorization"]).contains("OAuth")
        assertThat(oauth2Req.headers["authorization"]).contains("""oauth_consumer_key="${consumer.key}"""")
        assertThat(oauth2Req.headers["authorization"]).contains("""oauth_token="${oAuth1.token}"""")
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
        }
        coVerify {
            tokenManager.saveOAuthConsumer(consumer)
            tokenManager.saveOAuth1(oAuth1)
            tokenManager.saveOAuth2(oAuth2)
            garth.getOAuthConsumer()
            garminDao.getCredentials()
            garminSSO.getCSRF()
            garminSSO.login(creds.username, creds.password, csrf)
        }
        confirmVerified(garminDao, garminSSO, garth, tokenManager)
    }

    @Test
    fun `Authenticate - failure get consumer`() = runTest{
        every { tokenManager.getOAuthConsumer() } returns flow { emit(null) }
        coEvery { garth.getOAuthConsumer() } returns Response.error(400, "error".toResponseBody())

        val res = repo.authenticate()

        assertThat(res.isSuccessful).isFalse()
        verify { tokenManager.getOAuthConsumer() }
        coVerify { garth.getOAuthConsumer() }
        confirmVerified(garminDao, garminSSO, garth, tokenManager)
    }

    @Test
    fun `Authenticate - failure signIn - no credentials`() = runTest{

        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        coEvery { garminDao.getCredentials() } returns null

        val res = repo.authenticate()

        assertThat(res.isSuccessful).isFalse()
        res as ApiResponse.Failure<OAuth2>
        assertThat(res.error).isEqualTo("No credentials")
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
        }
        coVerify {
            garminDao.getCredentials()
        }
        confirmVerified(garminDao, garminSSO, garth, tokenManager)
    }

    @Test
    fun `Authenticate - failure signIn - no csrf`() = runTest{
        val creds = Credentials(username = "user", password = "pass")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        coEvery { garminDao.getCredentials() } returns creds
        coEvery { garminSSO.getCSRF() } returns Response.error(400, "error".toResponseBody())

        val res = repo.authenticate()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
        }
        coVerify {
            garminDao.getCredentials()
            garminSSO.getCSRF()
        }
        confirmVerified(garminDao, garminSSO, garth, tokenManager)
    }

    @Test
    fun `Authenticate - failure signIn - failed login`() = runTest{
        val creds = Credentials(username = "user", password = "pass")
        val csrf = CSRF("csrf")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        coEvery { garminDao.getCredentials() } returns creds
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.error(400, "error".toResponseBody())

        val res = repo.authenticate()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
        }
        coVerify {
            garminDao.getCredentials()
            garminSSO.getCSRF()
            garminSSO.login(creds.username, creds.password, csrf)
        }
        confirmVerified(garminDao, garminSSO, garth, tokenManager)
    }

    @Test
    fun `Authenticate - failure oauth1`() = runTest{
        val creds = Credentials(username = "user", password = "pass")
        val csrf = CSRF("csrf")
        val ticket = Ticket("ticket")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(null) }
        coEvery { garminDao.getCredentials() } returns creds
        coEvery { garminSSO.getCSRF() } returns Response.success(csrf)
        coEvery { garminSSO.login(any(), any(), any()) } returns Response.success(ticket)

        val oauth1Response = MockResponse().setResponseCode(400)
        garminOAuth1Server.enqueue(oauth1Response)

        val res = repo.authenticate()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
        }
        coVerify {
            garminDao.getCredentials()
            garminSSO.getCSRF()
            garminSSO.login(creds.username, creds.password, csrf)
        }
        confirmVerified(garminDao, garminSSO, garth, tokenManager)
    }

    @Test
    fun `Authenticate - failure oauth2`() = runTest{
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")

        every { tokenManager.getOAuthConsumer() } returns flow { emit(consumer) }
        every { tokenManager.getOauth1() } returns flow { emit(oauth1) }

        val response = MockResponse().setResponseCode(400)
        garminOAuth2Server.enqueue(response)

        val res = repo.authenticate()

        assertThat(res.isSuccessful).isFalse()
        verify {
            tokenManager.getOAuthConsumer()
            tokenManager.getOauth1()
        }
        confirmVerified(garminDao, garminSSO, garth, tokenManager)
    }
}