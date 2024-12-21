package paufregi.connectfeed.core.usecases

import kotlinx.coroutines.flow.Flow
import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetCredential @Inject constructor (private val garminRepository: GarminRepository) {
    operator fun invoke(): Flow<Credential?> = garminRepository.getCredential()
}