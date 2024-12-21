package paufregi.connectfeed.core.usecases

import kotlinx.coroutines.flow.firstOrNull
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class ChangePassword @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(password: String):Result<Unit> {
        if (password.isNotBlank()) {
            val oldCredential = garminRepository.getCredential().firstOrNull()
            val newCredential = oldCredential?.copy(password = password) ?: return Result.Failure("No credential found")

            garminRepository.deleteTokens()
            garminRepository.saveCredential(newCredential)

            return when (val res = garminRepository.fetchUser()) {
                is Result.Success -> Result.Success(Unit)
                is Result.Failure -> {
                    garminRepository.saveCredential(oldCredential)
                    garminRepository.deleteTokens()
                    Result.Failure(res.reason)
                }
            }
        }
        return Result.Failure("Validation error")
    }
}