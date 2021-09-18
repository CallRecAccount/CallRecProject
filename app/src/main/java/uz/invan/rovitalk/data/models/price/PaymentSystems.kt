package uz.invan.rovitalk.data.models.price

import android.util.Base64
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import timber.log.Timber
import uz.invan.rovitalk.R

data class PaymentSystemData(
    @StringRes val name: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int,
    var url: String,
    val system: PaymentSystems,
)

enum class PaymentSystems {
    PAYME, CLICK, ROBO_CASSA, PAYPAL, VISA, MASTERCARD, IO_MONEY, QIWI, APPLE_PAY, OTHER
}

fun fetchRoviPaymentSystems(): List<PaymentSystemData> {
    val systems = arrayListOf<PaymentSystemData>()
    systems.add(PaymentSystemData(
        name = R.string.payme,
        description = R.string.payme_description,
        image = R.drawable.ic_payme,
        url = "",
        system = PaymentSystems.PAYME
    ))
    systems.add(PaymentSystemData(
        name = R.string.click,
        description = R.string.click_description,
        image = R.drawable.ic_click,
        url = "",
        system = PaymentSystems.CLICK
    ))
    systems.add(PaymentSystemData(
        name = R.string.robo_kassa,
        description = R.string.robo_kassa_description,
        image = R.drawable.ic_robo_kassa,
        url = "",
        system = PaymentSystems.ROBO_CASSA
    ))
    systems.add(PaymentSystemData(
        name = R.string.paypal,
        description = R.string.paypal_description,
        image = R.drawable.ic_pay_pal,
        url = "",
        system = PaymentSystems.PAYPAL
    ))
    systems.add(PaymentSystemData(
        name = R.string.visa,
        description = R.string.visa_description,
        image = R.drawable.ic_visa,
        url = "",
        system = PaymentSystems.VISA
    ))
    systems.add(PaymentSystemData(
        name = R.string.mastercard,
        description = R.string.mastercard_description,
        image = R.drawable.ic_mastercard,
        url = "",
        system = PaymentSystems.MASTERCARD
    ))
    systems.add(PaymentSystemData(
        name = R.string.io_money,
        description = R.string.io_money_description,
        image = R.drawable.ic_io_money,
        url = "",
        system = PaymentSystems.IO_MONEY
    ))
    systems.add(PaymentSystemData(
        name = R.string.qiwi,
        description = R.string.qiwi_description,
        image = R.drawable.ic_qiwi,
        url = "",
        system = PaymentSystems.QIWI
    ))
    systems.add(PaymentSystemData(
        name = R.string.other,
        description = R.string.other_description,
        image = R.drawable.ic_other,
        url = "",
        system = PaymentSystems.OTHER
    ))

    return systems
}

/*enum class PaymentSystems(val system: PaymentSystemData) {
    PAYME(PaymentSystemData(
        name = R.string.payme,
        description = R.string.payme_description,
        image = R.drawable.ic_payme,
        url = "")
    )
}*/

fun generatePaymeLink(receiptId: Int, amount: Double): String {
    val base = "https://checkout.paycom.uz"
    val m = "m=5fb775faf8caf1c00e7e5c3b"
    val ac = "ac.receipt_id=$receiptId"
    val a = "a=${amount.toBigDecimal().toPlainString()}"
    val l = "l=uz"

    Timber.tag("PAYME_TAG").d("$m;$ac;$a;$l")
    val data = "$m;$ac;$a;$l".toByteArray()
    val encoded = Base64.encodeToString(data, Base64.DEFAULT)
    return "$base/$encoded"
}