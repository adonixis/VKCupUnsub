package ru.adonixis.vkcupunsub.util

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import ru.adonixis.vkcupunsub.util.Utils.convertDpToPx

class RecyclerItemDecoration(context: Context?) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position <= 2) {
            outRect.top = convertDpToPx(24.0f).toInt()
        } else if (position >= state.itemCount - 3) {
            outRect.bottom = convertDpToPx(20.0f).toInt()
        }
    }
}