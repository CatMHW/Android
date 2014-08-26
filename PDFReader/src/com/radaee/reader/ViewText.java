package com.radaee.reader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.util.EncodingUtils;

import com.radaee.main.Scaning;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;



public class ViewText extends Activity {

	private static String TAG = "TEXT";
	private EditText mTXTEdit;
	private String mTXTFilePath;
	private Dialog mDialog;	
	
	private boolean isTextChanged = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//去掉 标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.txt);

		mTXTEdit = (EditText)findViewById(R.id.text_edit);
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		mTXTFilePath = bundle.getString("name");
		mTXTEdit.setText(convertCodeAndGetText(mTXTFilePath));
		mTXTEdit.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				isTextChanged = true;
				
			}
		});
		
	}
	
	
	 /**
	 * @param fileName 
	 * @param message
	 * 写
	 */
	public void writeFileSdcard(String fileName, String message) { 
		 
	        try { 
	 
	            // FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE); 
	 
	            FileOutputStream fout = new FileOutputStream(fileName); 
	 
	            byte[] bytes = message.getBytes(); 
	 
	            fout.write(bytes); 
	 
	            fout.close(); 
	 
	        } 
	 
	        catch (Exception e) { 
	 
	            e.printStackTrace(); 
	 
	        } 
	 
	    } 
	 
	    // 读在/mnt/sdcard/目录下面的文件 
	 
	    public String readFileSdcard(String fileName) { 
	 
	        String res = ""; 
	        String text = null;
	        try { 
	 
	            FileInputStream fin = new FileInputStream(fileName); 
	            BufferedReader reader = new BufferedReader(new InputStreamReader
	            		(new FileInputStream(fileName),"GB2312"));
	            
	            
//	            int length = fin.available(); 
//	 
//	            byte[] buffer = new byte[length]; 
//	 
//	            fin.read(buffer); 
	            while((res = reader.readLine()) != null)
	            {
	            	text += EncodingUtils.getString(res.getBytes(), "utf-8"); 
	            	
	            }
	            reader.close();
	           // fin.close(); 
	 
	        } 
	 
	        catch (Exception e) { 
	 
	            e.printStackTrace(); 
	 
	        } 
	 
	        return text; 
	 
	    } 
	    
	    public String convertCodeAndGetText(String str_filepath) {// 转码
            File file = new File(str_filepath);
            BufferedReader reader;
            String text = "";
            try {
                    // FileReader f_reader = new FileReader(file);
                    // BufferedReader reader = new BufferedReader(f_reader);
                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream in = new BufferedInputStream(fis);
                    in.mark(4);
                    byte[] first3bytes = new byte[3];
                    in.read(first3bytes);//找到文档的前三个字节并自动判断文档类型。
                    in.reset();
                    if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
                                    && first3bytes[2] == (byte) 0xBF) {// utf-8
                            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                    } else if (first3bytes[0] == (byte) 0xFF
                                    && first3bytes[1] == (byte) 0xFE) {
                            reader = new BufferedReader(
                                            new InputStreamReader(in, "unicode"));
                    } else if (first3bytes[0] == (byte) 0xFE
                                    && first3bytes[1] == (byte) 0xFF) {
                            reader = new BufferedReader(new InputStreamReader(in,
                                            "utf-16be"));
                    } else if (first3bytes[0] == (byte) 0xFF
                                    && first3bytes[1] == (byte) 0xFF) {
                            reader = new BufferedReader(new InputStreamReader(in,
                                            "utf-16le"));
                    } else {
                            reader = new BufferedReader(new InputStreamReader(in, "GBK"));
                    }
                    String str = reader.readLine();
                    while (str != null) {
                            text = text + str + "\n";
                            str = reader.readLine();
                    }
                    reader.close();
            } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            Log.e("VIEWTEXT", "convert");
            return text;
    }

	    public boolean onKeyDown(int keyCode, KeyEvent event)
		{	
			if(keyCode == KeyEvent.KEYCODE_BACK)
			{			
				if(!isTextChanged)
				{
					backToTab();
				}
				else
				{
					showDialog();
				}
				
			}
			return false;	
		}
	    
		private void backToTab()
		{
			Intent intent = new Intent(this, ViewPagerActivity.class);
			this.startActivity(intent);
			//writeFileSdcard(mTXTFilePath, mTXTEdit.getText().toString());
			this.finish();		
		}
		
		public void showDialog()
		{
			mDialog = new AlertDialog.Builder(this)
//			.setIcon(R.drawable.home)
			.setTitle("文档已修改")
			.setItems(new String[]{"放弃更改","保存","取消"}, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch(which)
					{
					//放弃
					case 0:
						backToTab();
						break;
					//保存
					case 1:
						ViewText.this.writeFileSdcard(mTXTFilePath, mTXTEdit.getText().toString());
						backToTab();
						break;
					//取消
					case 2:
						mDialog.dismiss();
						break;						
					}
				}
			}).create();
			mDialog.show();		
		}
				 
}