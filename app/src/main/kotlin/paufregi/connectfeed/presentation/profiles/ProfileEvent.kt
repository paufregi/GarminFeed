package paufregi.connectfeed.presentation.profiles

import paufregi.connectfeed.core.models.Profile

sealed interface ProfileEvent {
    data class Delete(val profile: Profile): ProfileEvent
}
