package com.radaee.word;



import java.io.IOException;

import com.radaee.excel.FileManager;
import com.radaee.reader.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class ViewWord extends Activity{
	
	private WebView mWebView = null;
	private String mWordHtmlPath = null;
	private String mWordFilePath = null;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.view);
		
		this.mWebView = (WebView)findViewById(R.id.show);
		this.mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		this.mWebView.getSettings().setBuiltInZoomControls(true); 
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		mWordFilePath = bundle.getString("name");
		
		makeHtml();
		
		this.mWebView.loadUrl("file:///"+ mWordHtmlPath);
		
	}
	
	public void makeHtml()
	{
		try {
			mWordHtmlPath = makeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileManager word = new FileManager();
		String wordPicDir = word.makeDir("WordPic");
		//WordToHtml toHtml = new WordToHtml();
		
		try {
			WordToHtml.convertHtml(mWordFilePath, mWordHtmlPath, wordPicDir, "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.e("ViewExcel", mExcelHtmlPath);
		
	}
	
	private String makeFile() throws IOException
	{
		FileManager word = new FileManager();
		word.makeDir("Word");
		return word.makeFilePath("WordHtml", "html");			
	}
	
	
}
