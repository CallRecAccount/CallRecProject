package uz.invan.rovitalk.ui.feed.section

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class ItemFeedLayoutManager @JvmOverloads constructor(
    private val itemsPerPage: Float,
    private val itemsOffset: Float,
    context: Context,
    @RecyclerView.Orientation orientation: Int = RecyclerView.HORIZONTAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {
    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
        return super.checkLayoutParams(lp) && lp?.width == itemsSize()
    }

    override fun generateDefaultLayoutParams() =
        setProperItemSize(super.generateDefaultLayoutParams())

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) =
        setProperItemSize(super.generateLayoutParams(lp))

    private fun setProperItemSize(lp: RecyclerView.LayoutParams?): RecyclerView.LayoutParams? {
        val itemsSize = itemsSize()
        if (orientation == HORIZONTAL) lp?.width = itemsSize
        else lp?.height == itemsSize
        return lp
    }

    private fun itemsSize(): Int {
        val pageSize = if (orientation == HORIZONTAL) width else height
        return ((pageSize / itemsPerPage) - (itemsPerPage * (itemsOffset / 2 - 1))).roundToInt()
    }
}