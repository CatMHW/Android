package com.example.mytable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.itextpdf.text.xml.simpleparser.NewLineHandler;
import com.radaee.excel.CellInfo;
import com.radaee.excel.ExcelInfo;
import com.radaee.reader.R;
import com.radaee.reader.ViewFile;
import com.radaee.reader.ViewPagerActivity;
import com.radaee.reader.ViewText;
import com.radaee.rotate.RotatePop;
import com.reader.setting.Setting;


import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MyTable extends Activity implements OnGestureListener,OnDoubleTapListener{
	private static final String TAG = "MyTable path";
	//�����С������������
	private final int MAX_ROW_NUM = 1000;
	private final int MAX_LINE_NUM = 1000;
	private final int MIN_ROW_NUM = 10;
	private final int MIN_LINE_NUM = 30;
	
	//��Ԫ��Ĭ�ϵĿ���
	private final int DEFAULT_HEIGHT = 35;
	private final int DEFAULT_WIDTH = 150;
	private final int EXCEL_DEFAULT_HEIGHT = 270;
	private final int EXCEL_DEFAULT_WIDTH = 8;
	
	//��ͷ����ͷ�Ŀ��
	private int ROW_HEAD_HEIGHT = 35;
	private int ROW_HEAD_WIDTH = 35;
	private int COLUNM_HEAD_HEIGHT = 35;
	private int COLUNM_HEAD_WIDTH = 150;
		
	private final int TEXT_SIZE = 15;
	private final int TEXT_MIN_SIZE = 15;
	
	private int rowCount = 10;
	private int lineCount = 30;
	
//	private TextView mRowTv = null;
//	private TextView mLineTv = null;
//	private EditText mViewEt = null;
	private MyTextView mRowTv = null;
	private MyTextView mLineTv = null;
	private MyTextView mViewEt = null;
	//private EditText mViewEt1 = null;						//�յ�ET������
	
	private CustomHScrollView mHRowHeadScrollView;
	private CustomHScrollView mHViewScroll;
	private CustomScrollView mLineScrollView;
	private CustomScrollView mViewScroll;
	
	private GestureDetector mGestureDetector = null;		//����
	private TableLayout mTable = null;
	private EditPop mEditPop = null;
	private RotatePop mRotatePop = null;
	
	private ExcelInfo mExcelInfo = null;
	private List<CellInfo> cellList = null;
	private List<Map<String, Integer>> mergedRegionMapList = null;
	private List<Map<Integer, Integer>> rowHeightMapList = null;
	private List<Integer> columnsWidthList = null;
	private List<CellInfo> textChangedCellList = new ArrayList<CellInfo>();		//��¼���޸ĵĵ�Ԫ��
	
	private String mExcelFilePath = null;
	
	private int mRowIndex = 0;
	private int mColunmIndex = 0;
	private int pre_row_index = 0;
	private int pre_colunm_index = 0;
	private int mX = 0;
	private int mY = 0;
	private int mVerticalOffSet = 0;
	private int mHorizontalScrollOffSet = 0;
	
	/**�����б�**/
	private ArrayList<ArrayList<String>> lists;
	
	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_table);
		
		//��������������
		mGestureDetector = new GestureDetector(this); 
		mEditPop = new EditPop(this);
		mRotatePop = new RotatePop(this);
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		mExcelFilePath = bundle.getString("name");
		Log.e(TAG, mExcelFilePath);
		getFileInfo(mExcelFilePath);
		
		mRotatePop.setPath(mExcelFilePath);
		//��ʼ����ͼ
		initLineHeads();
		initRowHeads();
		initDataView();
//		InitViewThread thread = new InitViewThread();
//		thread.start();
			
		//����ͼͬ������
		synScrollViews();
	}
	

	private void getFileInfo(String filePath)
	{
		try {
			mExcelInfo = new ExcelInfo(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e){
			e.printStackTrace();
		}
		
		cellList = mExcelInfo.getCellInfoList();
		//Log.e(TAG, "cellListSize:"+cellList.size());
		mergedRegionMapList = mExcelInfo.getMergedRegionMapList();
		rowHeightMapList = mExcelInfo.getRowHeightMapList();
		columnsWidthList = mExcelInfo.getColumnsWidth();
		
		rowCount = mExcelInfo.getColumnCount();
		lineCount = mExcelInfo.getRowCount();
	}
	
	 /**
     * ��Ӳ�������
     */
    public void getData()
    {
    	lists=new ArrayList<ArrayList<String>>();//������
    	ArrayList<String> list;//ÿ������
    	for(int i=0;i<rowCount;i++){
    		list=new ArrayList<String>();//����һ������
    		for(int j=0;j<lineCount;j++){
    			list.add(String.valueOf((i+1)*(j+1)));//��ֵ
    		}
    		lists.add(list);//�������
    	}   	
    }
    
    /**
     * ��ʼ����ʾ���ݵ���ͼ
     */
    public void initDataView ()
    {
    	
    	if(lineCount < MIN_LINE_NUM)
    	{
    		lineCount = MIN_LINE_NUM;
    	}	
    	if(rowCount < MIN_ROW_NUM)
    	{
    		rowCount = MIN_ROW_NUM;
    	}
    	
    	addToView(rowCount, lineCount);

    }
//    public class InitViewThread extends Thread
//    {
//    	public void run()
//    	{
//    		MyTable.this.initDataView();
//    	}
//    }
    
    /**
     * ��ʼ���еı��
     */
    public void initRowHeads()
    {
    	if(rowCount < MIN_ROW_NUM)
    	{
    		rowCount = MIN_ROW_NUM;
    	}
    	
    	if(rowCount > lineCount)
    	{
    		addToRowHead(rowCount);
    	}
    	else
    	{
    		addToRowHead(rowCount);
    	}
    }
    
    /**
     * ��ʼ���еı��
     */
    public void initLineHeads()
    {
    	if(lineCount < MIN_LINE_NUM)
    	{
    		lineCount = MIN_LINE_NUM;
    	}	
    	
    	if(lineCount < rowCount)
    	{
    		addToLineHead(rowCount);
    	}
    	else
    	{
    		addToLineHead(lineCount);
    	}
    	
    }
    
    /**
     * @param number Ҫ��ӵ�����
     */
    public void addToLineHead(int number)
    {
    	TableLayout table = (TableLayout)findViewById(R.id.line_head_tab);
    	table.setStretchAllColumns(true);
    	
    	TableLayout.LayoutParams tableLparams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
    	tableLparams.setMargins(0, 0, 0, 0);
    	
    	Map<Integer, Integer> map = null;
    	int height = 0;
    	int rowCount = 0;
    	
    	mLineTv = new MyTextView(this);		
		mLineTv.setId(10087);//ע��ID
//		mLineTv.setBackgroundResource(R.drawable.excel_bg);//����ͼƬ
		mLineTv.setGravity(Gravity.CENTER);
	//	mLineTv.setTextSize(20);//�����С
		mLineTv.setTextColor(Color.parseColor("#095583"));//�ı���ɫ
		mLineTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);//������ʽ		
		
    	//��ʼ������������
    	for(int i=0; i < number; ++i)
    	{
    		
    		MyTextView cloneLineTv = mLineTv.clone();
    		if(rowCount < rowHeightMapList.size())
    		{
    			map = rowHeightMapList.get(rowCount);
    		}
    		
			if(map.containsKey(i))
			{
				height = getMyHeight(map.get(i));
				cloneLineTv.setLayoutParams(new android.widget.TableRow.
						LayoutParams(ROW_HEAD_WIDTH, height));
				rowCount++;	
				
				//Log.e(TAG, ":"+i+" height:"+height);
			}
			else
			{
				cloneLineTv.setLayoutParams(new android.widget.TableRow.
						LayoutParams(ROW_HEAD_WIDTH, DEFAULT_HEIGHT));
			}
			
    		TableRow tablerow = new TableRow(MyTable.this);
    		 
    		
    		cloneLineTv.setText(String.valueOf(i+1));//�ı���Ϣ	
    		cloneLineTv.setBackgroundResource(R.drawable.top02);//����ͼƬ
			tablerow.addView(cloneLineTv);//��ӵ����
			table.addView(tablerow, tableLparams);
    	} 
    }
    
    /**
     * @param number Ҫ��ӵ�����
     */
    public void addToRowHead(int number)
    {
    	TableLayout table = (TableLayout)findViewById(R.id.row_head_tab);
    	table.setStretchAllColumns(true);
    	
    	TableLayout.LayoutParams tableLparams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
    	tableLparams.setMargins(0, 0, 0, 0);
    	
    	TableRow tablerow = new TableRow(MyTable.this);    	
    	
    	int columnWidth = 150;
    	mRowTv = new MyTextView(this);
    	mRowTv.setId(10086);//ע��ID	
//    	mRowTv.setBackgroundResource(R.drawable.excel_bg);//����ͼƬ
		mRowTv.setGravity(Gravity.CENTER);
	//	mRowTv.setTextSize(20);//�����С
		mRowTv.setTextColor(Color.parseColor("#095583"));//�ı���ɫ
		mRowTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);//������ʽ
    	//��ʼ������������
    	for(int i=0; i < number; ++i)
    	{
    		MyTextView cloneRowTv = mRowTv.clone();
    		String str = getRowName(i);    		    		
    		if(i < columnsWidthList.size())
    		{
    			columnWidth = getMyColumnWidth(columnsWidthList.get(i));
    			//Log.e(TAG, "width:" + columnWidth);
    			
    		}   		
    		
    		cloneRowTv.setLayoutParams(new android.widget.TableRow.
    				LayoutParams(columnWidth, COLUNM_HEAD_HEIGHT));
    		cloneRowTv.setBackgroundResource(R.drawable.top02);//����ͼƬ
    		cloneRowTv.setText(str);//�ı���Ϣ
			tablerow.addView(cloneRowTv);//��ӵ����
    	}
    	
    	table.addView(tablerow, tableLparams);
    }
    
    /**
     * @param number ��ӵ��У��е�����
     * ����ͼ���������
     */
    public void addToView(int rowNum, int lineNum)
    {
    	mTable = (TableLayout)findViewById(R.id.tablelayout);
    	mTable.setStretchAllColumns(true);
    	
    	TableLayout.LayoutParams tableLparams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
    	tableLparams.setMargins(0, 0, 0, 0); 
    	
    	
    	//��Ԫ��ĳ���
    	int width = 150;
    	int height = 35;
    	
    	//�У���Ԫ��ĸ���
    	int rowCount = 0;
    	int cellCount = 0;
    	
    	//���е�����
    	int rowIndex = 0;
    	int columnIndex = 0;
    	
    	//��Ԫ����Ϣ
    	String content = null;
    	int fontSize = 9;
    	boolean isBold = false;
    	boolean isItalic = false;
    	
    	//�ϲ���Ԫ����У��е���ĩ��ַ
//    	int mergedFColumn = 0;
//    	int mergedLColumn = 0;
//    	int mergedFRow = 0;
//    	int mergedLRow = 0;
    	
    	//�ϲ���Ԫ�����??????
//    	int mergedCount = mergedRegionMapList.size()-1;	
//    	int mergedRowCount = 0; 							//һ���м����ϲ��ĵ�Ԫ��	
//    	Map<String, Integer> mergedMap = null;
//    	if(mergedCount != 0 && mergedCount < 0)
//    	{
//	    	mergedMap = mergedRegionMapList.get(mergedCount);
//	    	mergedFColumn = mergedMap.get("FC");
//	    	mergedLColumn = mergedMap.get("LC");
//	    	mergedFRow = mergedMap.get("FR");
//	    	mergedLRow = mergedMap.get("LR");
//    	}
    	
    	//String Comment    	
    	Map<Integer, Integer> map = null;
    	CellInfo cellInfo = cellList.get(0);
    	 	   	
    	mViewEt = new MyTextView(this);
    	mViewEt.setLayoutParams(new android.widget.TableRow.LayoutParams(DEFAULT_WIDTH, 
				DEFAULT_HEIGHT));
		//mViewEt.setBackgroundResource(R.drawable.top_02);//����ͼƬ
		mViewEt.setGravity(Gravity.CENTER);
		mViewEt.setTextSize(TEXT_SIZE);//�����С
		mViewEt.setTextColor(Color.parseColor("#095583"));//�ı���ɫ
	//	mViewEt.setOnTouchListener(this);

		
    	//��
    	for(int i=0; i < lineNum; ++i)
    	{
    		TableRow tablerow = new TableRow(MyTable.this);
    		
    		//�õ��и�
    		if(rowCount < rowHeightMapList.size())
    		{
    			map = rowHeightMapList.get(rowCount);
    		}
    		
			if(map.containsKey(i))
			{
				height = getMyHeight(map.get(i));
				//mLineTv.setLayoutParams(new android.widget.TableRow.LayoutParams(35, height));
				rowCount++;					
				//Log.e(TAG, ":"+i+" height:"+height);
			}   					
			
    		//��
    		for(int j=0; j < rowNum; ++j)
        	{
        		
    			MyTextView cloneViewTv = mViewEt.clone();
//    			cloneViewTv.setOnLongClickListener(new View.OnLongClickListener() {
//					
//					public boolean onLongClick(View v) {
//						// TODO Auto-generated method stub
//						
//						Toast.makeText(MyTable.this, "text", Toast.LENGTH_SHORT).show();
//						return false;
//					}
//				});
        		if(j < columnsWidthList.size())
        		{
        			width = getMyColumnWidth(columnsWidthList.get(j));
        			//Log.e(TAG, "width:" + width);
        		} 
        		
        		//����������ĵ�Ԫ��û�������
        		if(cellCount < lineCount*this.rowCount)
        		{
        			if(cellCount < cellList.size())
        			{
        				cellInfo = cellList.get(cellCount);
            			rowIndex = cellInfo.getRowIndex();
            	    	columnIndex = cellInfo.getColumnIndex();
        			}        			
        	    	
//        			//����Ǻϲ���Ԫ��??
//        			if(mergedFColumn == j && mergedFRow == i)
//        			{
//        				width = 0;
//        				//�õ��ϲ���Ԫ��Ŀ��
//        				for(int k=0; k<(mergedRowCount = mergedLColumn-mergedFColumn); k++)
//        				{
//        					if(j < columnsWidthList.size())
//        	        		{
//        						//Log.e("j", ":"+);
//        						width = width + getMyColumnWidth(columnsWidthList.get(j+k));
//        	        			//Log.e(TAG, "width:" + width);
//        	        		} 
//        					
//        				}
//        			}
        			
        			//����Ǳ������ĵ�Ԫ��
        			if(rowIndex == i && columnIndex == j)
            		{       				
        				//��ȡ��Ԫ������,��Ϣ
            			content = cellInfo.getContent();
            			fontSize = cellInfo.getFontSize();
            			isItalic = cellInfo.getItalic();
            			isBold = cellInfo.getBoldweight();
            			
            			//������ݲ�Ϊ�գ������õ�Ԫ�����ԣ���������
            			if(content != null)
            			{
            				
            				if(isBold)
                			{
            					cloneViewTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);//������ʽ
                			}
                			
                			if(isItalic)
                			{
                				
                			}
                			
                			//���õ�Ԫ��Ŀ��	
                    		if(height != DEFAULT_HEIGHT || width != DEFAULT_WIDTH)
                    		{
                    			cloneViewTv.setLayoutParams(new android.widget.TableRow.LayoutParams(width, 
                    					height));
                    			cloneViewTv.setBackgroundResource(R.drawable.top_02);
                    			
                    		}                    	                    		                   		
                			
                    		cloneViewTv.setText(content);//�ı���Ϣ

                    		
                    		//���õĲ����������հ״��Ĳ�����һ�¾ͻ���ַ�϶
//                			mViewEt.setTextSize(TEXT_SIZE);//�����С
//                    		mViewEt.setTextColor(Color.parseColor("#095583"));//�ı���ɫ
                    		
                    		tablerow.addView(cloneViewTv);//��ӵ����
                    		
                			cellCount++;
                			
//                			rowIndex = cellInfo.getRowIndex();
//                			columnIndex = cellInfo.getColumnIndex();
//                			Log.e(TAG, "rowIndex:" + rowIndex);
//                			Log.e(TAG, "columnIndex:" + columnIndex);
            			}
            			
            		}
        			else
        			{
                		//���õ�Ԫ��Ŀ��	
                		if(height == DEFAULT_HEIGHT && width == DEFAULT_WIDTH)
                		{
                			tablerow.addView(cloneViewTv);
                		//	Log.e(TAG, "��"+i+"��"+"��"+j+"��.."+"width:" + width+"height:" + height);
                		}
                		else
                		{
                			cloneViewTv.setLayoutParams(new android.widget.TableRow.LayoutParams(width, 
                					height));
                			cloneViewTv.setBackgroundResource(R.drawable.top_02);
                			tablerow.addView(cloneViewTv);
                			
                		//	Log.e(TAG, "��"+i+"��"+"��"+j+"��.."+"width:" + width+"height:" + height);
                		}  
        			}
        		}
//        		j += mergedRowCount;??
//        		mergedRowCount = 0;

        	}
    		mTable.addView(tablerow, tableLparams);
    		height = DEFAULT_HEIGHT;
    		width = DEFAULT_WIDTH;
    		
//    		//������һ���ϲ���Ԫ��??
//    		if(mergedCount != 0)
//    		{
//    			mergedCount--;
//    			mergedMap = mergedRegionMapList.get(mergedCount);
//    			mergedFColumn = mergedMap.get("FC");
//    	    	mergedLColumn = mergedMap.get("LC");
//    	    	mergedFRow = mergedMap.get("FR");
//    	    	mergedLRow = mergedMap.get("LR");
//    		}
    		
    	}
    }
    
    /**
     * @param rowNum ��ʾ�ڼ���
     * @return �����б������ĸ
     */
    public String getRowName(int rowNum)
    {
    	int sec = rowNum/26;
    	int i = (int)'A'; 
    	String B = null;
    	
    	if(sec != 0)
    	{   		
    		B = String.valueOf((char)(sec+i-1));
    	}
    	
    	int firstNum = rowNum%26;
    	if(firstNum != 0)
    	{
    		if(B == null)
    		{
    			return String.valueOf((char)(firstNum+i));
    		}
    		else
    		{
    			return B + String.valueOf((char)(firstNum+i));
    		}   		
    	}
    	else 
    	{
    		if(B != null)
    		{	
    			return B + String.valueOf('A');
    		}
    		else
    		{
    			return String.valueOf('A');
    		}
    	}
    }
    
    /**
     * @param num �����getHeightInPoints��ֵ
     * ����ת�������Ļ����ֵ
     */
    private int getMyHeight(int num)
    {
    	return (int) (num/EXCEL_DEFAULT_HEIGHT*DEFAULT_HEIGHT);
    }
    
    /**
     * @param num
     * @return 
     */
    private int getMyColumnWidth(float num)
    {
    	//Log.e(TAG, "width:"+num);
    	return (int) (num/(256*EXCEL_DEFAULT_WIDTH)*DEFAULT_WIDTH);  	
    }
    
    /**
     * ����ͼͬ����ʾ
     */
    private void synScrollViews()
    {
    	mHRowHeadScrollView = (CustomHScrollView)findViewById(R.id.row_scroll);
    	mHViewScroll = (CustomHScrollView)findViewById(R.id.view_h_scroll);
    	mLineScrollView = (CustomScrollView)findViewById(R.id.line_scroll);
    	mViewScroll = (CustomScrollView)findViewById(R.id.view_v_scroll);
    	   	
    	//��
    	mHRowHeadScrollView.setScrollView(mHViewScroll);
    	mHViewScroll.setScrollView(mHRowHeadScrollView);
    	mLineScrollView.setScrollView(mViewScroll);
    	mViewScroll.setScrollView(mLineScrollView);
    }

    
    //�õ���Ԫ����е�λ��
    /**
     * @return ��Ԫ�����ڵ�x����
     */
    public int getColunmCellPosition()
    {    	
    	mColunmIndex = 0;
    	int width = 0;
    	//������ͼ��ƫ����+����������ƫ����
    	int x_offset = mHorizontalScrollOffSet + mX;
    	Log.e(TAG, ""+x_offset);
    	//�ڱ�����������ͼ��
    	for(int i=0; i < columnsWidthList.size(); i++)
    	{
    		width += getMyColumnWidth(columnsWidthList.get(i));    		
    		if(x_offset > width)
    		{
    			mColunmIndex++;
    		}
    		else
    		{
    			break;
    		}   		
    	}
    	
    	//δ��������ͼ
    	
    	return width;
    }
    
    /**
     * @return ��Ԫ�����ڵ�X����
     */
    public int getRowCellPosition()
    {
    	mRowIndex = 0;
    	int height = 0;
    	Map<Integer, Integer> map = null;
    	//������ͼ��ƫ����+����������ƫ����
    	int y_offset = mVerticalOffSet + mY;
    	for(int i=0; i < rowHeightMapList.size(); i++)
    	{
    		map = rowHeightMapList.get(i);  		    		
			if(map.containsKey(i))
			{
				height += getMyHeight(map.get(i));	
				
			}
			
    		if(y_offset > height)
    		{
    			mRowIndex++;
    		}
    		else
    		{
    			break;
    		}   		
    	}
    	Log.e("MyTable", ":"+height);
    	//δ��������ͼ
    	
    	
    	return height;
    }

	
	public boolean onDown(MotionEvent event) {
		// TODO Auto-generated method stub	
		Log.e("MyTable", "onDown");
		mX = (int) event.getX()-ROW_HEAD_WIDTH;
		mY = (int) event.getY()-COLUNM_HEAD_HEIGHT;
		Log.e(TAG, "Row:"+mX+" Colunm:"+mY);
		return false;
	}

	
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void onLongPress(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.e("MyTable", "onLongPress");
	
	}

	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.e("MyTable", "onSingleTapUp");
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{      		
		//Log.e("MyTable", "onTouchEvent");
		return this.mGestureDetector.onTouchEvent(event);
	}
	
	 @Override   
	public boolean dispatchTouchEvent(MotionEvent ev) 
	{   
		// Log.e("MyTable", "dispatchTouchEvent");
	     mGestureDetector.onTouchEvent(ev);   
	     return super.dispatchTouchEvent(ev);   
	} 
	 
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{	
		switch(keyCode)
	   	{
	   	case KeyEvent.KEYCODE_BACK:
	   		
	   		if(mRotatePop.hasBtnShow())
	   		{
	   			mRotatePop.restore();
	   			mRotatePop.setBtnShow(false);
	   		}
	   		else if(!mRotatePop.hasBtnShow() && mRotatePop.hasShow())
	   		{
	   			mRotatePop.dismiss();	
	   		}
	   		else if(mEditPop.getTextChangedList().size() != 0)
	   		{
	   			showDialog();
	   		}
	   		else
	   		{
	   			backToTab();
	   		}

	   		break;
	   	case KeyEvent.KEYCODE_MENU:
	   		if(!mRotatePop.hasShow())
	   		{
	   			mRotatePop.show();
	   		}	
	   		break;
	   	}
		return false;	
	}
    
	private void backToTab()
	{
		Intent intent = new Intent(this, ViewPagerActivity.class);
		this.startActivity(intent);
		this.finish();		
	}


	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
	//	Log.e("MyTable", "onDoubleTap");	
		
		if(mX < 0 || mY < 0)
		{
			return false;
		}
		
		//ˮƽ��ͼ��ƫ����
		mHorizontalScrollOffSet = mHViewScroll.computeHorizontalScrollOffset();
		mVerticalOffSet = mViewScroll.computeVerticalScrollOffset();
		int x = getColunmCellPosition();
		int y = getRowCellPosition();
		Log.e(TAG, "Row:"+mRowIndex+" Colunm:"+mColunmIndex);

		getMyEditView(mRowIndex-1, mColunmIndex, x, y);
			
		return false;
	}


	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub		
		return false;
	}


	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private String pre_text;
	private String last_text;
	private CellInfo cellInfo =null;
	private EditText et;
	private TextView tv =null;
	/**
	 * �õ���������Ǹ��༭��
	 * @param rowIndex �е�����
	 * @param colunmIndex ������
	 * @param x ����
	 * @param y ����
	 */
	public void getMyEditView(int rowIndex, int colunmIndex, int x, int y)
	{
		TableRow tableRow = new TableRow(this);
		tableRow =  (TableRow) mTable.getChildAt(rowIndex);
		tv = ( TextView )tableRow.getChildAt(colunmIndex);
			
		pre_text = tv.getText().toString();
		mEditPop.setPopHeight(tv.getHeight()+8);
		mEditPop.setPopWidth(tv.getWidth()+8);
		mEditPop.setText(pre_text);
		mEditPop.setRowIndex(mRowIndex-1);
		mEditPop.setColunmIndex(mColunmIndex);
		mEditPop.setView(tv);
		Log.e("CHANGE", "2");
		mEditPop.showAtLocation(tv, 
				getCellX(mColunmIndex)-mHorizontalScrollOffSet,
				getCellY(mRowIndex)-mVerticalOffSet);
		//tv.setText(mEditPop.getLastText());
		
		
	}
	
	public int getCellX(int colunmIndex)
	{
		int width = 0;
		for(int i=0; i<colunmIndex; i++)
		{
			width += getMyColumnWidth(columnsWidthList.get(i)); 
			Log.e("CellX", ":"+width);
		}
		
		return width+ROW_HEAD_WIDTH+16;
	}
	
	public int getCellY(int rowIndex)
	{
		Map<Integer, Integer> map = null;
		int height = 0;
    	for(int i=0; i < rowIndex; i++)
    	{
    		map = rowHeightMapList.get(i);  		    		
			if(map.containsKey(i))
			{
				height += getMyHeight(map.get(i));	
				Log.e("CellY", ":"+height);
			}  		
    	}
    	
    	//״̬���߶�
    	return height+COLUNM_HEAD_HEIGHT;
	}
	
	public void writeToExcel(String filePath) throws IOException, InvalidFormatException
	{	
		textChangedCellList = mEditPop.getTextChangedList();
		Workbook mWb =null;
		InputStream inp = null;    	
    	inp = new FileInputStream(filePath);
		mWb = WorkbookFactory.create(inp);
		    	
		if(textChangedCellList.size()!=0)
		{
			Sheet mSheet = mWb.getSheetAt(0);
			for(int i=0; i < textChangedCellList.size(); i++)
			{
				CellInfo cellInfo = textChangedCellList.get(i);
				
				Row row = mSheet.getRow(cellInfo.getRowIndex());
				Cell cell = row.getCell(cellInfo.getColumnIndex());
				if(cell == null)
				{
					cell = row.createCell(cellInfo.getColumnIndex());
				}
				
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(cellInfo.getContent());
				Log.e(TAG, "content:"+ cellInfo.getContent()+
						"row:"+cellInfo.getRowIndex()+"colunm"+cellInfo.getColumnIndex());
			}
		}
		
		//inp.close();
	
		
	    FileOutputStream fileOut = new FileOutputStream(mExcelFilePath);
	    mWb.write(fileOut);
	    fileOut.close();
	}	
	
	private Dialog mDialog = null;
	
	public void showDialog()
	{
		mDialog = new AlertDialog.Builder(this)
		.setTitle("�ĵ����޸�")
		.setItems(new String[]{"��������","����","ȡ��"}, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(which)
				{
				//����
				case 0:
					backToTab();
					break;
				//����
				case 1:
					try {
						MyTable.this.writeToExcel(mExcelFilePath);
					} catch (InvalidFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					backToTab();
					break;
				//ȡ��
				case 2:
					mDialog.dismiss();
					break;						
				}
			}
		}).create();
		mDialog.show();		
	}
		
}
