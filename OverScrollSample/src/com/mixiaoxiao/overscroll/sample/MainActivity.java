package com.mixiaoxiao.overscroll.sample;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OverScrollRecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.mixiaoxiao.overscroll.OverScrollDelegate.OverScrollStyle;
import com.mixiaoxiao.overscroll.OverScrollDelegate.OverScrollable;
import com.mixiaoxiao.overscroll.OverScrollGridView;
import com.mixiaoxiao.overscroll.OverScrollListView;
import com.mixiaoxiao.overscroll.OverScrollScrollView;
import com.mixiaoxiao.overscroll.OverScrollWebView;
import com.mixiaoxiao.overscroll.sample.R;
import com.mixiaoxiao.overscroll.style.LogoOverScrollStyle;
import com.mixiaoxiao.overscroll.style.Miui8OverScrollStyle;
import com.mixiaoxiao.overscroll.style.WebHostOverScrollStyle;
import com.mixiaoxiao.recyclerview.decoration.LinearDividerItemDecoration;
import com.mixiaoxiao.recyclerview.quickadapter.BaseQuickAdapter;
import com.mixiaoxiao.recyclerview.quickadapter.QuickAdapterInterface.SectionQuickAdapterCallback;
import com.mixiaoxiao.recyclerview.quickadapter.QuickViewHolder;
import com.mixiaoxiao.recyclerview.quickadapter.SectionQuickAdapter;

public class MainActivity extends Activity {

	private static final String GITHUB_URL = "https://github.com/Mixiaoxiao/OverScroll-Everywhere";
	OverScrollRecyclerView recyclerView;
	OverScrollWebView webView;
	OverScrollListView listView;
	ScrollView scrollView;
	OverScrollGridView gridView;
	View currentVisibleView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// RecyclerView, with DefaultOverScrollStyle
		recyclerView = (OverScrollRecyclerView) findViewById(R.id.overScrollRecyclerView1);
		currentVisibleView = recyclerView;
		
		// ScrollView, with LogoOverScrollStyle
		scrollView = (ScrollView) findViewById(R.id.overScrollScrollView1);
		Drawable logo = ContextCompat.getDrawable(this, R.drawable.github_32dp).mutate();
		logo.setColorFilter(0xffd3d3d7, PorterDuff.Mode.SRC_IN);
		((OverScrollable)scrollView).getOverScrollDelegate().setOverScrollStyle(new LogoOverScrollStyle(logo));

		// WebView, with WebHostOverScrollStyle
		webView = (OverScrollWebView) findViewById(R.id.overScrollWebView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(GITHUB_URL);
		webView.getOverScrollDelegate().setOverScrollStyle(new WebHostOverScrollStyle() {
			@Override
			public String formatUrlHost(String url) {
				try {
					return "Provided by " + new URL(url).getHost();
				} catch (Exception e) {
				}
				return "";
			}
		});

		// ListView, with Miui8OverScrollStyle
		listView = (OverScrollListView) findViewById(R.id.overScrollListView1);
		ArrayList<String> listViewData = new ArrayList<String>(Arrays.asList(Cheeses.sCheeseStrings));
		listViewData.add(0, "OverScrollListView with Miui8OverScrollStyle");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem_sample, listViewData);
		listView.setAdapter(adapter);
		listView.getOverScrollDelegate().setOverScrollStyle(new Miui8OverScrollStyle());

		
		// GridView, the content can not fill the parent.
		gridView = (OverScrollGridView) findViewById(R.id.overScrollGridView1);
		ArrayList<String> gridViewData = new ArrayList<String>();
		gridViewData.add("OverScrollGridView");
		gridViewData.add("DefaultOverScrollStyle");
		gridViewData.add("Content can not");
		gridViewData.add("fill the parent");
		gridViewData.add("Item 0");
		gridViewData.add("Item 1");
		gridViewData.add("Item 2");
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.listitem_sample, gridViewData);
		gridView.setAdapter(adapter1);
		initRecyclerView();
		RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				View targetVisibleView = null;
				switch (checkedId) {
				case R.id.button_recyclerview:
					targetVisibleView = recyclerView;
					break;
				case R.id.button_scrollview:
					targetVisibleView = scrollView;
					break;
				case R.id.button_webview:
					targetVisibleView = webView;
					break;
				case R.id.button_listview:
					targetVisibleView = listView;
					break;
				case R.id.button_gridview:
					targetVisibleView = gridView;
					break;
				}
				if (targetVisibleView != currentVisibleView) {
					currentVisibleView.setVisibility(View.GONE);
					targetVisibleView.setVisibility(View.VISIBLE);
					currentVisibleView = targetVisibleView;
				}
			}
		};
		((RadioGroup) findViewById(R.id.radiogroup)).setOnCheckedChangeListener(listener);

	}

	private void initRecyclerView() {
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.addItemDecoration(new LinearDividerItemDecoration(this));
		HashSet<Character> set = new HashSet<Character>();
		for (String cheese : Cheeses.sCheeseStrings) {
			set.add(cheese.charAt(0));
		}
		final int sectionCount = set.size();
		SectionQuickAdapterCallback<String> callback = new SectionQuickAdapterCallback<String>() {
			@Override
			public void onBindQuickViewHolderTypeSection(SectionQuickAdapter<String> adapter, QuickViewHolder holder,
					long sectionId) {
				int id = (int) sectionId;
				char a = (char) id;
				holder.setText(0, "" + a);
			}

			@Override
			public int[] getVariableViewsTypeSection(SectionQuickAdapter<String> adapter, QuickViewHolder holder) {
				return new int[] { R.id.section_title };
			}

			@Override
			public long getItemSectionId(SectionQuickAdapter<String> adapter, int dataPosition) {
				return adapter.getItemData(dataPosition).charAt(0);
			}

			@Override
			public int getItemSectionCount(SectionQuickAdapter<String> adapter) {
				return sectionCount;
			}

			@Override
			public int[] getVariableViewsTypeData(BaseQuickAdapter<String> adapter, QuickViewHolder holder) {
				return new int[] { R.id.item_sample_title };
			}

			@Override
			public void onBindQuickViewHolderTypeData(BaseQuickAdapter<String> adapter, QuickViewHolder holder,
					String itemData, int dataPosition) {
				holder.setText(0, itemData);
			}

			@Override
			public void onItemClick(BaseQuickAdapter<String> adapter, QuickViewHolder holder, int dataPosition,
					String itemData) {
				toast("onItemClick dataPosition->" + dataPosition + "\nitemData->" + itemData);
			}

			@Override
			public void onSectionItemClick(SectionQuickAdapter<String> adapter, QuickViewHolder holder,
					long itemSectionId) {
				int id = (int) itemSectionId;
				char a = (char) id;
				toast("onSectionItemClick itemSectionId->" + itemSectionId + "\nsectionName->" + a);

			}

		};
		final SectionQuickAdapter<String> adapter = new SectionQuickAdapter<String>(this, R.layout.recycleritem_sample,
				R.layout.recycleritem_sample_section, new ArrayList<String>(Arrays.asList(Cheeses.sCheeseStrings)),
				callback);
		recyclerView.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.action_github) {
			final String url = GITHUB_URL;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(Intent.createChooser(intent, url));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
