package ru.adonixis.vkcupunsub.util

import android.view.View

interface OnItemClickListener {
    fun onClick(view: View, position: Int)
    fun onLongClick(view: View, position: Int)
}
