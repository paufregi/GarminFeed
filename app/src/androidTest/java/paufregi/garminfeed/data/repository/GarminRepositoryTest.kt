package paufregi.garminfeed.data.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.LargeTest
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.garminfeed.connectOAuth1Port
import paufregi.garminfeed.connectOAuth1Server
import paufregi.garminfeed.connectOAuth2Port
import paufregi.garminfeed.connectOAuth2Server
import paufregi.garminfeed.connectPort
import paufregi.garminfeed.connectServer
import paufregi.garminfeed.createOAuth2
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuthConsumer
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.GarminDatabase
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.garminSSOPort
import paufregi.garminfeed.garminSSOServer
import paufregi.garminfeed.garthPort
import paufregi.garminfeed.garthServer
import paufregi.garminfeed.htmlForCSRF
import paufregi.garminfeed.htmlForTicket
import paufregi.garminfeed.startServer
import paufregi.garminfeed.tomorrow
import java.io.File
import javax.inject.Inject


@LargeTest
@HiltAndroidTest
@ExperimentalCoroutinesApi
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

    @Before
    fun setUp() {
        hiltRule.inject()
        startServer(connectServer, connectPort)
        startServer(connectOAuth1Server, connectOAuth1Port)
        startServer(connectOAuth2Server, connectOAuth2Port)
        startServer(garminSSOServer, garminSSOPort)
        startServer(garthServer, garthPort)
        dao = database.garminDao()
    }

    @After
    fun tearDown() {
        connectServer.shutdown()
        connectOAuth1Server.shutdown()
        connectOAuth2Server.shutdown()
        garminSSOServer.shutdown()
        garthServer.shutdown()
        database.close()
    }

    @Test
    fun `Upload file`() = runTest {
        val creds = Credentials(username = "user", password = "pass")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val consumerBody = """{"consumer_key":"${consumer.key}","consumer_secret":"${consumer.secret}"}"""
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")
        val oauth1Body = "oauth_token=${oauth1.token}&oauth_token_secret=${oauth1.secret}"
        val oauth2 = createOAuth2(tomorrow)
        val oauth2Body = """{"scope": "${oauth2.scope}","jti": "${oauth2.jti}","access_token": "${oauth2.accessToken}","token_type": "${oauth2.tokenType}","refresh_token": "${oauth2.refreshToken}","expires_in": ${oauth2.expiresIn},"refresh_token_expires_in": ${oauth2.refreshTokenExpiresIn}}"""

        dao.saveCredentials(creds)

        val garthResponse = MockResponse().setResponseCode(200).setBody(consumerBody)
        garthServer.enqueue(garthResponse)

        val dispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.method) {
                    "GET" -> MockResponse().setResponseCode(200).setBody(htmlForCSRF)
                    "POST" -> MockResponse().setResponseCode(200).setBody(htmlForTicket)
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        garminSSOServer.dispatcher = dispatcher

        val oauth1Response = MockResponse().setResponseCode(200).setBody(oauth1Body)
        connectOAuth1Server.enqueue(oauth1Response)

        val oauth2Response = MockResponse().setResponseCode(200).setBody(oauth2Body)
        connectOAuth2Server.enqueue(oauth2Response)

        val uploadResponse = MockResponse().setResponseCode(200)
        connectServer.enqueue(uploadResponse)

        val testFile = File.createTempFile("test", "test")
        testFile.deleteOnExit()
        val res = repo.uploadFile(testFile)

        val connectReq = connectServer.takeRequest()

        assertThat(connectReq.headers["Authorization"]).isEqualTo("Bearer ${oauth2.accessToken}")
        assertThat(res.isSuccessful).isTrue()
    }
}