package paufregi.garminfeed.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.google.common.truth.Truth.assertThat
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import paufregi.garminfeed.garmin.data.Oauth1
import paufregi.garminfeed.garmin.data.Oauth2
import paufregi.garminfeed.models.CachedOauth1
import paufregi.garminfeed.models.CachedOauth2
import paufregi.garminfeed.models.Credentials

class DatabaseTest {

    private lateinit var database: Database

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, Database::class.java).build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun `Save and retrieve credentials`() = runBlocking {
        val credentials = Credentials(username = "username", password = "password")

        assertThat(database.garminDao.getCredentials()).isNull()

        database.garminDao.upsertCredentials(credentials)

        assertThat(database.garminDao.getCredentials()).isEqualTo(credentials)
    }

    @Test
    fun `Save and retrieve flow credentials`() = runBlocking {
        val credentials = Credentials(username = "username", password = "password")

        assertThat(database.garminDao.getCredentials()).isNull()

        database.garminDao.upsertCredentials(credentials)

        val result = database.garminDao.getFlowCredentials().first()

        assertThat(result).isEqualTo(credentials)
    }

    @Test
    fun `Save and retrieve CachedOauth1`() = runBlocking {
        val cachedOauth1 = CachedOauth1(oauth1 = Oauth1(token = "TOKEN", secret = "SECRET"))

        assertThat(database.garminDao.getCachedOauth1()).isNull()

        database.garminDao.upsertCachedOauth1(cachedOauth1)

        assertThat(database.garminDao.getCachedOauth1()).isEqualTo(cachedOauth1)
    }

    @Test
    fun `Clear CachedOauth1`() = runBlocking {
        val cachedOauth1 = CachedOauth1(oauth1 = Oauth1(token = "TOKEN", secret = "SECRET"))

        assertThat(database.garminDao.getCachedOauth1()).isNull()

        database.garminDao.upsertCachedOauth1(cachedOauth1)

        assertThat(database.garminDao.getCachedOauth1()).isEqualTo(cachedOauth1)

        database.garminDao.clearCachedOauth1()

        assertThat(database.garminDao.getCachedOauth1()).isNull()
    }

    @Test
    fun `Save and retrieve CachedOauth2`() = runBlocking {
        val cachedOauth2 = CachedOauth2(oauth2 = Oauth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "ACCESS_TOKEN",
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEM",
            expiresIn = 100,
            refreshTokenExpiresIn = 100
        ))

        assertThat(database.garminDao.getCachedOauth2()).isNull()

        database.garminDao.upsertCachedOauth2(cachedOauth2)

        assertThat(database.garminDao.getCachedOauth2()).isEqualTo(cachedOauth2)
    }

    @Test
    fun `Clear CachedOauth2`() = runBlocking {
        val cachedOauth2 = CachedOauth2(oauth2 = Oauth2(
            scope =  "SCOPE",
            jti = "JTI",
            accessToken = "ACCESS_TOKEN",
            tokenType = "TOKEN_TYPE",
            refreshToken = "REFRESH_TOKEM",
            expiresIn = 100,
            refreshTokenExpiresIn = 100
        ))

        assertThat(database.garminDao.getCachedOauth2()).isNull()

        database.garminDao.upsertCachedOauth2(cachedOauth2)

        assertThat(database.garminDao.getCachedOauth2()).isEqualTo(cachedOauth2)

        database.garminDao.clearCachedOauth2()

        assertThat(database.garminDao.getCachedOauth2()).isNull()
    }
}