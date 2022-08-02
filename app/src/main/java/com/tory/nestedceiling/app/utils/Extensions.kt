package com.tory.nestedceiling.app.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.*
import androidx.annotation.LayoutRes

inline fun ViewGroup.inflate(@LayoutRes res: Int, attachRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(res, this, attachRoot)
}

inline fun Int.dp(context: Context? = null): Int {
    val res = context?.resources ?: Resources.getSystem()
    return dp2px(res, this.toFloat())
}

inline fun Float.dp(context: Context? = null): Int {
    val res = context?.resources ?: Resources.getSystem()
    return dp2px(res, this)
}


fun dp2px(resources: Resources, dpVal: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpVal, resources.displayMetrics
    ).toInt()
}
