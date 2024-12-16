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
import paufregi.connectfeed.cred
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
    fun `Save and retrieve Setup`() = runTest {
        dataStore.getSetup().test {
            assertThat(awaitItem()).isFalse()
            dataStore.saveSetup(true)
            assertThat(awaitItem()).isTrue()
            dataStore.saveSetup(false)
            assertThat(awaitItem()).isFalse()
        }
    }

    @Test
    fun `Save retrieve and delete Credential`() = runTest {
        dataStore.getCredential().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveCredential(cred)
            assertThat(awaitItem()).isEqualTo(cred)
            dataStore.deleteCredential()
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `Save retrieve and delete OAuth1`() = runTest {
        val token = OAuth1(token = "TOKEN", secret = "SECRET")

        dataStore.getOauth1().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveOAuth1(token)
            assertThat(awaitItem()).isEqualTo(token)
            dataStore.deleteOAuth1()
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `Save retrieve and delete OAuth2`() = runTest {
        val token = OAuth2(
            scope = "SCOPE",
            jti = "JTI",
            accessToken = "ACCESS_TOKEN",
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEN",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704024000
        )

        dataStore.getOauth2().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveOAuth2(token)
            assertThat(awaitItem()).isEqualTo(token)
            dataStore.deleteOAuth2()
            assertThat(awaitItem()).isNull()
        }
    }

    @Test
    fun `Save retrieve and delete OAuthConsumer`() = runTest {
        val consumer = OAuthConsumer(key = "KEY", secret = "SECRET")

        dataStore.getOAuthConsumer().test {
            assertThat(awaitItem()).isNull()
            dataStore.saveOAuthConsumer(consumer)
            assertThat(awaitItem()).isEqualTo(consumer)
            dataStore.deleteOAuthConsumer()
            assertThat(awaitItem()).isNull()
        }
    }

}