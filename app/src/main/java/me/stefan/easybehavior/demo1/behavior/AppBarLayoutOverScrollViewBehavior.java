package me.stefan.easybehavior.demo1.behavior;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import me.stefan.easybehavior.widget.DisInterceptNestedScrollView;


/**
 * Created by gjm on 2017/5/24.
 * 目前包括的事件：
 * 图片放大回弹
 * 个人信息布局的top和botoom跟随图片位移
 * toolbar背景变色
 */
public class AppBarLayoutOverScrollViewBehavior extends AppBarLayout.Behavior {
    private static final String TAG = "overScroll";
    private static final String TAG_TOOLBAR = "toolbar";
    private static final String TAG_MIDDLE = "middle";
    private static final float TARGET_HEIGHT = 1500;
    private View mTargetView;
    private int mParentHeight;
    private int mTargetViewHeight;
    private float mTotalDy;
    private float mLastScale;
    private int mLastBottom;
    private boolean isAnimate;
    private Toolbar mToolBar;
    private ViewGroup middleLayout;//个人信息布局
    private int mMiddleHeight;
    private boolean isRecovering = false;//是否正在自动回弹中

    private final float MAX_REFRESH_LIMIT = 0.3f;//达到这个下拉临界值就开始刷新动画

    public AppBarLayoutOverScrollViewBehavior() {
    }

    public AppBarLayoutOverScrollViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        boolean handled = super.onLayoutChild(parent, abl, layoutDirection);

        if (mToolBar == null) {
            mToolBar = (Toolbar) parent.findViewWithTag(TAG_TOOLBAR);
        }
        if (middleLayout == null) {
            middleLayout = (ViewGroup) parent.findViewWithTag(TAG_MIDDLE);
        }
        // 需要在调用过super.onLayoutChild()方法之后获取
        if (mTargetView == null) {
            mTargetView = parent.findViewWithTag(TAG);
            if (mTargetView != null) {
                initial(abl);
            }
        }
        abl.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {


            @Override
            public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                mToolBar.setAlpha(Float.valueOf(Math.abs(i)) / Float.valueOf(appBarLayout.getTotalScrollRange()));

            }

        });
        return handled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes,int type) {
        isAnimate = true;
        if (target instanceof DisInterceptNestedScrollView) return true;//这个布局就是middleLayout
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes,type);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        if (!isRecovering) {
            if (mTargetView != null && ((dy < 0 && child.getBottom() >= mParentHeight)
                    || (dy > 0 && child.getBottom() > mParentHeight))) {
                scale(child, target, dy);
                return;
            }
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed,type);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        if (velocityY > 100) {//当y速度>100,就秒弹回
            isAnimate = false;
        }
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }


    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {
        recovery(abl);
        super.onStopNestedScroll(coordinatorLayout, abl, target,type);
    }

    private void initial(AppBarLayout abl) {
        abl.setClipChildren(false);
        mParentHeight = abl.getHeight();
        mTargetViewHeight = mTargetView.getHeight();
        mMiddleHeight = middleLayout.getHeight();
    }

    private void scale(AppBarLayout abl, View target, int dy) {
        mTotalDy += -dy;
        mTotalDy = Math.min(mTotalDy, TARGET_HEIGHT);
        mLastScale = Math.max(1f, 1f + mTotalDy / TARGET_HEIGHT);
        ViewCompat.setScaleX(mTargetView, mLastScale);
        ViewCompat.setScaleY(mTargetView, mLastScale);
        mLastBottom = mParentHeight + (int) (mTargetViewHeight / 2 * (mLastScale - 1));
        abl.setBottom(mLastBottom);
        target.setScrollY(0);

        middleLayout.setTop(mLastBottom - mMiddleHeight);
        middleLayout.setBottom(mLastBottom);

        if (onProgressChangeListener != null) {
            float progress = Math.min((mLastScale - 1) / MAX_REFRESH_LIMIT, 1);//计算0~1的进度
            onProgressChangeListener.onProgressChange(progress, false);
        }

    }

    public interface onProgressChangeListener {
        /**
         * 范围 0~1
         *
         * @param progress
         * @param isRelease 是否是释放状态
         */
        void onProgressChange(float progress, boolean isRelease);
    }

    public void setOnProgressChangeListener(AppBarLayoutOverScrollViewBehavior.onProgressChangeListener onProgressChangeListener) {
        this.onProgressChangeListener = onProgressChangeListener;
    }

    onProgressChangeListener onProgressChangeListener;

    private void recovery(final AppBarLayout abl) {
        if (isRecovering) return;
        if (mTotalDy > 0) {
            isRecovering = true;
            mTotalDy = 0;
            if (isAnimate) {
                ValueAnimator anim = ValueAnimator.ofFloat(mLastScale, 1f).setDuration(200);
                anim.addUpdateListener(
                        new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {

                                float value = (float) animation.getAnimatedValue();
                                ViewCompat.setScaleX(mTargetView, value);
                                ViewCompat.setScaleY(mTargetView, value);
                                abl.setBottom((int) (mLastBottom - (mLastBottom - mParentHeight) * animation.getAnimatedFraction()));
                                middleLayout.setTop((int) (mLastBottom -
                                        (mLastBottom - mParentHeight) * animation.getAnimatedFraction() - mMiddleHeight));

                                if (onProgressChangeListener != null) {
                                    float progress = Math.min((value - 1) / MAX_REFRESH_LIMIT, 1);//计算0~1的进度
                                    onProgressChangeListener.onProgressChange(progress, true);
                                }
                            }
                        }
                );
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isRecovering = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                anim.start();
            } else {
                ViewCompat.setScaleX(mTargetView, 1f);
                ViewCompat.setScaleY(mTargetView, 1f);
                abl.setBottom(mParentHeight);
                middleLayout.setTop(mParentHeight - mMiddleHeight);
//                middleLayout.setBottom(mParentHeight);
                isRecovering = false;

                if (onProgressChangeListener != null)
                    onProgressChangeListener.onProgressChange(0, true);
            }
        }
    }


}