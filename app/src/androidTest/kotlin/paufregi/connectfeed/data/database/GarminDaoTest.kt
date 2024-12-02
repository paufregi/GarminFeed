package paufregi.connectfeed.data.database

import android.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.cred
import paufregi.connectfeed.data.database.entities.CredentialEntity
import paufregi.connectfeed.data.database.entities.ProfileEntity
import javax.inject.Inject

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
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `Save and retrieve credential`() = runTest {
        val credEntity = CredentialEntity(credential = cred)

        val credFlow = dao.getCredential()

        credFlow.test {
            assertThat(awaitItem()).isNull()
            dao.saveCredential(credEntity)
            assertThat(awaitItem()).isEqualTo(credEntity)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Save and retrieve profile`() = runTest {
        val profile1 = ProfileEntity(
            id = 1,
            name = "profile1",
            eventType = EventType(id = 1, name = "event1"),
            activityType = ActivityType.Running,
            water = 100,
        )
        val profile2 = ProfileEntity(
            id = 2,
            name = "profile1",
            eventType =  EventType(id = 2, name = "event2"),
            activityType = ActivityType.Cycling,
            water = 550,
        )

        val credFlow = dao.getAllProfiles()

        credFlow.test {
            assertThat(awaitItem()).isNull()
            dao.saveProfile(profile1)
            dao.saveProfile(profile2)
            assertThat(awaitItem()).containsExactly(profile1, profile2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}