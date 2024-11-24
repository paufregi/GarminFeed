package paufregi.connectfeed.data.database.coverters

import androidx.room.TypeConverter
import paufregi.connectfeed.core.models.ActivityType

class ActivityTypeConverter {
    @TypeConverter
    fun fromString(type: String): ActivityType = ActivityType.fromName(type)

    @TypeConverter
    fun toString(type: ActivityType): String = type.name
}