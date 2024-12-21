package paufregi.connectfeed.data.datastore

import android.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.User
import paufregi.connectfeed.data.api.models.OAuth1
import paufregi.connectfeed.data.api.models.OAuth2
import paufregi.connectfeed.data.api.models.OAuthConsumer
import javax.inject.Inject

@HiltAndroidTest
@ExperimentalCoroutinesApi
class UserDataStoreTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataStore: UserDataStore

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `Save retrieve and delete User`() = runTest {
        val user1 = User("user_1", "avatar_1")
        val user2 = User("user_2", "avatar_2")
        dataStore.getUser().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveUser(user1)
            assertThat(awaitItem()).isEqualTo(user1)
            dataStore.saveUser(user2)
            assertThat(awaitItem()).isEqualTo(user2)
            dataStore.deleteUser()
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `Save retrieve and delete Credential`() = runTest {
        val credential1 = Credential("user_1", "password_1")
        val credential2 = Credential("user_2", "password_2")
        dataStore.getCredential().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveCredential(credential1)
            assertThat(awaitItem()).isEqualTo(credential1)
            dataStore.saveCredential(credential2)
            assertThat(awaitItem()).isEqualTo(credential2)
            dataStore.deleteCredential()
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `Save retrieve and delete OAuth1`() = runTest {
        val token1 = OAuth1(token = "TOKEN_1", secret = "SECRET_2")
        val token2 = OAuth1(token = "TOKEN_2", secret = "SECRET_2")

        dataStore.getOauth1().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveOAuth1(token1)
            assertThat(awaitItem()).isEqualTo(token1)
            dataStore.saveOAuth1(token2)
            assertThat(awaitItem()).isEqualTo(token2)
            dataStore.deleteOAuth1()
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `Save retrieve and delete OAuth2`() = runTest {
        val token1 = OAuth2(
            scope = "SCOPE_1",
            jti = "JTI_1",
            accessToken = "ACCESS_TOKEN_1",
            tokenType = "TOKEN_TYPE_1",
            refreshToken = "REFRESH_TOKEN_1",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704024000
        )

        val token2 = OAuth2(
            scope = "SCOPE_2",
            jti = "JTI_2",
            accessToken = "ACCESS_TOKEN_2",
            tokenType = "TOKEN_TYPE_2",
            refreshToken = "REFRESH_TOKEN_2",
            expiresIn = 1704020500,
            refreshTokenExpiresIn = 1704025000
        )

        dataStore.getOauth2().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveOAuth2(token1)
            assertThat(awaitItem()).isEqualTo(token1)
            dataStore.saveOAuth2(token2)
            assertThat(awaitItem()).isEqualTo(token2)
            dataStore.deleteOAuth2()
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `Save retrieve and delete OAuthConsumer`() = runTest {
        val consumer1 = OAuthConsumer(key = "KEY_1", secret = "SECRET_1")
        val consumer2 = OAuthConsumer(key = "KEY_2", secret = "SECRET_2")

        dataStore.getOAuthConsumer().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveOAuthConsumer(consumer1)
            assertThat(awaitItem()).isEqualTo(consumer1)
            dataStore.saveOAuthConsumer(consumer2)
            assertThat(awaitItem()).isEqualTo(consumer2)
            dataStore.deleteOAuthConsumer()
            assertThat(awaitItem()).isNull()
        }
    }

}