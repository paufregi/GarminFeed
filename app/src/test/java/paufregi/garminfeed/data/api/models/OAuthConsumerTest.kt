package paufregi.garminfeed.data.api.models

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class OAuthConsumerTest {

    @Test
    fun `To string`() {
        val consumer = OAuthConsumer(key = "KEY", secret = "SECRET")

        assertThat(consumer.toString()).isEqualTo("OAuthConsumer(key: KEY, secret: SECRET)")
    }
}