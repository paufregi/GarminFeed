package paufregi.garminfeed.core.usecases

import paufregi.garminfeed.data.database.models.Credentials
import paufregi.garminfeed.data.repository.GarminRepository
import javax.inject.Inject

class SaveCredentialsUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(
        username: String,
        password: String,
    ) {
        if (username.isNotBlank() && password.isNotBlank()) {
            garminRepository.saveCredentials(Credentials(username = username, password = password))
        }
    }
}