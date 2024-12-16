package paufregi.connectfeed.data.api

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
import paufregi.connectfeed.coursesJson
import paufregi.connectfeed.data.api.models.Activity
import paufregi.connectfeed.data.api.models.ActivityType
import paufregi.connectfeed.data.api.models.Course
import paufregi.connectfeed.data.api.models.EventType
import paufregi.connectfeed.data.api.models.Metadata
import paufregi.connectfeed.data.api.models.Summary
import paufregi.connectfeed.data.api.models.UpdateActivity
import paufregi.connectfeed.data.api.models.UserProfile
import paufregi.connectfeed.data.api.utils.AuthInterceptor
import paufregi.connectfeed.eventTypesJson
import paufregi.connectfeed.latestActivitiesJson
import paufregi.connectfeed.userProfileJson
import java.io.File

class GarminConnectTest {

    private var server: MockWebServer = MockWebServer()
    private lateinit var api: GarminConnect
    private val authInterceptor = mockk<AuthInterceptor>()

    val chain = slot<Interceptor.Chain>()

    private val testFile = File.createTempFile("test", "test")
    private val fitFile =
        MultipartBody.Part.createFormData("fit", testFile.name, testFile.asRequestBody())

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
    fun `Get user profile`() = runTest {
        val response = MockResponse().setResponseCode(200).setBody(userProfileJson)
        server.enqueue(response)

        val res = api.getUserProfile()

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(UserProfile("GarminUser"))
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get user profile - failure`() = runTest {
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)

        val res = api.getUserProfile()

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Upload file`() = runTest {
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
    fun `Upload file - failure`() = runTest {
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)

        val res = api.uploadFile(fitFile)

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get latest activities`() = runTest {
        val response = MockResponse().setResponseCode(200).setBody(latestActivitiesJson)
        server.enqueue(response)

        val res = api.getLatestActivity(limit = 3)

        val expected = listOf(
            Activity(
                id = 1,
                name = "Activity 1",
                type = ActivityType(id = 10, key = "road_biking")
            ),
            Activity(id = 2, name = "Activity 2", type = ActivityType(id = 10, key = "road_biking"))
        )

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(expected)
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get latest activities - empty`() = runTest {
        val response = MockResponse().setResponseCode(200).setBody("[]")
        server.enqueue(response)

        val res = api.getLatestActivity(limit = 3)

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(emptyList<Activity>())
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get latest activities - failure`() = runTest {
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)

        val res = api.getLatestActivity(limit = 3)

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Update activity`() = runTest {
        val response = MockResponse().setResponseCode(200)
        server.enqueue(response)
        val updateActivity = UpdateActivity(
            id = 1,
            name = "newName",
            eventType = EventType(id = 1, key = "key"),
            metadata = Metadata(courseId = 1),
            summary = Summary(500, null, null),
        )
        val res = api.updateActivity(id = 1, updateActivity = updateActivity)

        assertThat(res.isSuccessful).isTrue()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Update activity - failure`() = runTest {
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)
        val updateActivity = UpdateActivity(
            id = 1,
            name = "newName",
            eventType = EventType(id = 1, key = "key"),
            metadata = Metadata(courseId = 1),
            summary = Summary(500, null, null),
        )
        val res = api.updateActivity(id = 1, updateActivity = updateActivity)

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get course`() = runTest {
        val response = MockResponse().setResponseCode(200).setBody(coursesJson)
        server.enqueue(response)

        val res = api.getCourses()

        val expected = listOf(
            Course(id = 1, name = "Course 1", type = ActivityType(id = 1, key = "running")),
            Course(id = 2, name = "Course 2", type = ActivityType(id = 10, key = "road_biking"))
        )

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(expected)
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get course - empty`() = runTest {
        val response = MockResponse().setResponseCode(200).setBody("[]")
        server.enqueue(response)

        val res = api.getCourses()

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEmpty()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get course - failure`() = runTest {
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)

        val res = api.getCourses()

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get event types`() = runTest {
        val response = MockResponse().setResponseCode(200).setBody(eventTypesJson)
        server.enqueue(response)

        val res = api.getEventTypes()

        val expected = listOf(
            EventType(id = 1, key = "race"),
            EventType(id = 2, key = "training"),
        )

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEqualTo(expected)
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get event types - empty`() = runTest {
        val response = MockResponse().setResponseCode(200).setBody("[]")
        server.enqueue(response)

        val res = api.getEventTypes()

        assertThat(res.isSuccessful).isTrue()
        assertThat(res.body()).isEmpty()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }

    @Test
    fun `Get event types - failure`() = runTest {
        val response = MockResponse().setResponseCode(400)
        server.enqueue(response)

        val res = api.getEventTypes()

        assertThat(res.isSuccessful).isFalse()
        verify { authInterceptor.intercept(any()) }
        confirmVerified(authInterceptor)
    }
}