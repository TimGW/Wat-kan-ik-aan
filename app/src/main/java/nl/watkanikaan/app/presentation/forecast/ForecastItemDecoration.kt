package nl.watkanikaan.app.presentation.forecast

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class ForecastItemDecoration(private val itemOffset: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) return
        val itemCount = state.itemCount

        val isFirstItem = itemPosition == 0
        val isLastItem = itemCount > 0 && itemPosition == itemCount - 1

        when {
            isFirstItem -> outRect.set(0, 0, itemOffset, 0)
            isLastItem -> outRect.set(itemOffset, 0, 0, 0)
            else -> outRect.set(itemOffset, 0, itemOffset, 0)
        }
    }
}