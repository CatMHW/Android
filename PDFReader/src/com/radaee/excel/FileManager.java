package com.radaee.excel;

import java.io.File;
import java.io.IOException;

import android.util.Log;

public class FileManager 
{
	private String mDirPath = null;
	private String mFileName = null;
	private String mFilePath = null;
	private String mDir = null;
//	private Document mDocument = null;
	
	public FileManager()
	{
		
	}
	/**
	 * 生成PDF的目录
	 */
	public String makeDir(String dir)
	{		
		String sdStateString = android.os.Environment.getExternalStorageState();
    	mDir = dir;
		
    	if(sdStateString.equals(android.os.Environment.MEDIA_MOUNTED))//判断SD卡是否存在
    	{
    		try
    		{
    			File sdFile = android.os.Environment.getExternalStorageDirectory();// 获取扩展存储设备的文件目录  
    			
    			//getAbsolutePath() : 返回抽象路径名的绝对路径名字符串。
    			//File.separator（静态常量）表示分隔符 Windows中为”\“，Linux中为”/“
    			mDirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX";
    			File dirFile1 = new File(mDirPath);
    			if(!dirFile1.exists())        //测试此抽象路径名表示的文件或目录是否存在。存在时返回true 
    			{
    				dirFile1.mkdir();         //创建此抽象路径名指定的目录。
    				Log.e("Soft", String.valueOf(dirFile1.exists()));
    			}
    			mDirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX"+ File.separator+mDir;
    			
    			
    			File dirFile = new File(mDirPath);
    			if(!dirFile.exists())        //测试此抽象路径名表示的文件或目录是否存在。存在时返回true 
    			{
    				dirFile.mkdir();         //创建此抽象路径名指定的目录。
    				Log.e("Soft", String.valueOf(dirFile.exists()));
    			}
    	   			
//    			File myFile = new File(path + File.separator + "my.html");
//    			
//    			if(!myFile.exists()){
//    				myFile.createNewFile();   //创建文件
//    			}
    			
    			//htmlPath = myFile.getAbsolutePath();
    		}
    		catch(Exception e){

    		}
    	}
    	return mDirPath;
	}
	
	/**
	 * @throws IOException
	 * 生成PDF的文件的路径,name为文件名，type为文件类型
	 */
	public String makeFilePath(String name, String type) throws IOException
	{
		if(mDirPath == null)
		{
			return null;
		}
		Log.e("FileManager", mDirPath);
		String dot = ".";
		mFileName = name;
		File myFile = new File(mDirPath + File.separator + mFileName+ dot + type);
		
		if(!myFile.exists())
		{
			myFile.createNewFile();   //创建文件
		}
		
		mFilePath = myFile.getAbsolutePath();
		return mFilePath;
	}	
	
	/**
	 * @param name 文件名，包括了文件类型后缀
	 * @return
	 * @throws IOException
	 */
	public String makeFilePath(String name) throws IOException
	{
		if(mDirPath == null)
		{
			return null;
		}
		Log.e("FileManager", mDirPath);
		String dot = ".";
		mFileName = name;
		File myFile = new File(mDirPath + File.separator + mFileName);
		
		if(!myFile.exists())
		{
			myFile.createNewFile();   //创建文件
		}
		
		mFilePath = myFile.getAbsolutePath();
		return mFilePath;
	}	
	
}
