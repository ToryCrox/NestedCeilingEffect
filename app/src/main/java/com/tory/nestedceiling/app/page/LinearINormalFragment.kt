package com.tory.nestedceiling.app.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tory.module_adapter.base.ItemSpace
import com.tory.module_adapter.base.NormalModuleAdapter
import com.tory.nestedceiling.app.utils.MaterialColor
import com.tory.nestedceiling.app.utils.dp
import com.tory.nestedceiling.app.views.ModuleNormalItemModel
import com.tory.nestedceiling.app.views.ModuleNormalItemView
import com.tory.nestedceiling.widget.NestedChildRecyclerView

class LinearINormalFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recyclerView = NestedChildRecyclerView(requireContext())

        val listAdapter = NormalModuleAdapter()
        val itemSpace = ItemSpace(spaceV = 12.dp(), edgeH = 16.dp())
        listAdapter.register(itemSpace = itemSpace) {
            ModuleNormalItemView(it.context)
        }
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = listAdapter.getGridLayoutManager(requireContext())

        listAdapter.setItems(MutableList(200) {
            ModuleNormalItemModel(MaterialColor.values()[it % MaterialColor.size])
        })


        return recyclerView
    }

    fun resetToTop() {

    }
}
