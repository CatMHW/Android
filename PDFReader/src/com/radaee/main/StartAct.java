package com.radaee.main;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.SettingNotFoundException;
import android.view.Window;
import android.view.WindowManager;

import com.radaee.reader.R; 
import com.radaee.reader.ScreenAdjust;
import com.reader.setting.Setting;

public class StartAct extends Activity{

	private ScreenAdjust mScreenAdjust =null;
	public static int pre_timeOut ;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		mScreenAdjust = new ScreenAdjust(this);
		try {
			pre_timeOut = mScreenAdjust.getTimeOut();
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Handler().postDelayed(new Runnable(){ 
			   
		         
		         public void run() { 
		             Intent mainIntent = new Intent(StartAct.this,SoftWareCup.class); 
		             StartAct.this.startActivity(mainIntent); 
		             StartAct.this.overridePendingTransition(R.anim.alpha_in,
							R.anim.alpha_out);
		             StartAct.this.finish(); 
		         } 
		            
		    }, 2000); 
		    
		
	}
}
