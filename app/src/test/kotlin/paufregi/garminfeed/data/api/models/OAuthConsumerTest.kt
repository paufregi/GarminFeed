package paufregi.garminfeed.data.api.models

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OAuthConsumerTest {

    @Test
    fun `To string`() {
        val consumer = OAuthConsumer(key = "KEY", secret = "SECRET")

        assertThat(consumer.toString()).isEqualTo("OAuthConsumer(key: KEY, secret: SECRET)")
    }
}