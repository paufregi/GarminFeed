package paufregi.connectfeed.core.usecases

import kotlinx.coroutines.flow.Flow
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetProfiles @Inject constructor (private val garminRepository: GarminRepository) {
    operator fun invoke(): Flow<List<Profile>> = garminRepository.getAllProfiles()
}