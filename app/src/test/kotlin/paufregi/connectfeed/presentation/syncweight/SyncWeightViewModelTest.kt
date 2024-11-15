package paufregi.connectfeed.presentation.syncweight

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import io.mockk.awaits
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.SyncWeightUseCase
import paufregi.connectfeed.presentation.utils.MainDispatcherRule

@ExperimentalCoroutinesApi
class SyncWeightViewModelTest {

    private val syncWeight = mockk<SyncWeightUseCase>()

    private lateinit var viewModel: SyncWeightModelView

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup(){
        viewModel = SyncWeightModelView(syncWeight)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Sync weight`() = runTest {
        val csvText = """
            Time of Measurement,Weight(kg),BMI,Body Fat(%),Fat-Free Mass(kg),Subcutaneous Fat(%),Visceral Fat,Body Water(%),Skeletal Muscle(%),Muscle Mass(kg),Bone Mass(kg),Protein(%),BMR(kcal),Metabolic Age,Optimal weight(kg),Target to optimal weight(kg),Target to optimal fat mass(kg),Target to optimal muscle mass(kg),Body Type,Remarks
            2024-01-01 10:20:30,76.15,23.8,23.2,58.48,20.9,7.0,55.4,49.5,55.59,2.89,17.5,1618,35,,,,,,
        """.trimIndent()

        val inputStream = IOUtils.toInputStream(csvText, "UTF-8")

        coEvery { syncWeight.invoke(any()) } coAnswers {
            delay(1)
            Result.Success(Unit)
        }

        viewModel.state.test {
            assertThat(awaitItem()).isEqualTo(SyncWeightState.Idle)
            viewModel.syncWeight(inputStream)
            assertThat(awaitItem()).isEqualTo(SyncWeightState.Uploading)
            assertThat(awaitItem()).isEqualTo(SyncWeightState.Success)
            cancelAndIgnoreRemainingEvents()
        }
        coVerify { syncWeight.invoke(inputStream) }
        confirmVerified(syncWeight)
    }
}