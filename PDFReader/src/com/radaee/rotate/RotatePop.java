package com.radaee.rotate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.radaee.excel.FileManager;
import com.radaee.main.CaptureScreenActivity;
import com.radaee.main.SMSSenderActivity;
import com.radaee.main.SoftWareCup;
import com.radaee.reader.GlobalData;
import com.radaee.reader.R;
import com.radaee.reader.ScreenAdjust;
import com.radaee.reader.ViewFile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RotatePop 
{
	private boolean areButtonsShowing;
	private boolean isPopWinShow = false;
	public static boolean isTimeOutSet = false;
	private int SCREEN_MODEL = 0;				//竖屏为0，横屏为1
	private RelativeLayout composerButtonsWrapper;
	private ImageView composerButtonsShowHideButtonIcon;
	private RelativeLayout composerButtonsShowHideButton;
	private Activity mActivity = null;
	private View mRotatePopView = null;
	private PopupWindow mRotatePop = null;
	private String mFilePath;
	private ImageView captureImg = null;
	private ScreenAdjust mScreenAdjust = null;
	private String capturePicPath = null;
	private WebView mWebView = null;
	
	private boolean WORD_MODEL = false;
	
	public RotatePop(Activity screen)
	{		
		mActivity = screen;
		initMain();
		MyAnimations.initOffset(screen);
	}
	
	public RotatePop(Activity screen, WebView webView)
	{		
		mActivity = screen;
		initMain();
		MyAnimations.initOffset(screen);
		mWebView = webView;
		WORD_MODEL = true;
	}
	
	private void setListener() {
		// 缁欏ぇ鎸夐挳璁剧疆鐐瑰嚮浜嬩欢
		composerButtonsShowHideButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//小按钮返回
				if (!areButtonsShowing) {
					// 按钮出去动画
					MyAnimations.startAnimationsIn(composerButtonsWrapper, 300);
					// 鍔犲彿鐨勫姩鐢?
					composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(0, -225, 300));
				} else {
					// 按钮还原
					MyAnimations.startAnimationsOut(composerButtonsWrapper, 300);
					// 鍔犲彿鐨勫姩鐢?
					composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(-225, 0, 300));
				}
				areButtonsShowing = !areButtonsShowing;
			}
		});

		// 缁欏皬鍥炬爣璁剧疆鐐瑰嚮浜嬩欢
		for (int i = 0; i < composerButtonsWrapper.getChildCount(); i++) {
			final ImageView smallIcon = (ImageView) composerButtonsWrapper.getChildAt(i);
			final int position = i;
			smallIcon.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					// 杩欓噷鍐欏悇涓猧tem鐨勭偣鍑讳簨浠?
					// 1.鍔犲彿鎸夐挳缂╁皬鍚庢秷澶?缂╁皬鐨刟nimation
					// 2.鍏朵粬鎸夐挳缂╁皬鍚庢秷澶?缂╁皬鐨刟nimation
					// 3.琚偣鍑绘寜閽斁澶у悗娑堝け 閫忔槑搴︽笎鍙?鏀惧ぇ娓愬彉鐨刟nimation
					//composerButtonsShowHideButton.startAnimation(MyAnimations.getMiniAnimation(300));
					
					//点击与未被点击的执行不同的动画
					if(areButtonsShowing){
						composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(-225, 0, 300));
						smallIcon.startAnimation(MyAnimations.getMaxAnimation(400));
						for (int j = 0; j < composerButtonsWrapper.getChildCount(); j++) {
							if (j != position) {
								final ImageView smallIcon = (ImageView) composerButtonsWrapper.getChildAt(j);
								smallIcon.startAnimation(MyAnimations.getMiniAnimation(300));
								//MyAnimations.getMiniAnimation(300).setFillAfter(true); 
							}
						}
						areButtonsShowing = !areButtonsShowing;
					}
				}
			});
		}
	}
	private void initMain(){
        //tContentView(R.layout.Rotate);
      //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		mRotatePopView = mActivity.getLayoutInflater().inflate(R.layout.rotate, null);		
		
		//小按钮的布局
        composerButtonsWrapper = (RelativeLayout) mRotatePopView.
        		findViewById(R.id.composer_buttons_wrapper);
        //主按钮的相对布局
		composerButtonsShowHideButton = (RelativeLayout) mRotatePopView.
				findViewById(R.id.composer_buttons_show_hide_button);
		//主按钮
		composerButtonsShowHideButtonIcon = (ImageView) mRotatePopView.
				findViewById(R.id.composer_buttons_show_hide_button_icon);
		
		mScreenAdjust = new ScreenAdjust(mActivity);
		setListener();
	}
	
	public void show()
	{
		mRotatePop = new PopupWindow(mRotatePopView, LayoutParams.WRAP_CONTENT, 300);		
		
		//设置弹出菜单窗口的位置
		mRotatePop.showAtLocation(mRotatePopView, Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
		isPopWinShow = true;
		
		//分享按钮
		View shareBtn = mRotatePopView.findViewById(R.id.composer_button_photo);
		shareBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(GlobalData.logonflag){
				// TODO Auto-generated method stub
					Intent i = new Intent(RotatePop.this.mActivity, SMSSenderActivity.class);
					Uri path = Uri.parse(mFilePath);
					i.setData(path);
					mActivity.startActivity(i);
					mRotatePop.dismiss();
					isPopWinShow = false;
					mActivity.finish();
				}else{
					GlobalData.sendflag = true;
					Intent i = new Intent(RotatePop.this.mActivity, SoftWareCup.class);
					Uri path = Uri.parse(mFilePath);
					i.setData(path);
					mActivity.startActivity(i);
					mRotatePop.dismiss();
					isPopWinShow = false;
					mActivity.finish();
					
				}
			//	Log.e("Rotate", mFilePath);
			}
		});
		
		//横屏按钮
		View orientationBtn = mRotatePopView.findViewById(R.id.composer_button_people);
		orientationBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				switch(SCREEN_MODEL)
				{
				case 0:
					RotatePop.this.mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);					
					MyAnimations.startAnimationsOut(composerButtonsWrapper, 300);
					Log.e("2", "2");
					composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(-225, 0, 300));
					SCREEN_MODEL = 1;
					break;
				case 1:
					
					MyAnimations.startAnimationsOut(composerButtonsWrapper, 300);
					Log.e("2", "2");
					composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(-225, 0, 300));
					RotatePop.this.mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					SCREEN_MODEL = 0;
					break;
				}
				
				mRotatePop.dismiss();
				isPopWinShow = false;
			}
		});
		
		//截屏
		View captureBtn = mRotatePopView.findViewById(R.id.composer_button_place);
		captureImg = new ImageView(mActivity);
		captureBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RotatePop.this.captureScreen();
				animation(2, v);				
				Intent i = new Intent(RotatePop.this.mActivity, CaptureScreenActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", capturePicPath);
				Log.e("capturePicPath", capturePicPath);
				i.putExtras(bundle);
				RotatePop.this.mActivity.startActivity(i);				
			}
		});
		
		//屏幕超时
		View timeoutBtn = mRotatePopView.findViewById(R.id.composer_button_music);
		timeoutBtn.setOnClickListener( new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub			
				//设置超时10分钟
				if(mScreenAdjust.setTimeOut(600000))
				{					
					Toast.makeText(RotatePop.this.mActivity, "屏幕超时设置成功", Toast.LENGTH_SHORT).show();
					isTimeOutSet = true;
				}
				else
				{
					Toast.makeText(RotatePop.this.mActivity, "屏幕超时设置失败", Toast.LENGTH_SHORT).show();
					isTimeOutSet = false;
				}
				
				animation(3, v);
			}
		});
		
		//调节屏幕亮度
		View brightAdjustBtn = mRotatePopView.findViewById(R.id.composer_button_thought);
		brightAdjustBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				int lightNess = 50;
//				mScreenAdjust.setScreenBrightness(lightNess);
//				mScreenAdjust.saveScreenBrightness(lightNess);
				mScreenAdjust.showBrightAdjustBar();
				animation(4, v);
			}
		});
		
		//夜间模式
		View nightModelBtn = mRotatePopView.findViewById(R.id.composer_button_sleep);
		nightModelBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int lightNess = 50;
				if(!WORD_MODEL)
				{
					setBrightNess(lightNess);
				}
				else
				{
					if(SoftWareCup.isNightModel)
					{

						mWebView.loadUrl("file:///"+ ViewFile.htmlPath);
						SoftWareCup.isNightModel = false;
					}
					else
					{						
						mWebView.loadUrl("file:///"+ ViewFile.htmlPath1);
						SoftWareCup.isNightModel = true;
					}
				}
				animation(5, v);
			}
		});
		
	}
	
	private void setBrightNess(int lightNess)
	{		
		mScreenAdjust.setScreenBrightness(lightNess);
		mScreenAdjust.saveScreenBrightness(lightNess);
	}
	/**
	 * @param position	点击按钮的在父视图中的位置
	 * @param v	点击的按钮
	 */
	public void animation(int position, View v)
	{
		if(areButtonsShowing){
			composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(-225, 0, 300));
			v.startAnimation(MyAnimations.getMaxAnimation(400));
			for (int j = 0; j < composerButtonsWrapper.getChildCount(); j++) {
				if (j != position) {
					final ImageView smallIcon = (ImageView) composerButtonsWrapper.getChildAt(j);
					smallIcon.startAnimation(MyAnimations.getMiniAnimation(300));
					//MyAnimations.getMiniAnimation(300).setFillAfter(true); 
				}
			}
			areButtonsShowing = !areButtonsShowing;
		}
	}
	
	public boolean getState()
	{
		return mScreenAdjust.getBrightBarState();
	}
	public void setPath(String path)
	{
		mFilePath = path;
	}
	public String getPath()
	{
		return mFilePath;
	}
	
	public boolean hasShow()
	{
		return isPopWinShow;
	}
	
	public void dismiss()
	{
		if(getState())
		{
			mRotatePop.dismiss();
			isPopWinShow = false;
		}
		else
		{
			mScreenAdjust.dismiss();
		}
	}
	
	public void restore()
	{
		MyAnimations.startAnimationsOut(composerButtonsWrapper, 300);
		Log.e("2", "2");
		composerButtonsShowHideButtonIcon.startAnimation(MyAnimations.getRotateAnimation(-225, 0, 300));
	}
	
	public boolean hasBtnShow()
	{
		return areButtonsShowing;
	}
	
	public void setBtnShow(boolean is)
	{
		areButtonsShowing = is;
	}
	
	public void captureScreen()
	{
		View view = RotatePop.this.mActivity.getWindow().getDecorView();	//得到当前view所在view结构中的根view
		view.setDrawingCacheEnabled(true);		//设置属性
	    view.buildDrawingCache();  				
	    Bitmap b1 = view.getDrawingCache();  	//取得位图
	  
	    // 获取状态栏+标题栏高度  
	    int contentTop = RotatePop.this.mActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
	    Log.i("TAG", "" + contentTop);  
	  
	    // 获取屏幕长和高  
	    int width = RotatePop.this.mActivity.getWindowManager().getDefaultDisplay().getWidth();  
	    int height = RotatePop.this.mActivity.getWindowManager().getDefaultDisplay().getHeight();  
	    // 去掉标题栏  
	    Bitmap b = Bitmap.createBitmap(b1, 0, contentTop, width, height  
	                -contentTop);  
	    view.destroyDrawingCache(); 
		//显示截取的位图
	//	captureImg.setImageBitmap(b);
	//	RotatePop.this.mActivity.setContentView(captureImg);
		
		FileManager capturePic = new FileManager();
		capturePic.makeDir("CapturePicture");
		Date date =new Date(System.currentTimeMillis());				//获取系统时间
    	SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");	//设置时间格式
		
		FileOutputStream fos = null;  
	        try {  
	        	
	        	capturePicPath = capturePic.makeFilePath(f.format(date), "png");//设置文件路径
	        	Log.e("capture", "11"+capturePicPath);
	            fos = new FileOutputStream(capturePicPath); 					//创建文件
	            if (null != fos) { 												//创建是否成功
	                b.compress(Bitmap.CompressFormat.PNG, 90, fos);				//文件内容写入 
	                fos.flush(); 												//刷新缓冲区 
	                fos.close();  												//关闭缓冲区
	            }  
	        } catch (FileNotFoundException e) {  
	            e.printStackTrace();  											//文件未找到问题
	        } catch (IOException e) {  
	            e.printStackTrace();  											//文件写入问题
	        }  
	}
	
}
