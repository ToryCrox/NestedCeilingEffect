package com.tory.nestedceiling.app.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.tory.module_adapter.views.AbsModuleView
import com.tory.nestedceiling.app.utils.MaterialColor
import com.tory.nestedceiling.app.utils.dp

data class ModuleNormalItemModel(
    val color: MaterialColor
)


class ModuleNormalItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsModuleView<ModuleNormalItemModel>(context, attrs) {

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 100.dp())
    }

    override fun onChanged(model: ModuleNormalItemModel) {
        super.onChanged(model)
        setBackgroundColor(model.color.color)
    }

}