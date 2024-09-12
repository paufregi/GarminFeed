package paufregi.garminfeed.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.google.common.truth.Truth.assertThat
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
    fun `Save credentials`() = runBlocking {
        val credentials = Credentials(username = "username", password = "password")

        assertThat(database.garminDao.getCredentials()).isNull()

        database.garminDao.upsertCredentials(credentials)

        assertThat(database.garminDao.getCredentials()).isEqualTo(credentials)
    }

    @Test
    fun `Update credentials`() = runBlocking {
        val credentials = Credentials(username = "username", password = "password")
        val newCredentials = Credentials(username = "username2", password = "password2")

        assertThat(database.garminDao.getCredentials()).isNull()

        database.garminDao.upsertCredentials(credentials)
        database.garminDao.upsertCredentials(newCredentials)

        assertThat(database.garminDao.getCredentials()).isEqualTo(newCredentials)
    }
}