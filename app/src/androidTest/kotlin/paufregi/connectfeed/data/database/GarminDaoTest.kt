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
        val profile = ProfileEntity(
            id = 1,
            name = "profile",
            updateName = true,
            eventTypeId = 1,
            eventTypeKey = "event",
            activityTypeId = 2,
            activityTypeKey = "running",
            courseId = 1,
            courseName = "course",
            water = 550
        )

        val credFlow = dao.getProfile(1)

        credFlow.test {
            assertThat(awaitItem()).isNull()
            dao.saveProfile(profile)
            assertThat(awaitItem()).isEqualTo(profile)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Save and retrieve all profiles`() = runTest {
        val profile1 = ProfileEntity(
            id = 1,
            name = "profile1",
            updateName = true,
            eventTypeId = 1,
            eventTypeKey = "event",
            activityTypeId = 2,
            activityTypeKey = "running",
            courseId = 1,
            courseName = "course",
            water = 550
        )

        val profile2 = ProfileEntity(
            id = 2,
            name = "profile2",
            updateName = false,
            eventTypeId = 1,
            eventTypeKey = "event",
            activityTypeId = 2,
            activityTypeKey = "cycling",
            courseId = 1,
            courseName = "course",
            water = 250
        )

        dao.saveProfile(profile1)
        dao.saveProfile(profile2)

        val credFlow = dao.getAllProfiles()

        credFlow.test {
            assertThat(awaitItem()).containsExactly(profile1, profile2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}