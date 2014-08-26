package com.radaee.reader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.example.mytable.MyTable;
import com.radaee.reader.MyListView.OnRefreshListener;
import com.radaee.reader.MyListView;
import com.radaee.excel.ViewExcel;
import com.radaee.main.PowerPointActivity;
import com.radaee.main.SoftWareCup;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
public class Search extends Activity {
	
	private MyListView i;
	boolean flag = false;
	private Map<String, String> history = new HashMap<String,String>();
	File sdFile = android.os.Environment.getExternalStorageDirectory();
	String DirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX"+ File.separator+"data"+File.separator;
	String docPath = DirPath+"doc.txt";
	String pdfPath = DirPath+"pdf.txt";
	String excelPath = DirPath+"excel.txt";
	String pptPath = DirPath+"ppt.txt";
	String hisPath = DirPath + "history.txt";
	
	private boolean searching = false;
	
	
	private void SearchSdcard(){
		File params = new File("/");
		File tempFile[] = params.listFiles();
		
		for (int x = 0; x < tempFile.length; x++) {
			if (tempFile[x].isDirectory()&&(tempFile[x].getPath().contains("card")||tempFile[x].getPath().contains("Card"))) {
				getAllFiles(tempFile[x]);
				Log.e("search", tempFile[x].getPath().toString());
			}
				
		}

		
	}
	
	
	private CategoryAdapter mCategoryAdapter = new CategoryAdapter() {
		@Override
		protected View getTitleView(String title, int index, View convertView, ViewGroup parent) {
			TextView titleView;
			
			if (convertView == null) {
				titleView = (TextView)getLayoutInflater().inflate(R.layout.title, null);
			} else {
				titleView = (TextView)convertView;
			}
			
			titleView.setText(title);
			
			return titleView;
		}
	};
	
	Comparator<File> comparator = new Comparator<File>(){
	      public int compare(File f1, File f2) {  
	   if(f1==null||f2==null){//先比较null 
	        if(f1==null){{         
	         return -1;
	        } 
	        }else{          
	         return 1;}
	        }
	    else{     
	     if(f1.isDirectory()==true && f2.isDirectory()==true){ //再比较文件夹    
	         return f1.getName().compareToIgnoreCase(f2.getName());
	       }
	    else{
	     if((f1.isDirectory() && !f2.isDirectory())==true){     
	      return -1;
	     }       
	     else if((f2.isDirectory() && !f1.isDirectory())==true){     
	      return 1;
	     }
	     else{
	      return f1.getName().compareToIgnoreCase(f2.getName());//最后比较文件
	     } 
	     }     
	    }   
	    }
	 
	 };

	
	 public void initail(){
		 	searching = false;
		 	setContentView(R.layout.search_title_list);
			MyListView listView = (MyListView)findViewById(R.id.searchtitle_list);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			 
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("img", R.drawable.arrow);
	        list.add(map);
	        
	        SimpleAdapter adapter = new SimpleAdapter(this,list,R.layout.search_title_data_list,
	        		new String[]{"img"},
	        		new int[]{R.id.search_title});
	        listView.setAdapter(adapter);
	        
	        
	        listView.setonRefreshListener(new OnRefreshListener() {
				public void onRefresh() {
					new AsyncTask<Void, Void, Void>() {
						protected Void doInBackground(Void... params) {
							
//							getAllFiles(sdFile);
							searching = true;
							SearchSdcard();
	                    	
	                    	Collections.sort(list1,comparator);
	                    	getMapData(list1);
	                    	Collections.sort(list2,comparator);
	                    	getMapData(list2);
	                    	Collections.sort(list3,comparator);
	                    	getMapData(list3);
	                    	Collections.sort(list4,comparator);
	                    	getMapData(list4);
	                    	
//	                        Recreate();
	                    	GlobalData.logonflag = true;
	                    	GlobalData.search_word = data1;
	                    	GlobalData.search_pdf = data2;
	                    	GlobalData.search_excel = data3;
	                    	GlobalData.search_ppt = data4;
	                    	
	                    	new ArrayListRead(data1, docPath);
	                    	new ArrayListRead(data2, pdfPath);
	                    	new ArrayListRead(data3, excelPath);
	                    	new ArrayListRead(data4, pptPath);
	                    	
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							setContentView(R.layout.search_list);
							i = (MyListView) findViewById(R.id.searchlist);
				            Recreate();
				            mCategoryAdapter.setData(data1.size(),data2.size());
				            i.setAdapter(mCategoryAdapter);
				            i.setOnItemClickListener(new OnItemClickListenerImpl()); 
							mCategoryAdapter.notifyDataSetChanged();
							i.onRefreshComplete();
							
							searching =false;
							
							i.setonRefreshListener(new OnRefreshListener() {
								public void onRefresh() {
									new AsyncTask<Void, Void, Void>() {
										protected Void doInBackground(Void... params) {
											
											list1 = new ArrayList<File>();
											list2 = new ArrayList<File>();
											list3 = new ArrayList<File>();
											list4 = new ArrayList<File>();
											
											
											data1 = new ArrayList<Map<String, Object>>();
											data2 = new ArrayList<Map<String, Object>>();
											data3 = new ArrayList<Map<String, Object>>();
											data4 = new ArrayList<Map<String, Object>>();
											
//											getAllFiles(sdFile);
											searching =true;
											SearchSdcard();
					                    	
					                    	Collections.sort(list1,comparator);
					                    	getMapData(list1);
					                    	Collections.sort(list2,comparator);
					                    	getMapData(list2);					                 
					                    	Collections.sort(list3,comparator);
					                    	getMapData(list3);
					                    	Collections.sort(list4,comparator);
					                    	getMapData(list4);
					                        Recreate();
					                    	GlobalData.logonflag = true;
					                    	GlobalData.search_word = data1;
					                    	GlobalData.search_pdf = data2;
					                    	GlobalData.search_excel = data3;
					                    	GlobalData.search_ppt = data4;
					                    	
					                    	new ArrayListRead(data1, docPath);
					                    	new ArrayListRead(data2, pdfPath);
					                    	new ArrayListRead(data3, excelPath);
					                    	new ArrayListRead(data4, pptPath);

											return null;
										}
					
										@Override
										protected void onPostExecute(Void result) {
											mCategoryAdapter.notifyDataSetChanged();
											i.setAdapter(mCategoryAdapter);
											i.onRefreshComplete();
											searching =false;
										}
					
									}.execute();
								}
							});
						}

					}.execute();
				}
			});

	        
	        
	 }

	 
	//data 
	private ArrayList<File> list1 = new ArrayList<File>();
	private ArrayList<File> list2 = new ArrayList<File>();
	private ArrayList<File> list3 = new ArrayList<File>();
	private ArrayList<File> list4 = new ArrayList<File>();	
	private ArrayList<Map<String, Object>> data1 = new ArrayList<Map<String, Object>>();
	private ArrayList<Map<String, Object>> data2 = new ArrayList<Map<String, Object>>();
	private ArrayList<Map<String, Object>> data3 = new ArrayList<Map<String, Object>>();
	private ArrayList<Map<String, Object>> data4 = new ArrayList<Map<String, Object>>();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       	
        
        if(GlobalData.searchflag){
        	data1 = GlobalData.search_word;
        	data2 = GlobalData.search_pdf;
        	data3 = GlobalData.search_excel;
        	data4 = GlobalData.search_ppt;
            Recreate();
            i.setAdapter(mCategoryAdapter);
            i.setOnItemClickListener(new OnItemClickListenerImpl());   	
        }
        
        if(fileIsExists(hisPath))
        {
        	history = new HashMapRead().readObject(hisPath);
        }
        
        if(fileIsExists(docPath) && fileIsExists(pdfPath) && fileIsExists(excelPath))
        {
        	searching = false;
        	setContentView(R.layout.search_list);
        	i = (MyListView) findViewById(R.id.searchlist);
        	data1 = new ArrayListRead().readObject(docPath);
        	data2 = new ArrayListRead().readObject(pdfPath);
        	data3 = new ArrayListRead().readObject(excelPath);
        	data4 = new ArrayListRead().readObject(pptPath);
        	
        	Recreate();
        	i.setAdapter(mCategoryAdapter);
        	i.setOnItemClickListener(new OnItemClickListenerImpl()); 
     		i.setonRefreshListener(new OnRefreshListener() {
     			public void onRefresh() {
     				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						list1 = new ArrayList<File>();
						list2 = new ArrayList<File>();
						list3 = new ArrayList<File>();
						list4 = new ArrayList<File>();
						
						
						data1 = new ArrayList<Map<String, Object>>();
						data2 = new ArrayList<Map<String, Object>>();
						data3 = new ArrayList<Map<String, Object>>();
						data4 = new ArrayList<Map<String, Object>>();
//						getAllFiles(sdFile);
						searching =true;
						SearchSdcard();
						
						Collections.sort(list1,comparator);
                  		getMapData(list1);
                  		Collections.sort(list2,comparator);
                  		getMapData(list2);
                  		Collections.sort(list3,comparator);
                  		getMapData(list3);
                  		Collections.sort(list4,comparator);
                  		getMapData(list4);
                  	
                      	Recreate();
                  		GlobalData.logonflag = true;
                  		GlobalData.search_word = data1;
                  		GlobalData.search_pdf = data2;
                  		GlobalData.search_excel = data3;
                  		GlobalData.search_ppt = data4;
                  	
                  		new ArrayListRead(data1, docPath);
                    	new ArrayListRead(data2, pdfPath);
                    	new ArrayListRead(data3, excelPath);
                    	new ArrayListRead(data4, pptPath);

						return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							mCategoryAdapter.notifyDataSetChanged();
							i.onRefreshComplete();
							 i.setAdapter(mCategoryAdapter);
							searching =false;
						}

					}.execute();
				}
     		});

        }else{
        	 initail();
        }
        
        
         
        
    }
    
    public boolean fileIsExists(String Path){
        File f=new File(Path);
        if(!f.exists()){
                return false;
        }
        return true;
}
    
    private void Recreate(){
    	mCategoryAdapter = new CategoryAdapter() {
    		@Override
    		protected View getTitleView(String title, int index, View convertView, ViewGroup parent) {
    			TextView titleView;
    			
    			if (convertView == null) {
    				titleView = (TextView)getLayoutInflater().inflate(R.layout.title, null);
    			} else {
    				titleView = (TextView)convertView;
    			}
    			
    			titleView.setText(title);
    			
    			return titleView;
    		}
    	};
    	
    	mCategoryAdapter.setData(data1.size(),data2.size());

    	if(data1.size() !=0)
        	mCategoryAdapter.addCategory("Word", new SimpleAdapter(this,
        			this.data1,
        			R.layout.search_data_list,
        			new String[]{"Itempic","ItemText","ItemTitle"},
        			new int[]{R.id.Itempic,R.id.ItemTitle,R.id.ItemText}));
        if(data2.size() !=0)
        	mCategoryAdapter.addCategory("PDF", new SimpleAdapter(this,
        			this.data2,
        			R.layout.search_data_list,
        			new String[]{"Itempic","ItemText","ItemTitle"},
        			new int[]{R.id.Itempic,R.id.ItemTitle,R.id.ItemText}));
        
        if(data3.size() !=0)
        	mCategoryAdapter.addCategory("Excel", new SimpleAdapter(this,
        			this.data3,
        			R.layout.search_data_list,
        			new String[]{"Itempic","ItemText","ItemTitle"},
        			new int[]{R.id.Itempic,R.id.ItemTitle,R.id.ItemText}));
        if(data3.size() !=0)
        	mCategoryAdapter.addCategory("PPT", new SimpleAdapter(this,
        			this.data4,
        			R.layout.search_data_list,
        			new String[]{"Itempic","ItemText","ItemTitle"},
        			new int[]{R.id.Itempic,R.id.ItemTitle,R.id.ItemText}));

        
    	
    }
   
    
    private class OnItemClickListenerImpl implements OnItemClickListener{


        	
        String Path ;
        int iposition;
		@SuppressLint("SimpleDateFormat")
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
		    
			if(searching==false){
			
			iposition = position;
			HashMap<String,Object> item;
			
			if(iposition >0 && iposition <=data1.size()+1 ){
				iposition -= 2;
				Log.e("position",String.valueOf(iposition));
				item = (HashMap<String, Object>)data1.get(iposition);
				Path = (String) item.get("ItemTitle");
			}else if (iposition > data1.size()+2 && iposition <= data1.size()+data2.size()+2){
				iposition -= 3;
				iposition -= data1.size();
				item = (HashMap<String, Object>)data2.get(iposition);
				Path = (String) item.get("ItemTitle");
			}else if (iposition > data1.size()+3 && iposition <= data1.size()+data2.size()+4){
				iposition -= 4;
				iposition -= data1.size();
				iposition -= data2.size();
				item = (HashMap<String, Object>)data3.get(iposition);
				Path = (String) item.get("ItemTitle");

			}else if (iposition > data1.size()+data2.size()+4){
				iposition -= 5;
				iposition -= data1.size();
				iposition -= data2.size();
				iposition -= data3.size();
				item = (HashMap<String, Object>)data4.get(iposition);
				Path = (String) item.get("ItemTitle");
				
			}
			
			
			Log.e("position",String.valueOf(iposition));
			Log.e("Path",Path.toString());
			
    		if(fileIsExists(Path)){
			
    			Intent i = new Intent();
			
    			Date date =new Date(System.currentTimeMillis());				//获取系统时间
    			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	//设置时间格式
    			String time = f.format(date);
			
    			if(Path.endsWith(".doc"))	{	
    				
    				i.setClass(Search.this, ViewFile.class);
    				i.putExtra("name",Path);


					history.put(Path,time);
					new HashMapRead(history, hisPath);

				
    				startActivity(i);
    				finish();
    				Search.this.overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
    			}
    			else if(Path.endsWith(".pdf")){
    				Intent intent = new Intent(Search.this, PDFReaderAct.class);
    				PDFReaderAct.path = Path;
    				PDFReaderAct.isMaked = false;

					history.put(Path,time);
					new HashMapRead(history, hisPath);
                
    				startActivity(intent);
    				finish();
    				Search.this.overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
    			}else if(Path.endsWith(".xls")){
				
					history.put(Path,time);
					new HashMapRead(history, hisPath);
            
    				i.setClass(Search.this, MyTable.class);
    				i.putExtra("name",Path);
    				startActivity(i);
    				finish();
    				Search.this.overridePendingTransition(R.anim.push_left_in,
							R.anim.push_left_out);
    			}else if( Path.endsWith(".ppt") ) {
    				i.setClass(Search.this, PowerPointActivity.class);						
    				i.putExtra("name",Path);
    			history.put(Path,time);
    			new HashMapRead(history, hisPath);
    			
    			
    			startActivity(i);
    			finish();
    			Search.this.overridePendingTransition(R.anim.push_left_in,
    					R.anim.push_left_out);
    		}
    			else if( Path.endsWith(".pptx") ) {
    				i.setClass(Search.this, PowerPointActivity.class);						
    				i.putExtra("name",Path);
        			history.put(Path,time);
        			new HashMapRead(history, hisPath);
        			
        			
        			startActivity(i);
        			finish();
        			Search.this.overridePendingTransition(R.anim.push_left_in,
        					R.anim.push_left_out);
        		}
    		}
    		else{
    			AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);  
    			builder.setMessage("This file is not exist!")  
    			       .setCancelable(false)  
    			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {  
    			           public void onClick(DialogInterface dialog, int id) {
    			        	   if(iposition >0 && iposition <=data1.size()+1 ){
    			   					iposition -= 2;
    			   					data1.remove(iposition);
    			        	   }else if (iposition > data1.size()+2 && iposition <= data1.size()+data2.size()+2){
    			   					iposition -= 3;
    			   					iposition -= data1.size();
    			   					data2.remove(iposition);
    			   				}else if (iposition > data1.size()+3 && iposition <= data1.size()+data2.size()+4){
    			   					iposition -= 4;
    			   					iposition -= data1.size();
    			   					iposition -= data2.size();
    			   					data3.remove(iposition);

    			   				}else if (iposition > data1.size()+data2.size()+4){
    			   					iposition -= 5;
    			   					iposition -= data1.size();
    			   					iposition -= data2.size();
    			   					iposition -= data3.size();
    			   					data4.remove(iposition);

    			   				}
    			        	   Recreate();
    			        	   mCategoryAdapter.notifyDataSetChanged();
    			        	   i.onRefreshComplete();
 
    			           }  
    			       }); 
    			builder.create().show();
    		}
		}
		}
    }
 
    private void getMapData(ArrayList<File> list){
    	HashMap<String,Object> item;
        int i = 0 ;
        for(i=0;i<list.size();i++){
                item = new HashMap<String,Object>();
                String path  = list.get(i).toString();
                String name = path.substring(path.lastIndexOf("/")+1,path.length());
                //保存每一格list单元格的数据 ，
                if(name.indexOf(".doc")>0)
                {
                	item.put("Itempic", R.drawable.word);
                	item.put("ItemText",name);
                    item.put("ItemTitle",path);

                    data1.add(item);
                }
                else if(name.indexOf(".pdf")>0)
                {
                	item.put("Itempic", R.drawable.pdf);
                	item.put("ItemText",name);
                    item.put("ItemTitle",path);
                    data2.add(item);
                }
                else if(name.indexOf(".xls")>0)
                {
                	item.put("Itempic", R.drawable.xls);
                	item.put("ItemText",name);
                    item.put("ItemTitle",path);
                    data3.add(item);
                }
                else
                {
                	item.put("Itempic", R.drawable.ppt);
                	item.put("ItemText",name);
                    item.put("ItemTitle",path);
                    data4.add(item);
                }
                
               
        }
    }    
    
    private void getAllFiles(File root){
		
		File files[] = root.listFiles();
		
		if(files != null)
			for(File f:files){
				if(f.isDirectory()){
					getAllFiles(f);
				}
				else{
//					if(f.getName().endsWith(".doc")||f.getName().endsWith(".pdf")||f.getName().endsWith(".xls"))
//					{	
//						Log.e("search", f.getName());
//					}
					
					if((f.getName().indexOf(".doc")>0 ) && f.getName().indexOf(".docx")<=0)
					{
						this.list1.add(f);
						

					}
					
					if( f.getName().indexOf(".pdf")>0)
					{
						this.list2.add(f);
						
					}
					if(f.getName().indexOf(".xls")>0  && f.getName().indexOf(".xlsx")<=0)
					{
						this.list3.add(f);
						
					}
					if(f.getName().indexOf(".ppt")>0  || f.getName().indexOf(".pptx")>0)
					{
						this.list4.add(f);
						
					}
				}
			}
    }

    
    @Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) { //监控返回键
			Intent intent = new Intent(Search.this, SoftWareCup.class);
			startActivity(intent);
			this.finish();
			this.overridePendingTransition(R.anim.main_right_in,
					R.anim.main_right_out);
		}
		return true;
	}	
    
}