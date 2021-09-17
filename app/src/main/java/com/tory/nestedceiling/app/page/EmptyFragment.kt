package com.tory.nestedceiling.app.page

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment

/**
 * - Author: xutao
 * - Date: 2021/9/16
 * - Email: xutao@shizhuang-inc.com
 * - Description:
 */
class EmptyFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val textView = AppCompatTextView(requireContext())
        textView.gravity = Gravity.CENTER
        textView.text = "empty"
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        return textView
    }
}