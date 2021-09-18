package uz.invan.rovitalk.data.models.feed

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class FeedSection(
    val id: String,
    val title: String,
    val image: String,
    val isActive: Boolean,
    val isMain: Boolean,
    val boughtDate: Long?,
    val expireDate: Long?,
    var categories: List<FeedCategory>,
) : Serializable

@Parcelize
data class FeedCategory(
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
    val subCategories: List<FeedSubCategory>,
    val mostListened: Boolean,
    val isBanner: Boolean,
    val isActive: Boolean,
    val lastListenedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
) : Parcelable, Serializable

@Parcelize
data class FeedSubCategory(
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
    val boughtDate: Long?,
    val openingDay: Int,
    val isOpen: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
) : Parcelable, Serializable

enum class Category(val type: String) {
    PLAYER("simple"), LIST("list")
}

enum class Purchase(val type: String) {
    FREE("free"), PAID("paid")
}