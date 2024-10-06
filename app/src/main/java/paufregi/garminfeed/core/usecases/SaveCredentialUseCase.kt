package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.data.repository.GarminRepository
import paufregi.garminfeed.core.models.Result
import javax.inject.Inject

class SaveCredentialUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(
        credential: Credential
    ):Result<Unit> {
        if (credential.username.isNotBlank() && credential.password.isNotBlank()) {
            garminRepository.saveCredential(credential)
            return Result.Success(Unit)
        }
        return Result.Failure("Validation error")
    }
}