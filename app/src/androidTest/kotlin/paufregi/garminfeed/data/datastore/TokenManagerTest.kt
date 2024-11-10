package paufregi.garminfeed.data.datastore

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.garminfeed.data.api.models.OAuth1
import paufregi.garminfeed.data.api.models.OAuth2
import paufregi.garminfeed.data.api.models.OAuthConsumer
import javax.inject.Inject

@HiltAndroidTest
@ExperimentalCoroutinesApi
class TokenManagerTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataStore: TokenManager

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `Save retrieve and delete OAuth1`() = runTest {
        assertThat(dataStore.getOauth1().first()).isNull()

        val token = OAuth1(token = "TOKEN", secret = "SECRET")

        dataStore.saveOAuth1(token)

        assertThat(dataStore.getOauth1().first()).isEqualTo(token)

        dataStore.deleteOAuth1()

        assertThat(dataStore.getOauth1().first()).isNull()
    }

    @Test
    fun `Save retrieve and delete OAuth2`() = runTest {
        assertThat(dataStore.getOauth2().first()).isNull()

        val token = OAuth2(
            scope = "SCOPE",
            jti = "JTI",
            accessToken = "ACCESS_TOKEN",
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEN",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704024000
        )

        dataStore.saveOAuth2(token)

        assertThat(dataStore.getOauth2().first()).isEqualTo(token)

        dataStore.deleteOAuth2()

        assertThat(dataStore.getOauth2().first()).isNull()
    }

    @Test
    fun `Save retrieve and delete OAuthConsumer`() = runTest {
        assertThat(dataStore.getOAuthConsumer().first()).isNull()

        val consumer = OAuthConsumer(key = "KEY", secret = "SECRET")

        dataStore.saveOAuthConsumer(consumer)

        assertThat(dataStore.getOAuthConsumer().first()).isEqualTo(consumer)

        dataStore.deleteOAuthConsumer()

        assertThat(dataStore.getOAuthConsumer().first()).isNull()
    }

}