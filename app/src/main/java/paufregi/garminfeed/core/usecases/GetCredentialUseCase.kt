package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetCredentialUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(): Credential? {
        return garminRepository.getCredential()
    }
}