package paufregi.connectfeed.data.database.coverters

import androidx.room.TypeConverter
import paufregi.connectfeed.core.models.ActivityType
import paufregi.connectfeed.core.models.ActivityType.Cycling
import paufregi.connectfeed.core.models.ActivityType.Running
import paufregi.connectfeed.core.models.ActivityType.Strength
import paufregi.connectfeed.core.models.ActivityType.TrailRunning
import paufregi.connectfeed.core.models.ActivityType.Unknown

class ActivityTypeConverter {
    @TypeConverter
    fun fromString(type: String?): ActivityType = when (type?.lowercase()) {
        "any" -> ActivityType.Any
        "running" -> Running
        "trail_running", "trail running" -> TrailRunning
        "cycling" -> Cycling
        "strength_training", "strength" -> Strength
        else -> Unknown
    }

    @TypeConverter
    fun toString(type: ActivityType?): String? = type?.name
}