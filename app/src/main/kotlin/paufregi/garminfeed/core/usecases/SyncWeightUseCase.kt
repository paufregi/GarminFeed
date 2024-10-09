package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.data.repository.GarminRepository
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.utils.FitWriter
import paufregi.garminfeed.core.utils.Formatter
import paufregi.garminfeed.core.utils.RenphoReader
import java.io.File
import java.io.InputStream
import java.time.Instant
import javax.inject.Inject
import javax.inject.Named

class SyncWeightUseCase @Inject constructor (
    private val garminRepository: GarminRepository,
    @Named("tempFolder") val folder: File
) {
    suspend operator fun invoke(inputStream: InputStream):Result<Unit> {
        val weights = RenphoReader.read(inputStream)

        val filename = "ws_${Formatter.dateTimeForFilename.format(Instant.now())}.fit"
        val file = File(folder, filename)
        FitWriter.weights(file, weights)
        val res = garminRepository.uploadFile(file)
        file.delete()

        return when (res) {
            is Result.Success -> Result.Success(Unit)
            is Result.Failure -> Result.Failure("Failed to upload file")
        }

    }
}