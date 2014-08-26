package com.radaee.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;


import com.example.mytable.MyTable;
import com.radaee.excel.FileManager;
import com.radaee.reader.GlobalData;
import com.radaee.reader.PDFReaderAct;
import com.radaee.reader.R;
import com.radaee.reader.ScreenAdjust;
import com.radaee.reader.ViewFile;
import com.radaee.reader.ViewPagerActivity;
import com.reader.setting.Setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RemoteViews;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;
import android.widget.RemoteViews.RemoteView;
import android.widget.Toast;


public class SoftWareCup extends Activity implements OnGestureListener,
OnTouchListener{

	//����ʹ������һ��Ƥ��,0ΪĬ�ϵ�
	public static int CLOTHES = 0;
	public static int PRE_TIMEOUT = 0;
	static String picPath = null;
//	private static final TextView TextView = null;
	private RelativeLayout mLayout;
	private Context mContext;
	private Button mBrowseBtn = null;						//�����ť
	private Button mScanBtn = null;							//ɨ�谴ť
	private Button mTakePhotoBtn = null;
	private Button mScanPhotoBtn = null;
	private Intent mIntent = null;
	private GestureDetector mGestureDetector = null;		//����
//	private SlidingDrawer mSlidingDrawer = null;			//���س���
	private MultiDirectionSlidingDrawer mSlidingDrawer = null;
	private ImageView mSettingView = null;
	private PopupWindow mPopWindow = null;
	private MyImageView mLight =null;
	private View mPopView = null;							//���浯�����ڵĲ���
	private View mView = null;
//	private View mLayout = null;
	private int mPreviousx = 0;								//�����ʼ����
	private int mPreviousy = 0;
	private String FilePath;
	private ScreenAdjust mScreenAdjust = null;
	
	private EditText username = null;
	private EditText password = null;
	private Button logon =null;
	private TextView login_success = null;
		
	boolean isPopWindowShow = false;
	boolean isSlidingDrawerOpen = false;
	public static boolean isNightModel = false;
	
	private ProgressDialog	progressDialog;
	private static final int CLOSE = 0;
	private static final int FAILD = 1;
	private static final int DISCONNECT = 2;
	private static final int LOGOUT = 3;
	

	File sdFile = android.os.Environment.getExternalStorageDirectory();
	String DirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX"+ File.separator+"data"+File.separator;
	String usernamePath = DirPath+"username.txt";

	public boolean UsernameExist(){	
		if(fileIsExists(usernamePath))
		{
			FileManager fileManager = new FileManager();
			fileManager.makeDir("data");
			try {
				String filePath = fileManager.makeFilePath("username", "txt");
				
				File myFile = new File(filePath);
				FileInputStream input =new FileInputStream(myFile);
				byte b[] =new byte[(int)myFile.length()];
				input.read(b);
				input.close();
				GlobalData.username = new String(b);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}else{
			return false;
		}
	}
	
	public void UsernameWrite(){	
		FileManager fileManager = new FileManager();
		fileManager.makeDir("data");
		try {
			String filePath = fileManager.makeFilePath("username", "txt");
			File myFile = new File(filePath);
			FileOutputStream output = new FileOutputStream(myFile);
			output.write(GlobalData.username.getBytes());
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
	}

	
	public boolean fileIsExists(String Path){
        File f=new File(Path);
        if(!f.exists()){
                return false;
        }
        return true;
	}
	
	private Handler myhandle = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:	
				//�ر�ProgressDialog  
	            progressDialog.dismiss();
	            setLogoutView();
	            
	           UsernameWrite();
	            
	            break;
			case 1:
				progressDialog.dismiss();
				MessageToast("��¼ʧ�ܣ� ��˶��˺����룡");
				break;
			case 2:

				MessageToast("��¼ʧ�ܣ� �����������ӣ�");

				break;
			case 3:
				setLoginView();
				progressDialog.dismiss();
				
				break;
	                
			}		
		}
	};
	
	public void MessageToast(String str){
		Toast.makeText(SoftWareCup.this, str, Toast.LENGTH_LONG).show();
	}
	
	//����
	//private TextView mTestTv = null;
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ȥ�� ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
				
		//����ʹ���Զ���ı�����
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		
		//ȥ����Ϣ��
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//���ز���
		super.setContentView(R.layout.main_activity);
		
		//�Զ�����������ָ�ֵ 
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
//				R.layout.inti_activity_title);

		//��ʼ��һ���Զ����֪ͨ��
	//	this.intiNotification();
		
		initView();
	//	getPreTimeOut();
		
		
		if(GlobalData.logonflag)
		{
			setLogoutView();
		}
	
	}

	private void initView()
	{
		mScreenAdjust = new ScreenAdjust(this);
		
		mContext = this;
		LayoutInflater inflater = LayoutInflater.from(this);
		mLayout = (RelativeLayout)findViewById(R.id.mainLayout);
		//��ʼ��Ƥ��
		initClothes();
		this.mView = inflater.inflate(R.layout.main_activity, null);
		
		mLight = (MyImageView) findViewById(R.id.light);
		mLight.setOnClickIntent(new MyImageView.OnViewClick() {
			
			public void onClick() {
				// TODO Auto-generated method stub
				//Log.e("soft", "1");
				//SoftWareCup.this.getWindow().setBackgroundDrawableResource(R.drawable.night_model_bg);
				if(isNightModel)
				{
					switch (CLOTHES) 
					{
					case 0:
						SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.main_bg1);
						break;
					case 1:
						SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.main_bg2);
						break;
					case 2:
						SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.main_bg3);
						break;
					default:
						break;
					}			
				}
				else
				{
					switch (CLOTHES) 
					{
					case 0:
						SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.night_model_bg);
						break;
					case 1:
						SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.night_model_bg1);
						break;
					case 2:
						SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.night_model_bg2);
						break;
					default:
						break;
					}
				}
				isNightModel =! isNightModel;

			}
		});
		//����
		mSettingView = (ImageView)findViewById(R.id.main_setting);
		mSettingView.setOnTouchListener(new OnTouchListener() {
			
			@SuppressLint("NewApi")
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //����Ϊ����ʱ�ı���ͼƬ     
                     v.setBackgroundResource(R.drawable.setting_pressed);  
                  //   Drawable drawable = getResources().getDrawable(R.drawable.setting_pressed);
                     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //��Ϊ̧��ʱ��ͼƬ     
	                      v.setBackgroundResource(R.drawable.handle1);   
	              }     
				return false;
			}
		});
		
		mSettingView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mainToSetting();			
			}
		});
		
		//���������ť
		mBrowseBtn = (Button)findViewById(R.id.browseBtn);
		mBrowseBtn.setOnClickListener(new startBrowse());
		mBrowseBtn.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //����Ϊ����ʱ�ı���ͼƬ     
                     v.setBackgroundResource(R.drawable.main_btn_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //��Ϊ̧��ʱ��ͼƬ     
	                      v.setBackgroundResource(R.drawable.main_btn);   
	              }     
				return false;
			}
		});
		
		//����ɨ�谴ť
		mScanBtn = (Button)findViewById(R.id.scanBtn);
		mScanBtn.setOnClickListener(new startScan());
		mScanBtn.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //����Ϊ����ʱ�ı���ͼƬ     
                     v.setBackgroundResource(R.drawable.main_btn_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //��Ϊ̧��ʱ��ͼƬ     
	                      v.setBackgroundResource(R.drawable.main_btn);   
	              }     
				return false;
			}
		});
		
		//��¼����
		username = (EditText)findViewById(R.id.mailEditText1);
		password = (EditText)findViewById(R.id.mailPassWordEdit);
		logon = (Button)findViewById(R.id.maillogon);
		
		if(UsernameExist())
		{
			username.setText(GlobalData.username);
		}
		
		logon.setOnClickListener(new OnClickListener() {
			//***@Override
			public void onClick(View v) {
				
				if(Test_WIFI()){
				
					progressDialog = ProgressDialog.show(SoftWareCup.this,
							"Loging...", "Please wait...", true, false);

					new Thread() {
                    public void run() { 
                    	if(logon()){
            			Message msg_listData = new Message();
                    	msg_listData.what = CLOSE;
                    	myhandle.sendMessage(msg_listData);
                    	}else{
                    		Message msg_listData = new Message();
                        	msg_listData.what = FAILD;
                        	myhandle.sendMessage(msg_listData);
                    	}
                    	
                    }
					}.start();
				}else{
				Message msg_listData = new Message();
            	msg_listData.what = DISCONNECT;
            	myhandle.sendMessage(msg_listData);
			}
			}
		});
		logon.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //����Ϊ����ʱ�ı���ͼƬ     
                     v.setBackgroundResource(R.drawable.login_btn_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //��Ϊ̧��ʱ��ͼƬ     
	                      v.setBackgroundResource(R.drawable.login_btn);   
	              }     
				return false;
			}
		});
//		login_success = (TextView)findViewById(R.id.login_success_text);
//		login_success.setText("��½�ɹ�");
		
//		setOnClickListener(new OnClickListener() {
//			//***@Override
//			public void onClick(View v) {
//				
//				logon();
//				
//			}
//		});

		
		//ע�������Ĳ˵�
		registerForContextMenu(mScanBtn);
		
		//��������������
		mGestureDetector = new GestureDetector(this);
		
		
		
		//���Ե��϶��ؼ�Ч��
//		mTestTv = (TextView)findViewById(R.id.testTv);
//		mTestTv.setOnTouchListener(this);
//		mTestTv.setLongClickable(true);
		
		//���س����ʼ��
		this.mSlidingDrawer = (MultiDirectionSlidingDrawer)findViewById(R.id.slidingDrawer);
	//	this.mHandle = (ImageView)findViewById(R.id.handle);
		
		int width = getWindowManager().getDefaultDisplay().getWidth();
	//	this.mSetting = (ImageView)findViewById(R.id.main_setting);
	//	this.mSetting.setLayoutParams(ImageView.));
//		mLayout = getLayoutInflater().inflate(R.layout.main_activity, null);
//		View handle = mLayout.findViewById(R.id.handle);
//		handle.layout(20, 0, 0, 0);
		this.mSlidingDrawer.setOnDrawerCloseListener(new SlidingCloseImpl());
		this.mSlidingDrawer.setOnDrawerOpenListener(new SlidingOpenImpl());
		
		if(GlobalData.sendflag)
		{
			this.mSlidingDrawer.open();
			Intent intent = this.getIntent();
			FilePath = intent.getData().toString();
		}
		
		initFile();
	}
	
	public void initFile()
	{
		FileManager fileManager = new FileManager();
		fileManager.makeDir("data");				
	}
	
	public Boolean Test_WIFI(){
		if(isConnect(this)==false)
		{
//			Toast.makeText(this, "��������ʧ�ܣ�", Toast.LENGTH_LONG).show();
			return false;
		}else{
			return true;
		}
	}
	
	private void initClothes()
	{
		if(!isNightModel)
		{
			switch (CLOTHES) 
			{
			case 0:
				SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.main_bg1);
				break;
			case 1:
				SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.main_bg2);
				break;
			case 2:
				SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.main_bg3);
				break;
			default:
				break;
			}			
		}
		else
		{
			switch (CLOTHES) 
			{
			case 0:
				SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.night_model_bg);
				break;
			case 1:
				SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.night_model_bg1);
				break;
			case 2:
				SoftWareCup.this.mLayout.setBackgroundResource(R.drawable.night_model_bg2);
				break;
			default:
				break;
			}
		}
	}
	

	//��ʼ��һ���Զ����֪ͨ��
	public void intiNotification()
	{
		//ͨ��getSystemService��ȡһ��NotificationManager����
		//NotificationManager notificationManager = 
			//	(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		//����һ��Notification����
	/*	Notification notification = new Notification(R.drawable.head1,
				"�Զ�����Ϣ", System.currentTimeMillis());
		
		//��Notification�ӵ�����������һ��
	//	notification.flags = Notification.FLAG_ONGOING_EVENT;
		
		//����һ��PendingIntent����
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, 
				getIntent(), 0);
		
		//����Notification����ϸ��Ϣ
		notification.setLatestEventInfo(this, "����Ԥ��", "��ת����", contentIntent);
		
		//��ʾNotification��Ϣ
		notificationManager.notify(R.drawable.head1, notification);*/

	}

	
	/**
	 * ����ɨ��ʱ�����������Ĳ˵�
	 * @param menu 
	 * @param v
	 * @param menuInfo
	 */
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("��ѡ����Ҫִ�е�����");
	//	super.getMenuInflater().inflate(R.menu.scaning, menu);
	}

	/**
	 * ����д�Ĳ˵�ĳһ����ѡ��ʱִ�еĲ���
	 */
	public boolean onContextItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		Toast.makeText(this, "���ղ˵�"+ id, Toast.LENGTH_SHORT).show();
		switch (id)
		{		
		case Menu.FIRST+1:
			Toast.makeText(this, "���ղ˵�", Toast.LENGTH_SHORT).show();
			break;
		case Menu.FIRST+2:
			Toast.makeText(this, "�����Ƭ�˵�", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * �������Ĳ˵��ر�ʱִ��
	 */
	public void onContextMenuClosed(Menu menu)
	{
		
	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onDown", Toast.LENGTH_SHORT).show();
		return false;
	}

	
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onFling", Toast.LENGTH_SHORT).show();
		return false;
	}


	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onLongPress", Toast.LENGTH_SHORT).show();
		
	}

	/**
	 * �����¼�
	 */
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		float y1 = e1.getY();
		float y2 = e2.getY();
		
		//�������ϻ���
		if(y1-y2 > 120)
		{
		//	Intent intent = new Intent(this, LoginActivity.class);
		//	this.startActivity(intent);
		//	mBrowseBtn.setText("YOU");
		}
		else if(y1-y2 < -120)//��������
		{
		//	mBrowseBtn.setText("FUCK");
		}
		return false;
	}


	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}


	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onTouchEvent(MotionEvent event)
	{      
	//	Toast.makeText(this, "onTouchEvents", Toast.LENGTH_SHORT).show();  
		return this.mGestureDetector.onTouchEvent(event);   
	}
	 
	/*
	 * ��ʼ���
	 */
	private class startBrowse implements OnClickListener
	{
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
//			mIntent = new Intent(SoftWareCup.this, ActTabActivityDemo1.class);
			mIntent = new Intent(SoftWareCup.this, ViewPagerActivity.class);
			startActivity(mIntent);
			SoftWareCup.this.finish();
			SoftWareCup.this.overridePendingTransition(R.anim.main_left_in,
					R.anim.main_left_out);
		}
	}

	/*
	 * ��ʼɨ��
	 */
	private class startScan implements OnClickListener
	{

		public void onClick(View v) 
		{
			mIntent = new Intent(SoftWareCup.this, Scaning.class);
			startActivity(mIntent);
			SoftWareCup.this.finish();
			SoftWareCup.this.overridePendingTransition(R.anim.main_left_in,
					R.anim.main_left_out);
		}
		
	}
	

	
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	//	Toast.makeText(SoftWareCup.this, "���� 1", Toast.LENGTH_SHORT).show();
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(isSlidingDrawerOpen)
			{
				mSlidingDrawer.close();
				if(GlobalData.sendflag){
					Intent i = new Intent();

					if(FilePath.endsWith(".doc"))	{	
						i.setClass(SoftWareCup.this, ViewFile.class);
						i.putExtra("name",FilePath);
						startActivity(i);
						finish();
					}
					else if(FilePath.endsWith(".pdf")){
						Intent intent = new Intent(SoftWareCup.this, PDFReaderAct.class);
						PDFReaderAct.path = FilePath;
						startActivity(intent);
						finish();
					}else if(FilePath.endsWith(".xls") || FilePath.endsWith(".xlsx")){
						i.setClass(SoftWareCup.this, MyTable.class);
						i.putExtra("name",FilePath);
						startActivity(i);
						finish();
					}
					
				}
			}
			else
			{
				exitBy2Click();
			}			
			
		}
		if(keyCode == KeyEvent.KEYCODE_MENU)
		{
			mainToSetting();
		}
		return false;	
	}
	
	/** 
	 * ˫���˳����� 
	*/  
	private static Boolean isExit = false;  
		  
	private void exitBy2Click() 
	{  
		  Timer tExit = null;  
		    if (isExit == false) 
		    {  
		    	Animation shakeAnim = AnimationUtils.loadAnimation(mContext, R.anim.shake_x);
				mLayout.startAnimation(shakeAnim);
				
		        isExit = true; // ׼���˳�  
		        Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();  
		        tExit = new Timer();  
		        tExit.schedule(new TimerTask()
		        {  
		            @Override  
		            public void run() 
		            {  
		                isExit = false; // ȡ���˳�  
		            }  
		        }, 2000); // ���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����  
		  
		    } 
		    else 		    	
		    {  		    	
		    	mScreenAdjust.setTimeOut(StartAct.pre_timeOut);
		        finish();  		        
		        this.overridePendingTransition(R.anim.main_left_in,
						R.anim.main_left_out);
		        System.exit(0);  
		    }  
	}
	
	
	public void mainToSetting()
	{
		if(!isSlidingDrawerOpen)
		{
			Intent intent = new Intent();
			intent.setClass(SoftWareCup.this, Setting.class);
			SoftWareCup.this.startActivity(intent);
			SoftWareCup.this.finish();
			SoftWareCup.this.overridePendingTransition(R.anim.push_up_in,
					R.anim.push_up_out);
		}
	}
	
	public boolean onTouch(View v, MotionEvent event)
	{
		// TODO Auto-generated method stub
	//	Toast.makeText(this, "onTouch", Toast.LENGTH_SHORT).show();
		int x = (int) event.getX();
		int y = (int) event.getY();

		Log.e("Soft", "x:"+x);
		final int iAction = event.getAction();
		final int iCurrentx = (int)event.getX();
		final int iCurrenty = (int)event.getY();
		switch(iAction)
		{
		case MotionEvent.ACTION_DOWN:
			mPreviousx = iCurrentx;
			mPreviousy = iCurrenty;
			break;
		case MotionEvent.ACTION_MOVE:
			int iDeltx = iCurrentx - mPreviousx;
			int iDelty = iCurrenty - mPreviousy;
//			final int iLeft = mTestTv.getLeft();
//			final int iTop = mTestTv.getTop();
//			if(iDeltx != 0 || iDelty != 0)
//				mTestTv.layout(iLeft + iDeltx, 
//						iTop + iDelty, 
//						iLeft + iDeltx + mTestTv.getWidth(), 
//						iTop + iDelty + mTestTv.getHeight());
//			
		//	mCurrentLayout[0] = iLeft + iDeltx ;
		//	mCurrentLayout[1] = iTop + iDelty ;
		//	mCurrentLayout[2] = iLeft + iDeltx + getWidth() ;
		//	mCurrentLayout[3] = iTop + iDelty + getHeight() ;
			
			mPreviousx = iCurrentx - iDeltx;
			mPreviousy = iCurrenty - iDelty;
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}

		return false;
	}
	
	
	private class SlidingCloseImpl implements com.radaee.main.MultiDirectionSlidingDrawer.OnDrawerCloseListener
	{

		public void onDrawerClosed() {
			// TODO Auto-generated method stub
			Animation shakeAnim = AnimationUtils.loadAnimation(mContext, R.anim.shake);
			mScanBtn.startAnimation(shakeAnim);
			mBrowseBtn.startAnimation(shakeAnim);
			SoftWareCup.this.mSettingView.setVisibility(View.VISIBLE);

			mLight.setVisibility(View.VISIBLE);
			mBrowseBtn.setVisibility(View.VISIBLE);
			mScanBtn.setVisibility(View.VISIBLE);
			isSlidingDrawerOpen = false;
			
		}
		
	}
	
	private class SlidingOpenImpl implements com.radaee.main.MultiDirectionSlidingDrawer.OnDrawerOpenListener
	{

		public void onDrawerOpened() {
			// TODO Auto-generated method stub
			isSlidingDrawerOpen = true;
			
			mLight.setVisibility(View.INVISIBLE);
			mBrowseBtn.setVisibility(View.INVISIBLE);
			mScanBtn.setVisibility(View.INVISIBLE);
			
			//�����½�ɹ�
			//setLogoutView();
			
			SoftWareCup.this.mSettingView.setVisibility(View.GONE);
		}		
	}
	
	
	/**
	 * ���������½�ɹ�����
	 */
	public void setLogoutView()
	{
		this.password.setVisibility(View.GONE);		
		this.logon.setBackgroundResource(R.drawable.logout);
		this.logon.setText("ע         ��");
		this.username.setFocusable(false);
		this.username.setCompoundDrawablesWithIntrinsicBounds(R.drawable.m_user1, 0, 0, 0);
		//this.login_success.setVisibility(View.VISIBLE);
		
		logon = (Button)findViewById(R.id.maillogon);
		
		logon.setOnClickListener(new OnClickListener() {
			//***@Override
			public void onClick(View v) {
				if(GlobalData.logonflag==false){
					if(Test_WIFI()){
				
					progressDialog = ProgressDialog.show(SoftWareCup.this,
							"Loging...", "Please wait...", true, false);

					new Thread() {
                    public void run() { 
                    	if(logon()){
            			Message msg_listData = new Message();
                    	msg_listData.what = CLOSE;
                    	myhandle.sendMessage(msg_listData);
                    	}else{
                    		Message msg_listData = new Message();
                        	msg_listData.what = FAILD;
                        	myhandle.sendMessage(msg_listData);
                    	}
                    	
                    }
					}.start();
					}else{
						Message msg_listData = new Message();
						msg_listData.what = DISCONNECT;
						myhandle.sendMessage(msg_listData);
					}
				}else{
					GlobalData.logonflag = false;
					Message msg_listData = new Message();
					msg_listData.what = LOGOUT;
					myhandle.sendMessage(msg_listData);
				}
			}
		});

	}
	
	/**
	 * ��ʼ����½����
	 */
	public void setLoginView()
	{
		this.password.setVisibility(View.VISIBLE);
		this.logon.setBackgroundResource(R.drawable.login_btn);
		this.logon.setText("��         ¼");	
		this.username.setFocusable(true);
		
		if(UsernameExist())
		{
			username.setText(GlobalData.username);
			password.setFocusableInTouchMode(true);
		}

		
		logon = (Button)findViewById(R.id.maillogon);
		
		logon.setOnClickListener(new OnClickListener() {
			//***@Override
			public void onClick(View v) {
				if(GlobalData.logonflag==false){
					if(Test_WIFI()){
				
					progressDialog = ProgressDialog.show(SoftWareCup.this,
							"Loging...", "Please wait...", true, false);

					new Thread() {
                    public void run() { 
                    	if(logon()){
            			Message msg_listData = new Message();
                    	msg_listData.what = CLOSE;
                    	myhandle.sendMessage(msg_listData);
                    	}else{
                    		Message msg_listData = new Message();
                        	msg_listData.what = FAILD;
                        	myhandle.sendMessage(msg_listData);
                    	}
                    	
                    }
					}.start();
					}else{
						Message msg_listData = new Message();
						msg_listData.what = DISCONNECT;
						myhandle.sendMessage(msg_listData);
					}
				}else{
					GlobalData.logonflag = false;
					
					Message msg_listData = new Message();
					msg_listData.what = LOGOUT;
					myhandle.sendMessage(msg_listData);
				}
			}
		});

	}
	
	public boolean logon() {
		//Session s=Session.getDefaultInstance(props);
		if(isConnect(this)==false)
		{
			Toast.makeText(this, "��������ʧ�ܣ�", Toast.LENGTH_LONG).show();
		}else{
			try {
			 	Properties props =new  Properties();
				props.setProperty("mail.smtp.host", "smtp.163.com");
				props.setProperty("mail.transport.protocol", "smtp");
				props.setProperty("mail.smtp.auth", "true");
				props.setProperty("mail.smtp.port", "25");
				Session s=Session.getInstance(props, new MailAuthenticators(username.getText().toString(), password.getText().toString()));					s.setDebug(true);
				Transport t=s.getTransport();
				t.connect("smtp.163.com",25,username.getText().toString(), password.getText().toString());
				t.close();
//				Toast.makeText(this, "sucessful", Toast.LENGTH_LONG).show();
				
				GlobalData.username = username.getText().toString();
				GlobalData.pasword = password.getText().toString();
				GlobalData.logonflag = true;
				if(GlobalData.sendflag){
					Intent i = new Intent(SoftWareCup.this, SMSSenderActivity.class);
					Uri path = Uri.parse(FilePath);
					i.setData(path);
					startActivity(i);
					this.finish();
					GlobalData.sendflag = false;	
				}
				return true;
				} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
//				Toast.makeText(this, "failed", Toast.LENGTH_LONG).show();
			}
			
		}
		return false;
		 
	}
	
	public static boolean isConnect(Context context) { 
	    // ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ��� 
			try { 
				ConnectivityManager connectivity = (ConnectivityManager) context 
						.getSystemService(Context.CONNECTIVITY_SERVICE); 
				if (connectivity != null) { 
					// ��ȡ�������ӹ���Ķ��� 
					NetworkInfo info = connectivity.getActiveNetworkInfo(); 
					if (info != null&& info.isConnected()) { 
						// �жϵ�ǰ�����Ƿ��Ѿ����� 
						if (info.getState() == NetworkInfo.State.CONNECTED) { 
							return true; 
						} 
					} 
				} 
			} catch (Exception e) { 
				//TODO: handle exception 
				Log.v("error",e.toString()); 	
			} 
			return false; 
		} 
}

	


	class MailAuthenticators extends Authenticator {
		private String userName;
		private String password;
		public MailAuthenticators(String userName, String password) {
			super();
			this.userName = userName;
			this.password = password;
		}
		@Override
		protected PasswordAuthentication  getPasswordAuthentication() {
			return new PasswordAuthentication(userName, password);
		}
		
	
}