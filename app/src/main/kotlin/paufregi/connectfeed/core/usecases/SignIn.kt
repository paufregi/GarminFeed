package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Credential
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.core.models.User
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class SignIn @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun invoke(credential: Credential):Result<User> {
        if (credential.username.isNotBlank() && credential.password.isNotBlank()) {
            garminRepository.saveCredential(credential)

            return when (val res = garminRepository.fetchUser()) {
                is Result.Success -> Result.Success(res.data!!)
                is Result.Failure -> {
                    garminRepository.deleteCredential()
                    garminRepository.deleteTokens()
                    Result.Failure(res.reason)
                }
            }
        }
        return Result.Failure("Validation error")
    }
}