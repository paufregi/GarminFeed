package paufregi.garminfeed.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import paufregi.garminfeed.garmin.data.Oauth1

@Entity
data class CachedOauth1(
    @PrimaryKey
    val id: Int = 1,

    @Embedded
    val oauth1: Oauth1
)