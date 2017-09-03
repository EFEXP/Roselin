package xyz.donot.roselin.view.custom

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class FixedViewPager : ViewPager {
	private var mStartDragX: Float = 130F
	var mListener: OnSwipeOutListener? = null

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


	override fun onTouchEvent(ev: MotionEvent): Boolean {
		try {
			return super.onTouchEvent(ev)
		} catch (ex: IllegalArgumentException) {

		}

		return false
	}

	override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
		try {

			val x = ev.x
			when (ev.action) {
				MotionEvent.ACTION_DOWN -> mStartDragX = x +  130F
				MotionEvent.ACTION_MOVE -> if (mStartDragX < x && currentItem == 0) {
					mListener?.onSwipeOutAtStart()
				} else if (mStartDragX > x && currentItem == adapter.count - 1) {
					mListener?.onSwipeOutAtEnd()
				}
			}

			return super.onInterceptTouchEvent(ev)
		} catch (ex: IllegalArgumentException) {

		}

		return false
	}

	interface OnSwipeOutListener {
		fun onSwipeOutAtStart()
		fun onSwipeOutAtEnd()
	}
}
