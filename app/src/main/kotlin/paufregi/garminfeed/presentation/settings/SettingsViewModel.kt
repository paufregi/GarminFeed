package paufregi.garminfeed.presentation.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.core.models.Result
import paufregi.garminfeed.core.usecases.GetCredentialUseCase
import paufregi.garminfeed.core.usecases.SaveCredentialUseCase
import paufregi.garminfeed.presentation.ui.ShortToast
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCredentialUseCase: GetCredentialUseCase,
    private val saveCredentialUseCase: SaveCredentialUseCase,
    val toast: ShortToast
) : ViewModel() {

    val state = mutableStateOf(SettingsState())

    init {
        viewModelScope.launch {
            val cred = getCredentialUseCase()
            if (cred != null) {
                state.value = state.value.copy(username = cred.username, password = cred.password)
            }
        }
    }

    fun saveCredential() = viewModelScope.launch {
        when (saveCredentialUseCase(Credential(state.value.username, state.value.password))) {
            is Result.Success -> toast.show("Credential saved")
            is Result.Failure -> toast.show("Unable to save credential")
        }
    }
}