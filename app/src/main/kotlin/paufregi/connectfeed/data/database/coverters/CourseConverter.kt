package paufregi.connectfeed.data.database.coverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import paufregi.connectfeed.core.models.Course

class CourseConverter {
    @TypeConverter
    fun fromString(course: String): Course = Gson().fromJson(course, Course::class.java)

    @TypeConverter
    fun toString(course: Course): String = Gson().toJson(course)
}