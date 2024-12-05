package paufregi.connectfeed.core.usecases

import android.util.Log
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
            profile.course != null &&
            profile.course.type != profile.activityType) return Result.Failure("Course must match activity type")
        Log.i("SaveProfile", "Validation passed")
        garminRepository.saveProfile(profile)
        Log.i("SaveProfile", "All done")
        return Result.Success(Unit)
    }
}