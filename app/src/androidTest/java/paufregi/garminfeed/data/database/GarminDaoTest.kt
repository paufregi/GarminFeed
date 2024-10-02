package paufregi.garminfeed.data.database

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import androidx.room.Room
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.google.common.truth.Truth.assertThat
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import paufregi.garminfeed.data.database.models.Credentials
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@HiltAndroidTest
@ExperimentalCoroutinesApi
class GarminDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: GarminDatabase

    @Inject
    lateinit var dao: GarminDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `Save and retrieve credentials`() = runTest {
        val credentials = Credentials(username = "username", password = "password")

        assertThat(dao.getCredentials()).isNull()

        dao.saveCredentials(credentials)

        assertThat(dao.getCredentials()).isEqualTo(credentials)
    }
}