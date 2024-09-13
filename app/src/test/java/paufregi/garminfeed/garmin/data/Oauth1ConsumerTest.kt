package paufregi.garminfeed.garmin.data

import org.junit.Test
import com.google.common.truth.Truth.assertThat
import java.util.Date

class Oauth1ConsumerTest {

    @Test
    fun `To string`() {
        val oauth1Consumer = Oauth1Consumer(key = "KEY", secret = "SECRET")

        assertThat(oauth1Consumer.toString()).isEqualTo("OauthConsumer(key: KEY, secret: SECRET)")
    }
}