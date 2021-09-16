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


public class NestedChildRecyclerView extends NestedPublicRecyclerView {
    private FlingHelper mFlingHelper;


    public NestedChildRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NestedChildRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedChildRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
        setNestedScrollingEnabled(true);
    }

    private void setup() {
        mFlingHelper = new FlingHelper(getContext());
        //setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (!isAttachedToWindow()) return false;
        velocityY = mFlingHelper.getFlingVelocity(velocityY);
        return super.fling(velocityX, velocityY);
    }

}
