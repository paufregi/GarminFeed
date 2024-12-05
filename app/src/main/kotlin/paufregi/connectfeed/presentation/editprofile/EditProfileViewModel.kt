package paufregi.connectfeed.presentation.editprofile

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.models.Profile
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.usecases.GetActivityTypes
import paufregi.connectfeed.core.usecases.GetCourses
import paufregi.connectfeed.core.usecases.GetEventTypes
import paufregi.connectfeed.core.usecases.GetProfile
import paufregi.connectfeed.core.usecases.SaveProfile
import paufregi.connectfeed.presentation.Routes
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val getProfile: GetProfile,
    val getActivityTypes: GetActivityTypes,
    val getEventTypes: GetEventTypes,
    val getCourses: GetCourses,
    val saveProfile: SaveProfile,
) : ViewModel() {

    private val profileId: Long = savedStateHandle.toRoute<Routes.EditProfile>().id

    private val _state = MutableStateFlow(EditProfileState())

    val state = _state
        .onStart { load() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), EditProfileState())

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(processing = ProcessState.Processing) }
        var errors = mutableListOf<String>()

        _state.update { it.copy(
            profile = getProfile(profileId) ?: Profile(),
            activityTypes = getActivityTypes()
        ) }

        when (val res = getEventTypes()) {
            is Result.Success -> _state.update { it.copy(eventTypes = res.data) }
            is Result.Failure -> errors.add("event types")
        }
        when (val res = getCourses()) {
            is Result.Success -> _state.update { it.copy(courses = res.data) }
            is Result.Failure -> errors.add("courses")
        }

        when (errors.isNotEmpty()) {
            true -> _state.update { it.copy(processing = ProcessState.FailureLoading("Couldn't load ${errors.joinToString(" & ")}")) }
            false -> _state.update { it.copy(processing = ProcessState.Idle) }
        }
    }

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.SetName -> _state.update { it.copy(profile = it.profile.copy(name = event.name)) }
            is EditProfileEvent.SetActivityType -> _state.update {
                it.copy(
                    profile = it.profile.copy(
                        activityType = event.activityType,
                        course = if (event.activityType == it.profile.course?.type) it.profile.course else null,
                    ),
                )
            }
            is EditProfileEvent.SetEventType -> _state.update { it.copy(profile = it.profile.copy(eventType = event.eventType)) }
            is EditProfileEvent.SetCourse -> _state.update { it.copy(profile = it.profile.copy(course = event.course)) }
            is EditProfileEvent.SetWater -> _state.update { it.copy(profile = it.profile.copy(water = event.water)) }
            is EditProfileEvent.SetRename -> _state.update { it.copy(profile = it.profile.copy(rename = event.rename)) }
            is EditProfileEvent.SetCustomWater -> _state.update { it.copy(profile = it.profile.copy(customWater = event.customWater)) }
            is EditProfileEvent.SetFeelAndEffort -> _state.update { it.copy(profile = it.profile.copy(feelAndEffort = event.feelAndEffort)) }
            is EditProfileEvent.Save -> save()
        }
    }

    private fun save() = viewModelScope.launch {
        Log.i("EditProfileViewModel", "Saving profile")
        _state.update { it.copy(processing = ProcessState.Processing) }
        when (saveProfile(state.value.profile) ) {
            is Result.Success -> {
                Log.i("EditProfileViewModel", "Profile saved")
                _state.update { it.copy(processing = ProcessState.Success) }
            }
            is Result.Failure -> {
                Log.i("EditProfileViewModel", "Couldn't save profile")
                _state.update { it.copy(processing = ProcessState.FailureSaving)
                }
            }
        }
    }
}