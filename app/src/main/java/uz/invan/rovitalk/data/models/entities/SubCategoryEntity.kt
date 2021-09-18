package uz.invan.rovitalk.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sub_categories")
data class SubCategoryEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val author: String,
    val image: String,
    val duration: Long,
    val sectionTitle: String,
    val category: String,
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
    val boughtDate:Long?,
    val openingDay: Int,
    val isOpen: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)