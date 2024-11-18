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
import paufregi.connectfeed.presentation.utils.ProcessState
import javax.inject.Inject

@HiltViewModel
class QuickEditViewModel @Inject constructor(
    val getLatestActivitiesUseCase: GetLatestActivitiesUseCase,
    val getProfilesUseCase: GetProfilesUseCase,
    val updateActivityUseCase: UpdateActivityUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(QuickEditState(loading = ProcessState.Processing))
    val state = _state
        .onStart { loadData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), QuickEditState(loading = ProcessState.Processing))

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
        is QuickEditEvent.Save -> saveActivity()
        is QuickEditEvent.Restart -> {
            _state.update { QuickEditState(loading = ProcessState.Processing) }
            loadData()
        }
    }

    private fun loadData() = viewModelScope.launch {
        _state.update { it.copy(loading = ProcessState.Processing) }
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
            )
        }
        when (errors.isNotEmpty()) {
            true -> _state.update { it.copy(loading = ProcessState.Failure) }
            false -> _state.update { it.copy(loading = ProcessState.Idle) }
        }
    }

    private fun saveActivity() = viewModelScope.launch {
        _state.update { it.copy(updating = ProcessState.Failure) }
        val res = updateActivityUseCase(
            activity = state.value.selectedActivity,
            profile = state.value.selectedProfile,
            feel = state.value.selectedFeel,
            effort = state.value.selectedEffort
        )
        when (res) {
            is Result.Success -> _state.update { it.copy(updating = ProcessState.Success) }
            is Result.Failure -> _state.update { it.copy(updating = ProcessState.Failure) }
        }

    }
}