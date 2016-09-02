package com.mixiaoxiao.overscroll.sample.withfastscroll;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.mixiaoxiao.fastscroll.FastScrollDelegate;
import com.mixiaoxiao.fastscroll.FastScrollDelegate.FastScrollable;
import com.mixiaoxiao.overscroll.OverScrollDelegate;
import com.mixiaoxiao.overscroll.OverScrollDelegate.OverScrollable;

public class FastAndOverScrollScrollView extends ScrollView implements FastScrollable, OverScrollable {

	private FastScrollDelegate mFastScrollDelegate;
	private OverScrollDelegate mOverScrollDelegate;

	// ===========================================================
	// Constructors
	// ===========================================================
	public FastAndOverScrollScrollView(Context context) {
		super(context);
		createDelegates(context);
	}

	public FastAndOverScrollScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		createDelegates(context);
	}

	public FastAndOverScrollScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		createDelegates(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public FastAndOverScrollScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		createDelegates(context);
	}

	// ===========================================================
	// createDelegates
	// ===========================================================
	private void createDelegates(Context context) {
		mFastScrollDelegate = new FastScrollDelegate.Builder(this).build();
		mOverScrollDelegate = new OverScrollDelegate(this);
	}

	// ===========================================================
	// Modify these 3 methods, others are same as source code.
	// ===========================================================
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mFastScrollDelegate.onInterceptTouchEvent(ev)) {
			return true;
		}
		if (mOverScrollDelegate.onInterceptTouchEvent(ev)) {
			return true;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mFastScrollDelegate.onTouchEvent(event)) {
			return true;
		}
		if (mOverScrollDelegate.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean superAwakenScrollBars() {
		// Just call mFastScrollDelegate.awakenScrollBars()
		// Do not call super
		return awakenScrollBars();
	}

	// ===========================================================
	// OverScrollDelegate
	// ===========================================================

	@Override
	public void draw(Canvas canvas) {
		mOverScrollDelegate.draw(canvas);
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
			int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		return mOverScrollDelegate.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY,
				maxOverScrollX, maxOverScrollY, isTouchEvent);
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

	// ===========================================================
	// FastScrollable
	// ===========================================================

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mFastScrollDelegate.onAttachedToWindow();
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (mFastScrollDelegate != null) {
			mFastScrollDelegate.onVisibilityChanged(changedView, visibility);
		}
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		mFastScrollDelegate.onWindowVisibilityChanged(visibility);
	}

	@Override
	protected boolean awakenScrollBars() {
		return mFastScrollDelegate.awakenScrollBars();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		mFastScrollDelegate.dispatchDrawOver(canvas);
	}

	@Override
	public View getFastScrollableView() {
		return this;
	}

	@Override
	public FastScrollDelegate getFastScrollDelegate() {
		return mFastScrollDelegate;
	}

	@Override
	public void setNewFastScrollDelegate(FastScrollDelegate newDelegate) {
		mFastScrollDelegate = newDelegate;
	}

}