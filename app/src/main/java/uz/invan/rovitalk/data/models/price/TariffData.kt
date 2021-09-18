package uz.invan.rovitalk.data.models.price

data class TariffData(
    val title: String,
    val oneMonthTariff: Tariff?,
    val threeMonthTariff: Tariff?,
    val sixMonthTariff: Tariff?,
    val section: String,
)

data class Tariff(
    val id: String,
    val price: Int,
    val duration: Int,
)

enum class Tariffs {
    ONE_MONTH, THREE_MONTH, SIX_MONTH
}

fun List<String>.toPricesString(): String {
    return joinToString(";")
}

fun String.toPricesList(): List<String> {
    return split(";").toList()
}