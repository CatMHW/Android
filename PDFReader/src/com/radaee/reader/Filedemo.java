package com.radaee.reader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.example.mytable.MyTable;
import com.radaee.excel.FileManager;
import com.radaee.excel.ViewExcel;
import com.radaee.main.PowerPointActivity;
import com.radaee.main.SoftWareCup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;



public class Filedemo  extends Activity   {

	private ListView fileList;
	LinearLayout is;
	private TextView tv;
	private SimpleAdapter simple = null;
	private List<Map<String,Object>> allFileItems = new ArrayList<Map<String,Object>>();
	private List<String> paths = null;//存放路径 
	private File temp = null;
	private Map<String, String> history = new HashMap<String,String>();
	private Boolean flag_sdcard =false;
	private Boolean flag_sdcard_out =false;
	private File filePath;
	private List<String> sdcard = null;
	
	File sdFile = android.os.Environment.getExternalStorageDirectory();
	String DirPath = sdFile.getAbsolutePath() + File.separator + 
			"UIT-MAX"+ File.separator+"data"+File.separator;
	String docPath = DirPath+"doc.txt";
	String pdfPath = DirPath+"pdf.txt";
	String excelPath = DirPath+"excel.txt";
	String pptPath = DirPath+"ppt.txt";
	String hisPath = DirPath + "history.txt";
	
	Comparator<File> comparator = new Comparator<File>(){
		public int compare(File f1, File f2) {  
			if(f1==null||f2==null)//先比较null
	   
				if(f1==null)       
					return -1;
				else         
					return 1;
	   
			else
				
				if(f1.isDirectory()==true&&f2.isDirectory()==true) //再比较文件夹    
					return f1.getName().compareToIgnoreCase(f2.getName());
	    	
				else
					if((f1.isDirectory()&&!f2.isDirectory())==true)  
						return -1;
	    	      
					else if((f2.isDirectory()&&!f1.isDirectory())==true)    
						return 1;
	    	
					else
						return f1.getName().compareToIgnoreCase(f2.getName());//最后比较文件
	     	 
		}
	};

	public void initail(){
		ListView listView;
		File params = new File("/");
		File tempFile[] = params.listFiles();
		sdcard =new ArrayList<String>();
		List<String> data = new ArrayList<String>();
		
		for (int x = 0; x < tempFile.length; x++) {
			if (tempFile[x].isDirectory()&&(tempFile[x].getPath().contains("card")||tempFile[x].getPath().contains("Card"))) {
				sdcard.add(tempFile[x].getPath());
				data.add(tempFile[x].getName());
			}
				
		}
		
		
        listView = new ListView(this);
        listView.setAdapter((ListAdapter) new ArrayAdapter<String>(this, 
        		android.R.layout.simple_expandable_list_item_1,
        		data));
        listView.setDivider(getResources().getDrawable(R.drawable.liner));
        listView.setDividerHeight(1);
        listView.setBackgroundResource(R.drawable.list_bg);
        listView.setCacheColorHint(0) ;
        setContentView(listView);
        flag_sdcard=false;
       // listView.setBackgroundColor(getResources().getColor(R.color.white));
        listView.setOnItemClickListener(new OnItemClickListenerImpl());  
        
	}
	
	public void read_sdcard(){
		setContentView(R.layout.file_list);
        
		this.tv = (TextView) this.findViewById(R.id.load);  
		this.fileList = (ListView)super.findViewById(R.id.datalist);
		
		
		tv.setText(filePath.getName());
		doInBackground(filePath);
		
		this.fileList.setOnItemClickListener(new OnItemClickListenerImpl());  
	
		if(fileIsExists(hisPath))
		{
			history = new HashMapRead().readObject(hisPath);
		}
		
	}

    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	initail();
    	
    		
    }
    
    public boolean fileIsExists(String Path){
        File f=new File(Path);
        if(!f.exists()){
                return false;
        }
        return true;
    }

    
    private class OnItemClickListenerImpl implements OnItemClickListener{

    	//***@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(flag_sdcard){
				String path = paths.get(position);    
				File currFile = new File(path);
	        
	        
				if (currFile.isDirectory()) { // 当前是一个目录
					Filedemo.this.allFileItems = new ArrayList<Map<String,Object>>() ;
					doInBackground(currFile);
					Filedemo.this.tv.setText(currFile.toString());
				}
				else
				{
					String name = path.substring(path.lastIndexOf("/")+1,path.length());
				
					Date date =new Date(System.currentTimeMillis());				//获取系统时间
					SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	//设置时间格式
					String time = f.format(date);		
					Intent i = new Intent();
					File sdFile = android.os.Environment.getExternalStorageDirectory();
				
					if(path.endsWith(".doc"))	
					{	
					
						i.setClass(Filedemo.this, ViewFile.class);
						i.putExtra("name",path);

						
						
						history.put(path,time);
						new HashMapRead(history, hisPath);
						
						

					
						startActivity(i);
//						Intent intent = new Intent(Filedemo.this, PDFReaderAct.class);
//						PDFReaderAct.path = path;
//					
//
//						history.put(path,time);
//						new HashMapRead(history, hisPath);
//	                
//						startActivity(intent);
//						finish();
//						Filedemo.this.overridePendingTransition(R.anim.push_left_in,
//								R.anim.push_left_out);
					}
					else if(path.endsWith(".xls") || path.endsWith(".xlsx"))
					{
					


						history.put(path,time);
						new HashMapRead(history, hisPath);
						
						i.setClass(Filedemo.this, MyTable.class);
						i.putExtra("name",path);
						startActivity(i);
						finish();
						Filedemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					}else if(path.endsWith(".txt")){
						i.setClass(Filedemo.this, ViewText.class);
						i.putExtra("name",path);
						
						history.put(path,time);
						new HashMapRead(history, hisPath);
						
						startActivity(i);
						finish();
						Filedemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						
					}
					else if(path.endsWith(".pdf")){
						i.setClass(Filedemo.this, PDFReaderAct.class);
						PDFReaderAct.path = path;
						PDFReaderAct.isMaked = false;

						history.put(path,time);
						new HashMapRead(history, hisPath);
	                
	    				startActivity(i);
	    				finish();
	    				Filedemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
						
					}
					else if(path.endsWith(".ppt") || path.endsWith(".pptx")){
						i.setClass(Filedemo.this, PowerPointActivity.class);						
	    				i.putExtra("name",path);
						
						history.put(path,time);
						new HashMapRead(history, hisPath);
						
						startActivity(i);
						finish();
						Filedemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					}

				}
			}else{
					flag_sdcard = true;
					filePath = new File(sdcard.get(position));
					read_sdcard();
				
			}
			
		}
	}
    
		protected void doInBackground(File params) {
			
			allFileItems = new ArrayList<Map<String,Object>>();
			
			paths = new ArrayList<String>();
			this.temp=params;
			File tempFile[] = params.listFiles();
			List<File> list=Arrays.asList(tempFile);
			Collections.sort(list,comparator);
			int size=list.size();
			tempFile = (File[])list.toArray(new File[size]); 
			
			
			if (!params.getPath().equals(filePath.getPath())) { // 不是根目录
				Map<String,Object> fileItem = new HashMap<String,Object>() ;	// 表示可以返回
				fileItem.put("img",R.drawable.dir) ;	// 可以返回
				fileItem.put("name", ".....") ;
				fileItem.put("count"," ") ;
				paths.add(params.getParent());
				Filedemo.this.allFileItems.add(fileItem) ;
			}
			if(tempFile != null) {
				for (int x = 0; x < tempFile.length; x++) {
					Map<String,Object> fileItem = new HashMap<String,Object>() ;	// 表示可以返回
					
					
					String path  = tempFile[x].getPath();
					String name = path.substring(path.lastIndexOf("/")+1,path.length());
					if (tempFile[x].isDirectory()&& name.indexOf(".")!=0) {
						String str = countfiles(tempFile[x].getPath());
						paths.add(tempFile[x].getAbsolutePath());
						fileItem.put("img", R.drawable.dir);
						fileItem.put("name", name) ;
						fileItem.put("count",str) ;// 文件夹
						
						Filedemo.this.allFileItems.add(fileItem) ;
					} else if( name.indexOf(".")!=0){	// 是文件
						
						
						if(tempFile[x].getName().endsWith(".doc"))
						{	paths.add(tempFile[x].getAbsolutePath());
							fileItem.put("img",R.drawable.word) ;
						}
						else if(tempFile[x].getName().endsWith(".pdf"))
						{
							paths.add(tempFile[x].getAbsolutePath());
							fileItem.put("img",R.drawable.pdf) ;
						}
						else if(tempFile[x].getName().endsWith(".xls"))
						{
							paths.add(tempFile[x].getAbsolutePath());
							fileItem.put("img",R.drawable.xls) ;
						}
						else if(tempFile[x].getName().indexOf(".txt")>0)
						{
							paths.add(tempFile[x].getAbsolutePath());
							fileItem.put("img",R.drawable.txt) ;
						}
						else if(tempFile[x].getName().indexOf(".ppt")>0||tempFile[x].getName().indexOf(".pptx")>0)
						{
							paths.add(tempFile[x].getAbsolutePath());
							fileItem.put("img",R.drawable.ppt) ;
						}
//						else
//						{
//							fileItem.put("img",R.drawable.unknow) ;
//						}
						if(tempFile[x].getName().endsWith(".doc")||tempFile[x].getName().endsWith(".pdf")||
								tempFile[x].getName().endsWith(".txt")||tempFile[x].getName().endsWith(".xls")
								||tempFile[x].getName().endsWith(".ppt")||tempFile[x].getName().endsWith(".pptx")){
						
							fileItem.put("name", name) ;
							fileItem.put("count"," ") ;
						
							Filedemo.this.allFileItems.add(fileItem) ;
						}
					} 
					
				}
			}
			
			Filedemo.this.simple = new SimpleAdapter(
					Filedemo.this,
					allFileItems,
					R.layout.data_list, 
					new String[] { "img", "name","count" },
					new int[] { R.id.pic, R.id.file_name,R.id.count });
			Filedemo.this.fileList.setAdapter(Filedemo.this.simple);
		}
		
		private String countfiles(String path) {
			File params = new File(path);
			try{
				File tempFile[] = params.listFiles();
				if(tempFile.length==0 || tempFile==null)
				{
					return " ";
				}else 
				{
					int count = tempFile.length;
					for (int x = 0; x < tempFile.length; x++) {
						String name = tempFile[x].getName();
						if (name.indexOf(".")==0) {
							count--;
						}
							
					}
					if(count>0){
						String str = Integer.toString(count);
						return str;				
					}else{
						return " ";
					}
					
				}
			}catch(Exception e){
				Filedemo.this.tv.setText(e.getMessage());
				return " ";
			}
		}

		@Override 
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_BACK) { //监控返回键
				File params = temp;
				if(temp!=null &&filePath!=null ){
				if (!params.getPath().equals(filePath.getPath())) {
					params = new File(paths.get(0));
					Filedemo.this.allFileItems = new ArrayList<Map<String,Object>>() ;
					doInBackground(params);
					Filedemo.this.tv.setText(temp.getPath());
				}else{
					if(!flag_sdcard){
						Intent intent = new Intent(Filedemo.this, SoftWareCup.class);
						startActivity(intent);
						this.finish();
						this.overridePendingTransition(R.anim.main_right_in,
								R.anim.main_right_out);
					}else{
						
						initail();
						flag_sdcard = false;
					}
				}
				}else{
					Intent intent = new Intent(Filedemo.this, SoftWareCup.class);
					startActivity(intent);
					this.finish();
					this.overridePendingTransition(R.anim.main_right_in,
							R.anim.main_right_out);
				}
			}
			return true;
		}
		
		/** 
		 * 双击退出函数 
		 */  
		private static Boolean isExit = false;  
		  
		private void exitBy2Click() {  
		    Timer tExit = null;  
		    if (isExit == false) {  
		        isExit = true; // 准备退出  
		        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
		        tExit = new Timer();  
		        tExit.schedule(new TimerTask() {  
		            @Override  
		            public void run() {  
		                isExit = false; // 取消退出  
		            }  
		        }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  
		  
		    } else {  
		        finish();  
		        System.exit(0);  
		    }  
		}
		
		

}
        
  
    
