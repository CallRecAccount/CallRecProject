package uz.invan.rovitalk.data.models.feed

data class SectionData(val name: String, val image: String)

data class SectionItemData(
    val image: String,
    val title: String,
    val length: Int,
    val section: SectionData,
)