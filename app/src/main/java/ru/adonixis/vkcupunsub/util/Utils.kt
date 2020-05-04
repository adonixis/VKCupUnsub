package ru.adonixis.vkcupunsub.util

import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.material.snackbar.Snackbar

object Utils {
    @JvmStatic
    fun convertDpToPx(dp: Float): Float {
        return dp * Resources.getSystem().displayMetrics.density
    }

    fun convertPxToDp(px: Float): Float {
        return px / Resources.getSystem().displayMetrics.density
    }

    fun showSnackbar(view: View,
                     callback: Snackbar.Callback?,
                     @ColorInt backgroundColor: Int,
                     @ColorInt textColor: Int,
                     text: String,
                     @ColorInt actionTextColor: Int,
                     actionText: String,
                     onViewClickListener: View.OnClickListener?) {
        var onClickListener = onViewClickListener
        if (onClickListener == null) {
            onClickListener = View.OnClickListener { }
        }
        val snackbar = Snackbar
            .make(view, text, Snackbar.LENGTH_LONG)
            .addCallback(callback!!)
            .setActionTextColor(actionTextColor)
            .setAction(actionText, onClickListener)
        val sbView = snackbar.view
        sbView.setBackgroundColor(backgroundColor)
        val textView = sbView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(textColor)
        snackbar.show()
    }

}