package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.utils.FitWriter
import paufregi.connectfeed.core.utils.Formatter
import paufregi.connectfeed.core.utils.RenphoReader
import paufregi.connectfeed.data.repository.GarminRepository
import java.io.File
import java.io.InputStream
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Named

class SyncWeight @Inject constructor (
    private val garminRepository: GarminRepository,
    @Named("tempFolder") val folder: File
) {
    suspend operator fun invoke(inputStream: InputStream):Result<Unit> {
        val weights = RenphoReader.read(inputStream)

        val dateFormatter = Formatter.dateTimeForFilename(ZoneId.systemDefault())
        val filename = "ws_${dateFormatter.format(Instant.now())}.fit"
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