package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class ClearCacheUseCase @Inject constructor(private val garminRepository: GarminRepository) {
    suspend operator fun invoke(): Result<Unit> {
        if (username.isNotBlank() && password.isNotBlank()) {
            garminRepository.saveCredentials(Credentials(username = username, password = password))
            return Result.Success(Unit)
        }
        return Result.Failure("Validation error")
    }
}