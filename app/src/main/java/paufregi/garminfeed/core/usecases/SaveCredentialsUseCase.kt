package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.data.repository.GarminRepository
import paufregi.garminfeed.core.models.Result
import javax.inject.Inject

class SaveCredentialsUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(
        username: String,
        password: String,
    ):Result<Unit> {
        if (username.isNotBlank() && password.isNotBlank()) {
            garminRepository.saveCredentials(Credentials(username = username, password = password))
            return Result.Success(Unit)
        }
        return Result.Failure("Validation error")
    }
}