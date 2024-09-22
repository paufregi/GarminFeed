package paufregi.garminfeed.lifecycle

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.garminfeed.TestCoroutineRule
import paufregi.garminfeed.db.Database
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Oauth2
import paufregi.garminfeed.models.CachedOauth1
import paufregi.garminfeed.models.CachedOauth2
import paufregi.garminfeed.models.Credentials

@ExperimentalCoroutinesApi
class ViewModelTest {

    private lateinit var app: Application
    private lateinit var db: Database
    private lateinit var viewModel: ViewModel

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(app, Database::class.java).build()
        viewModel = ViewModel(app, db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `Initial empty state`() = runTest {
        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(State())
        }
    }

    @Test
    fun `Initial state with credentials`() = runTest {
        val cred = Credentials(username = "user", password = "pass")
        db.garminDao.upsertCredentials(cred)

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(State())
            assertThat(awaitItem()).isEqualTo(State(credentials = cred))
        }
    }

    @Test
    fun `Save credentials event`() = runTest {
        val cred = Credentials(username = "user", password = "pass")

        viewModel.onEvent(Event.SaveCredentials(cred))
        advanceUntilIdle()

        assertThat(db.garminDao.getCredentials()).isEqualTo(cred)
        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(State())
            assertThat(awaitItem()).isEqualTo(State(credentials = cred))
        }
    }

    @Test
    fun `Cache Oauth 1 event`() = runTest {
        val oauth1 = Oauth1(token = "TOKEN", secret = "SECRET")

        viewModel.onEvent(Event.CacheOauth1(oauth1))
        advanceUntilIdle()

        assertThat(db.garminDao.getCachedOauth1()).isEqualTo(CachedOauth1(oauth1 = oauth1))
    }

    @Test
    fun `Cache Oauth 2 event`() = runTest {
        val oauth2 = Oauth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "ACCESS",
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEN",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704024000
        )

        viewModel.onEvent(Event.CacheOauth2(oauth2))
        advanceUntilIdle()

        assertThat(db.garminDao.getCachedOauth2()).isEqualTo(CachedOauth2(oauth2 = oauth2))
    }

    @Test
    fun `Clear cache event`() = runTest {
        val oauth1 = Oauth1(token = "TOKEN", secret = "SECRET")
        val oauth2 = Oauth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "",
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEN",
            expiresIn = 1704020400,
            refreshTokenExpiresIn = 1704024000
        )

        db.garminDao.upsertCachedOauth1(CachedOauth1(oauth1 = oauth1))
        db.garminDao.upsertCachedOauth2(CachedOauth2(oauth2 = oauth2))

        viewModel.onEvent(Event.ClearCache)
        advanceUntilIdle()

        assertThat(db.garminDao.getCachedOauth1()).isNull()
        assertThat(db.garminDao.getCachedOauth2()).isNull()
        viewModel.state.test {
            assertThat(awaitItem().clearCacheToast).isFalse()
            assertThat(awaitItem().clearCacheToast).isTrue()
        }
    }
}