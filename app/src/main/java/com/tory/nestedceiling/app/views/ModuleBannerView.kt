package com.tory.nestedceiling.app.views

import android.content.Context
import android.util.AttributeSet
import com.tory.module_adapter.views.AbsModuleView
import com.tory.nestedceiling.app.adapter.BannerImageAdapter
import com.tory.nestedceiling.app.utils.dp
import com.youth.banner.Banner
import com.youth.banner.indicator.RectangleIndicator

data class ModuleBannerModel(
    val list: List<String> = listOf("https://hellorfimg.zcool.cn/provider_image/large/2238406784.jpg",
        "https://hellorfimg.zcool.cn/provider_image/large/2238407550.jpg",
        "https://hellorfimg.zcool.cn/provider_image/large/2238400920.jpg",
        "https://hellorfimg.zcool.cn/provider_image/large/2238389071.jpg")
)


class ModuleBannerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AbsModuleView<ModuleBannerModel>(context, attrs) {


    val banner = Banner<String, BannerImageAdapter>(context)
    val bannerAdapter = BannerImageAdapter(emptyList())

    init {
        addView(banner, LayoutParams.MATCH_PARENT, 150.dp())
        banner.adapter = bannerAdapter
        banner.let {
            it.indicator = RectangleIndicator(banner.context)
            it.setBannerRound(20f)
        }
    }

    override fun onChanged(model: ModuleBannerModel) {
        super.onChanged(model)
        bannerAdapter.setDatas(model.list)
    }

}