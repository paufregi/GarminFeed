package paufregi.garminfeed.data.api.models

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class OAuth1Test {

    @Test
    fun `Valid OAuth1`() {
        val oauth1 = OAuth1(token = "TOKEN", secret = "SECRET")

        assertThat(oauth1.isValid()).isTrue()
    }

    @Test
    fun `Invalid - missing token`() {
        val oauth1 = OAuth1(token = "", secret = "SECRET")

        assertThat(oauth1.isValid()).isFalse()
    }

    @Test
    fun `Invalid - missing secret`() {
        val oauth1 = OAuth1(token = "TOKEN", secret = "")

        assertThat(oauth1.isValid()).isFalse()
    }

    @Test
    fun `To string`() {
        val oauth1 = OAuth1(token = "TOKEN", secret = "SECRET")

        assertThat(oauth1.toString()).isEqualTo("Oauth1(token: TOKEN, secret: SECRET)")
    }
}