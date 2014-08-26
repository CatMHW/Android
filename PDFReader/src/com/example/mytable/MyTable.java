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
	//最大，最小的行数和列数
	private final int MAX_ROW_NUM = 1000;
	private final int MAX_LINE_NUM = 1000;
	private final int MIN_ROW_NUM = 10;
	private final int MIN_LINE_NUM = 30;
	
	//单元格默认的宽，高
	private final int DEFAULT_HEIGHT = 35;
	private final int DEFAULT_WIDTH = 150;
	private final int EXCEL_DEFAULT_HEIGHT = 270;
	private final int EXCEL_DEFAULT_WIDTH = 8;
	
	//列头，行头的宽高
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
	//private EditText mViewEt1 = null;						//空的ET，复用
	
	private CustomHScrollView mHRowHeadScrollView;
	private CustomHScrollView mHViewScroll;
	private CustomScrollView mLineScrollView;
	private CustomScrollView mViewScroll;
	
	private GestureDetector mGestureDetector = null;		//手势
	private TableLayout mTable = null;
	private EditPop mEditPop = null;
	private RotatePop mRotatePop = null;
	
	private ExcelInfo mExcelInfo = null;
	private List<CellInfo> cellList = null;
	private List<Map<String, Integer>> mergedRegionMapList = null;
	private List<Map<Integer, Integer>> rowHeightMapList = null;
	private List<Integer> columnsWidthList = null;
	private List<CellInfo> textChangedCellList = new ArrayList<CellInfo>();		//记录被修改的单元格
	
	private String mExcelFilePath = null;
	
	private int mRowIndex = 0;
	private int mColunmIndex = 0;
	private int pre_row_index = 0;
	private int pre_colunm_index = 0;
	private int mX = 0;
	private int mY = 0;
	private int mVerticalOffSet = 0;
	private int mHorizontalScrollOffSet = 0;
	
	/**数据列表**/
	private ArrayList<ArrayList<String>> lists;
	
	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_table);
		
		//声明监听此手势
		mGestureDetector = new GestureDetector(this); 
		mEditPop = new EditPop(this);
		mRotatePop = new RotatePop(this);
		
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		mExcelFilePath = bundle.getString("name");
		Log.e(TAG, mExcelFilePath);
		getFileInfo(mExcelFilePath);
		
		mRotatePop.setPath(mExcelFilePath);
		//初始化视图
		initLineHeads();
		initRowHeads();
		initDataView();
//		InitViewThread thread = new InitViewThread();
//		thread.start();
			
		//绑定视图同步闲书
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
     * 添加测试数据
     */
    public void getData()
    {
    	lists=new ArrayList<ArrayList<String>>();//总数据
    	ArrayList<String> list;//每行数据
    	for(int i=0;i<rowCount;i++){
    		list=new ArrayList<String>();//创建一行数据
    		for(int j=0;j<lineCount;j++){
    			list.add(String.valueOf((i+1)*(j+1)));//赋值
    		}
    		lists.add(list);//添加数据
    	}   	
    }
    
    /**
     * 初始化显示数据的视图
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
     * 初始化列的编号
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
     * 初始化行的编号
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
     * @param number 要添加的行数
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
		mLineTv.setId(10087);//注册ID
//		mLineTv.setBackgroundResource(R.drawable.excel_bg);//背景图片
		mLineTv.setGravity(Gravity.CENTER);
	//	mLineTv.setTextSize(20);//字体大小
		mLineTv.setTextColor(Color.parseColor("#095583"));//文本颜色
		mLineTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);//字体样式		
		
    	//初始化列名的数据
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
    		 
    		
    		cloneLineTv.setText(String.valueOf(i+1));//文本信息	
    		cloneLineTv.setBackgroundResource(R.drawable.top02);//背景图片
			tablerow.addView(cloneLineTv);//添加到表格
			table.addView(tablerow, tableLparams);
    	} 
    }
    
    /**
     * @param number 要添加的列数
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
    	mRowTv.setId(10086);//注册ID	
//    	mRowTv.setBackgroundResource(R.drawable.excel_bg);//背景图片
		mRowTv.setGravity(Gravity.CENTER);
	//	mRowTv.setTextSize(20);//字体大小
		mRowTv.setTextColor(Color.parseColor("#095583"));//文本颜色
		mRowTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);//字体样式
    	//初始化行名的数据
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
    		cloneRowTv.setBackgroundResource(R.drawable.top02);//背景图片
    		cloneRowTv.setText(str);//文本信息
			tablerow.addView(cloneRowTv);//添加到表格
    	}
    	
    	table.addView(tablerow, tableLparams);
    }
    
    /**
     * @param number 添加的行，列的数量
     * 往视图中添加数据
     */
    public void addToView(int rowNum, int lineNum)
    {
    	mTable = (TableLayout)findViewById(R.id.tablelayout);
    	mTable.setStretchAllColumns(true);
    	
    	TableLayout.LayoutParams tableLparams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);
    	tableLparams.setMargins(0, 0, 0, 0); 
    	
    	
    	//单元格的长宽
    	int width = 150;
    	int height = 35;
    	
    	//列，单元格的个数
    	int rowCount = 0;
    	int cellCount = 0;
    	
    	//行列的索引
    	int rowIndex = 0;
    	int columnIndex = 0;
    	
    	//单元格信息
    	String content = null;
    	int fontSize = 9;
    	boolean isBold = false;
    	boolean isItalic = false;
    	
    	//合并单元格的列，行的首末地址
//    	int mergedFColumn = 0;
//    	int mergedLColumn = 0;
//    	int mergedFRow = 0;
//    	int mergedLRow = 0;
    	
    	//合并单元格个数??????
//    	int mergedCount = mergedRegionMapList.size()-1;	
//    	int mergedRowCount = 0; 							//一行有几个合并的单元格	
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
		//mViewEt.setBackgroundResource(R.drawable.top_02);//背景图片
		mViewEt.setGravity(Gravity.CENTER);
		mViewEt.setTextSize(TEXT_SIZE);//字体大小
		mViewEt.setTextColor(Color.parseColor("#095583"));//文本颜色
	//	mViewEt.setOnTouchListener(this);

		
    	//行
    	for(int i=0; i < lineNum; ++i)
    	{
    		TableRow tablerow = new TableRow(MyTable.this);
    		
    		//得到行高
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
			
    		//列
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
        		
        		//如果被解析的单元格还没被添加完
        		if(cellCount < lineCount*this.rowCount)
        		{
        			if(cellCount < cellList.size())
        			{
        				cellInfo = cellList.get(cellCount);
            			rowIndex = cellInfo.getRowIndex();
            	    	columnIndex = cellInfo.getColumnIndex();
        			}        			
        	    	
//        			//如果是合并单元格??
//        			if(mergedFColumn == j && mergedFRow == i)
//        			{
//        				width = 0;
//        				//得到合并单元格的宽度
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
        			
        			//如果是被解析的单元格
        			if(rowIndex == i && columnIndex == j)
            		{       				
        				//获取单元格内容,信息
            			content = cellInfo.getContent();
            			fontSize = cellInfo.getFontSize();
            			isItalic = cellInfo.getItalic();
            			isBold = cellInfo.getBoldweight();
            			
            			//如果内容不为空，则设置单元格属性，否则跳过
            			if(content != null)
            			{
            				
            				if(isBold)
                			{
            					cloneViewTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);//字体样式
                			}
                			
                			if(isItalic)
                			{
                				
                			}
                			
                			//设置单元格的宽高	
                    		if(height != DEFAULT_HEIGHT || width != DEFAULT_WIDTH)
                    		{
                    			cloneViewTv.setLayoutParams(new android.widget.TableRow.LayoutParams(width, 
                    					height));
                    			cloneViewTv.setBackgroundResource(R.drawable.top_02);
                    			
                    		}                    	                    		                   		
                			
                    		cloneViewTv.setText(content);//文本信息

                    		
                    		//设置的参数比其他空白处的参数不一致就会出现缝隙
//                			mViewEt.setTextSize(TEXT_SIZE);//字体大小
//                    		mViewEt.setTextColor(Color.parseColor("#095583"));//文本颜色
                    		
                    		tablerow.addView(cloneViewTv);//添加到表格
                    		
                			cellCount++;
                			
//                			rowIndex = cellInfo.getRowIndex();
//                			columnIndex = cellInfo.getColumnIndex();
//                			Log.e(TAG, "rowIndex:" + rowIndex);
//                			Log.e(TAG, "columnIndex:" + columnIndex);
            			}
            			
            		}
        			else
        			{
                		//设置单元格的宽高	
                		if(height == DEFAULT_HEIGHT && width == DEFAULT_WIDTH)
                		{
                			tablerow.addView(cloneViewTv);
                		//	Log.e(TAG, "第"+i+"行"+"第"+j+"列.."+"width:" + width+"height:" + height);
                		}
                		else
                		{
                			cloneViewTv.setLayoutParams(new android.widget.TableRow.LayoutParams(width, 
                					height));
                			cloneViewTv.setBackgroundResource(R.drawable.top_02);
                			tablerow.addView(cloneViewTv);
                			
                		//	Log.e(TAG, "第"+i+"行"+"第"+j+"列.."+"width:" + width+"height:" + height);
                		}  
        			}
        		}
//        		j += mergedRowCount;??
//        		mergedRowCount = 0;

        	}
    		mTable.addView(tablerow, tableLparams);
    		height = DEFAULT_HEIGHT;
    		width = DEFAULT_WIDTH;
    		
//    		//遍历下一个合并单元格??
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
     * @param rowNum 表示第几列
     * @return 返回列标题的字母
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
     * @param num 传入的getHeightInPoints的值
     * 返回转换后的屏幕像素值
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
     * 绑定视图同步显示
     */
    private void synScrollViews()
    {
    	mHRowHeadScrollView = (CustomHScrollView)findViewById(R.id.row_scroll);
    	mHViewScroll = (CustomHScrollView)findViewById(R.id.view_h_scroll);
    	mLineScrollView = (CustomScrollView)findViewById(R.id.line_scroll);
    	mViewScroll = (CustomScrollView)findViewById(R.id.view_v_scroll);
    	   	
    	//绑定
    	mHRowHeadScrollView.setScrollView(mHViewScroll);
    	mHViewScroll.setScrollView(mHRowHeadScrollView);
    	mLineScrollView.setScrollView(mViewScroll);
    	mViewScroll.setScrollView(mLineScrollView);
    }

    
    //得到单元格的列的位置
    /**
     * @return 单元格所在的x坐标
     */
    public int getColunmCellPosition()
    {    	
    	mColunmIndex = 0;
    	int width = 0;
    	//滚动视图的偏移量+点击的坐标的偏移量
    	int x_offset = mHorizontalScrollOffSet + mX;
    	Log.e(TAG, ""+x_offset);
    	//在被解析过的视图内
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
    	
    	//未解析的视图
    	
    	return width;
    }
    
    /**
     * @return 单元格所在的X坐标
     */
    public int getRowCellPosition()
    {
    	mRowIndex = 0;
    	int height = 0;
    	Map<Integer, Integer> map = null;
    	//滚动视图的偏移量+点击的坐标的偏移量
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
    	//未解析的视图
    	
    	
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
		
		//水平视图的偏移量
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
	 * 得到被点击的那个编辑框
	 * @param rowIndex 行的索引
	 * @param colunmIndex 列索引
	 * @param x 坐标
	 * @param y 坐标
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
    	
    	//状态栏高度
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
		.setTitle("文档已修改")
		.setItems(new String[]{"放弃更改","保存","取消"}, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch(which)
				{
				//放弃
				case 0:
					backToTab();
					break;
				//保存
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
				//取消
				case 2:
					mDialog.dismiss();
					break;						
				}
			}
		}).create();
		mDialog.show();		
	}
		
}
