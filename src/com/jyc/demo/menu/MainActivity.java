package com.jyc.demo.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	LinearLayout menusLayout;
	RelativeLayout mainLayout;
	Button handleView;

	ListView menusView;
	WebView web;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		menusLayout = (LinearLayout) findViewById(R.id.menu_parent);
		mainLayout = (RelativeLayout) findViewById(R.id.main);
		handleView = (Button) findViewById(R.id.main_handle);
		menusView = (ListView) findViewById(R.id.list_menu);
		web = (WebView)findViewById(R.id.web);
		web.loadUrl("http://www.baidu.com");
		new SlidingMenuController(this, handleView, menusLayout, mainLayout);

		menusView.setAdapter(ArrayAdapter.createFromResource(this,
				R.array.menus, android.R.layout.simple_list_item_1));
	}

}