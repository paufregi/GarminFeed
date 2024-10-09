package paufregi.garminfeed.data.database

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.data.database.entities.CredentialEntity
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
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `Save and retrieve credentials`() = runTest {
        val cred = CredentialEntity(credential = Credential(username = "username", password = "password"))

        assertThat(dao.getCredential()).isNull()

        dao.saveCredential(cred)

        assertThat(dao.getCredential()).isEqualTo(cred)
    }
}