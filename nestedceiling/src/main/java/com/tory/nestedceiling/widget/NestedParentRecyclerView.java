package com.tory.nestedceiling.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.NestedPublicRecyclerView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.tory.nestedceiling.R;
import com.tory.nestedceiling.utils.FindTarget;
import com.tory.nestedceiling.utils.FlingHelper;

/**
 * 嵌套滑动的父View
 */
public class NestedParentRecyclerView extends NestedPublicRecyclerView implements NestedScrollingParent3, NestedScrollingParent2 {
    public final static String TAG = "NestedParentRecycler";
    public final static boolean DEBUG = false;

    private NestedScrollingParentHelper mParentHelper;
    private FlingHelper mFlingHelper;
    private ViewGroup mContentView;
    private int mTotalDy = 0;
    // 记录y轴加速度
    private int mVelocityY = 0;
    private int mActivePointerId;
    private Float mLastY = 0f;

    private boolean mIsStartChildFling = false;
    private boolean mIsChildAttachedToTop = false;
    private boolean mIsChildDetachedFromTop = true;
    private final ArrayList<OnChildAttachStateListener> mOnChildAttachStateListeners = new ArrayList<>();

    private final int[] mTempConsumed = new int[2];
    private final int[] mNestedScrollingV2ConsumedCompat = new int[2];

    public NestedParentRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NestedParentRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedParentRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        mParentHelper = new NestedScrollingParentHelper(this);
        mFlingHelper = new FlingHelper(getContext());
        setOverScrollMode(OVER_SCROLL_NEVER);
    }


    public void addOnChildAttachStateListener(OnChildAttachStateListener listener) {
        mOnChildAttachStateListeners.add(listener);
    }


    protected boolean isTargetContainer(View child) {
        return child instanceof NestedChildItemContainer
                || child.getTag(R.id.nested_child_item_container) != null;
    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        if (isTargetContainer(child)) {
            mContentView = (ViewGroup) child;
            if (DEBUG) {
                Log.d(TAG, "onChildAttachedToWindow....");
            }
        }
    }

    @Override
    public void onChildDetachedFromWindow(@NonNull View child) {
        if (child == mContentView) {
            mContentView = null;
            Log.d(TAG, "onChildDetachedFromWindow....");
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        boolean isTouchInChildArea =  (mContentView != null) && (e.getY() > mContentView.getTop())
                && (e.getY() < mContentView.getBottom())
                && FindTarget.findChildScrollTarget(mContentView) != null;
        // 此控件滑动到底部或者触摸区域在子嵌套布局不拦截事件
        if (isTouchInChildArea) {
            if (getScrollState() == SCROLL_STATE_SETTLING) {
                // 上划fling过程中，停止，否则会抖动
                stopScroll();
            }
            return false;
        }
        return super.onInterceptTouchEvent(e);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = e.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getY();
                mActivePointerId = e.getPointerId(0);
                mVelocityY = 0;
                stopScroll();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                final int index = e.getActionIndex();
                mLastY = e.getY(index);
                mActivePointerId = e.getPointerId(index);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(e);
                break;
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = e.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    Log.d(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                    break;
                }
                final float y = e.getY(activePointerIndex);
                if (isScrollEnd()) {
                    // 如果此控件已经滑动到底部，需要让子嵌套布局滑动剩余的距离
                    // 或者子嵌套布局向下还未到顶部，也需要让子嵌套布局先滑动一段距离
                    NestedChildRecyclerView child = FindTarget.findChildScrollTarget(mContentView);
                    if (child != null) {
                        int deltaY = (int) (mLastY - y);
                        child.scrollConsumed(0, deltaY, null);
                    }
                }
                mLastY = y;
                break;
        }

        return super.onTouchEvent(e);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastY = ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private boolean isScrollEnd() {
        return !canScrollVertically(1);
    }

    private boolean isChildScrollTop(RecyclerView child) {
        return !child.canScrollVertically(-1);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (mIsStartChildFling) {
            mTotalDy = 0;
            mIsStartChildFling = false;
        }
        mTotalDy += dy;
        boolean attached = dy > 0 && isScrollEnd();
        if (attached && mIsChildDetachedFromTop) {
            mIsChildAttachedToTop = true;
            mIsChildDetachedFromTop = false;
            final int listenerCount = mOnChildAttachStateListeners.size();
            for (int i = 0; i < listenerCount; i++) {
                OnChildAttachStateListener listener = mOnChildAttachStateListeners.get(i);
                listener.onChildAttachedToTop();
            }
        }
        boolean detached = dy < 0 && !isScrollEnd();
        if (detached && mIsChildAttachedToTop) {
            RecyclerView child = FindTarget.findChildScrollTarget(mContentView);
            if (child == null || isChildScrollTop(child)) {
                mIsChildDetachedFromTop = true;
                mIsChildAttachedToTop = false;
                final int listenerCount = mOnChildAttachStateListeners.size();
                for (int i = 0; i < listenerCount; i++) {
                    OnChildAttachStateListener listener = mOnChildAttachStateListeners.get(i);
                    listener.onChildDetachedFromTop();
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            dispatchChildFling();
        }
    }

    private void dispatchChildFling() {
        if (mVelocityY != 0) {
            double splineFlingDistance = mFlingHelper.getSplineFlingDistance(mVelocityY);
            if (splineFlingDistance > mTotalDy) {
                childFling(mFlingHelper.getVelocityByDistance(splineFlingDistance - mTotalDy));
            }
        }
        mTotalDy = 0;
        mVelocityY = 0;
    }

    private void childFling(int velocityY) {
        RecyclerView child = FindTarget.findChildScrollTarget(mContentView);
        if (child != null) {
            child.fling(0, velocityY);
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        boolean fling = super.fling(velocityX, velocityY);
        if (!fling || velocityY <= 0) {
            mVelocityY = 0;
        } else {
            mIsStartChildFling = true;
            mVelocityY = velocityY;
        }
        return fling;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        boolean isStart = (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        if (DEBUG) {
            Log.d(TAG, "onStartNestedScroll type: " + type + ", scrollState: " + getScrollState());
        }
        if (isStart && type == ViewCompat.TYPE_TOUCH && getScrollState() == SCROLL_STATE_SETTLING) {
            // 子view引起嵌套滑动是可能在fling，stop it
            stopScroll();
        }
        return isStart;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mParentHelper.onNestedScrollAccepted(child, target, axes, type);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, type);
    }

    @Override
    public boolean onStartNestedScroll(
            @NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(
            @NonNull View child, @NonNull View target, int nestedScrollAxes) {
        onNestedScrollAccepted(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        boolean isParentScroll = dispatchNestedPreScroll(dx, dy, consumed, null, type);
        // 在父嵌套布局没有滑动时，处理此控件是否需要滑动
        if (isParentScroll) {
            return;
        }
        // 向上滑动且此控件没有滑动到底部时，需要让此控件继续滑动以保证滑动连贯一致性
        boolean needKeepScroll = dy > 0 && !isScrollEnd();
        if (needKeepScroll) {
            mTempConsumed[1] = 0;
            scrollConsumed(0, dy, mTempConsumed);
            consumed[1] = mTempConsumed[1];
            if (DEBUG) {
                Log.d(TAG, "onNestedPreScroll dy:" + dy + ", consumedY: " + consumed[1] + ", type:" + type);
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, mNestedScrollingV2ConsumedCompat);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        onNestedScrollInternal(target, dyUnconsumed, type, consumed);
    }

    private void onNestedScrollInternal(@NonNull View target, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (dyUnconsumed == 0) {
            return;
        }
        mTempConsumed[1] = 0;
        scrollConsumed(0, dyUnconsumed, mTempConsumed);
        int consumedY = mTempConsumed[1];
        consumed[1] += consumedY;
        final int myUnconsumedY = dyUnconsumed - consumedY;

        dispatchNestedScroll(0, consumedY, 0, myUnconsumedY, null, type, consumed);

        if (DEBUG) {
            Log.d(TAG, "onNestedScrollInternal dyUnconsumed:" + dyUnconsumed
                    + ", consumedY:" + consumedY + ", myUnconsumedY:" + myUnconsumedY
                    + ", type:" + type);
        }

        if (type == ViewCompat.TYPE_NON_TOUCH && target instanceof NestedChildRecyclerView) {
            NestedChildRecyclerView nestedView = (NestedChildRecyclerView) target;
            OverScroller overScroller = nestedView.getFlingOverScroll();
            if (overScroller == null) {
                return;
            }
            float absVelocity = overScroller.getCurrVelocity();
            nestedView.stopScroll();
            float myVelocity = absVelocity * (dyUnconsumed > 0 ? 1 : -1);
            fling(0, Math.round(myVelocity));

            if (DEBUG) {
                Log.d(TAG, "onNestedScrollInternal start fling from child, absVelocity:" + absVelocity + ", myVelocity:" + myVelocity);
            }
        }
    }


    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        if (DEBUG) {
            Log.d(TAG, "onStopNestedScroll type: " + type + ", scrollState: " + getScrollState());
        }
        if (type == ViewCompat.TYPE_TOUCH && getScrollState() == SCROLL_STATE_SETTLING) {
            // 停止是可能正在fling，需要手动停止
            stopScroll();
        }
        mParentHelper.onStopNestedScroll(target, type);
        stopNestedScroll(type);
    }
    // NestedScrollingParent


    @Override
    public void onStopNestedScroll(@NonNull View target) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onNestedFling(
            @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, "onNestedFling velocityY: " + velocityY + ", consumed: " + consumed);
        if (!consumed) {
            dispatchNestedFling(0, velocityY, true);
            fling(0, (int) velocityY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }
}
