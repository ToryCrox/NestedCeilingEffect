package cc.solart.nestedceiling.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.NestedPublicRecyclerView;
import androidx.recyclerview.widget.RecyclerView;

import cc.solart.nestedceiling.utils.FindTarget;
import cc.solart.nestedceiling.utils.FlingHelper;


public class NestedChildRecyclerView extends NestedPublicRecyclerView implements NestedChildItemContainer {
    private FlingHelper mFlingHelper;


    public NestedChildRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NestedChildRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedChildRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setNestedScrollingEnabled(true);
    }

}
