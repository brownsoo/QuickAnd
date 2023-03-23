package com.hansoolabs.and.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntDef
import androidx.coordinatorlayout.widget.CoordinatorLayout


/**
 * https://gist.githubusercontent.com/NikolaDespotoski/1d6fef4949eb9be05a46/raw/bd58b4f5b893efc9dd31589483cad94ada187b81/VerticalScrollingBehavior.java
 * Created by Nikola on 11/22/2015.
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class VerticalScrollingBehavior<V : View> : CoordinatorLayout.Behavior<V> {
    
    companion object {

        const val SCROLL_DIRECTION_UP = 1
        const val SCROLL_DIRECTION_DOWN = -1
        const val SCROLL_NONE = 0
    }

    @IntDef(SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN, SCROLL_NONE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ScrollDirection

    private var mTotalDyUnconsumed = 0
    private var mTotalDy = 0
    /*
       @return Overscroll direction: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN, SCROLL_NONE
   */
    @ScrollDirection
    @get:ScrollDirection
    var overScrollDirection = SCROLL_NONE
        private set
    /**
     * @return Scroll direction: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN, SCROLL_NONE
     */

    @ScrollDirection
    @get:ScrollDirection
    var scrollDirection = SCROLL_NONE
        private set

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    
    constructor() : super()
    
    /**
     * @param coordinatorLayout
     * @param child
     * @param direction         Direction of the overscroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     * @param currentOverScroll Unconsumed value, negative or positive based on the direction;
     * @param totalOverScroll   Cumulative value for current direction
     */
    abstract fun onNestedVerticalOverScroll(coordinatorLayout: CoordinatorLayout, child: V, @ScrollDirection direction: Int, currentOverScroll: Int, totalOverScroll: Int)

    /**
     * @param scrollDirection Direction of the overscroll: SCROLL_DIRECTION_UP, SCROLL_DIRECTION_DOWN
     */
    abstract fun onDirectionNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, @ScrollDirection scrollDirection: Int)
    
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean =
        nestedScrollAxes and View.SCROLL_AXIS_VERTICAL != 0
    
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
        if (dyUnconsumed > 0 && mTotalDyUnconsumed < 0) {
            mTotalDyUnconsumed = 0
            overScrollDirection = SCROLL_DIRECTION_UP
        } else if (dyUnconsumed < 0 && mTotalDyUnconsumed > 0) {
            mTotalDyUnconsumed = 0
            overScrollDirection = SCROLL_DIRECTION_DOWN
        }
        mTotalDyUnconsumed += dyUnconsumed
        onNestedVerticalOverScroll(coordinatorLayout, child, overScrollDirection, dyConsumed, mTotalDyUnconsumed)
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed)
        if (dy > 0 && mTotalDy < 0) {
            mTotalDy = 0
            scrollDirection = SCROLL_DIRECTION_UP
        } else if (dy < 0 && mTotalDy > 0) {
            mTotalDy = 0
            scrollDirection = SCROLL_DIRECTION_DOWN
        }
        mTotalDy += dy
        onDirectionNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, scrollDirection)
    }


    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
        scrollDirection = if (velocityY > 0) SCROLL_DIRECTION_UP else SCROLL_DIRECTION_DOWN
        return onNestedDirectionFling(coordinatorLayout, child, target, velocityX, velocityY, scrollDirection)
    }

    protected abstract fun onNestedDirectionFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float, @ScrollDirection scrollDirection: Int): Boolean

//    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float): Boolean =
//        super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
//
//    override fun onApplyWindowInsets(coordinatorLayout: CoordinatorLayout?, child: V?, insets: WindowInsetsCompat): WindowInsetsCompat =
//        super.onApplyWindowInsets(coordinatorLayout, child, insets)
//
//    override fun onSaveInstanceState(parent: CoordinatorLayout?, child: V?): Parcelable =
//        super.onSaveInstanceState(parent, child)

}