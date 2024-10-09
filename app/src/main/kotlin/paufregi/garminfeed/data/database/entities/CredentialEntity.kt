package paufregi.garminfeed.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import paufregi.garminfeed.core.models.Credential

@Entity(tableName="credentials")
data class CredentialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,

    @Embedded
    val credential: Credential
)
