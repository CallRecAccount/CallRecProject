package uz.invan.rovitalk.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audios")
data class AudioEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val image: String,
    val duration: Long,
    val category: String,
    val categoryTitle: String,
    val author: String,
    val url: String?,
    val lastPlayedPosition: Int?,
    val createdAt: Long,
    val updatedAt: Long,
)