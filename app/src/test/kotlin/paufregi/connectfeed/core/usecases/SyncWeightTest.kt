package paufregi.connectfeed.core.usecases

import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.models.Weight
import paufregi.connectfeed.core.utils.FitWriter
import paufregi.connectfeed.core.utils.Formatter
import paufregi.connectfeed.core.utils.RenphoReader
import paufregi.connectfeed.data.repository.GarminRepository
import java.time.Instant
import java.util.Date


class SyncWeightTest {
    @Rule
    @JvmField
    var folder: TemporaryFolder = TemporaryFolder()

    private val repo = mockk<GarminRepository>()
    private lateinit var useCase: SyncWeight

    @Before
    fun setup(){
        mockkObject(RenphoReader)
        mockkObject(Formatter)
        mockkObject(FitWriter)

        useCase = SyncWeight(repo, folder.newFolder())
    }

    @After
    fun tearDown(){
        clearAllMocks()
        unmockkObject(RenphoReader)
        unmockkObject(Formatter::class)
        unmockkObject(FitWriter::class)
    }

    @Test
    fun `Upload file`() = runTest {
        val csvText = """
            Time of Measurement,Weight(kg),BMI,Body Fat(%),Fat-Free Mass(kg),Subcutaneous Fat(%),Visceral Fat,Body Water(%),Skeletal Muscle(%),Muscle Mass(kg),Bone Mass(kg),Protein(%),BMR(kcal),Metabolic Age,Optimal weight(kg),Target to optimal weight(kg),Target to optimal fat mass(kg),Target to optimal muscle mass(kg),Body Type,Remarks
            2024-01-01 10:20:30,76.15,23.8,23.2,58.48,20.9,7.0,55.4,49.5,55.59,2.89,17.5,1618,35,,,,,,
        """.trimIndent()

        val stubInputStream = IOUtils.toInputStream(csvText, "UTF-8")

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
        every { Formatter.dateTimeForFilename(any()).format(any()) } returns "20240101_000000"
        every { FitWriter.weights(any(), any()) } returns Unit
        coEvery { repo.uploadFile(any()) } returns Result.Success(Unit)

        coEvery { repo.saveCredential(any()) } returns Unit
        val res = useCase(stubInputStream)

        assertThat(res).isInstanceOf(Result.Success(Unit).javaClass)
        verify {
            RenphoReader.read(stubInputStream)
            Formatter.dateTimeForFilename(any()).format(any())
            FitWriter.weights(any(), weights)
        }
        coVerify { repo.uploadFile(any()) }
        confirmVerified(repo, RenphoReader, Formatter, FitWriter)
    }

    @Test
    fun `Upload empty file`() = runTest {
        val csvText = ""

        val stubInputStream = IOUtils.toInputStream(csvText, "UTF-8")

        every { RenphoReader.read(any()) } returns emptyList()
        every { Formatter.dateTimeForFilename(any()).format(any()) } returns "20240101_000000"
        every { FitWriter.weights(any(), any()) } returns Unit
        coEvery { repo.uploadFile(any()) } returns Result.Success(Unit)

        coEvery { repo.saveCredential(any()) } returns Unit
        val res = useCase(stubInputStream)

        assertThat(res).isInstanceOf(Result.Success(Unit).javaClass)
        verify {
            RenphoReader.read(stubInputStream)
            Formatter.dateTimeForFilename(any()).format(any())
            FitWriter.weights(any(), emptyList())
        }
        coVerify { repo.uploadFile(any()) }
        confirmVerified(repo, RenphoReader, Formatter, FitWriter)
    }

    @Test
    fun `Failed upload use-case`() = runTest {
        val csvText = ""

        val stubInputStream = IOUtils.toInputStream(csvText, "UTF-8")

        every { RenphoReader.read(any()) } returns emptyList()
        every { Formatter.dateTimeForFilename(any()).format(any()) } returns "20240101_000000"
        every { FitWriter.weights(any(), any()) } returns Unit
        coEvery { repo.uploadFile(any()) } returns Result.Failure("Failed to upload file")

        coEvery { repo.saveCredential(any()) } returns Unit
        val res = useCase(stubInputStream)

        assertThat(res).isInstanceOf(Result.Failure<Unit>("Failed to upload file").javaClass)
        verify {
            RenphoReader.read(stubInputStream)
            Formatter.dateTimeForFilename(any()).format(any())
            FitWriter.weights(any(), emptyList())
        }
        coVerify { repo.uploadFile(any()) }
        confirmVerified(repo, RenphoReader, Formatter, FitWriter)
    }
}
