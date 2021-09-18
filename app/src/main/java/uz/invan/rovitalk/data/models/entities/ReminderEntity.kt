package uz.invan.rovitalk.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    val title: String,
    val content: String,
    val time: String,
    val isPrivate: Boolean,
    @PrimaryKey
    val id: String,
)