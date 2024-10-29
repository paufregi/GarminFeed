package paufregi.garminfeed.data.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.util.Log
import app.cash.turbine.test
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
import paufregi.garminfeed.core.models.Activity as CoreActivity
import paufregi.garminfeed.core.models.ActivityType as CoreActivityType
import paufregi.garminfeed.core.models.Course
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.core.models.EventType
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.core.models.Result
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
import paufregi.garminfeed.latestActivitiesJson
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


    private  val cred = Credential(username = "user", password = "pass")
    private  val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
    private  val consumerBody = """{"consumer_key":"${consumer.key}","consumer_secret":"${consumer.secret}"}"""
    private  val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")
    private  val oauth1Body = "oauth_token=${oauth1.token}&oauth_token_secret=${oauth1.secret}"
    private  val oauth2 = createOAuth2(tomorrow)
    private  val oauth2Body = """{"scope": "${oauth2.scope}","jti": "${oauth2.jti}","access_token": "${oauth2.accessToken}","token_type": "${oauth2.tokenType}","refresh_token": "${oauth2.refreshToken}","expires_in": ${oauth2.expiresIn},"refresh_token_expires_in": ${oauth2.refreshTokenExpiresIn}}"""

    private val connectDispatcher: Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: ""
            print("Path: $path - Method: ${request.method}")
            Log.i("TEST", "Path: $path - Method: ${request.method}")
            return when {
                path.startsWith("/oauth-service/oauth/preauthorized") && request.method == "GET" ->
                    MockResponse().setResponseCode(200).setBody(oauth1Body)
                path == "/oauth-service/oauth/exchange/user/2.0" && request.method == "POST" ->
                    MockResponse().setResponseCode(200).setBody(oauth2Body)
                path == "/upload-service/upload" && request.method == "POST" ->
                    MockResponse().setResponseCode(200)
                (path.startsWith("/activitylist-service/activities/search/activities") && request.method == "GET") ->
                    MockResponse().setResponseCode(200).setBody(latestActivitiesJson)
                (path.startsWith("/activity-service/activity") && request.method == "PUT") ->
                    MockResponse().setResponseCode(200)
                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    private val garthDispatcher: Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: ""
            return when {
                path == "/oauth_consumer.json" && request.method == "GET" ->
                    MockResponse().setResponseCode(200).setBody(consumerBody)
                else -> MockResponse().setResponseCode(404)
            }
        }
    }

    private val garminSSODispatcher: Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.path ?: ""
            return when {
                path.startsWith("/sso/signin") && request.method == "GET" ->
                    MockResponse().setResponseCode(200).setBody(htmlForCSRF)
                path.startsWith("/sso/signin") && request.method == "POST" ->
                    MockResponse().setResponseCode(200).setBody(htmlForTicket)
                else -> MockResponse().setResponseCode(404)
            }
        }
    }



    @Before
    fun setup() {
        hiltRule.inject()
        connectServer.useHttps(sslSocketFactory, false)
        connectServer.start(connectPort)
        garminSSOServer.useHttps(sslSocketFactory, false)
        garminSSOServer.start(garminSSOPort)
        garthServer.useHttps(sslSocketFactory, false)
        garthServer.start(garthPort)

        connectServer.dispatcher = connectDispatcher
        garthServer.dispatcher = garthDispatcher
        garminSSOServer.dispatcher = garminSSODispatcher

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

        res.test{
            assertThat(awaitItem()).isEqualTo(cred)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Upload file`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))

        val testFile = File.createTempFile("test", "test")
        testFile.deleteOnExit()
        val res = repo.uploadFile(testFile)

        assertThat(res.isSuccessful).isTrue()
    }

    @Test
    fun `Get latest activities`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))

        val expected = listOf(
            CoreActivity(id = 17363361721, name = "Commute to home", type = CoreActivityType.Cycling),
            CoreActivity(id = 17359938034, name = "Commute to work", type = CoreActivityType.Cycling)
        )

        val res = repo.getLatestActivities(5)

        assertThat(res.isSuccessful).isTrue()
        res as Result.Success
        assertThat(res.data).isEqualTo(expected)
    }

    @Test
    fun `Update activity`() = runTest {
        dao.saveCredential(CredentialEntity(credential = cred))

        val activity = CoreActivity(id = 1, name = "activity", type = CoreActivityType.Cycling)
        val profile = Profile(activityName = "newName", eventType = EventType.transportation, activityType = CoreActivityType.Cycling, course = Course.work, water = 1)

        val res = repo.updateActivity(activity, profile)

        assertThat(res.isSuccessful).isTrue()
    }
}