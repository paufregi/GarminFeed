package paufregi.connectfeed.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import paufregi.connectfeed.core.models.ActivityType

@Entity(tableName="profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val updateName: Boolean,
    val activityType: ActivityType,
    val eventTypeId: Long?,
    val eventTypeKey: String?,
    val courseId: Long?,
    val courseName: String?,
    val water: Int?
)
