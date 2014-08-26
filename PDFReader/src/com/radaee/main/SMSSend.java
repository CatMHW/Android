package com.radaee.main;

import java.util.ArrayList;

import com.radaee.reader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Administrator
 *短信发送
 */
public class SMSSend extends Activity{

	String tel;
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.sms_send);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		tel = bundle.getString("tel");
		final EditText no = (EditText) findViewById(R.id.no);  
		no.setText(tel);
		final EditText sms = (EditText) findViewById(R.id.sms);  
		Button send = (Button) findViewById(R.id.send);  
				
		send.setOnClickListener(new View.OnClickListener() {  
		@SuppressWarnings("deprecation")
		public void onClick(View v) {  
		
		String content = sms.getText().toString();  
		// 得到一个短息管理器  
		SmsManager manger = SmsManager.getDefault();  
		//自动拆分短信！  
		ArrayList<String> str = manger.divideMessage(content);  
		for (String s : str) {  
		manger.sendTextMessage(tel, null, s, null, null);  
		Toast.makeText(SMSSend.this, "单击了发送", Toast.LENGTH_SHORT)  
		.show();  
		}  
		}  
		});  
		}  
}
