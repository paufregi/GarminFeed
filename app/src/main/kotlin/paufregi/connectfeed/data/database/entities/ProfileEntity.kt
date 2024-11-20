package paufregi.connectfeed.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val updateName: Boolean,
    val eventTypeId: Long?,
    val eventTypeKey: String?,
    val activityTypeId: Long?,
    val activityTypeKey: String?,
    val courseId: Long?,
    val courseName: String?,
    val water: Int?
)
