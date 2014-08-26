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
	//�˵�״̬��0��ʾδ������1��ʾ�ѵ���
	private int menu_state = 0;
	private int bright_adjust_state = 0;
	
	//1��ʾ�Զ����ڣ�0��ʾ�ֶ�����
	private int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
	private int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
	private static int PRE_SCREEN_BRIGHTNESS_MODE = -1;
	
	private int brightValue = 0;						//��Ļ������ֵ
	private int mTimeOut = 0;
	private int mTimeOutCheckedItem = 0;
	private static final int pre_timeOut = StartAct.pre_timeOut;
	
	private PopupWindow mPopMenu;						//�Զ���˵�
	private PopupWindow mPopBrightWin;					//���ȵ��ڵĽ�����
	private View mLayout;								//�Զ���˵��Ĳ���
	private View mBrightLayout;							//�Զ������ȵ��ڵĲ���
	private Activity ScreenActivity;
	
	private String mSleepItems[] = new String[]{"15��","30��","1����","2����","5����","10����"};
	
	public ScreenAdjust(Activity screen)
	{
		ScreenActivity = screen;
	}
	
	public ScreenAdjust()
	{
		
	}
	
//	/**
//	 * �˵����ͷ��ؼ�����Ӧ
//	 */
//	public boolean onKeyDown(int keyCode, KeyEvent event)
//	{
//		switch (keyCode)
//		{
//		case KeyEvent.KEYCODE_MENU:	
//			
//			//����˵��Ѿ����������ٵ����µĴ���
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
//			//װ��ѡ��˵������ļ�
//			mLayout = ScreenActivity.getLayoutInflater().inflate(R.layout.menu_layout, null);
//			
//			//����PopupWindow����
//			mPopMenu = new PopupWindow(mLayout, ScreenActivity.getWindowManager()
//					.getDefaultDisplay().getWidth(), ScreenActivity.getWindowManager()
//					.getDefaultDisplay().getHeight());
//			
//			//���õ����˵����ڵ�λ��
//			mPopMenu.showAtLocation(mLayout, Gravity.BOTTOM, 0, 0);
//			
//			//ȡ�����ȵ��ںͳ�ʱ��view
//			View brightAdjust = mLayout.findViewById(R.id.brightLayout);
//			View sleepAdjust = mLayout.findViewById(R.id.sleepLayout);
//			
//			//Ϊ���ȵ��ںͳ�ʱ��ӵ����¼�
//			brightAdjust.setOnClickListener(new brightAdjustClicked());	
//			sleepAdjust.setOnClickListener(new sleepAdjustClicked());
//			
//			menu_state = 1;
//			return false;
//
//		case KeyEvent.KEYCODE_BACK:
//			//������˲˵����͹ر���
//			if(1 == menu_state)
//			{
//				mPopMenu.dismiss();
//				menu_state = 0;
//			}
//			else if (0 == menu_state)
//			{
//				//����˵�δ��ʾ�����Ѿ��رգ���ֱ�ӹرյ�ǰ��Activity
//				ScreenActivity.finish();
//			}
//			return false;
//			
//		default:
//			return super.onKeyDown(keyCode, event);
//		}		
//	}
	
	/**
	 * �رղ˵�
	 * @return
	 */
	public boolean dismiss()
	{
		//������˲˵����͹ر���
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
			//����˵�δ��ʾ�����Ѿ��رգ���ֱ�ӹرյ�ǰ��Activity
			//ScreenActivity.finish();
		}
		return true;
	}
	
	/**
	 * @return �����˷���false
	 */
	public boolean getBrightBarState()
	{
		return (menu_state == 0 && bright_adjust_state == 0);
	}

	/**
	 * ��ʾ�˵�
	 * @return
	 */
	public boolean showMenu()
	{
		//����˵��Ѿ����������ٵ����µĴ���
		if(1 == menu_state)
		{
			return false;
		}
		
		if(1 == bright_adjust_state)
		{
			mPopBrightWin.dismiss();
		}
		
		//װ��ѡ��˵������ļ�
		mLayout = ScreenActivity.getLayoutInflater().inflate(R.layout.menu_layout, null);
		
		//����PopupWindow����
		mPopMenu = new PopupWindow(mLayout, ScreenActivity.getWindowManager()
				.getDefaultDisplay().getWidth(), ScreenActivity.getWindowManager()
				.getDefaultDisplay().getHeight());
		
		//���õ����˵����ڵ�λ��
		mPopMenu.showAtLocation(mLayout, Gravity.BOTTOM, 0, 0);
		
		//ȡ�����ȵ��ںͳ�ʱ��view
		View brightAdjust = mLayout.findViewById(R.id.brightLayout);
		View sleepAdjust = mLayout.findViewById(R.id.sleepLayout);
		
		//Ϊ���ȵ��ںͳ�ʱ��ӵ����¼�
		brightAdjust.setOnClickListener(new brightAdjustClicked());	
		sleepAdjust.setOnClickListener(new sleepAdjustClicked());
		
		menu_state = 1;
		return true;
	}
	
	/**
	 * @author CatM
	 * @��ʱ�ĵ����¼�����
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
			.setTitle("��ѡ����Ļ��ʱʱ��")
			.setNegativeButton("ȡ��", 
							new DialogInterface.OnClickListener() 
							{	
				//***@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									// TODO Auto-generated method stub
									
								}
							})
			.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() 
					{						
				//***@Override
						public void onClick(DialogInterface dialog, int which) 
						{
							Toast.makeText(ScreenActivity, "��Ļ��ʱ", Toast.LENGTH_SHORT).show();
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
	 * @��Ļ���ȵ��ڵĵ����¼�����
	 */
	class brightAdjustClicked implements OnClickListener
	{
		//***@Override
		public void onClick(View v) 
		{
			//Toast.makeText(ScreenActivity.this, "PopupWindow.��������", Toast.LENGTH_SHORT).show();
			ScreenAdjust.this.showBrightAdjustBar();
			
		}		
	}
	
	public void showBrightAdjustBar()
	{
		//���Ϊ�Զ����ڣ�������Ϊ�ֶ���
		if(SCREEN_BRIGHTNESS_MODE_AUTOMATIC == getScreenMode())
		{
			//Toast.makeText(ScreenActivity, "PopupWindow.��������", Toast.LENGTH_SHORT).show();
			setScreenMode(SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
		//mPopMenu.dismiss();
		
		mBrightLayout = ScreenActivity.getLayoutInflater().inflate(R.layout.bright_adjust, null);			
		mPopBrightWin = new PopupWindow(mBrightLayout, 400, 280);
		int x = ScreenActivity.getWindowManager().getDefaultDisplay().getWidth()/2;
		int y = ScreenActivity.getWindowManager().getDefaultDisplay().getHeight()/2;			
		
		//��ȡ��ǰ����Ļ����
		int brightValue = getScreenBrightness();
		Log.e("screen", ":"+brightValue);
		//�����϶���
		SeekBar seekBar = (SeekBar)mBrightLayout.findViewById(R.id.seekBar);
		seekBar.setProgress(brightValue);
		seekBar.setOnSeekBarChangeListener(new seekBarchanged());
		
		//��ʾ��ָ��λ��
		mPopBrightWin.showAtLocation(mBrightLayout, Gravity.CENTER, x-280, y+480);
		
		// ʹ��ۼ�
		mPopBrightWin.setFocusable(true);
		// ����������������ʧ
		mPopBrightWin.setOutsideTouchable(true);
		menu_state = 0;
		bright_adjust_state = 1;
	}
	
	/**
	 * @author CatM
	 * �϶����¼���Ӧ
	 */
	private class seekBarchanged implements OnSeekBarChangeListener
	{

		//***@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) 
		{
			Log.e("ProgressChanged", ":"+ progress);
			
			//������Ļ������СֵΪ30
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
	 * ��õ�ǰ��Ļ���ȵ�ģʽ    
	 * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 Ϊ�Զ�������Ļ����
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0  Ϊ�ֶ�������Ļ����
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
	 * ��õ�ǰ��Ļ����ֵ  0--255
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
	 * ���õ�ǰ��Ļ���ȵ�ģʽ    
	 * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 Ϊ�Զ�������Ļ����
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0  Ϊ�ֶ�������Ļ����
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
	   * ���õ�ǰ��Ļ����ֵ  0--255
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
	   * ���浱ǰ����Ļ����ֵ����ʹ֮��Ч
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
	   * ���ص�ǰϵͳ���õĳ�ʱʱ��
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
	   * �ж�Ӧ��ѡ��ڼ���item
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
		//���ó�ʱʱ��
		if(Settings.System.putInt(ScreenActivity.getContentResolver(), 
				Settings.System.SCREEN_OFF_TIMEOUT, time))
		{
			//Toast.makeText(this, "���óɹ�", Toast.LENGTH_SHORT).show();
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
