package paufregi.connectfeed.presentation.quickedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.GetLatestActivitiesUseCase
import paufregi.connectfeed.core.usecases.GetProfilesUseCase
import paufregi.connectfeed.core.usecases.UpdateActivityUseCase
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), QuickEditState())

    fun onEvent(event: QuickEditEvent) = when (event) {
        is QuickEditEvent.SelectActivity -> _state.update {
            it.copy(
                selectedActivity = event.activity,
                selectedProfile = if (it.selectedProfile?.activityType != event.activity.type) null else it.selectedProfile,
                availableProfiles = it.allProfiles.filter { it.activityType == event.activity.type },
            )
        }
        is QuickEditEvent.SelectProfile -> _state.update { it.copy( selectedProfile = event.profile ) }
        is QuickEditEvent.SelectEffort -> _state.update { it.copy( selectedEffort = if (event.effort == 0f) null else event.effort ) }
        is QuickEditEvent.SelectFeel -> _state.update { it.copy( selectedFeel = event.feel ) }
        is QuickEditEvent.Save -> saveActivity()
        is QuickEditEvent.Restart -> {
            _state.update { QuickEditState() }
            loadData()
        }
    }

    private fun loadData() = viewModelScope.launch {
        _state.update { it.copy(processing = ProcessState.Processing) }
        var errors = mutableListOf<String>()
        when (val res = getLatestActivitiesUseCase()) {
            is Result.Success -> _state.update { it.copy(activities = res.data) }
            is Result.Failure -> {
                errors.add("activities")
            }
        }

        getProfilesUseCase().collect { profiles ->
            _state.update {
                it.copy(
                    allProfiles = profiles,
                    availableProfiles = profiles,
                )
            }
        }
        when (errors.isNotEmpty()) {
            true -> _state.update { it.copy(processing = ProcessState.FailureLoading) }
            false -> _state.update { it.copy(processing = ProcessState.Idle) }
        }
    }

    private fun saveActivity() = viewModelScope.launch {
        _state.update { it.copy(processing = ProcessState.Processing) }
        val res = updateActivityUseCase(
            activity = state.value.selectedActivity,
            profile = state.value.selectedProfile,
            feel = state.value.selectedFeel,
            effort = state.value.selectedEffort
        )
        when (res) {
            is Result.Success -> _state.update { it.copy(processing = ProcessState.Success) }
            is Result.Failure -> _state.update { it.copy(processing = ProcessState.FailureUpdating) }
        }
    }
}