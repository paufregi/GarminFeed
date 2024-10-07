package paufregi.garminfeed.core.usecases

import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.input.CharSequenceInputStream
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.models.Weight
import paufregi.garminfeed.core.utils.FitWriter
import paufregi.garminfeed.core.utils.Formatter
import paufregi.garminfeed.core.utils.RenphoReader
import paufregi.garminfeed.data.repository.GarminRepository
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.nio.charset.CharsetEncoder
import java.time.Instant
import java.util.Date


class SyncWeightUseCaseTest {
    @JvmField
    @Rule
    var folder: TemporaryFolder = TemporaryFolder()

    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: SyncWeightUseCase


    @Before
    fun setUp(){
        mockkStatic(RenphoReader::class)
        mockkStatic(Formatter::class)
        mockkStatic(FitWriter::class)

        useCase = SyncWeightUseCase(repo, folder.newFolder())
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }

    @Test
    fun `Upload file use-case`() = runTest{
        val csvInputStream: InputStream = InputStreamReader(StringReader("""
            Time of Measurement,Weight(kg),BMI,Body Fat(%),Fat-Free Mass(kg),Subcutaneous Fat(%),Visceral Fat,Body Water(%),Skeletal Muscle(%),Muscle Mass(kg),Bone Mass(kg),Protein(%),BMR(kcal),Metabolic Age,Optimal weight(kg),Target to optimal weight(kg),Target to optimal fat mass(kg),Target to optimal muscle mass(kg),Body Type,Remarks
            2024-01-01 10:20:30,76.15,23.8,23.2,58.48,20.9,7.0,55.4,49.5,55.59,2.89,17.5,1618,35,,,,,,
        """.trimIndent()))

        val weights = listOf(Weight(
            timestamp = Date.from(Instant.ofEpochMilli(1704057630000)),
            weight = 76.15f,
            bmi = 23.8f,
            fat = 23.2f,
            visceralFat = 7,
            water = 55.4f,
            muscle = 55.59f,
            bone = 2.89f,
            basalMet = 1618f,
            metabolicAge = 35,
        ))

        every { RenphoReader.read(any()) } returns weights
        every { Formatter.dateTimeForFilename.format(any()) } returns "20240101_000000"
        every { FitWriter.weights(any(), any()) } returns Unit

        coEvery { repo.saveCredential(any()) } returns Unit
        val res = useCase(csvInputStream)

        assertThat(res).isInstanceOf(Result.Success(Unit).javaClass)
        verify {
            RenphoReader.read(csvInputStream)
            Formatter.dateTimeForFilename.format(any())
            FitWriter.weights(any(), weights)
        }
        coVerify { repo.uploadFile(any()) }
        confirmVerified(repo)
    }
}



//class SyncWeightUseCaseTest @Inject constructor (
//    private val garminRepository: GarminRepository,
//    @Named("tempFolder") val folder: File
//) {
//    suspend operator fun invoke(inputStream: InputStream):Result<Unit> {
//        val weights = RenphoReader.read(inputStream)
//
//        val filename = "ws_${Formatter.dateTimeForFilename.format(Instant.now())}.fit"
//        val file = File(folder, filename)
//        FitWriter.weights(file, weights)
//
//        return when (garminRepository.uploadFile(file)) {
//            is ApiResponse.Success -> Result.Success(Unit)
//            is ApiResponse.Failure -> Result.Failure("Failed to upload file")
//        }
//
//    }
//}