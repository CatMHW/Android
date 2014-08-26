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
	 * ����PDF��Ŀ¼
	 */
	public String makeDir(String dir)
	{		
		String sdStateString = android.os.Environment.getExternalStorageState();
    	mDir = dir;
		
    	if(sdStateString.equals(android.os.Environment.MEDIA_MOUNTED))//�ж�SD���Ƿ����
    	{
    		try
    		{
    			File sdFile = android.os.Environment.getExternalStorageDirectory();// ��ȡ��չ�洢�豸���ļ�Ŀ¼  
    			
    			//getAbsolutePath() : ���س���·�����ľ���·�����ַ�����
    			//File.separator����̬��������ʾ�ָ��� Windows��Ϊ��\����Linux��Ϊ��/��
    			mDirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX";
    			File dirFile1 = new File(mDirPath);
    			if(!dirFile1.exists())        //���Դ˳���·������ʾ���ļ���Ŀ¼�Ƿ���ڡ�����ʱ����true 
    			{
    				dirFile1.mkdir();         //�����˳���·����ָ����Ŀ¼��
    				Log.e("Soft", String.valueOf(dirFile1.exists()));
    			}
    			mDirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX"+ File.separator+mDir;
    			
    			
    			File dirFile = new File(mDirPath);
    			if(!dirFile.exists())        //���Դ˳���·������ʾ���ļ���Ŀ¼�Ƿ���ڡ�����ʱ����true 
    			{
    				dirFile.mkdir();         //�����˳���·����ָ����Ŀ¼��
    				Log.e("Soft", String.valueOf(dirFile.exists()));
    			}
    	   			
//    			File myFile = new File(path + File.separator + "my.html");
//    			
//    			if(!myFile.exists()){
//    				myFile.createNewFile();   //�����ļ�
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
	 * ����PDF���ļ���·��,nameΪ�ļ�����typeΪ�ļ�����
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
			myFile.createNewFile();   //�����ļ�
		}
		
		mFilePath = myFile.getAbsolutePath();
		return mFilePath;
	}	
	
	/**
	 * @param name �ļ������������ļ����ͺ�׺
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
			myFile.createNewFile();   //�����ļ�
		}
		
		mFilePath = myFile.getAbsolutePath();
		return mFilePath;
	}	
	
}
