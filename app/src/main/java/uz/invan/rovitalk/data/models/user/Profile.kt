package uz.invan.rovitalk.data.models.user

data class Profile(
    val phone: String,
    val firstName: String,
    val lastName: String,
    val photo: String?,
    val token: String,
)