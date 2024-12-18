package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class RefreshToken @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke() : Result<Unit> {
        garminRepository.deleteTokens()
        return when (val res = garminRepository.fetchFullName()) {
            is Result.Success -> Result.Success(Unit)
            is Result.Failure -> {
                garminRepository.deleteCredential()
                garminRepository.deleteTokens()
                Result.Failure(res.reason)
            }
        }
    }
}