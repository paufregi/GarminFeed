package paufregi.connectfeed.data.keystore

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CryptoManagerTest {

    @Test
    fun `Encrypt and decrypt data`() {
        val cryptoManager = CryptoManager()
        val data = "ConnectFeed"
        val encrypted = cryptoManager.encrypt(data)
        val decrypted = cryptoManager.decrypt(encrypted)
        assertThat(decrypted).isEqualTo(data)
    }
}