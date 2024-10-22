package paufregi.garminfeed.core.usecases

import android.util.Log
import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.core.models.Profile
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class SaveActivityUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(
        activity: Activity?,
        profile: Profile?
    ):Result<Unit> {
        return if (activity != null && profile != null)
            Result.Success(Unit)
        else
            Result.Failure("Invalid values")
    }
}