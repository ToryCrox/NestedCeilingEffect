package com.tory.nestedceiling.app.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.tory.module_adapter.base.groupPosition
import com.tory.module_adapter.views.AbsModuleView
import com.tory.nestedceiling.app.R

class ModuleStaggerItemModel

class ModuleStaggerItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsModuleView<ModuleStaggerItemModel>(context, attrs) {

    private val items = arrayOf(
        R.drawable.drawable_item_1,
        R.drawable.drawable_item_2,
        R.drawable.drawable_item_3,
        R.drawable.drawable_item_4
    )

    val imageView = AppCompatImageView(context)

    init {
        addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        imageView.adjustViewBounds = true
    }

    override fun onChanged(model: ModuleStaggerItemModel) {
        super.onChanged(model)
        imageView.setImageResource(items[groupPosition % 4])
    }
}