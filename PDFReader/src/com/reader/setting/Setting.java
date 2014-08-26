package com.reader.setting;

import java.util.ArrayList;
import java.util.HashMap;

import com.radaee.main.SoftWareCup;
import com.radaee.reader.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Setting extends Activity {

	private CornerListView mListView = null;
	private CornerListView mListView1 = null;
	private ArrayList<HashMap<String, String>> map_list1 = null;
	private ArrayList<HashMap<String, String>> map_list2 = null;
	
	private ImageView mBack = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		initView();
		
		getDataSource1();
		getDataSource2();

		SimpleAdapter adapter1 = new SimpleAdapter(this, map_list1,
				R.layout.simple_list_item_1, new String[] { "item" },
				new int[] { R.id.item_title });
		mListView = (CornerListView) findViewById(R.id.list1);
		mListView.setAdapter(adapter1);
		mListView.setOnItemClickListener(new OnItemListSelectedListener());
		
		SimpleAdapter adapter2 = new SimpleAdapter(this, map_list2,
				R.layout.simple_list_item_1, new String[] { "item" },
				new int[] { R.id.item_title });
		mListView1 = (CornerListView) findViewById(R.id.list2);
		mListView1.setAdapter(adapter2);
		mListView1.setOnItemClickListener(new OnItemListSelectedListener1());
	}

	private void initView()
	{
		mBack = (ImageView)findViewById(R.id.back1);
		mBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Setting.this.backToMain();
				
			}
		});
		mBack.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //更改为按下时的背景图片     
                     v.setBackgroundResource(R.drawable.back1_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //改为抬起时的图片     
	                      v.setBackgroundResource(R.drawable.back1);   
	                      
	              }     
				return false;
			}
		});
	}
	
	public ArrayList<HashMap<String, String>> getDataSource1() {

		map_list1 = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		HashMap<String, String> map2 = new HashMap<String, String>();
		HashMap<String, String> map3 = new HashMap<String, String>();

		map1.put("item", "分享设置");
		map2.put("item", "阅读设置");
		map3.put("item", "更换皮肤");
		

		map_list1.add(map1);
		map_list1.add(map2);
		map_list1.add(map3);

		return map_list1;
	}

	public ArrayList<HashMap<String, String>> getDataSource2() 
	{

		map_list2 = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		HashMap<String, String> map2 = new HashMap<String, String>();

		map1.put("item", "版本信息");
		map2.put("item", "关于...");

		map_list2.add(map1);
		map_list2.add(map2);

		return map_list2;
	}
	
	class OnItemListSelectedListener implements OnItemClickListener {
	
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			switch(position)
			{
			case 0:
				Intent intent2 = new Intent(Setting.this, Share.class);
				Setting.this.startActivity(intent2);				
				Setting.this.finish();
				Setting.this.overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				break;
			case 1:
				Intent intent1 = new Intent(Setting.this, Reading.class);
				Setting.this.startActivity(intent1);				
				Setting.this.finish();
				Setting.this.overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				break;
			case 2:
				Intent intent = new Intent(Setting.this, Clothes.class);
				Setting.this.startActivity(intent);				
				Setting.this.finish();
				Setting.this.overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);				
				break;
			}
		}
	}
	
	class OnItemListSelectedListener1 implements OnItemClickListener {

		
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			if (position == 0) {
				Intent intent1 = new Intent(Setting.this, Version.class);
				Setting.this.startActivity(intent1);				
				Setting.this.finish();
				Setting.this.overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}else{
				Intent intent2 = new Intent(Setting.this, About.class);
				Setting.this.startActivity(intent2);				
				Setting.this.finish();
				Setting.this.overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{	
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{			
			backToMain();
		}
		return false;	
	}
	
	private void backToMain()
	{
		Intent intent = new Intent(this, SoftWareCup.class);
		this.startActivity(intent);
		this.finish();
		
		this.overridePendingTransition(R.anim.push_down_in,
				R.anim.push_down_out);
	}
}
