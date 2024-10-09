package paufregi.garminfeed.data.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.util.Log
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.garminfeed.connectPort
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.createOAuth2
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.GarminDatabase
import paufregi.garminfeed.data.database.entities.CredentialEntity
import paufregi.garminfeed.garminSSOPort
import paufregi.garminfeed.garthPort
import paufregi.garminfeed.htmlForCSRF
import paufregi.garminfeed.htmlForTicket
import paufregi.garminfeed.sslSocketFactory
import paufregi.garminfeed.tomorrow
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
class GarminRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repo: GarminRepository

    @Inject
    lateinit var database: GarminDatabase

    private lateinit var dao: GarminDao

    private val connectServer = MockWebServer()
    private val garminSSOServer = MockWebServer()
    private val garthServer = MockWebServer()

    @Before
    fun setUp() {
        hiltRule.inject()
        connectServer.useHttps(sslSocketFactory, false)
        connectServer.start(connectPort)
        garminSSOServer.useHttps(sslSocketFactory, false)
        garminSSOServer.start(garminSSOPort)
        garthServer.useHttps(sslSocketFactory, false)
        garthServer.start(garthPort)
        dao = database.garminDao()
    }

    @After
    fun tearDown() {
        connectServer.shutdown()
        garminSSOServer.shutdown()
        garthServer.shutdown()
        database.close()
    }

    @Test
    fun `Store credentials`() = runTest {
        val cred = Credential(username = "user", password = "pass")
        repo.saveCredential(cred)
        val res = repo.getCredential()
        assertThat(res).isEqualTo(cred)
    }

    @Test
    fun `Upload file`() = runTest {
        val cred = Credential(username = "user", password = "pass")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val consumerBody = """{"consumer_key":"${consumer.key}","consumer_secret":"${consumer.secret}"}"""
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")
        val oauth1Body = "oauth_token=${oauth1.token}&oauth_token_secret=${oauth1.secret}"
        val oauth2 = createOAuth2(tomorrow)
        val oauth2Body = """{"scope": "${oauth2.scope}","jti": "${oauth2.jti}","access_token": "${oauth2.accessToken}","token_type": "${oauth2.tokenType}","refresh_token": "${oauth2.refreshToken}","expires_in": ${oauth2.expiresIn},"refresh_token_expires_in": ${oauth2.refreshTokenExpiresIn}}"""

        dao.saveCredential(CredentialEntity(credential = cred))

        val connectDispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                Log.i("DEBUG", request.method + " " + request.path)
                val path = request.path ?: return MockResponse().setResponseCode(404)
                if(path.startsWith("/oauth-service/oauth/preauthorized")){
                    return when (request.method) {
                        "GET" -> MockResponse().setResponseCode(200).setBody(oauth1Body)
                        else -> MockResponse().setResponseCode(404)
                    }
                }
                return when (request.method to request.path) {
                    "POST" to "/oauth-service/oauth/exchange/user/2.0" -> MockResponse().setResponseCode(200).setBody(oauth2Body)
                    "POST" to "/upload-service/upload" -> MockResponse().setResponseCode(200)
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        connectServer.dispatcher = connectDispatcher

        val garthDispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                Log.i("DEBUG", request.method + " " + request.path)
                return when (request.method to request.path) {
                    "GET" to "/oauth_consumer.json" -> MockResponse().setResponseCode(200).setBody(consumerBody)
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        garthServer.dispatcher = garthDispatcher

        val garminSSODispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                Log.i("DEBUG", request.method + " " + request.path)
                val path = request.path ?: return MockResponse().setResponseCode(404)
                if (path.startsWith("/sso/signin")) {
                    return when (request.method) {
                        "GET" -> MockResponse().setResponseCode(200).setBody(htmlForCSRF)
                        "POST" -> MockResponse().setResponseCode(200).setBody(htmlForTicket)
                        else -> MockResponse().setResponseCode(404)
                    }
                }
                return MockResponse().setResponseCode(404)
            }
        }
        garminSSOServer.dispatcher = garminSSODispatcher

        val testFile = File.createTempFile("test", "test")
        testFile.deleteOnExit()
        val res = repo.uploadFile(testFile)

        assertThat(res.isSuccessful).isTrue()
    }
}