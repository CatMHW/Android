package com.radaee.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.olivephone.office.TempFileManager;
import com.olivephone.office.powerpoint.DocumentSession;
import com.olivephone.office.powerpoint.DocumentSessionBuilder;
import com.olivephone.office.powerpoint.DocumentSessionStatusListener;
import com.olivephone.office.powerpoint.IMessageProvider;
import com.olivephone.office.powerpoint.ISystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidMessageProvider;
import com.olivephone.office.powerpoint.android.AndroidSystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidTempFileStorageProvider;
import com.olivephone.office.powerpoint.view.PersentationView;
import com.olivephone.office.powerpoint.view.SlideShowNavigator;
import com.olivephone.office.powerpoint.view.SlideView;
import com.radaee.reader.PDFReaderAct;
import com.radaee.reader.R;
import com.radaee.reader.ViewPagerActivity;

public class PowerPointActivity extends Activity implements
		DocumentSessionStatusListener {
	private PersentationView content;
	private DocumentSession session;
	private SlideShowNavigator navitator;

//	private String filePath = Environment.getExternalStorageDirectory()
//			.getPath() + "/example1.pptx";
	private String filePath;
	private int currentSlideNumber;
	private Button prev;
	private Button next;
	private SeekBar scale;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//copyFileToSdcard();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.setContentView(R.layout.powerpoint_main);
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		filePath = bundle.getString("name");
		
		this.content = (PersentationView) this.findViewById(R.id.content);
		this.prev = (Button) this.findViewById(R.id.prev);
		this.prev.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				prev();
			}
		});
		this.next = (Button) this.findViewById(R.id.next);
		this.next.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				next();
			}
		});
		this.scale = (SeekBar) this.findViewById(R.id.scale);
		this.scale
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					
					public void onStopTrackingTouch(SeekBar seekBar) {
					}

				
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

						if (progress < 1) {
							progress = 1;
						}
						PowerPointActivity.this.content
								.notifyScale(progress / 250.0);
					}
				});

		try {
			Context context = PowerPointActivity.this.getApplicationContext();
			IMessageProvider msgProvider = new AndroidMessageProvider(context);
			TempFileManager tmpFileManager = new TempFileManager(
					new AndroidTempFileStorageProvider(context));
			ISystemColorProvider sysColorProvider = new AndroidSystemColorProvider();

			session = new DocumentSessionBuilder(new File(filePath))
					.setMessageProvider(msgProvider)
					.setTempFileManager(tmpFileManager)
					.setSystemColorProvider(sysColorProvider)
					.setSessionStatusListener(this).build();
			session.startSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.content.setContentView(null);
	}

	@Override
	protected void onDestroy() {
		if (this.session != null) {
			this.session.endSession();
		}
		super.onDestroy();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Toast.makeText(this,
		// "(" + event.getRawX() + "," + event.getRawY() + ")",
		// Toast.LENGTH_SHORT).show();
		return super.onTouchEvent(event);
	}


	public void onSessionStarted() {
		this.runOnUiThread(new Runnable() {
			
			public void run() {
				Toast.makeText(PowerPointActivity.this, "onSessionStarted",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	
	public void onDocumentReady() {
		this.runOnUiThread(new Runnable() {
			
			public void run() {
				Toast.makeText(PowerPointActivity.this, "onDocumentReady",
						Toast.LENGTH_SHORT).show();
				PowerPointActivity.this.navitator = new SlideShowNavigator(
						PowerPointActivity.this.session.getPPTContext());
				PowerPointActivity.this.currentSlideNumber = PowerPointActivity.this.navitator
						.getFirstSlideNumber() - 1;
				PowerPointActivity.this.next();
			}
		});
	}

	
	public void onDocumentException(Exception e) {
		this.runOnUiThread(new Runnable() {
			
			public void run() {
				Toast.makeText(PowerPointActivity.this, "onDocumentException",
						Toast.LENGTH_SHORT).show();
				PowerPointActivity.this.finish();
			}
		});
	}

	
	public void onSessionEnded() {
		this.runOnUiThread(new Runnable() {
			
			public void run() {
				Toast.makeText(PowerPointActivity.this, "onSessionEnded",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void navigateTo(int slideNumber) {
		SlideView slideShow = this.navitator.navigateToSlide(
				this.content.getGraphicsContext(), slideNumber);
		this.content.setContentView(slideShow);
	}

	private void next() {
		if (this.navitator != null) {
			if (this.navitator.getFirstSlideNumber()
					+ this.navitator.getSlideCount() - 1 > this.currentSlideNumber) {
				this.navigateTo(++this.currentSlideNumber);
			} else {
				Toast.makeText(this, "Next page", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void prev() {
		if (this.navitator != null) {
			if (this.navitator.getFirstSlideNumber() < this.currentSlideNumber) {
				this.navigateTo(--this.currentSlideNumber);
			} else {
				Toast.makeText(this, "Pre page", Toast.LENGTH_SHORT).show();
			}
		}

	}

//	private void copyFileToSdcard() {
//		InputStream inputstream = getResources().openRawResource(R.raw.example);
//		//InputStream inputstream;	
//		byte[] buffer = new byte[1024];
//		int count = 0;
//		FileOutputStream fos = null;
//		try {
//			//inputstream = new FileInputStream(new File(filePath));
//			fos = new FileOutputStream(new File(filePath));
//			while ((count = inputstream.read(buffer)) > 0) {
//				fos.write(buffer, 0, count);
//			}
//			fos.close();
//		} catch (FileNotFoundException e1) {
//			Toast.makeText(PowerPointActivity.this, "Check your sdcard", Toast.LENGTH_LONG).show();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	   	switch(keyCode)
	   	{
	   	case KeyEvent.KEYCODE_BACK:
//	   		if(!mRotatePop.hasShow())
//	   		{
//	   			if(!isMaked)
//	   			{
//	   				backToTab();
//	   			}
//	   			else
//	   			{
//	   				backToScaning();
//	   			}
//	   		}
//	   		if(mRotatePop.hasBtnShow())
//	   		{
//	   			mRotatePop.restore();
//	   			mRotatePop.setBtnShow(false);
//	   		}
//	   		else if(!mRotatePop.hasBtnShow() && mRotatePop.hasShow())
//	   		{
//	   			mRotatePop.dismiss();	
//	   		}
	   		backToTab();
	   		break;
	   	case KeyEvent.KEYCODE_MENU:
//	   		if(!mRotatePop.hasShow())
//	   		{
//	   			mRotatePop.show();
//	   		}	
	   		break;	
	   	}
		return false;	
	 }
	
	private void backToTab()
	{
		Intent intent = new Intent();
   		intent.setClass(PowerPointActivity.this, ViewPagerActivity.class);
   		startActivity(intent);
   		this.finish();
	}
	
}
