package com.mixiaoxiao.overscroll.style;

import android.graphics.Canvas;
import android.view.View;

import com.mixiaoxiao.overscroll.OverScrollDelegate.OverScrollStyle;


public class Miui8OverScrollStyle extends OverScrollStyle{
	
	final float scaleRate = 0.2f;
	@Override
	public float scaleFlingOverScrollDelatY(int deltaY) {
		return deltaY * 2f;
	}
	
	@Override
	public void transformOverScrollCanvas(float offsetY, Canvas canvas, View view) {
		final int viewHeight = view.getHeight();
		final int viewWidth = view.getWidth();
		//scaleY ,depends on viewWidth.
		final float scaleY = (Math.abs(offsetY * scaleRate) + viewWidth ) / viewWidth ;
		canvas.scale(1, scaleY, viewWidth /2f, offsetY >= 0 ? 0 : (viewHeight + view.getScrollY()));
	}

}
