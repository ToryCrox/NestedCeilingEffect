package com.tory.nestedceiling.app.views

import android.content.Context
import android.util.AttributeSet
import com.tory.module_adapter.base.groupPosition
import com.tory.module_adapter.views.AbsModuleView
import com.tory.nestedceiling.app.utils.MaterialColor
import com.tory.nestedceiling.app.utils.dp
import kotlin.random.Random

class ModuleStaggerItemModel

class ModuleStaggerItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsModuleView<ModuleStaggerItemModel>(context, attrs) {

    private val items = mutableListOf<StaggerItem>()


    init {
        val colorList = MaterialColor.values().toList().shuffled()
        for (color in colorList) {
            val height = Random.nextInt(200.dp(), 350.dp())
            items.add(StaggerItem(color, height))
        }
    }

    override fun onChanged(model: ModuleStaggerItemModel) {
        super.onChanged(model)
        val item = items[groupPosition % items.size]
        setBackgroundColor(item.color.color)
        val lp = layoutParams as MarginLayoutParams
        lp.height = item.height
        layoutParams = lp
    }

    class StaggerItem(
        val color: MaterialColor,
        val height: Int
    )
}
