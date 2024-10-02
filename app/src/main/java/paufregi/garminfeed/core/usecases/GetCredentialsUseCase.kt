package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetCredentialsUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke() {
        return garminRepository.getCredentials()
    }
}