package uz.invan.rovitalk.data.models.price

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.invan.rovitalk.databinding.ItemPaymentSystemBinding
import uz.invan.rovitalk.ui.base.BaseVH
import uz.invan.rovitalk.util.custom.setOnDebounceClickListener
import uz.invan.rovitalk.util.ktx.layoutInflater

class PaymentSystemsAdapter(private val data: List<PaymentSystemData>) :
    RecyclerView.Adapter<PaymentSystemsAdapter.PaymentSystemVH>() {
    private var listener: PaymentSystemListener? = null

    fun setOnPaymentSystemClick(onClickListener: PaymentSystemListener) {
        listener = onClickListener
    }

    inner class PaymentSystemVH(private val binding: ItemPaymentSystemBinding) :
        BaseVH<PaymentSystemData>(binding.root) {
        override fun onInit(item: PaymentSystemData) {
            binding.itemPaymentSystems.setOnDebounceClickListener {
                listener?.onPaymentSystemClick(data[bindingAdapterPosition])
            }
        }

        override fun onBind(item: PaymentSystemData) {
            super.onBind(item)

            Glide.with(binding.image)
                .load(item.image)
                .into(binding.image)

//            val name = context.getString(item.name)
//            val description = context.getString(item.description, name)
//            binding.name.text = name
//            binding.description.text = description
        }
    }

    fun interface PaymentSystemListener {
        fun onPaymentSystemClick(system: PaymentSystemData)
    }

    class PaymentSystemsItemDecoration(@IntRange(from = 0) val margin: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            outRect.left = margin
            outRect.right = margin
            outRect.top = margin
            outRect.bottom = margin
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentSystemVH {
        val binding = ItemPaymentSystemBinding.inflate(parent.layoutInflater, parent, false)
        return PaymentSystemVH(binding)
    }

    override fun onBindViewHolder(holder: PaymentSystemVH, position: Int) =
        holder.onBind(data[position])

    override fun getItemCount() = data.size
}