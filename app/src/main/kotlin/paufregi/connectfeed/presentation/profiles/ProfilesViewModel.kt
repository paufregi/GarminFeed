package paufregi.connectfeed.presentation.profiles

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfilesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ProfilesState())

    val state = _state as StateFlow<ProfilesState>

    fun onEvent(event: ProfilesEvent) {
        when (event) {
            is ProfilesEvent.Save -> {}
        }
    }
}