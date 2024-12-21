package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class RefreshTokens @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke() : Result<Unit> {
        garminRepository.deleteTokens()

        return when (val res = garminRepository.fetchUser()) {
            is Result.Success -> Result.Success(Unit)
            is Result.Failure -> Result.Failure(res.reason)
        }
    }
}