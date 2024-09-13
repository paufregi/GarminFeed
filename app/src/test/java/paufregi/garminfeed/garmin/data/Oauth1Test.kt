package paufregi.garminfeed.garmin.data

import org.junit.Test
import com.google.common.truth.Truth.assertThat
import java.util.Date

class Oauth1Test {

    @Test
    fun `Valid Oauth1`() {
        val oauth1 = Oauth1(token = "TOKEN", secret = "SECRET")

        assertThat(oauth1.isValid()).isTrue()
    }

    @Test
    fun `Invalid - missing token`() {
        val oauth1 = Oauth1(token = "", secret = "SECRET")

        assertThat(oauth1.isValid()).isFalse()
    }

    @Test
    fun `Invalid - missing secret`() {
        val oauth1 = Oauth1(token = "TOKEN", secret = "")

        assertThat(oauth1.isValid()).isFalse()
    }
}