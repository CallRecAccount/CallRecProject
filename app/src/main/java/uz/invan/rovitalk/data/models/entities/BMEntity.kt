package uz.invan.rovitalk.data.models.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "background_musics",
    indices = [Index(value = ["audio", "category"], unique = true)]
)
data class BMEntity(
    val id: String,
    val image: String,
    val audio: String,
    val category: String,
    @PrimaryKey(autoGenerate = true)
    val counter: Int = 0
)