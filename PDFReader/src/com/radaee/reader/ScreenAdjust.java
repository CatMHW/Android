package com.radaee.reader;

import com.radaee.main.StartAct;

import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class ScreenAdjust
{
	//菜单状态：0表示未弹出，1表示已弹出
	private int menu_state = 0;
	private int bright_adjust_state = 0;
	
	//1表示自动调节，0表示手动调节
	private int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
	private int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
	private static int PRE_SCREEN_BRIGHTNESS_MODE = -1;
	
	private int brightValue = 0;						//屏幕的亮度值
	private int mTimeOut = 0;
	private int mTimeOutCheckedItem = 0;
	private static final int pre_timeOut = StartAct.pre_timeOut;
	
	private PopupWindow mPopMenu;						//自定义菜单
	private PopupWindow mPopBrightWin;					//亮度调节的进度条
	private View mLayout;								//自定义菜单的布局
	private View mBrightLayout;							//自定义亮度调节的布局
	private Activity ScreenActivity;
	
	private String mSleepItems[] = new String[]{"15秒","30秒","1分钟","2分钟","5分钟","10分钟"};
	
	public ScreenAdjust(Activity screen)
	{
		ScreenActivity = screen;
	}
	
	public ScreenAdjust()
	{
		
	}
	
//	/**
//	 * 菜单键和返回键的响应
//	 */
//	public boolean onKeyDown(int keyCode, KeyEvent event)
//	{
//		switch (keyCode)
//		{
//		case KeyEvent.KEYCODE_MENU:	
//			
//			//如果菜单已经弹出，不再弹出新的窗口
//			if(1 == menu_state)
//			{
//				return false;
//			}
//			
//			if(1 == bright_adjust_state)
//			{
//				mPopBrightWin.dismiss();
//			}
//			
//			//装载选项菜单布局文件
//			mLayout = ScreenActivity.getLayoutInflater().inflate(R.layout.menu_layout, null);
//			
//			//创建PopupWindow对象
//			mPopMenu = new PopupWindow(mLayout, ScreenActivity.getWindowManager()
//					.getDefaultDisplay().getWidth(), ScreenActivity.getWindowManager()
//					.getDefaultDisplay().getHeight());
//			
//			//设置弹出菜单窗口的位置
//			mPopMenu.showAtLocation(mLayout, Gravity.BOTTOM, 0, 0);
//			
//			//取出亮度调节和超时的view
//			View brightAdjust = mLayout.findViewById(R.id.brightLayout);
//			View sleepAdjust = mLayout.findViewById(R.id.sleepLayout);
//			
//			//为亮度调节和超时添加单击事件
//			brightAdjust.setOnClickListener(new brightAdjustClicked());	
//			sleepAdjust.setOnClickListener(new sleepAdjustClicked());
//			
//			menu_state = 1;
//			return false;
//
//		case KeyEvent.KEYCODE_BACK:
//			//如果打开了菜单，就关闭它
//			if(1 == menu_state)
//			{
//				mPopMenu.dismiss();
//				menu_state = 0;
//			}
//			else if (0 == menu_state)
//			{
//				//如果菜单未显示，或已经关闭，则直接关闭当前的Activity
//				ScreenActivity.finish();
//			}
//			return false;
//			
//		default:
//			return super.onKeyDown(keyCode, event);
//		}		
//	}
	
	/**
	 * 关闭菜单
	 * @return
	 */
	public boolean dismiss()
	{
		//如果打开了菜单，就关闭它
		if(1 == menu_state)
		{
			mPopMenu.dismiss();
			menu_state = 0;
		}
		else if(bright_adjust_state == 1)
		{
			mPopBrightWin.dismiss();
			bright_adjust_state = 0;
		}
		else if (0 == menu_state && bright_adjust_state ==0 )
		{
			//如果菜单未显示，或已经关闭，则直接关闭当前的Activity
			//ScreenActivity.finish();
		}
		return true;
	}
	
	/**
	 * @return 出现了返回false
	 */
	public boolean getBrightBarState()
	{
		return (menu_state == 0 && bright_adjust_state == 0);
	}

	/**
	 * 显示菜单
	 * @return
	 */
	public boolean showMenu()
	{
		//如果菜单已经弹出，不再弹出新的窗口
		if(1 == menu_state)
		{
			return false;
		}
		
		if(1 == bright_adjust_state)
		{
			mPopBrightWin.dismiss();
		}
		
		//装载选项菜单布局文件
		mLayout = ScreenActivity.getLayoutInflater().inflate(R.layout.menu_layout, null);
		
		//创建PopupWindow对象
		mPopMenu = new PopupWindow(mLayout, ScreenActivity.getWindowManager()
				.getDefaultDisplay().getWidth(), ScreenActivity.getWindowManager()
				.getDefaultDisplay().getHeight());
		
		//设置弹出菜单窗口的位置
		mPopMenu.showAtLocation(mLayout, Gravity.BOTTOM, 0, 0);
		
		//取出亮度调节和超时的view
		View brightAdjust = mLayout.findViewById(R.id.brightLayout);
		View sleepAdjust = mLayout.findViewById(R.id.sleepLayout);
		
		//为亮度调节和超时添加单击事件
		brightAdjust.setOnClickListener(new brightAdjustClicked());	
		sleepAdjust.setOnClickListener(new sleepAdjustClicked());
		
		menu_state = 1;
		return true;
	}
	
	/**
	 * @author CatM
	 * @超时的单击事件处理
	 */
	private class sleepAdjustClicked implements OnClickListener
	{
		//***@Override
		public void onClick(View v) 
		{
			mPopMenu.dismiss();
			if(bright_adjust_state == 1)
			{
				mPopBrightWin.dismiss();
				bright_adjust_state = 0;
			}
			
			try 
			{
				getmTimeOutCheckedItem();
			} catch (SettingNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Dialog dialog = new AlertDialog.Builder(ScreenActivity)
			.setIcon(R.drawable.screen_adjust1)
			.setTitle("请选择屏幕超时时间")
			.setNegativeButton("取消", 
							new DialogInterface.OnClickListener() 
							{	
				//***@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									// TODO Auto-generated method stub
									
								}
							})
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() 
					{						
				//***@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							Toast.makeText(ScreenActivity, "屏幕超时", Toast.LENGTH_SHORT).show();
							setTimeOut(mTimeOut);
						}
					})
					.setSingleChoiceItems(ScreenAdjust.this.mSleepItems, 
							mTimeOutCheckedItem, 
							new DialogInterface.OnClickListener() 
							{
						//***@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									mTimeOut = setTime(which);
								}
							}).create();
			dialog.show();
			
			menu_state = 0;
		}
		
	}
	
	/**
	 * @author CatM
	 * @屏幕亮度调节的单击事件处理
	 */
	class brightAdjustClicked implements OnClickListener
	{
		//***@Override
		public void onClick(View v) 
		{
			//Toast.makeText(ScreenActivity.this, "PopupWindow.调节亮度", Toast.LENGTH_SHORT).show();
			ScreenAdjust.this.showBrightAdjustBar();
			
		}		
	}
	
	public void showBrightAdjustBar()
	{
		//如果为自动调节，就设置为手动的
		if(SCREEN_BRIGHTNESS_MODE_AUTOMATIC == getScreenMode())
		{
			//Toast.makeText(ScreenActivity, "PopupWindow.调节亮度", Toast.LENGTH_SHORT).show();
			setScreenMode(SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
		//mPopMenu.dismiss();
		
		mBrightLayout = ScreenActivity.getLayoutInflater().inflate(R.layout.bright_adjust, null);			
		mPopBrightWin = new PopupWindow(mBrightLayout, 400, 280);
		int x = ScreenActivity.getWindowManager().getDefaultDisplay().getWidth()/2;
		int y = ScreenActivity.getWindowManager().getDefaultDisplay().getHeight()/2;			
		
		//获取当前的屏幕亮度
		int brightValue = getScreenBrightness();
		Log.e("screen", ":"+brightValue);
		//操纵拖动条
		SeekBar seekBar = (SeekBar)mBrightLayout.findViewById(R.id.seekBar);
		seekBar.setProgress(brightValue);
		seekBar.setOnSeekBarChangeListener(new seekBarchanged());
		
		//显示在指定位置
		mPopBrightWin.showAtLocation(mBrightLayout, Gravity.CENTER, x-280, y+480);
		
		// 使其聚集
		mPopBrightWin.setFocusable(true);
		// 设置允许在外点击消失
		mPopBrightWin.setOutsideTouchable(true);
		menu_state = 0;
		bright_adjust_state = 1;
	}
	
	/**
	 * @author CatM
	 * 拖动条事件响应
	 */
	private class seekBarchanged implements OnSeekBarChangeListener
	{

		//***@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) 
		{
			Log.e("ProgressChanged", ":"+ progress);
			
			//设置屏幕亮度最小值为30
			if(progress <= 30)
			{
				brightValue = 30;
			}
			else
			{
				brightValue = progress;
			}
			setScreenBrightness(brightValue);
		}

		//***@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{
			
		}

		//***@Override
		public void onStopTrackingTouch(SeekBar seekBar) 
		{
			saveScreenBrightness(brightValue);
		}
		
	}
	
	
	/**
	 * 获得当前屏幕亮度的模式    
	 * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度
	 */
	  private int getScreenMode()
	  {
	    int screenMode=0;
	    try
	    {
	    	screenMode = Settings.System.getInt(ScreenActivity.getContentResolver(),
	    			Settings.System.SCREEN_BRIGHTNESS_MODE);
	    }
	    catch (Exception localException)
	    {
	    	localException.printStackTrace();
	    }
	    
	    return screenMode;
	  }
	  
	 /**
	 * 获得当前屏幕亮度值  0--255
	 */
	  private int getScreenBrightness()
	  {
	    int screenBrightness=200;
	    try
	    {
	    	screenBrightness = Settings.System.getInt(ScreenActivity.getContentResolver(),
	    			Settings.System.SCREEN_BRIGHTNESS);
	    }
	    catch (Exception localException)
	    {
	    	localException.printStackTrace();	      
	    }
	    return screenBrightness;
	  }
	/**
	 * 设置当前屏幕亮度的模式    
	 * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度
	 */
	  private void setScreenMode(int paramInt)
	  {
	    try
	    {
	       Settings.System.putInt(ScreenActivity.getContentResolver(), 
	    		   Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
	    }
	    catch (Exception localException)
	    {
	       localException.printStackTrace();
	    }
	  }
	  
	  /**
	   * 设置当前屏幕亮度值  0--255
	   */
	  public void setScreenBrightness(int paramInt)
	  {
		  setScreenMode(SCREEN_BRIGHTNESS_MODE_MANUAL);
		    try
		    {
		      Settings.System.putInt(ScreenActivity.getContentResolver(), 
		    		  Settings.System.SCREEN_BRIGHTNESS, paramInt);
		    }
		    catch (Exception localException)
		    {
		      localException.printStackTrace();
		    }
	  }
	  
	  /**
	   * 保存当前的屏幕亮度值，并使之生效
	   */
	  public void saveScreenBrightness(int paramInt)
	  {
		   Window localWindow = ScreenActivity.getWindow();
		   WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
		   float f = paramInt / 255.0F;
		   localLayoutParams.screenBrightness = f;
		   localWindow.setAttributes(localLayoutParams);
	  }
	  
	  /**
	   * 返回当前系统设置的超时时间
	 * @return timeOut	
	 * @throws SettingNotFoundException
	 */
	  public int getTimeOut() throws SettingNotFoundException
	  {
		  int timeOut = Settings.System.getInt(ScreenActivity.getContentResolver(), 
				  Settings.System.SCREEN_OFF_TIMEOUT);
		  
		  Log.e("TimeOut", ":" + timeOut);
		  
		  return timeOut;
	  }
	  
	  public void setPreTimeOut()
	  {
		  setTimeOut(pre_timeOut);
	  }
	  /**
	   * 判断应该选择第几个item
	 * @return
	 * @throws SettingNotFoundException
	 */
	public void getmTimeOutCheckedItem() throws SettingNotFoundException
	  {
		  int timeOut = getTimeOut();

		  switch(timeOut)
		  {
		  case 15000:
			  mTimeOutCheckedItem = 0;
			  break;
		  case 30000:
			  mTimeOutCheckedItem = 1;
			  break;
		  case 60000:
			  mTimeOutCheckedItem = 2;
			  break;
		  case 120000:
			  mTimeOutCheckedItem = 3;
			  break;
		  case 300000:
			  mTimeOutCheckedItem = 4;
			  break;
		  case 600000:
			  mTimeOutCheckedItem = 5;
			  break;
		  }
	  }
	
	public int setTime(int which)
	{
		int time = 5000;
		switch(which)
		{
		case 0:
			time = 15000;
			break;
		case 1:
			time = 30000;
			break;
		case 2:
			time = 60000;
			break;
		case 3:
			time = 120000;
			break;	
		case 5:
			time = 300000;
			break;
		case 6:
			time = 600000;
			break;	
		}
		
		return time;
	}
	  
	public boolean setTimeOut(int time)
	{
		//设置超时时间
		if(Settings.System.putInt(ScreenActivity.getContentResolver(), 
				Settings.System.SCREEN_OFF_TIMEOUT, time))
		{
			//Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
			return true;
		}
		else
		{
			return false;
		}
	}
		
	public void setActivity(Activity act)
	{
		this.ScreenActivity = act;		
	}
	
	public int getPreTimeOut()
	{
		return pre_timeOut;
	}
}
