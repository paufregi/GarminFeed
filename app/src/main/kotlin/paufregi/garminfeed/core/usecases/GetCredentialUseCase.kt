package paufregi.garminfeed.core.usecases

import kotlinx.coroutines.flow.Flow
import paufregi.garminfeed.core.models.Credential
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetCredentialUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    operator fun invoke(): Flow<Credential?> {
        return garminRepository.getCredential()
    }
}