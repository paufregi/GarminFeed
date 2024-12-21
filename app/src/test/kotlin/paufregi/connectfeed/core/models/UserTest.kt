package paufregi.connectfeed.core.models

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class UserTest {

    @Test
    fun `Serialize User`() {
        val user = User("user", "avatar")

        val json = Json.encodeToString(user)
        val res = Json.decodeFromString<User>(json)

        assertThat(res).isEqualTo(user)
    }
}