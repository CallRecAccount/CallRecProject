package uz.invan.rovitalk.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val image: String,
    val duration: Long,
    val section: String,
    val sectionTitle: String,
    val type: String,
    val purchaseType: String,
    val count: Int,
    val description: String,
    val header: String?,
    val subHeader: String?,
    val mostListened: Boolean,
    val isBanner: Boolean,
    val isActive: Boolean,
    val lastListenedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
)