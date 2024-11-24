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
import javax.inject.Inject

@HiltViewModel
class QuickEditViewModel @Inject constructor(
    val getLatestActivitiesUseCase: GetLatestActivitiesUseCase,
    val getProfilesUseCase: GetProfilesUseCase,
    val updateActivityUseCase: UpdateActivityUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(QuickEditState())
    val state = _state
        .onStart { load() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), QuickEditState())

    fun onEvent(event: QuickEditEvent) = when (event) {
        is QuickEditEvent.SetActivity -> _state.update {
            it.copy(
                activity = event.activity,
                profile = if (it.profile?.activityType != event.activity.type) null else it.profile,
                availableProfiles = it.allProfiles.filter { it.activityType == event.activity.type },
            )
        }
        is QuickEditEvent.SetProfile -> _state.update { it.copy( profile = event.profile ) }
        is QuickEditEvent.SetEffort -> _state.update { it.copy( effort = if (event.effort == 0f) null else event.effort ) }
        is QuickEditEvent.SetFeel -> _state.update { it.copy( feel = event.feel ) }
        is QuickEditEvent.Save -> saveActivity()
        is QuickEditEvent.Restart -> {
            _state.update { QuickEditState() }
            load()
        }
    }

    private fun load() = viewModelScope.launch {
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
            activity = state.value.activity,
            profile = state.value.profile,
            feel = state.value.feel,
            effort = state.value.effort
        )
        when (res) {
            is Result.Success -> _state.update { it.copy(processing = ProcessState.Success) }
            is Result.Failure -> _state.update { it.copy(processing = ProcessState.FailureUpdating) }
        }
    }
}