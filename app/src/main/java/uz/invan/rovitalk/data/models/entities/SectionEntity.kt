package uz.invan.rovitalk.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sections")
data class SectionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val image: String,
    val isActive: Boolean,
    val isMain: Boolean,
    val boughtDate: Long?,
    val expireDate: Long?,
)