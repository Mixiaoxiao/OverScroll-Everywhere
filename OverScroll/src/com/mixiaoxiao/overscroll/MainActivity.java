package com.mixiaoxiao.overscroll;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.mixiaoxiao.overscroll.style.LogoOverScrollStyle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OverScrollScrollView scrollView = (OverScrollScrollView) findViewById(R.id.overScrollScrollView1);
//        scrollView.getOverScrollDelegate().setOverScrollStyle(new Miui8OverScrollStyle());
       Drawable logo = ContextCompat.getDrawable(this,R.drawable.github_32dp).mutate();
       logo.setColorFilter(0xffd3d3d7, PorterDuff.Mode.SRC_IN);
        scrollView.getOverScrollDelegate().setOverScrollStyle(new LogoOverScrollStyle(logo));
//       scrollView.getOverScrollDelegate().setOverScrollStyle(new WebHostOverScrollStyle() {
//		
//		@Override
//		public String formatUrlHost(String url) {
//			// TODO Auto-generated method stub
//			return "aaaa";
//		}
//	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
