package cc.solart.nestedceiling.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import cc.solart.nestedceiling.widget.NestedChildRecyclerView;
import cc.solart.nestedceiling.widget.NestedParentRecyclerView;

public final class FindTarget {

    @Nullable
    public static NestedChildRecyclerView findChildScrollTarget(ViewGroup contentView) {
        if(contentView == null) return null;
        for (int i = 0; i < contentView.getChildCount(); i++) {
            View view = contentView.getChildAt(i);
            int centerX = (view.getLeft() + view.getRight()) / 2;
            int contentLeft = contentView.getScrollX();
            if (centerX <= contentLeft || centerX >= contentLeft + contentView.getWidth()) {
                continue;
            }
            NestedChildRecyclerView target = null;
            if(view instanceof NestedChildRecyclerView){
                target = (NestedChildRecyclerView) view;
            } else if(view instanceof ViewGroup){
                target = findChildScrollTarget((ViewGroup) view);
            }
            if(target != null){
                return target;
            }
        }
        return null;
    }

    @Nullable
    public static RecyclerView findParentScrollTarget(View view){
        ViewParent parent = view.getParent();
        while ((parent != null && parent.getClass() != NestedParentRecyclerView.class)){
            parent = parent.getParent();
        }

        if(parent != null){
            return (RecyclerView) parent;
        }
        return null;
    }
}
