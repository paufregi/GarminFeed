package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetEventTypesUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun  invoke(): Result<List<EventType>> {
        return garminRepository.getEventTypes()
    }
}