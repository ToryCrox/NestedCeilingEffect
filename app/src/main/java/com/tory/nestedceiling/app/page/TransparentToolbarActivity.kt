package com.tory.nestedceiling.app.page

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.*
import androidx.recyclerview.widget.NestedPublicRecyclerView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.tory.nestedceiling.app.R
import com.tory.nestedceiling.app.utils.*
import com.tory.nestedceiling.app.views.*
import com.tory.module_adapter.base.NormalModuleAdapter
import com.tory.module_adapter.views.ModuleEmptyModel
import com.tory.nestedceiling.widget.NestedParentRecyclerView
import java.util.ArrayList

/**
 * - Author: xutao
 * - Date: 2021/9/28
 * - Description:
 */
class TransparentToolbarActivity: AppCompatActivity() {

    val toolbarBackground = ColorDrawable(Color.WHITE)
    var navigationIcon: Drawable? = null

    var scrollProgress: Float = -1f

    val toolbar: Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar)}

    var isLightBar: Boolean? = null
        set(value) {
            field = value
            SystemBarUtils.setStatusBarDarkMode(TransparentToolbarActivity@this, value == true)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transparent_toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.background = toolbarBackground
         toolbar.navigationIcon?.mutate()?.let {
             navigationIcon = DrawableCompat.wrap(it)
         }
        toolbar.navigationIcon = navigationIcon


        toolbar.updatePadding(top = SystemBarUtils.getStatusBarHeight(this))
        SystemBarUtils.translucentStatusBar(this)
        val recyclerView = findViewById<NestedParentRecyclerView>(R.id.recyclerView)
        toolbar.doOnPreDraw {
            Log.d("TransparentToolbar", "topOffset " + toolbar.measuredHeight)
            recyclerView.topOffset = toolbar.measuredHeight
        }

        val listAdapter = NormalModuleAdapter()
        listAdapter.register {
            ModuleBannerView(it.context)
        }
        listAdapter.register {
            ModuleNormalItemView(it.context)
        }
        listAdapter.register {
            LastViewPagerItemView(it.context)
        }
        listAdapter.register {
            LastViewPager2ItemView(it.context)
        }

        recyclerView.layoutManager = listAdapter.getGridLayoutManager(this)
        recyclerView.adapter = listAdapter

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.postDelayed({ swipeRefreshLayout.isRefreshing = false }, 1000)
        }

        val data: MutableList<Any> = ArrayList()
        data.add(ModuleBannerModel(height = 300.dp()))
        data.add(ModuleEmptyModel(height = 8.dp()))
        data.add(ModuleNormalItemModel(MaterialColor.random()))
        data.add(ModuleEmptyModel(height = 8.dp()))
        data.add(ModuleNormalItemModel(MaterialColor.random()))
        data.add(ModuleEmptyModel(height = 8.dp()))
        data.add(ModuleNormalItemModel(MaterialColor.random()))
        data.add(LastViewPager2Model())

        listAdapter.setItems(data)

        SystemBarUtils.setStatusBarDarkMode(TransparentToolbarActivity@this, false)
        onScrollProgressChanged(0f)
        initScroll(recyclerView)
    }

    private fun initScroll(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d("TransparentToolbar", "onScrolled dy: $dy")
                val firstChild: View? = recyclerView.getChildAt(0)
                val scrollFraction = if (firstChild != null && recyclerView.getChildLayoutPosition(firstChild) == 0) {
                    val scrollRange = firstChild.height - toolbar.height
                    val fraction = if (scrollRange > 0) (firstChild.bottom - toolbar.height).toFloat() / scrollRange else 0f
                    Math.abs(1 - fraction).coerceAtLeast(0f).coerceAtMost(1f)
                } else 1f
                onScrollProgressChanged(scrollFraction)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d("NestedCeilingEffect", "onScrollStateChanged parent newState: $newState")
            }
        })
    }

    private fun onScrollProgressChanged(progress: Float) {
        if (scrollProgress == progress) return
        scrollProgress = progress
        val alpha = (0xFF * progress.coerceIn(0f, 1f)).toInt()

        toolbarBackground.alpha = alpha
        val resultColor = ArgbEvaluatorCompat.getInstance()
            .evaluate(progress, Color.WHITE, Color.BLACK)
        navigationIcon?.let { DrawableCompat.setTint(it, resultColor) }
        toolbar.setTitleTextColor(resultColor)


        val light = progress > 0.6
        if (light != isLightBar) { // 修改状态栏深浅
            isLightBar = light
        }
    }
}
