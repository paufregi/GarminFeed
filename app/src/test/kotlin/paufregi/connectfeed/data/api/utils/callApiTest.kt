package paufregi.connectfeed.data.api.utils

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.clearStaticMockk
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import paufregi.connectfeed.core.models.Result
import retrofit2.Response

class CallApiTest {
    @Before
    fun setup(){
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown(){
        clearAllMocks()
        clearStaticMockk(Log::class)
    }

    @Test
    fun `Successful call`() = runTest {
        val result = callApi(
            { Response.success("ok") },
            { res -> res.body() }
        )

        assertThat(result.isSuccessful).isTrue()
        result as Result.Success
        assertThat(result.data).isEqualTo("ok")
    }

    @Test
    fun `Failed call`() = runTest {
        val result = callApi(
            { Response.error<String>(400, "error".toResponseBody()) },
            { res -> res.body() }
        )

        assertThat(result.isSuccessful).isFalse()
    }

    @Test
    fun `Thrown exception`() = runTest {
        val result = callApi<String, String>(
            { throw IllegalArgumentException("ops") },
            { res -> "nope" }
        )

        assertThat(result.isSuccessful).isFalse()
        result as Result.Failure
        assertThat(result.reason).isEqualTo("ops")
    }
}
