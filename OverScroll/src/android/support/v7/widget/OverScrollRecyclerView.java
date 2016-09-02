package android.support.v7.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mixiaoxiao.overscroll.OverScrollDelegate;
import com.mixiaoxiao.overscroll.OverScrollDelegate.OverScrollable;

/**
 * https://github.com/Mixiaoxiao/OverScroll-Everywhere
 * We need to override the package-method "absorbGlows",
 * so this class is placed in package "android.support.v7.widget"
 * @author Mixiaoxiao 2016-08-31
 */
public class OverScrollRecyclerView extends RecyclerView implements OverScrollable {

	private OverScrollDelegate mOverScrollDelegate;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OverScrollRecyclerView(Context context) {
		super(context);
		createOverScrollDelegate(context);
	}

	public OverScrollRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		createOverScrollDelegate(context);
	}

	public OverScrollRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createOverScrollDelegate(context);
	}

	// ===========================================================
	// createOverScrollDelegate
	// ===========================================================

	private void createOverScrollDelegate(Context context) {
		mOverScrollDelegate = new OverScrollDelegate(this);
		setOverScrollMode(OVER_SCROLL_ALWAYS);
	}

	// ===========================================================
	// Delegate
	// ===========================================================

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mOverScrollDelegate.onInterceptTouchEvent(ev)) {
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mOverScrollDelegate.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void draw(Canvas canvas) {
		mOverScrollDelegate.draw(canvas);
	}

	// @Override
	// protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int
	// scrollY, int scrollRangeX,
	// int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean
	// isTouchEvent) {
	// return mOverScrollDelegate.overScrollBy(deltaX, deltaY, scrollX, scrollY,
	// scrollRangeX, scrollRangeY,
	// maxOverScrollX, maxOverScrollY, isTouchEvent);
	// }

	@Override
	void absorbGlows(int velocityX, int velocityY) {
		mOverScrollDelegate.recyclerViewAbsorbGlows(velocityX, velocityY);
	}

	// ===========================================================
	// OverScrollable, aim to call view internal methods
	// ===========================================================

	@Override
	public int superComputeVerticalScrollExtent() {
		return super.computeVerticalScrollExtent();
	}

	@Override
	public int superComputeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	@Override
	public int superComputeVerticalScrollRange() {
		return super.computeVerticalScrollRange();
	}

	@Override
	public void superOnTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
	}

	@Override
	public void superDraw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	public boolean superAwakenScrollBars() {
		return super.awakenScrollBars();
	}

	@Override
	public boolean superOverScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
			int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				maxOverScrollY, isTouchEvent);
	}

	@Override
	public View getOverScrollableView() {
		return this;
	}

	@Override
	public OverScrollDelegate getOverScrollDelegate() {
		return mOverScrollDelegate;
	}

}
