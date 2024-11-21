package paufregi.connectfeed.core.usecases

import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.Result
import paufregi.connectfeed.data.repository.GarminRepository
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor (private val garminRepository: GarminRepository) {
    suspend operator fun  invoke(): Result<List<Course>> {
        return garminRepository.getCourses()
    }
}