package com.radaee.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.radaee.main.SoftWareCup;
import com.radaee.rotate.RotatePop;
import com.reader.setting.Reading;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;


public class ViewFile extends Activity{
	
	private String nameStr = null;
	private Range range = null;
	private HWPFDocument hwpf = null;
	public static String htmlPath;			//����ģʽ
	public static String htmlPath1;			//ҹ��ģʽ��html
	private String picturePath;
	private WebView view;
	private List pictures;
	private TableIterator tableIterator;
	private int presentPicture = 0;
	private int screenWidth; //��Ļ���
	private FileOutputStream output;
	private FileOutputStream output1;
	private File myFile;
	private File myFile1;
	private ScreenAdjust mPopMenu;
	private RotatePop mRotatePop = null;

	
	//private EditText view;
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//ȥ�� ������
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.view);
		
		this.view = (WebView)findViewById(R.id.show);
		
		
		this.view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		this.view.setHorizontalScrollBarEnabled(false);
		WebSettings webSettings =view.getSettings();
	 	webSettings.setJavaScriptEnabled(true);
	 	webSettings.setBuiltInZoomControls(true);  	
	 	webSettings.setDisplayZoomControls(false);
	 	
	
		this.view.getSettings().setDefaultTextEncodingName("UTF-8");    
		screenWidth = this.getWindowManager().getDefaultDisplay().getWidth() - 10;
		
		mRotatePop = new RotatePop(this, this.view);
		
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		nameStr = bundle.getString("name");
		mRotatePop.setPath(nameStr);
	
		getRange();	
		
		makeFile();
		
		readAndWrite();
		

		if(SoftWareCup.isNightModel)
		{
			openNightModel();
		}
		else
		{
			openDayModel();
		}
		
//		File f = new File(htmlPath);
//		if(f.delete())
//		{
//			Log.e("f-delete","OK");
//		}

	}
	
	String path = null;
	public void makeFile(){
		
		String sdStateString = android.os.Environment.getExternalStorageState();
    	
    	if(sdStateString.equals(android.os.Environment.MEDIA_MOUNTED))//�ж�SD���Ƿ����
    	{
    		try{
    			File sdFile = android.os.Environment.getExternalStorageDirectory();// ��ȡ��չ�洢�豸���ļ�Ŀ¼  
    			
    			//getAbsolutePath() : ���س���·�����ľ���·�����ַ�����
    			//File.separator����̬��������ʾ�ָ��� Windows��Ϊ��\����Linux��Ϊ��/��
    			path = sdFile.getAbsolutePath() + File.separator + "UIT-MAX"+ File.separator+"Word";
    			
    			File dirFile = new File(path);
    			if(!dirFile.exists())           //���Դ˳���·������ʾ���ļ���Ŀ¼�Ƿ���ڡ�����ʱ����true 
    			{
    				dirFile.mkdir();         //�����˳���·����ָ����Ŀ¼��
    			}
    	   			
    			File myFile = new File(path + File.separator + "day.html");
    			File myFile1 = new File(path + File.separator + "night.html");
    			
    			if(!myFile.exists()){
    				myFile.createNewFile();   //�����ļ�
    			}
    			
    			htmlPath = myFile.getAbsolutePath();
    			htmlPath1 = myFile1.getAbsolutePath();
    		}
    		catch(Exception e){

    		}
    	}
	}
	
	/*������sdcard�ϴ���ͼƬ*/
	public void makePictureFile(){
		String sdString = android.os.Environment.getExternalStorageState();
		
		if(sdString.equals(android.os.Environment.MEDIA_MOUNTED)){
			try{
				File picFile = android.os.Environment.getExternalStorageDirectory();
				
				String picPath = picFile.getAbsolutePath() + 
						File.separator + "UIT-MAX"+File.separator+"Word";
				
				File picDirFile = new File(picPath);
				
				if(!picDirFile.exists()){
					picDirFile.mkdir();
				}
				File pictureFile = new File(picPath + File.separator + presentPicture + ".jpg");
				
				if(!pictureFile.exists()){
					pictureFile.createNewFile();
				}
				
				picturePath = pictureFile.getAbsolutePath();
				
			}
			catch(Exception e){
				System.out.println("PictureFile Catch Exception");
			}
		}
	}
	
	public void onDestroy(){
		this.deleteFile();
		super.onDestroy();
	}

	public void readAndWrite(){
		try{
			myFile = new File(htmlPath);
			output = new FileOutputStream(myFile);
			myFile1 = new File(htmlPath1);
			output1 = new FileOutputStream(myFile1);
			String head = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>%n<html>" +
					"<body  bgcolor=\"#FFFFFF\" text=\"#000000\" " +
					"link=\"#0000FF\" vlink=\"#8000FF\" alink=\"#FF0000\">" +
					"<script language=javascript>" +
					"function load_night()"+
					"{document.body.style.backgroundColor= \"#FFFF00\";}"+
					"</script>";
			String head1 =  "<?xml version=\"1.0\" encoding=\"utf-8\" ?>%n<html>" +
					"<body  bgcolor=\"#383838\" text=\"#cccccc\" " +
					"link=\"#0000FF\" vlink=\"#8000FF\" alink=\"#FF0000\">";
				
			String tagBegin = "<p";
			String tagEnd = "</p>";

			// getBytes() : �ǽ�һ���ַ���ת��Ϊһ���ֽ����顣
			output.write(head.getBytes());
			output1.write(head1.getBytes());
			//numParagraphs() : �����ڴ˷�Χ�ڵĶ�����
			int numParagraphs = range.numParagraphs();	

			for(int i = 0; i < numParagraphs; i++){
				//getParagraph() : �����ڴ˷�Χ�ڵ�ָ��������
				Paragraph p = range.getParagraph(i);
				
                //����Ǳ��
				if(p.isInTable()){
					i = writeTableContent(i);		
				}
				else{
					output.write(tagBegin.getBytes());
					output1.write(tagBegin.getBytes());
					writeParagraphContent(p,true);
					output.write(tagEnd.getBytes());
					output1.write(tagEnd.getBytes());
				}
			}
			String end = "</body></html>";
			output.write(end.getBytes());
			output1.write(end.getBytes());
			output.close();
			output1.close();
			
		}
		catch(Exception e){
			System.out.println("readAndWrite Exception");
		}
	}
	

	public int writeTableContent(int i) throws IOException{
		
		int temp = i;   //������ڶ���
		if(tableIterator.hasNext()){
			String tagBegin = "<p";
			String tagEnd = "</p>";
			String tableBegin = "<table style=\"border-collapse:collapse\" border=1 bordercolor=\"black\" align=\"center\">";
			String tableEnd = "</table>";
			String rowBegin = "<tr>";
			String rowEnd = "</tr>";
			String colBegin = "<td";
			String colEnd = "</td>";
			
			Table table = tableIterator.next();
			output.write(tableBegin.getBytes());
			output1.write(tableBegin.getBytes());
			int rows = table.numRows();//�������
						
			Map<Integer,Integer> cellIndexMapping=new HashMap<Integer,Integer>();
			int maxNumCellIndex=0;//��Ԫ��������һ��
			int columns=0;               //��Ԫ���������ж��ٸ���Ԫ��
			for (int a = 0; a < rows; a++) {        
				TableRow tr = table.getRow(a);
				if(columns<tr.numCells()){
					maxNumCellIndex=a;
					columns=tr.numCells();
				}
			}		
			 for (int j = 0; j < columns; j++) {        
	             TableCell td = table.getRow(maxNumCellIndex).getCell(j);//ȡ�õ�Ԫ��     
	             cellIndexMapping.put(td.getLeftEdge(),j+1);
	         }
     
			//ѭ����
			for( int r = 0; r < rows; r++){
				output.write(rowBegin.getBytes());
				output1.write(rowBegin.getBytes());
				TableRow row = table.getRow(r);		//��õ�ǰ�����					
				int cols = row.numCells();                //��ǰ�е�Ԫ����
				int rowNumParagraphs = row.numParagraphs();  //����ж�����
				int colsNumParagraphs = 0;
				//��Ԫ��ѭ��
				for( int c = 0; c < cols; c++){
					output.write(colBegin.getBytes());
					output1.write(colBegin.getBytes());
					TableCell cell = row.getCell(c);   //��õ�ǰ��Ԫ��
		
					TableCell td2;
					  if(c==cols-1){
				             td2 = row.getCell(c);
				            }else{
				             td2 = row.getCell(c+1);
				            }
					  //��ȡ���colspan
		            int td1_edge=cell.getLeftEdge();
		            int td2_edge=td2.getLeftEdge();
		            int td1_index=cellIndexMapping.get(td1_edge).intValue();
		            int td2_index=cellIndexMapping.get(td2_edge).intValue();
		            int span=td2_index-td1_index;
		            //������һ����Ԫ����е����β
		            if(cols<columns&&c==cols-1){
		             span=columns-cols+1;
		            }
		            String ss =" align=\"center\"" + " valign=\"middle\"" + " colspan="+span+">";    
                    output.write(ss.getBytes());
                    output1.write(ss.getBytes());
 									
				    int max = temp + cell.numParagraphs(); // ������ڶ���+ ��Ԫ�������
					colsNumParagraphs = colsNumParagraphs + cell.numParagraphs();
					//����ÿһ����񰴶�������ȡ
					for(int cp = temp; cp < max; cp++){
						Paragraph p1 = range.getParagraph(cp);  //��õ�ǰ����	
						output.write(tagBegin.getBytes());    
						output1.write(tagBegin.getBytes()); 
						writeParagraphContent(p1,false);                       //����д��
						output.write(tagEnd.getBytes());
						output1.write(tagEnd.getBytes());
						temp++;
					}
					output.write(colEnd.getBytes());
					output1.write(colEnd.getBytes());
				}
				int max1 = temp + rowNumParagraphs;
				for(int m = temp + colsNumParagraphs; m < max1; m++){
					Paragraph p2 = range.getParagraph(m);
					temp++;
				}
				output.write(rowEnd.getBytes());
				output1.write(rowEnd.getBytes());
			}
			output.write(tableEnd.getBytes());
			output1.write(tableEnd.getBytes());
		}
		i = temp;
		return i;
		
	}

	
	public void writeParagraphContent(Paragraph paragraph ,boolean sign) throws IOException{
		if(sign){
			//String str =">&nbsp;&nbsp;&nbsp;&nbsp;";
			String str =">";
			output.write(str.getBytes());
			output1.write(str.getBytes());
		}else{
			//String str = "align=\"center\">";
			String str = ">";
		    output.write(str.getBytes());
		    output1.write(str.getBytes());
		}
		Paragraph p = paragraph;
		int pnumCharacterRuns = p.numCharacterRuns();

		for( int j = 0; j < pnumCharacterRuns; j++){

			CharacterRun run = p.getCharacterRun(j);

			if(run.getPicOffset() == 0 || run.getPicOffset() >= 1000){
				if(presentPicture < pictures.size()){
					writePicture();
				}
			}
			else{
				try{
					String text = run.text();
					if(text.length() >= 2 && pnumCharacterRuns < 2){		
						output.write(text.getBytes());
						output1.write(text.getBytes());
					}
					else{
						int size = run.getFontSize();
						int color = run.getColor();
						String fontSizeBegin = "<font size=\"" + size/14. + "px"+ "\">";
						String fontColorBegin = "<font color=\"" + decideColor(color) + "\">";
						String fontEnd = "</font>";
						String boldBegin = "<b>";
						String boldEnd = "</b>";
						String islaBegin = "<i>";
						String islaEnd = "</i>";

						output.write(fontSizeBegin.getBytes());
						output1.write(fontSizeBegin.getBytes());
						output.write(fontColorBegin.getBytes());		
						output1.write(fontColorBegin.getBytes());	
						if(run.isBold()){
							output.write(boldBegin.getBytes());
							output1.write(boldBegin.getBytes());
						}
						if(run.isItalic()){
							output.write(islaBegin.getBytes());
							output1.write(islaBegin.getBytes());
						}

						output.write(text.getBytes());
						output1.write(text.getBytes());

						if(run.isBold()){
							output.write(boldEnd.getBytes());
							output1.write(boldEnd.getBytes());
						}
						if(run.isItalic()){
							output.write(islaEnd.getBytes());
							output1.write(islaEnd.getBytes());
						}
						output.write(fontEnd.getBytes());
						output1.write(fontEnd.getBytes());
						output.write(fontEnd.getBytes());
						output1.write(fontEnd.getBytes());
					}
				}
				catch(Exception e){
					System.out.println("Write File Exception");
				}
			}
		}
	}


	public void writePicture(){
		Picture picture = (Picture)pictures.get(presentPicture);

		byte[] pictureBytes = picture.getContent();

		Bitmap bitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);

		makePictureFile();
		presentPicture++;

		File myPicture = new File(picturePath);

		try{

			FileOutputStream outputPicture = new FileOutputStream(myPicture);

			outputPicture.write(pictureBytes);

			outputPicture.close();
		}
		catch(Exception e){
			System.out.println("outputPicture Exception");
		}

		String imageString = "<img src=\"" + picturePath + "\"";

		//if(bitmap.getWidth() > screenWidth){
		//����ͼƬ��ʾ��С
			double width =bitmap.getWidth()/1.5;
			double height =  bitmap.getHeight()/1.5;
			
			if(width>screenWidth){
				width = screenWidth/1.55;
				height = bitmap.getHeight()/(bitmap.getWidth()/width);
			}
			imageString = imageString +" " + "width=\"" + width+ "\" height=\""+ height +"\"";
		//}
		imageString = imageString + ">";

		try{
			output.write(imageString.getBytes());
			output1.write(imageString.getBytes());
		}
		catch(Exception e){
			System.out.println("output Exception");
		}
	}



	private String decideColor(int a){
		int color = a;
		switch(color){
		case 1:
			return "#000000";
		case 2:
			return "#0000FF";
		case 3:
		case 4:
			return "#00FF00";
		case 5:
		case 6:
			return "#FF0000";
		case 7:
			return "#FFFF00";
		case 8:
			return "#FFFFFF";
		case 9:
			return "#CCCCCC";
		case 10:
		case 11:
			return "#00FF00";
		case 12:
			return "#080808";
		case 13:
		case 14: 
			return "#FFFF00";
		case 15: 
			return "#CCCCCC";
		case 16:
			return "#080808";
		default:
			return "#000000";
		}
	}


	private void getRange(){
		FileInputStream in = null;
		POIFSFileSystem pfs = null;
		try{
			in = new FileInputStream(nameStr);
			pfs = new POIFSFileSystem(in);
			hwpf = new HWPFDocument(pfs);
		}
		catch(Exception e){

		}
		range = hwpf.getRange();     //���صķ�Χ�����������ĵ������������κ�ҳü��ҳ�š�
		// getPicturesTable(): ����PicturesTable����Ҳ�����ܹ��ӱ��ĵ�����ȡͼ��
		//getAllPictures(): �����ڵ�ǰ�ĵ��е�ͼƬ������б�
		//	this.string = range.text();

		pictures = hwpf.getPicturesTable().getAllPictures(); 

		tableIterator = new TableIterator(range);

	}
	/*���������ذ�ť*/
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	   	switch(keyCode)
	   	{
	   	case KeyEvent.KEYCODE_BACK:
	   		if(!mRotatePop.hasShow())
	   		{
	   			Intent intent = new Intent();
		   		intent.setClass(ViewFile.this, ViewPagerActivity.class);
		   		startActivity(intent);
		   		File f = new File(htmlPath);
		   		f.delete();
		   		this.finish();
	   		}
	   		if(mRotatePop.hasBtnShow())
	   		{
	   			mRotatePop.restore();
	   			mRotatePop.setBtnShow(false);
	   		}
	   		else if(!mRotatePop.hasBtnShow() && mRotatePop.hasShow())
	   		{
	   			mRotatePop.dismiss();	
	   		}

	   		break;
	   	case KeyEvent.KEYCODE_MENU:
	   		if(!mRotatePop.hasShow())
	   		{
	   			mRotatePop.show();
	   		}	
	   		break;
	   	case KeyEvent.KEYCODE_VOLUME_DOWN:
	   		if(Reading.volunmControl)
	   		{
		   		this.view.pageDown(false);
		   		return true;
	   		}
	   	case KeyEvent.KEYCODE_VOLUME_UP:
	   		if(Reading.volunmControl)
	   		{
		   		this.view.pageUp(false);
		   		return true;
	   		}
	   			   		
	   	}
		return false;	
	 }



	private void deleteFile(){
		File file = new File(path);
		final File files[] = file.listFiles();
		new Thread(new Runnable(){
			
			public void run() {
				for(File f: files){
					f.delete();
				}	
			}
		}).start();
	}
	
	public void openDayModel()
	{
		this.view.loadUrl("file:///"+ htmlPath);
		SoftWareCup.isNightModel = false;
	}
	
	public void openNightModel()
	{
		this.view.loadUrl("file:///"+ htmlPath1);
		SoftWareCup.isNightModel = true;
	}
	
}


