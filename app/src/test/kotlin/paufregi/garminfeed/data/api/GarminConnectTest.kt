package paufregi.garminfeed.data.api

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.clearStaticMockk
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
import paufregi.garminfeed.data.api.models.Activity
import paufregi.garminfeed.data.api.models.ActivityType
import paufregi.garminfeed.data.api.models.EventType
import paufregi.garminfeed.data.api.models.Metadata
import paufregi.garminfeed.data.api.models.Summary
import paufregi.garminfeed.data.api.models.UpdateActivityRequest
import paufregi.garminfeed.data.api.utils.AuthInterceptor
import paufregi.garminfeed.latestActivitiesJson
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

    @Test
    fun `Get latest activities`() = runTest{
        val response = MockResponse().setResponseCode(200).setBody(latestActivitiesJson)
        server.enqueue(response)

        val res = api.getLatestActivity(limit = 3)

        val expected = listOf(
            Activity(activityId = 17363361721, activityName = "Commute to home", activityType = ActivityType(typeId = 10, typeKey = "road_biking")),
            Activity(activityId = 17359938034, activityName = "Commute to work", activityType = ActivityType(typeId = 10, typeKey = "road_biking"))
        )

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(expected)
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get latest activities - empty`() = runTest{
        val response = MockResponse().setResponseCode(200).setBody("[]")
        server.enqueue(response)

        val res = api.getLatestActivity(limit = 3)

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(emptyList<Activity>())
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get latest activities - failure`() = runTest{
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)

        val res = api.getLatestActivity(limit = 3)

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Update activity`() = runTest{
        val response = MockResponse().setResponseCode(200)
        server.enqueue(response)
        val updateActivityRequest = UpdateActivityRequest(
            activityId = 1,
            activityName = "newName",
            eventTypeDTO = EventType(typeId = 1, typeKey = "key"),
            metadataDTO = Metadata(courseId = 1),
            summaryDTO = Summary(500),
        )
        val res = api.updateActivity(id = 1, updateActivityRequest = updateActivityRequest)

        assertThat(res.isSuccessful).isTrue()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Update activity - failure`() = runTest{
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)
        val updateActivityRequest = UpdateActivityRequest(
            activityId = 1,
            activityName = "newName",
            eventTypeDTO = EventType(typeId = 1, typeKey = "key"),
            metadataDTO = Metadata(courseId = 1),
            summaryDTO = Summary(500),
        )
        val res = api.updateActivity(id = 1, updateActivityRequest = updateActivityRequest)

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }
}