package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Profile
import javax.inject.Inject

class ValidateProfileUseCase @Inject constructor () {
    operator fun invoke(profile: Profile): Boolean {
        return profile.name.isNotBlank() &&
                (profile.activityType == ActivityType.Any || profile.eventType != null)
    }
}