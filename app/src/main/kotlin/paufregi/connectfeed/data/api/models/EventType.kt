package paufregi.connectfeed.data.api.models

import androidx.annotation.Keep
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import paufregi.connectfeed.core.models.EventType as CoreEventType

@Keep
@Serializable
data class EventType(
    @SerializedName("typeId")
    val id: Long?,
    @SerializedName("typeKey")
    val key: String?,
) {
    fun toCore(): CoreEventType? {
        if (id == null || key == null) return null

        return CoreEventType(
            id = id,
            name = key.replaceFirstChar { it.uppercase() }
        )
    }
}