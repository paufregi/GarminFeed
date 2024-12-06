package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetCourses @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun  invoke(): Result<List<Course>> {
        return when (val res = garminRepository.getCourses()) {
            is Result.Success -> Result.Success(
                res.data.sortedWith(compareBy({ it.type.order }, { it.name}))
            )
            is Result.Failure -> res
        }
    }
}