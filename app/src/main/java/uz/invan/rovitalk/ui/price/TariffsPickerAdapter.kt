package uz.invan.rovitalk.ui.price

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import uz.invan.rovitalk.R
import uz.invan.rovitalk.data.models.price.TariffData
import uz.invan.rovitalk.data.models.price.Tariffs
import uz.invan.rovitalk.databinding.ItemTariffsPickerBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.ktx.layoutInflater

class TariffsPickerAdapter(private val tariffs: List<TariffData>) :
    RecyclerView.Adapter<TariffsPickerAdapter.TariffsPickerVH>() {
    private var listener: TariffClickListener? = null
    private val prices = hashMapOf<String, String>()

    fun setOnTariffClick(onTariffClick: TariffClickListener) {
        listener = onTariffClick
    }

    inner class TariffsPickerVH(private val binding: ItemTariffsPickerBinding) :
        BaseVH<TariffData>(binding.root) {
        private var chosenPosition = NOT_CHOSEN

        init {
            binding.oneMonthTariffContainer.setOnClickListener {
                if (bindingAdapterPosition < 0) return@setOnClickListener
                val tariff = Tariffs.ONE_MONTH
                deselect(
                    binding.threeMonthSubscription,
                    binding.textThreeMonthTariffChosen
                )
                deselect(
                    binding.sixMonthSubscription,
                    binding.textSixMonthTariffChosen
                )
                if (chosenPosition == tariff.ordinal)
                    deselect(
                        binding.oneMonthSubscription,
                        binding.textOneMonthTariffChosen, true
                    )
                else
                    select(
                        binding.oneMonthSubscription,
                        binding.textOneMonthTariffChosen,
                        tariff
                    )
                listener?.onTariffClick(retrieveChosenTariffs())
            }
            binding.threeMonthTariffContainer.setOnClickListener {
                if (bindingAdapterPosition < 0) return@setOnClickListener
                val tariff = Tariffs.THREE_MONTH
                deselect(
                    binding.oneMonthSubscription,
                    binding.textOneMonthTariffChosen
                )
                deselect(
                    binding.sixMonthSubscription,
                    binding.textSixMonthTariffChosen
                )
                if (chosenPosition == tariff.ordinal)
                    deselect(
                        binding.threeMonthSubscription,
                        binding.textThreeMonthTariffChosen, true
                    )
                else
                    select(
                        binding.threeMonthSubscription,
                        binding.textThreeMonthTariffChosen,
                        tariff
                    )
                listener?.onTariffClick(retrieveChosenTariffs())
            }
            binding.sixMonthTariffContainer.setOnClickListener {
                if (bindingAdapterPosition < 0) return@setOnClickListener
                val tariff = Tariffs.SIX_MONTH
                deselect(
                    binding.oneMonthSubscription,
                    binding.textOneMonthTariffChosen
                )
                deselect(
                    binding.threeMonthSubscription,
                    binding.textThreeMonthTariffChosen
                )
                if (chosenPosition == tariff.ordinal)
                    deselect(
                        binding.sixMonthSubscription,
                        binding.textSixMonthTariffChosen, true
                    )
                else
                    select(
                        binding.sixMonthSubscription,
                        binding.textSixMonthTariffChosen,
                        tariff
                    )
                listener?.onTariffClick(retrieveChosenTariffs())
            }
        }

        private fun select(background: View, text: TextView, tariff: Tariffs) {
            chosenPosition = tariff.ordinal
            background.setBackgroundResource(R.drawable.background_contained_rounded_accent)
            text.isVisible = true


            val currentTariff = tariffs[bindingAdapterPosition]
            when (tariff) {
                Tariffs.ONE_MONTH -> {
                    if (currentTariff.oneMonthTariff == null) return
                    prices[currentTariff.title] = currentTariff.oneMonthTariff.id
                }
                Tariffs.THREE_MONTH -> {
                    if (currentTariff.threeMonthTariff == null) return
                    prices[currentTariff.title] = currentTariff.threeMonthTariff.id
                }
                Tariffs.SIX_MONTH -> {
                    if (currentTariff.sixMonthTariff == null) return
                    prices[currentTariff.title] = currentTariff.sixMonthTariff.id
                }
            }
        }

        private fun deselect(background: View, text: TextView, isMain: Boolean = false) {
            if (isMain)
                chosenPosition = NOT_CHOSEN
            background.setBackgroundResource(R.color.none)
            text.isInvisible = true

            val removedTariff = tariffs[bindingAdapterPosition]
            prices.remove(removedTariff.title)
        }

        override fun onBind(item: TariffData) {
            super.onBind(item)
            val oneMonthTariff = item.oneMonthTariff
            val threeMonthTariff = item.threeMonthTariff
            val sixMonthTariff = item.sixMonthTariff

            binding.textSectionName.text = item.title

            binding.oneMonthSubscription.isVisible = item.oneMonthTariff != null
            binding.threeMonthSubscription.isVisible = item.threeMonthTariff != null
            binding.sixMonthSubscription.isVisible = item.sixMonthTariff != null

            binding.textOneMonth.text = context.getString(R.string.x_day, item.oneMonthTariff?.duration)
            binding.textThreeMonth.text = context.getString(R.string.x_day, item.threeMonthTariff?.duration)
            binding.textSixMonth.text = context.getString(R.string.x_day, item.sixMonthTariff?.duration)

            binding.textOneMonthPrice.text =
                context.getString(R.string.x_sum, oneMonthTariff?.price)
            binding.textThreeMonthPrice.text =
                context.getString(R.string.x_sum, threeMonthTariff?.price)
            binding.textSixMonthPrice.text =
                context.getString(R.string.x_sum, sixMonthTariff?.price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TariffsPickerVH {
        val binding = ItemTariffsPickerBinding.inflate(parent.layoutInflater, parent, false)
        return TariffsPickerVH(binding)
    }

    override fun getItemCount() = tariffs.size

    override fun onBindViewHolder(holder: TariffsPickerVH, position: Int) =
        holder.onBind(tariffs[position])

    fun retrieveChosenTariffs(): List<String> {
        Timber.d("tariffs: ${prices.keys}")
        return prices.values.toList()
    }

    fun interface TariffClickListener {
        fun onTariffClick(prices: List<String>)
    }

    companion object {
        private const val NOT_CHOSEN = -1
    }
}