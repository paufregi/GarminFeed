package paufregi.connectfeed.presentation.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import paufregi.connectfeed.core.usecases.DeleteProfilesUseCase
import paufregi.connectfeed.core.usecases.GetProfilesUseCase
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    getProfiles: GetProfilesUseCase,
    val deleteProfile: DeleteProfilesUseCase
) : ViewModel() {
    val state = getProfiles()
        .map {  ProfilesState(profiles = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), ProfilesState())

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Delete -> viewModelScope.launch { deleteProfile(event.profile) }
        }
    }
}