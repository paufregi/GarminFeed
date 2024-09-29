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
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.garminfeed.data.api.GarminConnect
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.models.Credentials
import retrofit2.Response
import java.io.File

class GarminRepositoryTest {

    private lateinit var repo: GarminRepository
    private val garminDao = mockk<GarminDao>()
    private val garminConnect = mockk<GarminConnect>()

    @Before
    fun setUp(){
        repo = GarminRepository(garminDao, garminConnect)
        mockkStatic(Log::class)
        every { Log.i(any(), any()) } returns 0
    }

    @After
    fun tearDown(){
        clearAllMocks()
        clearStaticMockk(Log::class)
    }


    @Test
    fun `Save credentials`() = runTest {
        val creds = Credentials(username = "user", password = "pass")

        coEvery { garminDao.saveCredentials(any()) } returns Unit

        repo.saveCredentials(creds)

        coVerify { garminDao.saveCredentials(creds) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Get credentials`() = runTest{
        val creds = Credentials(username = "user", password = "pass")
        coEvery { garminDao.getCredentials() } returns creds

        val res = repo.getCredentials()

        assertThat(res).isEqualTo(creds)
        coVerify { garminDao.getCredentials() }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Upload file`() = runTest{
        val testFile = File.createTempFile("test", "test")

        coEvery { garminConnect.uploadFile(any()) } returns Response.success(Unit)

        val res = repo.uploadFile(testFile)

        assertThat(res.isSuccessful).isTrue()
        coVerify { garminConnect.uploadFile(any()) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Upload file - failure`() = runTest{
        val testFile = File.createTempFile("test", "test")

        coEvery { garminConnect.uploadFile(any()) } returns Response.error<Unit?>(400, "error".toResponseBody("text/plain; charset=UTF-8".toMediaType()))

        val res = repo.uploadFile(testFile)

        assertThat(res.isSuccessful).isFalse()
        coVerify { garminConnect.uploadFile(any()) }
        confirmVerified(garminDao, garminConnect)
    }
}