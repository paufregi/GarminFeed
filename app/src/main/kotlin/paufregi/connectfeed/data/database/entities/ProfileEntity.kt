package paufregi.connectfeed.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.EventType
import paufregi.connectfeed.core.models.Profile

@Entity(tableName="profiles", primaryKeys = ["id"])
data class ProfileEntity(
    @Embedded()
    val profile: Profile
)
