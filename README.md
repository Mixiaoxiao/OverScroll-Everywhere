OverScroll-Everywhere
===============

Add the over-scroll feature to any scrollable view: RecyclerView, ScrollView, WebView, ListView, GridView, etc. Support both fling and drag over-scroll，and easy to customize the over-scroll style.

为任意可滑动的View定制越界效果(over-scroll)，同时支持滑动惯性越界与拖动越界，方便地定制与扩展不同的越界风格。注：安卓本身使用EdgeEffect实现越界效果(边缘发亮)，安卓4.x为固定的holo_blue色，5.0+为半透明colorPrimary色。

![OverScroll-Everywhere](https://raw.github.com/Mixiaoxiao/OverScroll-Everywhere/master/Screenshots/OverScroll-Everywhere.png) 

Sample 
-----

[OverScroll-EverywhereSample.apk](https://raw.github.com/Mixiaoxiao/OverScroll-Everywhere/master/OverScroll-Everywhere-Sample.apk)

![GIF](https://raw.github.com/Mixiaoxiao/OverScroll-Everywhere/master/Screenshots/OverScroll-Everywhere.gif) 

Usage 
-----

* Use `OverScrollRecyclerView` `OverScrollScrollView`  `OverScrollWebView`  `OverScrollListView` `OverScrollGridView`  to replace the original one.

* Enable or disable fling/drag over-scroll
	```java
		OverScrollRecyclerView yourOverScrollRecyclerView = ...;
		boolean dragOverScroll = true | false;
		boolean flingOverScroll = true | false;
		yourOverScrollRecyclerView.getOverScrollDelegate().setOverScrollType(dragOverScroll, flingOverScroll);
	```

Style
-----

* Perset style
	```java
		//Show your logo at top
		Drawable yourLogo = ...;
		yourOverScrollRecyclerView.getOverScrollDelegate().setOverScrollStyle(new LogoOverScrollStyle(yourLogo));
		
		//Like MIUI8
		yourOverScrollRecyclerView.getOverScrollDelegate().setOverScrollStyle(new Miui8OverScrollStyle());
		
		//Show the host of webpage at top 
		yourOverScrollWebView.getOverScrollDelegate().setOverScrollStyle(new WebHostOverScrollStyle() {
			@Override
			public String formatUrlHost(String url) {
				try {
					return "Provided by " + new URL(url).getHost();
				} catch (Exception e) {
				}
				return "";
			}
		});
	```

* Customize your style
	```java
		OverScrollStyle yourOverScrollStyle = new OverScrollStyle() {
			/**
			 * Transform canvas before draw content, 
			 * for example, do canvas.translate
			 */
			public void transformOverScrollCanvas(float offsetY, Canvas canvas, View view) {
				final int translateY = Math.round(offsetY * DEFAULT_DRAW_TRANSLATE_RATE);
				canvas.translate(0, translateY);
			}

			/**
			 * Draw overscroll effect(e.g. logo) at top, the direction of offsetY is
			 * same as TouchEvent
			 */
			public void drawOverScrollTop(float offsetY, Canvas canvas, View view) {
				//draw someting
			};

			/**
			 * Draw overscroll effect(e.g. logo) at bottom, the direction of offsetY
			 * is same as TouchEvent
			 */
			public void drawOverScrollBottom(float offsetY, Canvas canvas, View view) {
				//draw someting
			};
		};
		yourOverScrollRecyclerView.getOverScrollDelegate().setOverScrollStyle(yourOverScrollStyle);
	```	
Use with FastScroll-Everywhere
-----

* Like this
	```java
		public class FastAndOverScrollScrollView extends ScrollView implements FastScrollable, OverScrollable {
			
			private FastScrollDelegate mFastScrollDelegate;
			private OverScrollDelegate mOverScrollDelegate;
			
			public FastAndOverScrollScrollView(Context context, AttributeSet attrs) {
				super(context, attrs);
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
			// Copy other methods from source code.
			// ===========================================================
		
		}
	
	```	
	
Extension (OverScroll-Everywhere!)
-----

* If you want to add the over-scroll feature to your `CustomScrollableView`, just copy the source code of `OverScrollScrollView`(or any `OverScrollXxxxView`) and change the super-class to your `CustomScrollableView`.
* Then, all done. All things are handled by `OverScrollDelegate`


Attention 
-----

* In RecyclerView, we override the package-method `absorbGlows` to get the velocity of fling overscroll. 
* In other views, we override the protected-method `overScrollBy` to compute the velocity of fling overscroll.

Developed By
------------

Mixiaoxiao(谜小小) - <xiaochyechye@gmail.com> or <mixiaoxiaogogo@163.com>



License
-----------

    Copyright 2016 Mixiaoxiao

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
