package paufregi.connectfeed.presentation.editprofile

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
import paufregi.connectfeed.core.usecases.GetActivityTypesUseCase
import paufregi.connectfeed.core.usecases.GetCoursesUseCase
import paufregi.connectfeed.core.usecases.GetEventTypesUseCase
import paufregi.connectfeed.core.usecases.SaveProfileUseCase
import paufregi.connectfeed.core.usecases.ValidateProfileUseCase
import paufregi.connectfeed.presentation.ui.components.SnackbarController
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    val getActivityTypes: GetActivityTypesUseCase,
    val getEventTypes: GetEventTypesUseCase,
    val getCourses: GetCoursesUseCase,

    val validateProfile: ValidateProfileUseCase,
    val saveProfile: SaveProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())

    val state = _state
        .onStart { load() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), EditProfileState())

    private fun load() = viewModelScope.launch {
        _state.update { it.copy(processing = ProcessState.Processing) }
        var errors = mutableListOf<String>()
        _state.update { it.copy(availableActivityType = getActivityTypes()) }

        when (val res = getEventTypes()) {
            is Result.Success -> _state.update { it.copy(availableEventType = res.data) }
            is Result.Failure -> errors.add("event types")
        }
        when (val res = getCourses()) {
            is Result.Success -> _state.update { it.copy(allCourses = res.data, availableCourses = res.data) }
            is Result.Failure -> errors.add("courses")
        }


        when (errors.isNotEmpty()) {
            true -> _state.update { it.copy(processing = ProcessState.FailureLoading("Couldn't load ${errors.joinToString(", ")}")) }
            false -> _state.update { it.copy(processing = ProcessState.Idle) }
        }
    }

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.SetName -> _state.update { it.copy(profile = it.profile.copy(name = event.name)) }
            is EditProfileEvent.SetActivityType -> _state.update { it.copy(profile = it.profile.copy(activityType = event.activityType)) }
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
        when (saveProfile(state.value.profile) ) {
            is Result.Success -> SnackbarController.sendEvent("Profile saved")
            is Result.Failure -> SnackbarController.sendEvent("Unable to save profile")
        }
    }
}