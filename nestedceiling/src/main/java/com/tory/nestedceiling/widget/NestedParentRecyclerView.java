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

/**
 * 嵌套滑动的父View
 */
public class NestedParentRecyclerView extends NestedPublicRecyclerView implements NestedScrollingParent3, NestedScrollingParent2 {
    public final static String TAG = "NestedParentRecycler";

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

    private int mTopOffset = 0;

    private boolean mIsChildNestedScrolling = false;

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
        setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    @Override
    public void setOverScrollMode(int overScrollMode) {
        if (overScrollMode == OVER_SCROLL_NEVER) {
            throw new IllegalArgumentException("NestedChildRecyclerView must be OVER_SCROLL_ALWAYS!!");
        }
        super.setOverScrollMode(overScrollMode);
    }

    public int getTopOffset() {
        return mTopOffset;
    }

    /**
     * 距离嵌套滑动距离顶部应该流出的空间
     * @param topOffset
     */
    public void setTopOffset(int topOffset) {
        this.mTopOffset = topOffset;
        if (mContentView != null){
            mContentView.requestLayout();
        }
    }


    public void addOnChildAttachStateListener(OnChildAttachStateListener listener) {
        mOnChildAttachStateListeners.add(listener);
    }

    protected boolean isTargetContainer(View child) {
        return NestedCeilingHelper.isNestedChildContainerTag(child);
    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        if (isTargetContainer(child)) {
            mContentView = (ViewGroup) child;
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            }
            if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            child.setLayoutParams(lp);
            if (NestedCeilingHelper.DEBUG) {
                log("onChildAttachedToWindow....");
            }
        }
    }

    @Override
    public void onChildDetachedFromWindow(@NonNull View child) {
        if (child == mContentView) {
            mContentView = null;
            log("onChildDetachedFromWindow....");
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
                    log("Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                    break;
                }
                final float y = e.getY(activePointerIndex);
                if (isScrollEnd()) {
                    // 如果此控件已经滑动到底部，需要让子嵌套布局滑动剩余的距离
                    // 或者子嵌套布局向下还未到顶部，也需要让子嵌套布局先滑动一段距离
                    NestedChildRecyclerView child = FindTarget.findChildScrollTarget(mContentView);
                    if (child != null) {
                        int deltaY = (int) (mLastY - y);
                        mTempConsumed[1] = 0;
                        child.doScrollConsumed(0, deltaY, mTempConsumed);
                        int consumedY = mTempConsumed[1];
                        if (consumedY != 0 && NestedCeilingHelper.DEBUG) {
                            log("onTouch scroll consumed: " + consumedY);
                        }
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
            if (NestedCeilingHelper.USE_OVER_SCROLL) {
                dispatchChildState(SCROLL_STATE_IDLE);
            } else {
                dispatchChildFling();
            }
        } else {
            dispatchChildState(state);
        }
    }

    @Override
    protected void onFlingEnd(int velocityX, int velocityY) {
        super.onFlingEnd(velocityX, velocityY);
        if (velocityY > 0 && NestedCeilingHelper.USE_OVER_SCROLL) {
            // 通过OverScroll传递滚动状态
            RecyclerView child = FindTarget.findChildScrollTarget(mContentView);
            if (child != null) {
                if (NestedCeilingHelper.DEBUG) {
                    log("onFlingEnd fling child velocityY: " + velocityY);
                }
                child.fling(0, velocityY);
            }
        }
    }

    private void dispatchChildState(int state) {
        if (mIsChildNestedScrolling) {
            return;
        }
        NestedChildRecyclerView child = FindTarget.findChildScrollTarget(mContentView);
        if (child != null && !child.isFling()) {
            child.updateScrollState(state);
        }
    }

    private void dispatchChildFling() {
        boolean isChildFling = false;
        if (mVelocityY != 0 && isScrollEnd()) {
            double splineFlingDistance = mFlingHelper.getSplineFlingDistance(mVelocityY);
            if (splineFlingDistance > mTotalDy) {
                childFling(mFlingHelper.getVelocityByDistance(splineFlingDistance - mTotalDy));
                isChildFling = true;
            }
        }
        mTotalDy = 0;
        mVelocityY = 0;
        if (!isChildFling) {
            dispatchChildState(SCROLL_STATE_IDLE);
        }
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
        if (NestedCeilingHelper.DEBUG) {
            log("onStartNestedScroll type: " + type + ", scrollState: " + getScrollState());
        }
        if (isStart && type == ViewCompat.TYPE_TOUCH && getScrollState() == SCROLL_STATE_SETTLING) {
            // 子view引起嵌套滑动是可能在fling，stop it
            stopScroll();
        }
        if (isStart) {
            mIsChildNestedScrolling = true;
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
            doScrollConsumed(0, dy, mTempConsumed);
            consumed[1] = mTempConsumed[1];

            updateScrollState(type == ViewCompat.TYPE_TOUCH ? SCROLL_STATE_DRAGGING : SCROLL_STATE_SETTLING);

            if (NestedCeilingHelper.DEBUG) {
                log("onNestedPreScroll dy:" + dy + ", consumedY: " + consumed[1] + ", type:" + type);
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

    /**
     * dyUnconsumed != 0时，嵌套的子view，表示嵌套的子view不能滑动，也就是到顶了，大于0表示下滑，小于0表示上划
     * @param target
     * @param dyUnconsumed
     * @param type
     * @param consumed
     */
    private void onNestedScrollInternal(@NonNull View target, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if (dyUnconsumed == 0) {
            return;
        }
        mTempConsumed[1] = 0;
        doScrollConsumed(0, dyUnconsumed, mTempConsumed);
        int consumedY = mTempConsumed[1];
        consumed[1] += consumedY;
        final int myUnconsumedY = dyUnconsumed - consumedY;

        dispatchNestedScroll(0, consumedY, 0, myUnconsumedY, null, type, consumed);

        if (NestedCeilingHelper.DEBUG) {
            log("onNestedScrollInternal dyUnconsumed:" + dyUnconsumed
                    + ", consumedY:" + consumedY + ", myUnconsumedY:" + myUnconsumedY
                    + ", type:" + type);
        }

        if (dyUnconsumed < 0 && type == ViewCompat.TYPE_TOUCH) {
            updateScrollState(SCROLL_STATE_DRAGGING);
        }

        // dyUnconsumed 大于0是下滑，小于0是上划
        if (dyUnconsumed < 0 && type == ViewCompat.TYPE_NON_TOUCH && target instanceof NestedChildRecyclerView) {
            NestedChildRecyclerView nestedView = (NestedChildRecyclerView) target;
            if (nestedView != FindTarget.findChildScrollTarget(mContentView)) {
                log("onNestedScrollInternal nestedView is changed, return");
                return;
            }
            OverScroller overScroller = nestedView.getFlingOverScroll();
            if (overScroller == null) {
                return;
            }
            float absVelocity = overScroller.getCurrVelocity();
            // nestedView.stopScroll();
            // 停止，但不更新状态，因为fling时在onStateChanged中要更新子view的状态
            nestedView.stopScrollWithoutState();
            float myVelocity = absVelocity * -1;
            fling(0, Math.round(myVelocity));

            if (NestedCeilingHelper.DEBUG) {
                log("onNestedScrollInternal start fling from child, absVelocity:" + absVelocity + ", myVelocity:" + myVelocity);
            }
        }
    }


    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        if (NestedCeilingHelper.DEBUG) {
            log("onStopNestedScroll type: " + type + ", scrollState: " + getScrollState());
        }
        if (type == ViewCompat.TYPE_TOUCH && getScrollState() == SCROLL_STATE_SETTLING) {
            // 停止是可能正在fling，需要手动停止
            log("onStopNestedScroll stop it");
            stopScroll();
        } else if (getFlingOverScroll() != null && getFlingOverScroll().isFinished()
                && target instanceof NestedChildRecyclerView
                && ((NestedChildRecyclerView) target).getScrollState() == SCROLL_STATE_IDLE){
            // 嵌套滑动停止时要将状态至为idle状态
            //((NestedChildRecyclerView) target).getScrollState() ==
            updateScrollState(SCROLL_STATE_IDLE);
        }
        mParentHelper.onStopNestedScroll(target, type);
        stopNestedScroll(type);
        mIsChildNestedScrolling = false;
    }
    // NestedScrollingParent


    @Override
    public void onStopNestedScroll(@NonNull View target) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onNestedFling(
            @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        log("onNestedFling velocityY: " + velocityY + ", consumed: " + consumed);
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

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
