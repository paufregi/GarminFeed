package paufregi.garminfeed.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import paufregi.garminfeed.garmin.data.Oauth2

@Entity
data class CachedOauth2(
    @PrimaryKey
    val id: Int = 1,

    @Embedded
    val oauth2: Oauth2
)


