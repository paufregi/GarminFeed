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
import paufregi.connectfeed.core.usecases.GetCredentialUseCase
import paufregi.connectfeed.core.usecases.SaveCredentialUseCase
import paufregi.connectfeed.core.usecases.SaveProfileUseCase
import paufregi.connectfeed.presentation.settings.SettingsEvent
import paufregi.connectfeed.presentation.ui.components.SnackbarController
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    val getProfileUseCase: GetCredentialUseCase,
    val saveProfileUseCase: SaveProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())

    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), EditProfileState())

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.UpdateName -> _state.update { it.copy(profile = it.profile.copy(name = event.name)) }
            is EditProfileEvent.UpdateUpdateName -> _state.update { it.copy(profile = it.profile.copy(updateName = event.updateName)) }
            is EditProfileEvent.UpdateActivityType -> _state.update { it.copy(profile = it.profile.copy(activityType = event.activityType)) }
            is EditProfileEvent.UpdateEventType -> _state.update { it.copy(profile = it.profile.copy(eventType = event.eventType)) }
            is EditProfileEvent.UpdateCourse -> _state.update { it.copy(profile = it.profile.copy(course = event.course)) }
            is EditProfileEvent.UpdateWater -> _state.update { it.copy(profile = it.profile.copy(water = event.water)) }
            is EditProfileEvent.Save -> save()
        }
    }

    private fun save() = viewModelScope.launch {
        when (saveProfileUseCase(state.value.profile) ) {
            is Result.Success -> SnackbarController.sendEvent("Profile saved")
            is Result.Failure -> SnackbarController.sendEvent("Unable to save profile")
        }
    }
}