package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class SetupDone @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke() = garminRepository.saveSetup(true)
}