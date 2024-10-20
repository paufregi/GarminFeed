package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class ClearCacheUseCase @Inject constructor(private val garminRepository: GarminRepository) {
    suspend operator fun invoke(): Unit {
        return garminRepository.clearCache()
    }
}