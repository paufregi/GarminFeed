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
import paufregi.connectfeed.core.usecases.GetLatestActivities
import paufregi.connectfeed.core.usecases.GetProfiles
import paufregi.connectfeed.core.usecases.UpdateActivity
import paufregi.connectfeed.presentation.ui.models.ProcessState
import javax.inject.Inject

@HiltViewModel
class QuickEditViewModel @Inject constructor(
    val getLatestActivities: GetLatestActivities,
    getProfiles: GetProfiles,
    val updateActivity: UpdateActivity
) : ViewModel() {
    private val _state = MutableStateFlow(QuickEditState())
    val state = combine(_state, getProfiles()) { state, profiles -> state.copy(profiles = profiles) }
        .onStart { load() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), QuickEditState())

    fun onEvent(event: QuickEditEvent) = when (event) {
        is QuickEditEvent.SetActivity -> _state.update {
            it.copy(
                activity = event.activity,
                profile = if (it.profile?.activityType != event.activity.type) null else it.profile,
            )
        }
        is QuickEditEvent.SetProfile -> _state.update { it.copy( profile = event.profile ) }
        is QuickEditEvent.SetWater -> _state.update { it.copy( profile = it.profile?.copy(water = event.water) ) }
        is QuickEditEvent.SetEffort -> _state.update { it.copy( effort = if (event.effort == 0f) null else event.effort ) }
        is QuickEditEvent.SetFeel -> _state.update { it.copy( feel = event.feel ) }
        is QuickEditEvent.Save -> saveActivity()
        is QuickEditEvent.Restart -> {
            _state.update { QuickEditState() }
            load()
        }
    }

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(process = ProcessState.Processing) }
        when (val res = getLatestActivities()) {
            is Result.Success -> _state.update { it.copy(
                process = ProcessState.Idle,
                activities = res.data
            ) }
            is Result.Failure -> _state.update { it.copy(process = ProcessState.Failure(res.reason)) }
        }
    }

    private fun saveActivity() = viewModelScope.launch {
        _state.update { it.copy(process = ProcessState.Processing) }
        val res = updateActivity(
            activity = state.value.activity,
            profile = state.value.profile,
            feel = state.value.feel,
            effort = state.value.effort
        )
        when (res) {
            is Result.Success -> _state.update { it.copy(process = ProcessState.Success("Activity updated")) }
            is Result.Failure -> _state.update { it.copy(process = ProcessState.Failure(res.reason)) }
        }
    }
}