package com.tory.nestedceiling.app.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.updateLayoutParams
import com.tory.module_adapter.views.AbsModuleView
import com.tory.nestedceiling.app.adapter.BannerImageAdapter
import com.tory.nestedceiling.app.utils.MaterialColor
import com.tory.nestedceiling.app.utils.dp
import com.youth.banner.Banner
import com.youth.banner.indicator.RectangleIndicator

data class ModuleBannerModel(
    val list: List<MaterialColor> = MaterialColor.values().take(5),
    val height: Int = 150.dp()
)


class ModuleBannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsModuleView<ModuleBannerModel>(context, attrs) {


    val banner = Banner<MaterialColor, BannerImageAdapter>(context)
    val bannerAdapter = BannerImageAdapter(emptyList())

    init {
        addView(banner, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        banner.adapter = bannerAdapter
        banner.let {
            it.indicator = RectangleIndicator(banner.context)
            it.setBannerRound(20f)
        }
    }

    override fun onChanged(model: ModuleBannerModel) {
        super.onChanged(model)
        bannerAdapter.setDatas(model.list)
        updateLayoutParams {
            height = model.height
        }
    }

}
