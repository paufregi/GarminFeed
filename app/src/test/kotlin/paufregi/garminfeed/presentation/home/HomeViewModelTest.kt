package paufregi.garminfeed.presentation.home

import io.mockk.clearAllMocks
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import paufregi.garminfeed.core.usecases.ClearCacheUseCase
import paufregi.garminfeed.core.usecases.GetCredentialUseCase


class HomeViewModelTest {

    private val getCredential = mockk<GetCredentialUseCase>()
    private val clearCache = mockk<ClearCacheUseCase>()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp(){
        viewModel = HomeViewModel(getCredential, clearCache)
    }

    @After
    fun tearDown(){
        clearAllMocks()
    }
}