package paufregi.connectfeed.presentation.quickedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.GetLatestActivitiesUseCase
import paufregi.connectfeed.core.usecases.GetProfilesUseCase
import paufregi.connectfeed.core.usecases.UpdateActivityUseCase
import paufregi.connectfeed.presentation.ui.components.SnackbarController
import javax.inject.Inject

@HiltViewModel
class QuickEditViewModel @Inject constructor(
    val getLatestActivitiesUseCase: GetLatestActivitiesUseCase,
    val getProfilesUseCase: GetProfilesUseCase,
    val updateActivityUseCase: UpdateActivityUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuickEditState())
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), QuickEditState())

    fun onEvent(event: QuickEditEvent) = when (event) {
        is QuickEditEvent.SelectActivity -> _state.update {
            it.copy(
                selectedActivity = event.activity,
                selectedProfile = if (it.selectedProfile?.activityType != event.activity.type) null else it.selectedProfile,
                availableProfiles = it.allProfiles.filter { it.activityType == event.activity.type },
            )
        }
        is QuickEditEvent.SelectProfile -> _state.update { it.copy( selectedProfile = event.profile ) }
        is QuickEditEvent.SelectEffort -> _state.update { it.copy( selectedEffort = event.effort ) }
        is QuickEditEvent.SelectFeel -> _state.update { it.copy( selectedFeel = event.feel ) }
        is QuickEditEvent.Save -> saveActivity(event.callback)
    }

    private fun loadData() = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        var errors = mutableListOf<String>()
        val activities = when (val res = getLatestActivitiesUseCase()) {
            is Result.Success -> res.data
            is Result.Failure -> {
                errors.add("activities")
                emptyList()
            }
        }
        val profiles = getProfilesUseCase()
        _state.update {
            it.copy(
                activities = activities,
                allProfiles = profiles,
                availableProfiles = profiles,
                loading = false
            )
        }
        if (errors.isNotEmpty())
            SnackbarController.sendEvent("Couldn't get ${errors.joinToString(" & ")}")
    }

    private fun saveActivity(callback: () -> Unit) = viewModelScope.launch {
        _state.update { it.copy(loading = true) }
        val res = updateActivityUseCase(
            activity = state.value.selectedActivity,
            profile = state.value.selectedProfile,
            feel = state.value.selectedFeel,
            effort = state.value.selectedEffort
        )
        when (res) {
            is Result.Success -> SnackbarController.sendEvent("Activity updated")
            is Result.Failure -> SnackbarController.sendEvent("Unable to update activity")
        }
        _state.update { it.copy(loading = true) }
        callback()
    }
}