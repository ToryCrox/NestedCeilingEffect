package com.tory.nestedceiling.app.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tory.nestedceiling.app.R
import com.tory.module_adapter.base.NormalModuleAdapter
import com.tory.nestedceiling.app.views.ModuleNormalItemModel
import com.tory.nestedceiling.app.views.ModuleNormalItemView
import com.tory.nestedceiling.widget.NestedChildRecyclerView

class LinearINormalFragment : Fragment() {

    private val items = arrayOf(
        R.drawable.drawable_item_5,
        R.drawable.drawable_item_6
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recyclerView = NestedChildRecyclerView(requireContext())

        val listAdapter = NormalModuleAdapter()
        listAdapter.register {
            ModuleNormalItemView(it.context)
        }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = listAdapter.getGridLayoutManager(requireContext())

        listAdapter.setItems(MutableList(200) {
            ModuleNormalItemModel(items[it % 2])
        })


        return recyclerView
    }

    fun resetToTop() {

    }
}