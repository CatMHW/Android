package com.radaee.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class CreatPdf 
{
	private String mPdfDirPath = null;
	private String mPdfName = null;
	private String mPdfPath = null;
//	private Document mDocument = null;
	
	public CreatPdf()
	{
		
	}
	/**
	 * 生成PDF的目录
	 */
	public void makePdfDir()
	{		
		String sdStateString = android.os.Environment.getExternalStorageState();
    	
    	if(sdStateString.equals(android.os.Environment.MEDIA_MOUNTED))//判断SD卡是否存在
    	{
    		try
    		{
    			File sdFile = android.os.Environment.getExternalStorageDirectory();// 获取扩展存储设备的文件目录  
    			
    			//getAbsolutePath() : 返回抽象路径名的绝对路径名字符串。
    			//File.separator（静态常量）表示分隔符 Windows中为”\“，Linux中为”/“
    			mPdfDirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX"+ File.separator+"PDF";
    			
    			File dirFile = new File(mPdfDirPath);
    			if(!dirFile.exists())        //测试此抽象路径名表示的文件或目录是否存在。存在时返回true 
    			{
    				dirFile.mkdir();         //创建此抽象路径名指定的目录。
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
	}
	
	/**
	 * @throws IOException
	 * 生成PDF的文件的路径
	 */
	public String makePdfFilePath(String name) throws IOException
	{
		if(mPdfDirPath == null)
		{
			return null;
		}
		
		mPdfName = name;
		File myFile = new File(mPdfDirPath + File.separator + mPdfName+".pdf");
		
		if(!myFile.exists())
		{
			myFile.createNewFile();   //创建文件
		}
		
		mPdfPath = myFile.getAbsolutePath();
		return mPdfPath;
	}
	
	/**
	 * @return
	 */
	public String getPdfPath()
	{
		return mPdfPath;
	}
	
	public void setPdfName(String name)
	{
		mPdfName = name;
	}
	
	public String getPdfName()
	{
		return mPdfName;
	}
	
}
