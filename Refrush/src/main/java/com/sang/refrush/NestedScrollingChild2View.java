package com.sang.refrush;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.sang.refrush.utils.FRLog;

import static androidx.core.view.ViewCompat.TYPE_TOUCH;



public class NestedScrollingChild2View extends LinearLayout implements NestedScrollingChild2 {


    private NestedScrollingChildHelper mScrollingChildHelper = new NestedScrollingChildHelper(this);
    private final int mMinFlingVelocity;
    private final int mMaxFlingVelocity;
    private Scroller mScroller;
    private int lastY = -1;
    private int lastX = -1;
    private int[] offset = new int[2];
    private int[] consumed = new int[2];
    private int mOrientation;
    private boolean fling;//判断当前是否是可以进行惯性滑动



    public NestedScrollingChild2View(Context context) {
        this(context, null);

    }

    public NestedScrollingChild2View(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedScrollingChild2View(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mOrientation = getOrientation();
        setNestedScrollingEnabled(true);
        ViewConfiguration vc = ViewConfiguration.get(context);
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context);
    }


    /**
     * 开始滑动前调用，在惯性滑动和触摸滑动前都会进行调用，此方法一般在 onInterceptTouchEvent或者onTouch中，通知父类方法开始滑动
     * 会调用父类方法的 onStartNestedScroll onNestedScrollAccepted 两个方法
     *
     * @param axes 滑动方向
     * @param type 开始滑动的类型 the type of input which cause this scroll event
     * @return 有父视图并且开始滑动，则返回true 实际上就是看parent的 onStartNestedScroll 方法
     */
    @Override
    public boolean startNestedScroll(int axes, int type) {
        return mScrollingChildHelper.startNestedScroll(axes, type);
    }


    /**
     * 子控件在开始滑动前，通知父控件开始滑动，同时由父控件先消耗滑动时间
     * 在子View的onInterceptTouchEvent或者onTouch中，调用该方法通知父View滑动的距离
     * 最终会调用父view的 onNestedPreScroll 方法
     *
     * @param dx             水平方向嵌套滑动的子控件想要变化的距离 dx<0 向右滑动 dx>0 向左滑动 （保持和 RecycleView 一致）
     * @param dy             垂直方向嵌套滑动的子控件想要变化的距离 dy<0 向下滑动 dy>0 向上滑动 （保持和 RecycleView 一致）
     * @param consumed       父控件消耗的距离，父控件消耗完成之后，剩余的才会给子控件，子控件需要使用consumed来进行实际滑动距离的处理
     * @param offsetInWindow 子控件在当前window的偏移量
     * @param type           滑动类型，ViewCompat.TYPE_NON_TOUCH fling效果,ViewCompat.TYPE_TOUCH 手势滑动
     * @return true    表示父控件进行了滑动消耗，需要处理 consumed 的值，false表示父控件不对滑动距离进行消耗，可以不考虑consumed数据的处理，此时consumed中两个数据都应该为0
     */
    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return mScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }


    /**
     * 在dispatchNestedPreScroll 之后进行调用
     * 当滑动的距离父控件消耗后，父控件将剩余的距离再次交个子控件，
     * 子控件再次消耗部分距离后，又继续将剩余的距离分发给父控件,由父控件判断是否消耗剩下的距离。
     * 如果四个消耗的距离都是0，则表示没有神可以消耗的了，会直接返回false，否则会调用父控件的
     * onNestedScroll 方法，父控件继续消耗剩余的距离
     * 会调用父控件的
     *
     * @param dxConsumed     水平方向嵌套滑动的子控件滑动的距离(消耗的距离)    dx<0 向右滑动 dx>0 向左滑动 （保持和 RecycleView 一致）
     * @param dyConsumed     垂直方向嵌套滑动的子控件滑动的距离(消耗的距离)    dy<0 向下滑动 dy>0 向上滑动 （保持和 RecycleView 一致）
     * @param dxUnconsumed   水平方向嵌套滑动的子控件未滑动的距离(未消耗的距离)dx<0 向右滑动 dx>0 向左滑动 （保持和 RecycleView 一致）
     * @param dyUnconsumed   垂直方向嵌套滑动的子控件未滑动的距离(未消耗的距离)dy<0 向下滑动 dy>0 向上滑动 （保持和 RecycleView 一致）
     * @param offsetInWindow 子控件在当前window的偏移量
     * @return 如果返回true, 表示父控件又继续消耗了
     */
    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return mScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    /**
     * 子控件停止滑动，例如手指抬起，惯性滑动结束
     *
     * @param type 停止滑动的类型 TYPE_TOUCH，TYPE_NON_TOUCH
     */
    @Override
    public void stopNestedScroll(int type) {
        mScrollingChildHelper.stopNestedScroll(type);
    }


    /**
     * 设置当前子控件是否支持嵌套滑动，如果不支持，那么父控件是不能够响应嵌套滑动的
     *
     * @param enabled true 支持
     */
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    /**
     * 当前子控件是否支持嵌套滑动
     */
    @Override
    public boolean isNestedScrollingEnabled() {
        return mScrollingChildHelper.isNestedScrollingEnabled();
    }

    /**
     * 判断当前子控件是否拥有嵌套滑动的父控件
     */
    @Override
    public boolean hasNestedScrollingParent(int type) {
        return mScrollingChildHelper.hasNestedScrollingParent(type);
    }


    private VelocityTracker mVelocityTracker;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        cancleFling();//停止惯性滑动
        if (lastX == -1 || lastY == -1) {
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
        }

        //添加速度检测器，用于处理fling效果
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {//当手指按下
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                //即将开始滑动，支持垂直方向的滑动
                if (mOrientation == VERTICAL) {
                    //此方法确定开始滑动的方向和类型，为垂直方向，触摸滑动
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, TYPE_TOUCH);
                } else {
                    startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL, TYPE_TOUCH);

                }
                break;
            }
            case MotionEvent.ACTION_MOVE://当手指滑动
                int currentY = (int) (event.getRawY());
                int currentX = (int) (event.getRawX());
                int dy = lastY - currentY;
                int dx = lastX - currentX;
                //即将开始滑动，在开始滑动前，先通知父控件，确认父控件是否需要先消耗一部分滑动
                //true 表示需要先消耗一部分
                if (dispatchNestedPreScroll(dx, dy, consumed, offset, TYPE_TOUCH)) {
                    //如果父控件需要消耗，则处理父控件消耗的部分数据
                    dy -= consumed[1];
                    dx -= consumed[0];
                }
                //剩余的自己再次消耗，
                int consumedX = 0, consumedY = 0;
                if (mOrientation == VERTICAL) {
                    consumedY = childConsumedY(dy);
                } else {
                    consumedX = childConsumeX(dx);
                }
                //子控件的滑动事件处理完成之后，剩余的再次传递给父控件，让父控件进行消耗
                //因为没有滑动事件，因此次数自己滑动距离为0，剩余的再次全部还给父控件
                dispatchNestedScroll(consumedX, consumedY, dx - consumedX, dy - consumedY, null, TYPE_TOUCH);
                lastY = currentY;
                lastX = currentX;
                break;

            case MotionEvent.ACTION_UP:  //当手指抬起的时，结束嵌套滑动传递,并判断是否产生了fling效果
            case MotionEvent.ACTION_CANCEL:  //取消的时候，结束嵌套滑动传递,并判断是否产生了fling效果
                //触摸滑动停止
                stopNestedScroll(TYPE_TOUCH);

                //开始判断是否需要惯性滑动
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                int xvel = (int) mVelocityTracker.getXVelocity();
                int yvel = (int) mVelocityTracker.getYVelocity();
                fling(xvel, yvel);
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                }
                lastY = -1;
                lastX = -1;
                break;


        }

        return true;
    }

    private boolean fling(int velocityX, int velocityY) {
        //判断速度是否足够大。如果够大才执行fling
        if (Math.abs(velocityX) < mMinFlingVelocity) {
            velocityX = 0;
        }
        if (Math.abs(velocityY) < mMinFlingVelocity) {
            velocityY = 0;
        }
        if (velocityX == 0 && velocityY == 0) {
            return false;
        }
        //通知父控件，开始进行惯性滑动
        if (mOrientation == VERTICAL) {
            //此方法确定开始滑动的方向和类型，为垂直方向，触摸滑动
            startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH);
        } else {
            startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL, ViewCompat.TYPE_NON_TOUCH);
        }

        velocityX = Math.max(-mMaxFlingVelocity, Math.min(velocityX, mMaxFlingVelocity));
        velocityY = Math.max(-mMaxFlingVelocity, Math.min(velocityY, mMaxFlingVelocity));
        //开始惯性滑动
        doFling(velocityX, velocityY);
        return true;

    }

    private int mLastFlingX;
    private int mLastFlingY;
    private final int[] mScrollConsumed = new int[2];

    /**
     * 实际的fling处理效果
     */
    private void doFling(int velocityX, int velocityY) {
        fling = true;
        mScroller.fling(0, 0, velocityX, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset() && fling) {
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            int dx = mLastFlingX - x;
            int dy = mLastFlingY - y;
            mLastFlingX = x;
            mLastFlingY = y;
            //在子控件处理fling之前，先判断父控件是否消耗
            if (dispatchNestedPreScroll(dx, dy, mScrollConsumed, null, ViewCompat.TYPE_NON_TOUCH)) {
                //计算父控件消耗后，剩下的距离
                dx -= mScrollConsumed[0];
                dy -= mScrollConsumed[1];
            }
            //因为之前默认向父控件传递的竖直方向，所以这里子控件也消耗剩下的竖直方向
            int hResult = 0;
            int vResult = 0;
            int leaveDx = 0;//子控件水平fling 消耗的距离
            int leaveDy = 0;//父控件竖直fling 消耗的距离

            //在父控件消耗完之后，子控件开始消耗
            if (dx != 0) {
                leaveDx = childFlingX(dx);
                hResult = dx - leaveDx;//得到子控件消耗后剩下的水平距离
            }
            if (dy != 0) {
                leaveDy = childFlingY(dy);//得到子控件消耗后剩下的竖直距离
                vResult = dy - leaveDy;
            }
            //将最后剩余的部分，再次还给父控件
            dispatchNestedScroll(leaveDx, leaveDy, hResult, vResult, null, ViewCompat.TYPE_NON_TOUCH);
            postInvalidate();
        } else {
            stopNestedScroll(ViewCompat.TYPE_NON_TOUCH);
            cancleFling();
        }
    }

    private void cancleFling() {
        fling = false;
        mLastFlingX = 0;
        mLastFlingY = 0;
    }


    /**
     * 判断子子控件是否能够滑动，只有能滑动才能处理fling
     */
    private boolean canScroll() {
        //具体逻辑自己实现
        return true;
    }

    /**
     * 子控件消耗多少竖直方向上的fling,由子控件自己决定
     *
     * @param dy 父控件消耗部分竖直fling后,剩余的距离
     * @return 子控件竖直fling，消耗的距离
     */
    private int childFlingY(int dy) {

        return 0;
    }

    /**
     * 子控件消耗多少竖直方向上的fling,由子控件自己决定
     *
     * @param dx 父控件消耗部分水平fling后,剩余的距离
     * @return 子控件水平fling，消耗的距离
     */
    private int childFlingX(int dx) {
        return 0;
    }

    /**
     * 触摸滑动时候子控件消耗多少竖直方向上的 ,由子控件自己决定
     *
     * @param dy 父控件消耗部分竖直fling后,剩余的距离
     * @return 子控件竖直fling，消耗的距离
     */
    private int childConsumedY(int dy) {

        return 0;
    }

    /**
     * 触摸滑动子控件消耗多少竖直方向上的,由子控件自己决定
     *
     * @param dx 父控件消耗部分水平fling后,剩余的距离
     * @return 子控件水平fling，消耗的距离
     */
    private int childConsumeX(int dx) {
        return 0;
    }


}