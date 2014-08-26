package com.reader.setting;

import com.radaee.main.SoftWareCup;
import com.radaee.reader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class Clothes extends Activity{

	private ImageButton mCornerChangeBtn = null;
	private ImageButton mLightChangeBtn = null;	
	private ImageButton mPaperChangeBtn = null;
	private ImageView mBack = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clothes);
		
		initView();
	}
	
	private void initView()
	{
		mBack = (ImageView)findViewById(R.id.back);		
		mBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Clothes.this.backToSetting();
				
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
		
		
		mLightChangeBtn = (ImageButton)findViewById(R.id.light_change);
		mLightChangeBtn.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				  if(event.getAction() == MotionEvent.ACTION_DOWN){     
                      //更改为按下时的背景图片     
                      v.setBackgroundResource(R.drawable.light_change_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //改为抬起时的图片     
	            	  	  SoftWareCup.CLOTHES = 0;
	                      v.setBackgroundResource(R.drawable.light_change);   
	                      Toast.makeText(Clothes.this, "皮肤更换成功", Toast.LENGTH_SHORT).show();
	              }     
				return false;
			}
		});
		
		mCornerChangeBtn = (ImageButton)findViewById(R.id.corner_change);
		mCornerChangeBtn.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //更改为按下时的背景图片     
                     v.setBackgroundResource(R.drawable.corner_change_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){ 
	            	  SoftWareCup.CLOTHES = 1;
	            	  //改为抬起时的图片     
	            	  v.setBackgroundResource(R.drawable.corner_change); 
	            	  Toast.makeText(Clothes.this, "皮肤更换成功", Toast.LENGTH_SHORT).show();
	                      
	              }  
				return false;
			}
		});
		
		mPaperChangeBtn = (ImageButton)findViewById(R.id.paper_change);
		mPaperChangeBtn.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //更改为按下时的背景图片     
                     v.setBackgroundResource(R.drawable.paper_change_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){ 
	            	  SoftWareCup.CLOTHES = 2;
	            	  //改为抬起时的图片     
	            	  v.setBackgroundResource(R.drawable.paper_change); 
	            	  Toast.makeText(Clothes.this, "皮肤更换成功", Toast.LENGTH_SHORT).show();
	                      
	              }  
				return false;
			}
		});
		
	}
	
	/**
	 * 返回设置界面
	 */
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
