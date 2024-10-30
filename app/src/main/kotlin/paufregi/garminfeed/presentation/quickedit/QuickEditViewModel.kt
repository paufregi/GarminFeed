package paufregi.garminfeed.presentation.quickedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.usecases.GetLatestActivitiesUseCase
import paufregi.garminfeed.core.usecases.GetProfilesUseCase
import paufregi.garminfeed.core.usecases.UpdateActivityUseCase
import paufregi.garminfeed.presentation.ui.components.SnackbarController
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
                availableProfiles = it.allProfiles.filter { it.activityType == event.activity.type }
            )
        }
        is QuickEditEvent.SelectProfile -> _state.update { it.copy( selectedProfile = event.profile ) }
        is QuickEditEvent.Save -> saveActivity()
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

    private fun saveActivity() = viewModelScope.launch {
        when (updateActivityUseCase(state.value.selectedActivity, state.value.selectedProfile)) {
            is Result.Success -> SnackbarController.sendEvent("Activity updated")
            is Result.Failure -> SnackbarController.sendEvent("Unable to update activity")
        }
    }
}