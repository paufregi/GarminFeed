package paufregi.connectfeed.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import paufregi.connectfeed.core.usecases.IsSetupDone
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getSetupDone: IsSetupDone
) : ViewModel() {

    val state = getSetupDone()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), null)
}