package paufregi.connectfeed.data.database.coverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import paufregi.connectfeed.core.models.EventType

class EventTypeConverter {
    @TypeConverter
    fun fromString(type: String): EventType = Gson().fromJson(type, EventType::class.java)

    @TypeConverter
    fun toString(type: EventType): String = Gson().toJson(type)
}