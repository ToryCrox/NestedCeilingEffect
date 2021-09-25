package com.tory.nestedceiling.app.adapter

import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tory.nestedceiling.app.utils.MaterialColor
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.util.BannerUtils

class BannerImageAdapter(imageUrls: List<MaterialColor>) : BannerAdapter<MaterialColor, BannerImageAdapter.ImageHolder>(imageUrls) {


    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val imageView = View(parent.context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        imageView.layoutParams = params
        return ImageHolder(imageView)
    }

    override fun onBindView(holder: ImageHolder, data: MaterialColor, position: Int, size: Int) {
        Log.d("onBindView", data.name)
        holder.itemView.setBackgroundColor(data.color)
    }


    class ImageHolder(view: View) : RecyclerView.ViewHolder(view)

}