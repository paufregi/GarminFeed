package paufregi.connectfeed.core.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetProfilesUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    operator fun invoke(): Flow<List<Profile>> = garminRepository.getAllProfiles()
}