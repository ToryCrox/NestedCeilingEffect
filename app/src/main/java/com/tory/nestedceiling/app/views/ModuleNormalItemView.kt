package com.tory.nestedceiling.app.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.tory.module_adapter.views.AbsModuleView

data class ModuleNormalItemModel(
    val resId: Int
)


class ModuleNormalItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsModuleView<ModuleNormalItemModel>(context, attrs) {


    val imageView = AppCompatImageView(context)

    init {
        addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        imageView.adjustViewBounds = true
    }

    override fun onChanged(model: ModuleNormalItemModel) {
        super.onChanged(model)
        imageView.setImageResource(model.resId)
    }


}