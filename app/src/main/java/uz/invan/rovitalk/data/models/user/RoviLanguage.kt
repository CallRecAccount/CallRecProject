package uz.invan.rovitalk.data.models.user

enum class RoviLanguage(val lang: String) {
    UZ("uz"), RU("ru"), EN("en")
}

data class LanguageData(
    val language: RoviLanguage,
    val isChosen: Boolean,
    val isCurrent: Boolean,
)