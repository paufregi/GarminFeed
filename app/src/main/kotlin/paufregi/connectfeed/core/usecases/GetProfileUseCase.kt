package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetProfileUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(id: Long): Profile? = garminRepository.getProfile(id)
}