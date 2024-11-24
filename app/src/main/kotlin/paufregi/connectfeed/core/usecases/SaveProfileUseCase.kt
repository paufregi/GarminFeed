package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(profile: Profile):Result<Unit> {
        garminRepository.saveProfile(profile)
        return Result.Success(Unit)
    }
}