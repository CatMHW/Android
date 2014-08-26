package com.radaee.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.radaee.reader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class ViewExcel extends Activity
{
	private WebView mWebView = null;
	private String mExcelHtmlPath = null;
	private String mExcelFilePath = null;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.view);
		
		this.mWebView = (WebView)findViewById(R.id.show);
		this.mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		this.mWebView.getSettings().setBuiltInZoomControls(true); 
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		mExcelFilePath = bundle.getString("name");
		
		makeHtml();
		
		this.mWebView.loadUrl("file:///"+ mExcelHtmlPath);
		
	}
	
	public void makeHtml()
	{
		try {
			mExcelHtmlPath = makeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Log.e("ViewExcel", mExcelHtmlPath);
		makeHtml(mExcelFilePath, mExcelHtmlPath);
	}
	
	
	 public void makeHtml(String excelPath, String htmlPath)
	 {
	    	ToHtml toHtml = null;
	    	try {
				 toHtml = ToHtml.create(new FileInputStream(excelPath),
						new PrintWriter( new OutputStreamWriter(new FileOutputStream(htmlPath), "UTF-8")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        toHtml.setCompleteHTML(true);
	        try {
	        	toHtml.printPage();
	        } catch (IOException e) {
	        	// TODO Auto-generated catch block
	        	e.printStackTrace();
	        }  	
	}
	 
	private String makeFile() throws IOException
	{
		FileManager excel = new FileManager();
		excel.makeDir("Html");
		return excel.makeFilePath("WordHtml", "html");			
	}
}
