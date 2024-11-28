package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class SaveProfile @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(profile: Profile):Result<Unit> {
        if (profile.name.isBlank()) return Result.Failure("Name cannot be empty")
        if (profile.activityType != ActivityType.Any &&
            profile.activityType != ActivityType.Strength &&
            profile.course?.type != profile.activityType) return Result.Failure("Course must match activity type")
        garminRepository.saveProfile(profile)
        return Result.Success(Unit)
    }
}