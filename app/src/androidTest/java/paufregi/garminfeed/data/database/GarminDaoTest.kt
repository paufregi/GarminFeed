package paufregi.garminfeed.data.database

import android.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.After
import org.junit.Before
import org.junit.Test

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import paufregi.garminfeed.data.database.models.Credentials
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@ExperimentalCoroutinesApi
class GarminDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("test_db")
    lateinit var db: GarminDatabase
    private lateinit var dao: GarminDao

    @Before
    fun setup() {
        hiltRule.inject()
        dao = db.garminDao()
    }

    @After
    fun closeDb() {
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