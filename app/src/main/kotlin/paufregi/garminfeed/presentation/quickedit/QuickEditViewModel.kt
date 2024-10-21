package paufregi.garminfeed.presentation.quickedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.usecases.GetCredentialUseCase
import paufregi.garminfeed.core.usecases.SaveCredentialUseCase
import paufregi.garminfeed.presentation.settings.SettingsEvent
import paufregi.garminfeed.presentation.settings.SettingsState
import paufregi.garminfeed.presentation.ui.components.SnackbarController
import javax.inject.Inject

@HiltViewModel
class QuickEditViewModel @Inject constructor(
) : ViewModel() {

    private val _state = MutableStateFlow(QuickEditState())
    val state = _state.asStateFlow()

}