package com.mixiaoxiao.overscroll.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.mixiaoxiao.overscroll.OverScrollDelegate.OverScrollStyle;

public class LogoOverScrollStyle extends OverScrollStyle{
	
	
	private final Drawable logo;
	private final int logoWidth;
	private final int logoHeight;
	private final int logoMarginBottom; 
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Rect bounds = new Rect();
	
	public LogoOverScrollStyle(Drawable logo){
		this(logo, 32, 32,24, 0xffeaeaf0);
	}
	
	
	public LogoOverScrollStyle(Drawable logo, int logoWidthDp, int logoHeightDp, int logoMarginBottomDp, int backgroundColor) {
		super();
		this.logo = logo;
		this.logoWidth = dp2px(logoWidthDp);
		this.logoHeight = dp2px(logoHeightDp);
		this.logoMarginBottom = dp2px(logoMarginBottomDp);
		this.paint.setColor(backgroundColor);
		if(this.logo == null){
			throw new IllegalArgumentException("The logo should NOT be NULL.");
		}
	}
	
	@Override
	public void drawOverScrollTop(float offsetY, Canvas canvas, View view) {
		final int viewWidth = view.getWidth();
		bounds.left = 0;
		bounds.right = view.getWidth();
		bounds.top = 0;
		bounds.bottom = Math.round(offsetY * DEFAULT_DRAW_TRANSLATE_RATE);
		canvas.drawRect(bounds, paint);
		int left = viewWidth / 2 - logoWidth / 2;
		int right = viewWidth / 2 + logoWidth / 2;
		int bottom = bounds.bottom - logoMarginBottom;
		int top = bottom - logoHeight;
		logo.setBounds(left, top, right, bottom);
		logo.draw(canvas);
		
		
	}
	
	

}
