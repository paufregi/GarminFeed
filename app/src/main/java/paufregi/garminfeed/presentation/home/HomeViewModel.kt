package paufregi.garminfeed.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import paufregi.garminfeed.core.usecases.ClearCacheUseCase
import paufregi.garminfeed.core.usecases.GetCredentialUseCase
import paufregi.garminfeed.presentation.ui.ShortToast
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    val getCredentialUseCase: GetCredentialUseCase,
    val clearCacheUseCase: ClearCacheUseCase,
    val toast: ShortToast
) : ViewModel() {

    private val mutableSetupDone = MutableStateFlow(false)
    val setupDone: StateFlow<Boolean> = mutableSetupDone.onStart {
        val cred = getCredentialUseCase()
        mutableSetupDone.value = cred != null && cred.username.isNotBlank() && cred.password.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun clearCache() = viewModelScope.launch {
        clearCacheUseCase()
        return@launch
    }.invokeOnCompletion { cause ->
        when (cause == null) {
            true -> toast.show("Cache cleared")
            false -> toast.show("Unable to clear cache")
        }
    }
}