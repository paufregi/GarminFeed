package paufregi.connectfeed.presentation.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import paufregi.connectfeed.core.usecases.GetProfilesUseCase
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor(
    getProfilesUseCase: GetProfilesUseCase
) : ViewModel() {
    val state = getProfilesUseCase()
        .map {  ProfilesState(profiles = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ProfilesState())
}