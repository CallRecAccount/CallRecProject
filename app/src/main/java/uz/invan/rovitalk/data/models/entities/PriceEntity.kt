package uz.invan.rovitalk.data.models.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prices")
data class PriceEntity(
    @PrimaryKey
    val id: String,
    val cost: Int,
    val duration: Int,
    val section: String,
    val title: String
)