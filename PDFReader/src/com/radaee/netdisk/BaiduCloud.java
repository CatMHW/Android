package com.radaee.netdisk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.example.mytable.MyTable;
import com.radaee.excel.FileManager;
import com.radaee.main.PowerPointActivity;
import com.radaee.main.SoftWareCup;
import com.radaee.reader.HashMapRead;
import com.radaee.reader.PDFReaderAct;
import com.radaee.reader.R;
import com.radaee.reader.ViewFile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class BaiduCloud extends Activity {

	private static final int DOWNLOAD = 1;
	private static String TYPE = null;
	
	private final String mbApiKey = "xxG4gWfENyhF8DV0VkGAyQgw"; //为申请应用时获取的字符串
	private Button getAccessToken;//添加响应按钮
	private final String mbRootPath =  "/apps/UIT_MAX"; //用户测试的根目录
	private String mCloudDirPath = null;
	private Button login;
	private Button getQuota;
	private String mbOauth = null;
	
	private ListView mLv;
	private SimpleAdapter adapter;
	private ArrayList<String> pathList = new ArrayList<String>();	//存储云端的文件路径
	private  ArrayList<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>();
	private static boolean isLogin = false;
	private Button mLoginbtn;
	private FileManager fileManager = null;
	private String openFilePath = null;		//下载后保存的文件的路径
	private Editor mEditor;
	
	private File sdFile = android.os.Environment.getExternalStorageDirectory();
	private String DirPath = sdFile.getAbsolutePath() + File.separator + 
			"UIT-MAX"+ File.separator+"data"+File.separator;
	private String cloudDataPath = DirPath + "cloud.txt";
	private Map<String, String> history = new HashMap<String,String>();
	
	private Handler mbUiThreadHandler = new Handler()
    {
    	public void handleMessage(android.os.Message msg)
    	{
    		switch (msg.what) {
			case 1:
				AlertDialog dialog = new AlertDialog.Builder(BaiduCloud.this).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub		
						openFile();
						dialog.dismiss();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						dialog.dismiss();
					}
				}).setTitle("提示").setMessage("文档下载成功,要查看吗？").show();
				
				break;
			case 2:
				
			default:
				break;
			}
    	}
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.baidu_cloud);
				
		mLv = (ListView) findViewById(R.id.cloudLv);
		mLv.setOnItemClickListener(new OnItemClickListenerImpl());
		initListView();
		adapter = new SimpleAdapter(this, this.listItems,
				R.layout.cloud_list_item, 
				new String[]{"file_type","file_name"}, 
				new int[]{R.id.itemIcon, R.id.itemName});
		mLv.setAdapter(adapter);
		
		
		SharedPreferences sharedPreferences = getSharedPreferences("oauth", Context.MODE_PRIVATE);
		mEditor = sharedPreferences.edit();//获取编辑器
				
		makeDir();
		
		mLoginbtn = (Button) findViewById(R.id.login);
		mLoginbtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isLogin)
				{
					File myFile = new File(cloudDataPath);
					if(myFile.exists())
					{						
						myFile.delete();
						listItems.clear();
						pathList.clear();
					}
					try {
						myFile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					getFileList();
				}
				else
				{					
					startOAuth();
				}
			}
		});
		
		mLoginbtn.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //更改为按下时的背景图片     
                     v.setBackgroundResource(R.drawable.main_btn_pressed);  
                  //   Drawable drawable = getResources().getDrawable(R.drawable.setting_pressed);
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //改为抬起时的图片     
	                      v.setBackgroundResource(R.drawable.main_btn);   
	              }     
				return false;
			}
		});
		String oauth = sharedPreferences.getString("OAuth", "null");
		Log.e("oauth", ":  "+oauth.equals("null"));
		
		if(!oauth.equals("null"))
		{
			mbOauth = oauth;
			mLoginbtn.setText("同	    步");
			isLogin = true;
		}
	}
	private void initListView()
	{
		  if(fileIsExists(cloudDataPath))
	        {
	        	history = new HashMapRead().readObject(cloudDataPath);
	        	
	        	HashMap<String,Object> item;
	        	
	        	List<Map.Entry<String, String>> infoIds =
	    			    new ArrayList<Map.Entry<String, String>>(history.entrySet());
	    		

	        	for (int i = 0; i < infoIds.size(); i++) {
	        		
	        		item = new HashMap<String,Object>();
	        	    String path = infoIds.get(i).getKey();	        	 	       			
	       			String name = infoIds.get(i).getValue();
	       			
	       			pathList.add(path);
	       			
	       			if(path.endsWith(".doc"))	{
	       				item.put("file_type", R.drawable.word);
	       				item.put("file_name", name);
	       				listItems.add(item);
	       			}
	       			
	       			if(path.endsWith(".pdf"))	{
	       				item.put("file_type", R.drawable.pdf);
	       				item.put("file_name", name);
	       				listItems.add(item);
	       			}
	       			
	       			if(path.endsWith(".xls"))	{
	       				item.put("file_type", R.drawable.xls);
	       				item.put("file_name", name);
	       				listItems.add(item);
	       			}
	       			       	
	       			
	       			if(path.endsWith(".ppt") || path.endsWith(".pptx"))	{
	       				item.put("file_type", R.drawable.ppt);
	       				item.put("file_name", name);
	       				listItems.add(item);
	       			}
	        	}
	        }
	}
	
	private void makeDir()
	{
		fileManager = new FileManager();
		fileManager.makeDir("cloud");
		File myFile = new File(cloudDataPath);
		if(!myFile.exists())
		{
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
		
	/**
	 * 登录百度账号，并获取AccessToken
	 */
	public void startOAuth()
	{
		BaiduOAuth oauthClient = new BaiduOAuth();
		oauthClient.startOAuth(BaiduCloud.this, mbApiKey, new String[]{"basic", "netdisk"}, new BaiduOAuth.OAuthListener() {
		                
		public void onException(String msg) {
			Toast.makeText(getApplicationContext(), "登录失败 " + msg, Toast.LENGTH_SHORT).show();
		}
		             
		public void onComplete(BaiduOAuthResponse response) {
			if(null != response){
				mbOauth = response.getAccessToken();
				mEditor.putString("OAuth", mbOauth);
				mEditor.commit();
				Log.e("Token", mbOauth);
				Toast.makeText(getApplicationContext(), "Token: " + mbOauth +
						"    User name:" + response.getUserName(), Toast.LENGTH_SHORT).show();
				setListView();
				isLogin = true;
				mLoginbtn.setText("同	    步");
		    }
		}
		                
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "取消登录", Toast.LENGTH_SHORT).show();
		    }
		});
	}
	

	private void setListView()
	{		
		getFileList();						
	}
	
	private void test()
	{
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("file_type", R.drawable.word);
		item.put("file_name", "word");
		listItems.add(item);
	}
	
	private class OnItemClickListenerImpl implements OnItemClickListener
	{

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String path = pathList.get(position);
			Map<String, Object> item = new HashMap<String, Object>();
			item = listItems.get(position);
			String name  = (String) item.get("file_name");
			
			Log.e("name", ":"+name+"   path:"+path);
			if(name.endsWith(".doc"))
			{
				TYPE = "doc";
			}
			else if(name.endsWith(".xls"))
			{
				TYPE = "xls";
			}
			else if(name.endsWith(".pdf"))
			{
				TYPE = "pdf";
			}
			else if(name.endsWith(".ppt"))
			{
				TYPE = "ppt";
			}
			else if( name.endsWith(".pptx"))
			{
				TYPE = "pptx";
			}
			Log.e("type", ":"  + TYPE);
			
			cloudPath = path;
			fileName = name;
			
			showDialog();
			
		}		
	}
	
	/**
	 * @return 
	 */
	private boolean is = false;
	private String cloudPath = null;
	private String fileName = null;
	private boolean showDialog()
	{

		AlertDialog dialog = new AlertDialog.Builder(this).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				is = true;
				downLoad(cloudPath, fileName, TYPE);
				Log.e("is", "true");
				dialog.dismiss();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				is = false;
				dialog.dismiss();
			}
		}).setTitle("提示").setMessage("查看文档需要先下载，要继续吗？").show();
		return is;
	}
	
	long bs,tl;
	int count;
	private void downLoad(final String cloudPath, final String name, final String type)
	{
		
		if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		//String source = mbRootPath + "/189.jpg";
		    		Log.e("cloudPath", cloudPath);
					try {
						openFilePath = fileManager.makeFilePath(name);
						
						history.put(cloudPath, openFilePath);
					//	new HashMapRead(history, cloudDataPath);
						
						Log.e("openFilePath", openFilePath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
		    		final BaiduPCSActionInfo.PCSSimplefiedResponse ret = api.downloadFileFromStream(cloudPath, openFilePath, new BaiduPCSStatusListener(){
		    			//yangyangdd
		    			
						@Override
						public void onProgress(long bytes, long total) {
							// TODO Auto-generated method stub
							bs = bytes;
							tl = total;

							
					    	mbUiThreadHandler.post(new Runnable(){
					    			public void run(){
					    				Toast.makeText(getApplicationContext(), "文件大小: " + tl +
					    						"    已下载大小:" + bs, Toast.LENGTH_SHORT).show();
								    
					    			}
					    	});	
					    	
					    	if(bs == tl && count !=0)
					    	{
					    		Message msg = new Message();
								msg.what = DOWNLOAD;
								BaiduCloud.this.mbUiThreadHandler.sendMessage(msg);
								count=0;
					    	}
					    	count++;
					    	
						}
						
						@Override
						public long progressInterval(){
							return 500;
						}		    			

		    		});
		    		
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "Download files:  " 
		    						+ ret.errorCode + "   " + ret.message, Toast.LENGTH_SHORT).show();
		    				
		    				if(ret.errorCode == 0 && bs == tl)
					    	{
//					    		Message msg = new Message();
//								msg.what = DOWNLOAD;
//								BaiduCloud.this.mbUiThreadHandler.sendMessage(msg);
					    	}
		    				
		    			}
		    		});	
				}
			});
			 
    		workThread.start();
    	}
		
	}
	/**
	 * @param path 云端路径
	 * 将从云盘得到的文件列表加到我的List里
	 */
	private void addToList(String path)
	{
		String name = path.substring(path.lastIndexOf("/")+1,path.length());
		Log.e("name", name + "  type:"+ path.endsWith(".doc"));
		
		Map<String, Object> item = new HashMap<String, Object>();		
		if(path.endsWith(".doc"))
		{
			item.put("file_type", R.drawable.word);
			item.put("file_name", name);
			listItems.add(item);
			pathList.add(path);
			
			history.put(path,name);
			new HashMapRead(history, cloudDataPath);
		}
		else if(path.endsWith(".xls"))
		{
			item.put("file_type", R.drawable.xls);
			item.put("file_name", name);
			listItems.add(item);
			pathList.add(path);
			
			history.put(path,name);
			new HashMapRead(history, cloudDataPath);
		}
		else if(path.endsWith(".pdf"))
		{
			item.put("file_type", R.drawable.pdf);
			item.put("file_name", name);
			listItems.add(item);
			pathList.add(path);
			
			history.put(path,name);
			new HashMapRead(history, cloudDataPath);
						
		}
		else if(path.endsWith(".ppt") || path.endsWith(".pptx"))
		{
			item.put("file_type", R.drawable.ppt);
			item.put("file_name", name);
			listItems.add(item);
			pathList.add(path);
			
			history.put(path,name);
			new HashMapRead(history, cloudDataPath);
		}
		
	}
	private static int DATA_CHANGED = 2;
	/**
	 * 从云盘得到指定文件夹的文件列表
	 */
	private void getFileList()
	{
		if(null != mbOauth){

    		Thread workThread = new Thread(new Runnable(){
				public void run() {

		    		BaiduPCSClient api = new BaiduPCSClient();
		    		api.setAccessToken(mbOauth);
		    		String path = mbRootPath;

		    		final BaiduPCSActionInfo.PCSListInfoResponse ret = api.list(path, "name", "asc");
		    		//final PCSActionInfo.PCSListInfoResponse ret = api.imageStreamWithLimit(0, -1);
		    
		    		//得到文件的信息
		    		for(int i = 0; i < ret.list.size(); i++)
		    		{
			    		PCSCommonFileInfo fileInfo = ret.list.get(i);
			    		Log.e("FileInfo_PATH", fileInfo.path);
			    		addToList(fileInfo.path);
		    		}
//		    		Message msg = new Message();
//					msg.what = DATA_CHANGED;
//					BaiduCloud.this.mbUiThreadHandler.sendMessage(msg);
		    		//得到指定文件类型的所有文件列表  ？？？？？出错，空指针
		    	//	final BaiduPCSActionInfo.PCSListInfoResponse ret1 = api.streamWithSpecificMediaType("xls",0,1);
//		    		final BaiduPCSActionInfo.PCSListInfoResponse ret1 = api.docStream();
//		    		for(int i = 0; i < ret1.list.size(); i++)
//		    		{
//			    		fileInfo = ret1.list.get(i);
//			    		Log.e("FileInfo_Specific_path", fileInfo.path);
//		    		}
		    		mbUiThreadHandler.post(new Runnable(){
		    			public void run(){
		    				Toast.makeText(getApplicationContext(), "List:  " + ret.status.errorCode + "    " + ret.status.message, Toast.LENGTH_SHORT).show();
		    				adapter.notifyDataSetChanged();
		    			}
		    		});	
		    		
				}
			});
			 
    		workThread.start();
    	}
	}
	/**
	 * 取得空间配额信息
	 */
	private void test_getQuota(){
	
		if(null != mbOauth){
			    Thread workThread = new Thread(new Runnable(){
			public void run() {
				BaiduPCSClient api = new BaiduPCSClient();
				api.setAccessToken(mbOauth);
				final BaiduPCSActionInfo.PCSQuotaResponse info = api.quota();
		
			mbUiThreadHandler.post(new Runnable(){
				public void run(){
						if(null != info){
			if(0 == info.status.errorCode){
			Toast.makeText(getApplicationContext(), "Quota :" + info.total + "  used: " + info.used, Toast.LENGTH_SHORT).show();
		                            }
			else{
			Toast.makeText(getApplicationContext(), "Quota failed: " + info.status.errorCode + "  " + info.status.message, Toast.LENGTH_SHORT).show();
			                            }
		                        }
		                    }
		                });
		            }
		    }); 
		
			workThread.start();
	   }
	}
	
	   //
    //logout
    //
    public void  logout(){
    	if(null != mbOauth){
    		/**
    		 * you can call this method to logout in Android 4.0.3
    		 */
//    		BaiduOAuth oauth = new BaiduOAuth();
//    	    oauth.logout(mbOauth, new BaiduOAuth.ILogoutListener(){
//
//				@Override
//				public void onResult(boolean success) {
//
//					Toast.makeText(getApplicationContext(), "Logout: " + success, Toast.LENGTH_SHORT).show();
//				}
//    			
//    		});
    	
    	    /**
    	     * you can call this method to logout in Android 2.X
    	     */
    		Thread workThread = new Thread(new Runnable(){
				
				public void run() {
					
		    		BaiduOAuth oauth = new BaiduOAuth();
		    		final boolean ret = oauth.logout(mbOauth);
		    		mbUiThreadHandler.post(new Runnable(){
		    			
						public void run(){
	    					Toast.makeText(getApplicationContext(), "Logout " + ret, Toast.LENGTH_SHORT).show();
		    			}
		    		});	
		    		
				}
    		});
    		
    		workThread.start();
    	}
    	
    }
    
	private void openFile()
	{
		
		if(openFilePath.endsWith("ppt") || openFilePath.endsWith("pptx"))
		{
			Intent intent = new Intent(this, PowerPointActivity.class);
			intent.putExtra("name", openFilePath);
			startActivity(intent);			
			this.finish();
			
		}
		else if(openFilePath.endsWith("doc"))
		{
			Intent intent = new Intent(this, ViewFile.class);
			intent.putExtra("name", openFilePath);
			startActivity(intent);
			this.finish();
		}
		else if(openFilePath.endsWith("xls"))
		{
			Intent intent = new Intent(this, MyTable.class);
			intent.putExtra("name", openFilePath);
			Log.e("fileOpen", "true" + "  path:"+openFilePath);
			startActivity(intent);
			this.finish();
		}
		else if(openFilePath.endsWith("pdf"))
		{
			Intent intent = new Intent(this, PDFReaderAct.class);
			PDFReaderAct.path = openFilePath;
			PDFReaderAct.isMaked = false;
			startActivity(intent);
			this.finish();
		}
										
	}
	
    public boolean fileIsExists(String Path){
        File f=new File(Path);
        if(!f.exists()){
                return false;
        }
        return true;
    }
    
    /*处理点击返回按钮*/
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	   	switch(keyCode)
	   	{
	   	case KeyEvent.KEYCODE_BACK:

	   		this.backToMain();
	   		
	   		break;
	   	case KeyEvent.KEYCODE_MENU:
	   		break;
	   	}
		return false;	
	 }

	private void backToMain()
	{
		Intent intent = new Intent(BaiduCloud.this, SoftWareCup.class);
		this.startActivity(intent);
		this.finish();
		
		this.overridePendingTransition(R.anim.main_right_in,
				R.anim.main_right_out);
	}


}
