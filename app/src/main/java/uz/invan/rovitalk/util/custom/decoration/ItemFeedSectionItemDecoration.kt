package uz.invan.rovitalk.util.custom.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.FloatRange
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class ItemFeedSectionItemDecoration(@FloatRange(from = 0.0) val margin: Float) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = margin.roundToInt()
    }
}