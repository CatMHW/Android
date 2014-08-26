package com.radaee.main;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

import org.apache.poi.hssf.util.HSSFColor.GOLD;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.example.mytable.MyTable;
import com.radaee.excel.FileManager;
import com.radaee.reader.GlobalData;
import com.radaee.reader.HashMapRead;
import com.radaee.reader.Historydemo;
import com.radaee.reader.PDFReaderAct;
import com.radaee.reader.R;
import com.radaee.reader.ViewFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;




public class SMSSenderActivity extends Activity {
	private Button send_button = null;
	private EditText receiver =null;
	private EditText mailtitle =null;
	private EditText textcontent =null;
	private TextView attachment = null;
	private ImageButton attachment_add =null;
	private String FilePath;
	MailSenderInfo mailInfo = new MailSenderInfo();
	private ProgressDialog	progressDialog;
	private static final int CLOSE = 0;
	
	
	private ContactInfo mContactInfo = null;
	private View mSendText = null;
	private String mContent = "你好,哥哥给你发了一封邮件，赶紧去看看吧";		//短信内容

	private Handler myhandle = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case 0:	
				//关闭ProgressDialog  
	            progressDialog.dismiss();
	            Finish();
			}		
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mailcontent);
		
		send_button = (Button)findViewById(R.id.sendmail);
		receiver = (EditText)findViewById(R.id.receiver_address);
		mailtitle = (EditText)findViewById(R.id.mailtitle);
		textcontent = (EditText)findViewById(R.id.textcontent);
		attachment = (TextView)findViewById(R.id.attachment);
		attachment_add = (ImageButton)findViewById(R.id.attachment_add);
		attachment_add.setVisibility(View.GONE);
		Intent intent = this.getIntent();
		FilePath = intent.getData().toString();
		
		attachment.setText(FilePath.substring(FilePath.lastIndexOf("/")+1,FilePath.length()));
		Toast.makeText(this, FilePath, Toast.LENGTH_SHORT).show();
		
		
		send_button.setOnClickListener(new OnClickListener() {
			//***@Override
			public void onClick(View v) {
        
				progressDialog = ProgressDialog.show(SMSSenderActivity.this,
						"Sending...", "Please wait...", true, false);

				new Thread() {
                    public void run() { 
                    	
                    	
                    	mailInfo.setMailServerHost("smtp.163.com");
            			mailInfo.setMailServerPort("25");
            			mailInfo.setValidate(true);
            			// 设置发件邮箱及密码
            			mailInfo.setUserName(GlobalData.username);
            			mailInfo.setPassword(GlobalData.pasword);
            			mailInfo.setFromAddress(GlobalData.username);
            			
            			// 收件地址
            			mailInfo.setToAddress(receiver.getText().toString());
            			//文本信息
            			mailInfo.setSubject(mailtitle.getText().toString());
            			mailInfo.setContent(textcontent.getText().toString());
//            			//附件
//            			mailInfo.setAttachFilePath("/sdcard/test.doc");
//            			mailInfo.setAttachFileNames("2013.png");
            			
            			SimpleMailSender sms = new SimpleMailSender();
            			//sms.sendTextMail(mailInfo);
            			
            			//附件
            			try {
            				String fileName= FilePath.substring(FilePath.lastIndexOf("/")+1,FilePath.length());
            				sms.add_attach(FilePath,MimeUtility.decodeText(fileName));
            				
            			} catch (MessagingException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			} catch (UnsupportedEncodingException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
            			
            			sms.sendHtmlMail(mailInfo);
            			

            			
            			Message msg_listData = new Message();
                    	msg_listData.what = CLOSE;
                    	myhandle.sendMessage(msg_listData);
                    
                    	
                    }
				}.start();
			}
		});
	}

	public void Finish(){
		
	
		
		mContactInfo = new ContactInfo(this,receiver.getText().toString());
		Log.e("SendText", mContactInfo.getName());
		Log.e("SendText", mContactInfo.getPhone());
		if(mContactInfo.getPhone()!=null && GlobalData.sms_send)
		{
			showDialog();
			Toast.makeText(SMSSenderActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
		}
		else
		{
			backToAct();
		}
		
		
	}
	
	private Dialog mDialog;
	
	public void showDialog()
	{

		mDialog = new AlertDialog.Builder(this)
			.setTitle("要给对方发送短信吗")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SmsManager.getDefault().sendTextMessage(mContactInfo.getPhone(),
							null, mContent, null, null);
//					Intent i = new Intent(SMSSenderActivity.this, SMSSend.class);
//					i.putExtra("tel", mContactInfo.getPhone());
//					startActivity(i);
					backToAct();
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					backToAct();
				}
			}).create();
		
			mDialog.show();		
	
	}

	
	private void backToAct()
	{
		Intent i = new Intent();

		if(FilePath.endsWith(".doc"))	{	
			i.setClass(SMSSenderActivity.this, ViewFile.class);
			i.putExtra("name",FilePath);
			startActivity(i);
			finish();
		}
		else if(FilePath.endsWith(".pdf")){
			Intent intent = new Intent(SMSSenderActivity.this, PDFReaderAct.class);
			PDFReaderAct.path = FilePath;
			startActivity(intent);
			finish();
		}else if(FilePath.endsWith(".xls") || FilePath.endsWith(".xlsx")){
			i.setClass(SMSSenderActivity.this, MyTable.class);
			i.putExtra("name",FilePath);
			startActivity(i);
			finish();
		}
		
		this.finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	   	switch(keyCode)
	   	{
	   	case KeyEvent.KEYCODE_BACK:

	   		Intent i = new Intent();

			if(FilePath.endsWith(".doc"))	{	
				i.setClass(SMSSenderActivity.this, ViewFile.class);
				i.putExtra("name",FilePath);
				startActivity(i);
				finish();
			}
			else if(FilePath.endsWith(".pdf")){
				Intent intent = new Intent(SMSSenderActivity.this, PDFReaderAct.class);
				PDFReaderAct.path = FilePath;
				startActivity(intent);
				finish();
			}else if(FilePath.endsWith(".xls") || FilePath.endsWith(".xlsx")){
				i.setClass(SMSSenderActivity.this, MyTable.class);
				i.putExtra("name",FilePath);
				startActivity(i);
				finish();
			}
	   		
	   		break;
	   	case KeyEvent.KEYCODE_MENU:
	   		break;
	   	}
		return false;	
	 }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 
	        if (resultCode == RESULT_OK) 
	        { 
	            if (requestCode == 1) 
	            { 
	            	backToAct();	            
	            } 
	            
	    
	        } 
	   } 

}


