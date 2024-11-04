package paufregi.garminfeed.presentation.quickedit

import paufregi.garminfeed.core.models.Activity
import paufregi.garminfeed.core.models.Profile

sealed class QuickEditEvent {
    data class SelectActivity(val activity: Activity): QuickEditEvent()
    data class SelectProfile(val profile: Profile): QuickEditEvent()
    data class SelectEffort(val effort: Float): QuickEditEvent()
    data class Save(val callback: () -> Unit): QuickEditEvent()
}
