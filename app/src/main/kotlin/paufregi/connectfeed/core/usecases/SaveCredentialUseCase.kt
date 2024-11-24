package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class SaveCredentialUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(credential: Credential):Result<Unit> {
        if (credential.username.isNotBlank() && credential.password.isNotBlank()) {
            garminRepository.saveCredential(credential)
            garminRepository.clearCache()
            return Result.Success(Unit)
        }
        return Result.Failure("Validation error")
    }
}