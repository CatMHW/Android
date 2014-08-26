package com.reader.setting;

import org.apache.poi.hssf.util.HSSFColor.GOLD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.radaee.main.SoftWareCup;
import com.radaee.reader.GlobalData;
import com.radaee.reader.R;
import com.reader.setting.SlipButton.OnChangedListener;

public class Share extends Activity{

	private SlipButton mBtn1 = null;
	private ImageView mBack = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);
		
		findView();
		setListener();
		
	}

	 /**
     * 初始化控件
     */
    private void findView()
    {
    	//夜间模式按钮
        mBtn1 = (SlipButton) findViewById(R.id.splitbutton);     
        mBtn1.setCheck(GlobalData.sms_send);
        
        mBack = (ImageView)findViewById(R.id.back);	
        
    }
    
    /**
     * 设置监听
     */
    private void setListener()
    {
        mBtn1.SetOnChangedListener(new OnChangedListener() 
        {
			
			public void OnChanged(boolean CheckState) {
				// TODO Auto-generated method stub
				GlobalData.sms_send = CheckState;				
			}
		});           
        
        	
		mBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Share.this.backToSetting();
				
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
