package paufregi.connectfeed.presentation.quickedit

import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Profile

sealed class QuickEditEvent {
    data class SelectActivity(val activity: Activity): QuickEditEvent()
    data class SelectProfile(val profile: Profile): QuickEditEvent()
    data class SelectEffort(val effort: Float): QuickEditEvent()
    data class SelectFeel(val feel: Float?): QuickEditEvent()
    data class Save(val callback: () -> Unit): QuickEditEvent()
}
