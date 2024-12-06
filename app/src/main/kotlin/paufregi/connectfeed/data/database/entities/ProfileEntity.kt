package paufregi.connectfeed.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.Course
import paufregi.connectfeed.core.models.EventType

@Entity(tableName="profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val activityType: ActivityType = ActivityType.Any,
    @Embedded(prefix = "event_")
    val eventType: EventType? = null,
    @Embedded(prefix = "course_")
    val course: Course? = null,
    val water: Int? = null,
    val rename: Boolean = true,
    val customWater: Boolean = false,
    val feelAndEffort: Boolean = false,
)
