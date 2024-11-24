package paufregi.connectfeed.presentation.quickedit

import paufregi.connectfeed.core.models.Activity
import paufregi.connectfeed.core.models.Profile

sealed interface QuickEditEvent {
    data class SetActivity(val activity: Activity): QuickEditEvent
    data class SetProfile(val profile: Profile): QuickEditEvent
    data class SetEffort(val effort: Float?): QuickEditEvent
    data class SetFeel(val feel: Float?): QuickEditEvent
    data object Save: QuickEditEvent
    data object Restart: QuickEditEvent
}
