package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class SignOut @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke() {
        garminRepository.deleteCredential()
        garminRepository.deleteTokens()
        garminRepository.saveSetup(false)
    }
}