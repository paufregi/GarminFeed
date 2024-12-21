package paufregi.connectfeed.core.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResultTest {

    @Test
    fun `Success is successful`() {
        val result = Result.Success("data")
        assertThat(result.isSuccessful).isTrue()
    }

    @Test
    fun `Failure is not successful`() {
        val result = Result.Failure<String>("reason")
        assertThat(result.isSuccessful).isFalse()
    }
}