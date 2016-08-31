package com.mixiaoxiao.overscroll;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

public class OverScrollDelegate {

	static final String LOG_TAG = "OverScrollDelegate";

	public static interface OverScrollable {
		public int superComputeVerticalScrollExtent();

		public int superComputeVerticalScrollOffset();

		public int superComputeVerticalScrollRange();

		public boolean superOnTouchEventOfOverScrollable(MotionEvent event);

		public void superDraw(Canvas canvas);

		public boolean superAwakenScrollBars();

		public boolean superOverScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
				int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);

		public View getOverScrollableView();

		public OverScrollDelegate getOverScrollDelegate();
	}

	public static abstract class OverScrollStyle { 

		public static final float DEFAULT_DRAW_TRANSLATE_RATE = 0.33f;
		public static final float DEFAULT_FLING_DELTA_RATE = 2.0f; 
		public final static float SYSTEM_DENSITY = Resources.getSystem().getDisplayMetrics().density;

		public float scaleFlingOverScrollDelatY(int deltaY) {
			return deltaY * DEFAULT_FLING_DELTA_RATE;
		}

		public void transformOverScrollCanvas(float offsetY, Canvas canvas, View view) {
			final int translateY = Math.round(offsetY * DEFAULT_DRAW_TRANSLATE_RATE);
			canvas.translate(0, translateY);
		}

		public void drawOverScrollTop(float offsetY, Canvas canvas, View view) {
		};

		public void drawOverScrollBottom(float offsetY, Canvas canvas, View view) {
		};

		public final int getOverScrollViewCanvasBottom(View view) {
			return view.getHeight() + view.getScrollY();
		}
		public static final int dp2px(int dp){
			return (int)(dp * SYSTEM_DENSITY + 0.5f);
		}
	} 

	private static final OverScrollStyle sDefaultStyle = new OverScrollStyle() {
	};

	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};

	public static final int OS_NONE = 0;// OS = OverScroll
	public static final int OS_DRAG_TOP = 1;
	public static final int OS_DRAG_BOTTOM = 2;
	public static final int OS_FLING_OS = 3;
	public static final int OS_DRAG_SPRING_BACK = 4;
	public static final int OS_FLING_SPRING_BACK = 5;

	public static final int SPRING_BACK_DURATION = 300;

	private static final int INVALID_POINTER = -1;

	private int mState = OS_NONE;

	private final int mTouchSlop;
	private float mLastMotionY;
	// private float mLastMotionX;
	private float mOffsetY;
	private int mActivePointerId = INVALID_POINTER;

	private boolean mNeedCheckFlingBackWhenDraw = false;

	private final OverScroller mScroller;

	private final View mView;
	private OverScrollable mOverScrollable;

	private OverScrollStyle mStyle; 

	public OverScrollDelegate(OverScrollable overScrollable) {
		this.mView = overScrollable.getOverScrollableView();
		mView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		this.mOverScrollable = overScrollable;
		Context context = mView.getContext();
		//TouchSlop() / 2 to make TouchSlop "more sensible"
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop() / 2;
		mScroller = new OverScroller(context, sInterpolator);
		mStyle = sDefaultStyle;
	}

	public void setOverScrollStyle(OverScrollStyle style) {
		if (style == null) {
			throw new IllegalArgumentException("OverScrollStyle should NOT be NULL!");
		}
		mStyle = style;
	}

	private void springBack(boolean isDragEvent) {
		final int startScrollY = Math.round(mOffsetY);
		mScroller.startScroll(0, startScrollY, 0, -startScrollY, SPRING_BACK_DURATION);
		setState(isDragEvent ? OS_DRAG_SPRING_BACK : OS_FLING_SPRING_BACK);
		mView.invalidate();
	}

	public boolean onTouchEvent(MotionEvent ev) {
		// log("onTouchEvent->" + ev.toString());
		final int action = MotionEventCompat.getActionMasked(ev);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			//log("onTouchEvent -> ACTION_DOWN");
			mLastMotionY = ev.getY();
			// mLastMotionX = ev.getX();
			mActivePointerId = ev.getPointerId(0);
			if (mState == OS_DRAG_SPRING_BACK) {
				mOffsetY = mScroller.getCurrY();
				mScroller.abortAnimation();
				if (mOffsetY == 0) {
					setState(OS_NONE);
					mView.invalidate();
				} else {
					setState(mOffsetY > 0 ? OS_DRAG_TOP : OS_DRAG_BOTTOM);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE: {
			// log("onTouchEvent -> ACTION_MOVE "+ ev.toString());
			if (mActivePointerId == INVALID_POINTER) {
				Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
				break;
			}
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			if (pointerIndex < 0) {
				Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
				break;
			}
			final float y = MotionEventCompat.getY(ev, pointerIndex);
			// final float x = ev.getX(pointerIndex);
			final float yDiff = y - mLastMotionY;
			// final float xDiff = x - mLastMotionX;
			mLastMotionY = y;
			if (!isOsDrag()) {
				boolean canScrollUp, canScrollDown;
				final int offset = mOverScrollable.superComputeVerticalScrollOffset();
				final int range = mOverScrollable.superComputeVerticalScrollRange()
						- mOverScrollable.superComputeVerticalScrollExtent();
				if (range == 0) {
					canScrollDown = canScrollUp = false;
				} else {
					canScrollUp = offset > 0;
					canScrollDown = offset < (range - 1);
				}
				if (canScrollUp && canScrollDown) {
					break;
				}
				// mLastMotionX = x;
				if ((Math.abs(yDiff) > mTouchSlop)) {// && (Math.abs(yDiff) >
														// Math.abs(xDiff))){
					boolean isOs = false;
					if (!canScrollUp && yDiff > 0) {
						setState(OS_DRAG_TOP);
						isOs = true;
					} else if (!canScrollDown && yDiff < 0) {
						setState(OS_DRAG_BOTTOM);
						isOs = true;
					}
					if (isOs) {
						MotionEvent fakeCancelEvent = MotionEvent.obtain(ev);
						fakeCancelEvent.setAction(MotionEvent.ACTION_CANCEL);
						mOverScrollable.superOnTouchEventOfOverScrollable(fakeCancelEvent);
						fakeCancelEvent.recycle();
						mOverScrollable.superAwakenScrollBars();
						// 下面会return true;不会交给superOnTouchEventOfOverScrollable
						final ViewParent parent = mView.getParent();
						if (parent != null) {
							parent.requestDisallowInterceptTouchEvent(true);
						}
					}
				}
			}
			if (isOsDrag()) {
				// mLastMotionX = MotionEventCompat.getX(ev, pointerIndex);
				mOffsetY += yDiff;
				if (isOsTop()) {// mDragOffsetY应该是>0
					if (mOffsetY <= 0) {
						setState(OS_NONE);
						mOffsetY = 0;
						// 造一个DOWN事件交给super处理
						MotionEvent fakeDownEvent = MotionEvent.obtain(ev);
						fakeDownEvent.setAction(MotionEvent.ACTION_DOWN);
						mOverScrollable.superOnTouchEventOfOverScrollable(fakeDownEvent);
						fakeDownEvent.recycle();
						return true;
					}
				} else if (isOsBottom()) {
					if (mOffsetY >= 0) {
						setState(OS_NONE);
						mOffsetY = 0;
						MotionEvent fakeDownEvent = MotionEvent.obtain(ev);
						fakeDownEvent.setAction(MotionEvent.ACTION_DOWN);
						mOverScrollable.superOnTouchEventOfOverScrollable(fakeDownEvent);
						fakeDownEvent.recycle();
						return true;
					}
				}
				mView.invalidate();
				return true;
			}
			break;
		}
		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int index = MotionEventCompat.getActionIndex(ev);
			mLastMotionY = MotionEventCompat.getY(ev, index);
			// mLastMotionX = MotionEventCompat.getX(ev, index);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		}

		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			final int index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			if (index != -1) {
				mLastMotionY = MotionEventCompat.getY(ev, index);
				// mLastMotionX = MotionEventCompat.getX(ev, index);
			}
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			if (isOsDrag()) {
				springBack(true);
			}
			mActivePointerId = INVALID_POINTER;
		}
		}
		return mOverScrollable.superOnTouchEventOfOverScrollable(ev);
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
		}
	}

	public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		final boolean overScroll = mOverScrollable.superOverScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
		if (!overScroll) {
			if (mState == OS_FLING_OS) {
				springBack(false);
			}
		} else {// overScroll = true;
			if (!isTouchEvent) {// isTouchEvent=false，means fling
				mOffsetY += (-mStyle.scaleFlingOverScrollDelatY(deltaY));
				if (mOffsetY == 0) {
					setState(OS_NONE);
				} else {
					setState(OS_FLING_OS);
					mNeedCheckFlingBackWhenDraw = true;
				}
				mView.invalidate();
			}
		}
		return overScroll;

	}

	// =================
	// Internal
	// =================

	private void setState(int newState) {
		if (mState != newState) {
			mState = newState;
			// String newStateName = "";
			// if (mState == OS_NONE) {
			// newStateName = "OS_NONE";
			// } else if (mState == OS_DRAG_TOP) {
			// newStateName = "OS_TOP";
			// } else if (mState == OS_DRAG_BOTTOM) {
			// newStateName = "OS_BOTTOM";
			// } else if (mState == OS_DRAG_SPRING_BACK) {
			// newStateName = "OS_BACK";
			// } else if (mState == OS_FLING_OS) {
			// newStateName = "OS_FLING";
			// } else if(mState == OS_FLING_SPRING_BACK){
			// newStateName = "OS_FLING_SPRING_BACK";
			// }
			//log("setState->" + newStateName);
		}
	}

	private boolean isOsTop() {
		return mState == OS_DRAG_TOP;
	}

	private boolean isOsBottom() {
		return mState == OS_DRAG_BOTTOM;
	}

	private boolean isOsDrag() {
		return mState == OS_DRAG_TOP || mState == OS_DRAG_BOTTOM;
	}

	public void draw(Canvas canvas) {
		//log("draw mOffsetY->" + mOffsetY);
		if (mState == OS_NONE) {
			mOverScrollable.superDraw(canvas);
		} else {
			if (mState == OS_FLING_OS) {
				if (mNeedCheckFlingBackWhenDraw) {
					mNeedCheckFlingBackWhenDraw = false;
					ViewCompat.postInvalidateOnAnimation(mView);
				} else {
					springBack(false);
				}
			} else if (mState == OS_DRAG_SPRING_BACK || mState == OS_FLING_SPRING_BACK) {
				if (mScroller.computeScrollOffset()) {
					mOffsetY = mScroller.getCurrY();
					ViewCompat.postInvalidateOnAnimation(mView);
				} else {
					mOffsetY = 0;
					setState(OS_NONE);
				}
			}
			final int sc = canvas.save();
			mStyle.transformOverScrollCanvas(mOffsetY, canvas, mView);
			mOverScrollable.superDraw(canvas);
			canvas.restoreToCount(sc);
			
			final int sc1 = canvas.save();
			if (mOffsetY > 0) {// top
				mStyle.drawOverScrollTop(mOffsetY, canvas, mView);
			} else if (mOffsetY < 0) {// bottom
				mStyle.drawOverScrollBottom(mOffsetY, canvas, mView);
			}
			canvas.restoreToCount(sc1);
		}
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		Log.d(LOG_TAG, msg);
	}

}
