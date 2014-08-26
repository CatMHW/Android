package com.radaee.reader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.example.mytable.MyTable;
import com.radaee.excel.ViewExcel;
import com.radaee.main.PowerPointActivity;
import com.radaee.main.SoftWareCup;
import com.radaee.word.ViewWord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Historydemo extends Activity {
	
	private ListView i ;
	private ArrayList<Map<String, Object>> historydata = new ArrayList<Map<String, Object>>();
	private Map<String, String> history;
	private SimpleAdapter ladapter;


	File sdFile = android.os.Environment.getExternalStorageDirectory();
	String DirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX"+ File.separator+"data"+File.separator;
	String hisPath = DirPath + "history.txt";
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.history_list);
	        
	        
	        i = (ListView) findViewById(R.id.historylist);
	        
	      
	        
	        if(fileIsExists(hisPath))
	        {
	        	history = new HashMapRead().readObject(hisPath);
	        	
	        	HashMap<String,Object> item;
	        	
	        	List<Map.Entry<String, String>> infoIds =
	    			    new ArrayList<Map.Entry<String, String>>(history.entrySet());
	    		
	    		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {   
	    		    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {      
	    		    	return (o2.getValue()).compareTo(o1.getValue());
	    		    }
	    		});
	        	
	        	for (int i = 0; i < infoIds.size(); i++) {
	        		item = new HashMap<String,Object>();
	        	    String path = infoIds.get(i).getKey();
	        	    String time = infoIds.get(i).getValue();
	       			
	       			String name = path.substring(path.lastIndexOf("/")+1,path.length());
	       			
	       			if(path.endsWith(".doc"))	{
	       				item.put("Itempic", R.drawable.word);
	       				item.put("ItemText",name);
	       				item.put("ItemTime",time);
	       				item.put("ItemTitle",path);
	       				Log.i("qwe", time);
	       				historydata.add(item);
	       			}
	       			
	       			if(path.endsWith(".pdf"))	{
	       				item.put("Itempic", R.drawable.pdf);
	       				item.put("ItemText",name);
	       				item.put("ItemTime",time);
	       				item.put("ItemTitle",path);
	       				historydata.add(item);
	       			}
	       			
	       			if(path.endsWith(".xls"))	{
	       				item.put("Itempic", R.drawable.xls);
	       				item.put("ItemText",name);
	       				item.put("ItemTime",time);
	       				item.put("ItemTitle",path);
	       				historydata.add(item);
	       			}
	       			
	       			if(path.endsWith(".txt"))	{
	       				item.put("Itempic", R.drawable.txt);
	       				item.put("ItemText",name);
	       				item.put("ItemTime",time);
	       				item.put("ItemTitle",path);
	       				historydata.add(item);
	       			}
	       			
	       			if(path.endsWith(".ppt") || path.endsWith(".pptx"))	{
	       				item.put("Itempic", R.drawable.ppt);
	       				item.put("ItemText",name);
	       				item.put("ItemTime",time);
	       				item.put("ItemTitle",path);
	       				historydata.add(item);
	       			}
	       			
	       		}
	        	
	        	ladapter = new SimpleAdapter(this,
        		this.historydata,
        		R.layout.history_data_list,
				new String[]{"Itempic","ItemText","ItemTime"},
				new int[]{R.id.Hispic,R.id.HisTitle,R.id.HisTime});
	        	i.setAdapter(ladapter);
	        	i.setOnItemClickListener(new OnItemClickListenerImpl());

	        }
	        	
	        
	 }

	 public boolean fileIsExists(String Path){
		 File f=new File(Path);
	     if(!f.exists()){
	                return false;
	        }
	        return true;
	}
	 int iposition;
	 private class OnItemClickListenerImpl implements OnItemClickListener{

	    	//***@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				iposition = position;
				
				Map<String,String> map = (Map<String,String>)ladapter.getItem(position);
				String	Path = map.get("ItemTitle");
				
				if(fileIsExists(Path)){
					Intent i = new Intent();
				
					Date date =new Date(System.currentTimeMillis());				//获取系统时间
					SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	//设置时间格式
					String time = f.format(date);
				
					if(Path.endsWith(".doc"))	{	
					
						i.setClass(Historydemo.this, ViewFile.class);
						i.putExtra("name",Path);


						history.put(Path,time);
						new HashMapRead(history, hisPath);

					
						startActivity(i);
						finish();
						Historydemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					}
					else if(Path.endsWith(".pdf")){
						Intent intent = new Intent(Historydemo.this, PDFReaderAct.class);
						PDFReaderAct.path = Path;
						PDFReaderAct.isMaked = false;

						history.put(Path,time);
						new HashMapRead(history, hisPath);
	                
						startActivity(intent);
						finish();
						Historydemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					}else if(Path.endsWith(".xls") || Path.endsWith(".xlsx"))
					{
					


						history.put(Path,time);
						new HashMapRead(history, hisPath);
						
						i.setClass(Historydemo.this, MyTable.class);
						i.putExtra("name",Path);
						startActivity(i);
						finish();
						Historydemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					}
					else if(Path.endsWith(".txt"))
					{
					


						history.put(Path,time);
						new HashMapRead(history, hisPath);
						
						i.setClass(Historydemo.this, ViewText.class);
						i.putExtra("name",Path);
						startActivity(i);
						finish();
						Historydemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					}
					else if(Path.endsWith(".ppt") || Path.endsWith(".pptx"))
					{
					


						history.put(Path,time);
						new HashMapRead(history, hisPath);
						
						
						i.setClass(Historydemo.this, PowerPointActivity.class);	
						i.putExtra("name",Path);
						startActivity(i);
						finish();
						Historydemo.this.overridePendingTransition(R.anim.push_left_in,
								R.anim.push_left_out);
					}


				}else{
	    			AlertDialog.Builder builder = new AlertDialog.Builder(Historydemo.this);  
	    			builder.setMessage("This file is not exist!")  
	    			       .setCancelable(false)  
	    			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
	    			           public void onClick(DialogInterface dialog, int id) {
	    			        	   historydata.remove(iposition);
	    			        	   ladapter.notifyDataSetChanged();
	 
	    			           }  
	    			       }); 
	    			builder.create().show();
	    		
				}
				}

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
			Intent intent = new Intent(Historydemo.this, SoftWareCup.class);
			this.startActivity(intent);
			this.finish();
			
			this.overridePendingTransition(R.anim.main_right_in,
					R.anim.main_right_out);
		}
}
