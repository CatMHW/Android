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
	 * ����PDF��Ŀ¼
	 */
	public void makePdfDir()
	{		
		String sdStateString = android.os.Environment.getExternalStorageState();
    	
    	if(sdStateString.equals(android.os.Environment.MEDIA_MOUNTED))//�ж�SD���Ƿ����
    	{
    		try
    		{
    			File sdFile = android.os.Environment.getExternalStorageDirectory();// ��ȡ��չ�洢�豸���ļ�Ŀ¼  
    			
    			//getAbsolutePath() : ���س���·�����ľ���·�����ַ�����
    			//File.separator����̬��������ʾ�ָ��� Windows��Ϊ��\����Linux��Ϊ��/��
    			mPdfDirPath = sdFile.getAbsolutePath() + File.separator + "UIT-MAX"+ File.separator+"PDF";
    			
    			File dirFile = new File(mPdfDirPath);
    			if(!dirFile.exists())        //���Դ˳���·������ʾ���ļ���Ŀ¼�Ƿ���ڡ�����ʱ����true 
    			{
    				dirFile.mkdir();         //�����˳���·����ָ����Ŀ¼��
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
	}
	
	/**
	 * @throws IOException
	 * ����PDF���ļ���·��
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
			myFile.createNewFile();   //�����ļ�
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
