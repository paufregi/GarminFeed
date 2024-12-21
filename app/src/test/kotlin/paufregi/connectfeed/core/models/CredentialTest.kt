package paufregi.connectfeed.core.models

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class CredentialTest {

    @Test
    fun `Serialize Credential`() {
        val credential = Credential("username", "password")

        val json = Json.encodeToString(credential)
        val res = Json.decodeFromString<Credential>(json)

        assertThat(res).isEqualTo(credential)
    }
}