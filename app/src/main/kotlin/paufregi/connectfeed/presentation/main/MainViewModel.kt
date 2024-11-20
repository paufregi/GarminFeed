package paufregi.connectfeed.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import paufregi.connectfeed.core.usecases.GetSetupDoneUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getSetupDoneUseCase: GetSetupDoneUseCase
) : ViewModel() {

    val state = getSetupDoneUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), null)
}