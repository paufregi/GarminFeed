package paufregi.connectfeed.presentation.syncWeight

/*
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import paufregi.connectfeed.connectPort
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.createOAuth2
import paufregi.connectfeed.data.api.models.OAuth1
import paufregi.connectfeed.data.api.models.OAuthConsumer
import paufregi.connectfeed.data.repository.GarminRepository
import paufregi.connectfeed.garminSSOPort
import paufregi.connectfeed.garthPort
import paufregi.connectfeed.htmlForCSRF
import paufregi.connectfeed.htmlForTicket
import paufregi.connectfeed.presentation.syncweight.SyncWeightActivity
import paufregi.connectfeed.sslSocketFactory
import paufregi.connectfeed.tomorrow
import java.io.File

@HiltAndroidTest
@ExperimentalMaterial3Api
@RunWith(AndroidJUnit4::class)
class SyncWeightActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var repo: GarminRepository

    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        hiltRule.inject()

        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        connectServer.useHttps(sslSocketFactory, false)
        connectServer.start(connectPort)
        garminSSOServer.useHttps(sslSocketFactory, false)
        garminSSOServer.start(garminSSOPort)
        garthServer.useHttps(sslSocketFactory, false)
        garthServer.start(garthPort)
    }

    @After
    fun tearDown() {
        connectServer.shutdown()
        garminSSOServer.shutdown()
        garthServer.shutdown()
    }

    private val connectServer = MockWebServer()
    private val garminSSOServer = MockWebServer()
    private val garthServer = MockWebServer()


    FIXME: android.os.FileUriExposedException: file:///data/user/0/paufregi.connectfeed/files/test.csv exposed beyond app through Intent.getData()
    @Test
    fun `Sync weight`() = runTest {
        val cred = Credential(username = "user", password = "pass")
        val consumer = OAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET")
        val consumerBody = """{"consumer_key":"${consumer.key}","consumer_secret":"${consumer.secret}"}"""
        val oauth1 = OAuth1("OAUTH_TOKEN", "OAUTH_SECRET")
        val oauth1Body = "oauth_token=${oauth1.token}&oauth_token_secret=${oauth1.secret}"
        val oauth2 = createOAuth2(tomorrow)
        val oauth2Body = """{"scope": "${oauth2.scope}","jti": "${oauth2.jti}","access_token": "${oauth2.accessToken}","token_type": "${oauth2.tokenType}","refresh_token": "${oauth2.refreshToken}","expires_in": ${oauth2.expiresIn},"refresh_token_expires_in": ${oauth2.refreshTokenExpiresIn}}"""

        repo.saveCredential(cred)

        val connectDispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
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
                return when (request.method to request.path) {
                    "GET" to "/oauth_consumer.json" -> MockResponse().setResponseCode(200).setBody(consumerBody)
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        garthServer.dispatcher = garthDispatcher

        val garminSSODispatcher: Dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
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

        val csvText = """
            Time of Measurement,Weight(kg),BMI,Body Fat(%),Fat-Free Mass(kg),Subcutaneous Fat(%),Visceral Fat,Body Water(%),Skeletal Muscle(%),Muscle Mass(kg),Bone Mass(kg),Protein(%),BMR(kcal),Metabolic Age,Optimal weight(kg),Target to optimal weight(kg),Target to optimal fat mass(kg),Target to optimal muscle mass(kg),Body Type,Remarks
            2024-01-01 10:20:30,76.15,23.8,23.2,58.48,20.9,7.0,55.4,49.5,55.59,2.89,17.5,1618,35,,,,,,
        """.trimIndent()

        val testFile = File("${instrumentationContext.filesDir}/test.csv")
        testFile.writeText(csvText)

        val intent = Intent(Intent.ACTION_SEND).apply {
            setDataAndType(Uri.fromFile(testFile), "application/vnd.ms-excel")
        }
        ActivityScenario.launchActivityForResult<SyncWeightActivity>(intent)

        composeTestRule.onNodeWithText("Sync succeeded").assertIsDisplayed()
    }
}
*/