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
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.data.api.GarminConnect
import paufregi.garminfeed.data.database.GarminDao
import paufregi.garminfeed.data.database.entities.CredentialEntity
import paufregi.garminfeed.data.datastore.TokenManager
import retrofit2.Response
import java.io.File

class GarminRepositoryTest {

    private lateinit var repo: GarminRepository
    private val garminDao = mockk<GarminDao>()
    private val garminConnect = mockk<GarminConnect>()
    private val tokenManager = mockk<TokenManager>()

    @Before
    fun setUp(){
        repo = GarminRepository(garminDao, garminConnect, tokenManager)
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
        val cred = Credential(username = "user", password = "pass")

        coEvery { garminDao.saveCredential(any()) } returns Unit

        repo.saveCredential(cred)

        coVerify { garminDao.saveCredential(CredentialEntity(credential = cred)) }
        confirmVerified(garminDao, garminConnect)
    }

    @Test
    fun `Get credential`() = runTest{
        val cred = Credential(username = "user", password = "pass")
        coEvery { garminDao.getCredential() } returns CredentialEntity(credential = cred)

        val res = repo.getCredential()

        assertThat(res).isEqualTo(cred)
        coVerify { garminDao.getCredential() }
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