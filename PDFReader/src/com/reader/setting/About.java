package com.reader.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.radaee.reader.R;

public class About extends Activity{

	private ImageView mBack = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us);
		
		findView();
		setListener();		
	}
	
	public void findView()
	{
		 mBack = (ImageView)findViewById(R.id.back);	
	}
	
	private void setListener()
	{
		mBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				About.this.backToSetting();
				
			}
		});
		mBack.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //更改为按下时的背景图片     
                     v.setBackgroundResource(R.drawable.back_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //改为抬起时的图片     
	                      v.setBackgroundResource(R.drawable.back);   	                      
	              }     
				return false;
			}
		});
	}
	
	private void backToSetting()
	{
		Intent intent = new Intent(this, Setting.class);
		this.startActivity(intent);
		this.finish();
		
		this.overridePendingTransition(R.anim.push_right_in,
				R.anim.push_right_out);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{	
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{			
			backToSetting();
		}
		return false;	
	}

}
