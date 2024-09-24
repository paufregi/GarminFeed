package paufregi.garminfeed.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Credentials(
    @PrimaryKey
    val id: Int = 1,

    val username: String,
    val password: String,
)
