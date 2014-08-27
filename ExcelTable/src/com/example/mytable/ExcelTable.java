package com.example.mytable;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ExcelTable extends Activity implements OnGestureListener{

	//最小的行数、列数
	private final int MIN_ROW_NUM = 10;
	private final int MIN_LINE_NUM = 40;
	
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
	
	//自定义TextView
	private MyTextView mLineTv = null;							//用来显示列号
	private MyTextView mViewEt = null;							//用来显示Excel数据
	private MyTextView mRowTv = null;							//用来显示行号
	
	private CustomHScrollView mHRowHeadScrollView;				//水平滚动视图，用来显示列号
	private CustomScrollView mLineScrollView;					//垂直滚动视图，用来显示行号
	
	//这两个View相互嵌套，用来显示数据
	private CustomHScrollView mHViewScroll;						
	private CustomScrollView mViewScroll;			
	
	private GestureDetector mGestureDetector = null;		//手势
	private TableLayout mTable = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_table);
		
		mGestureDetector = new GestureDetector(this); 
	
		//初始化视图
		addToLineHead(MIN_LINE_NUM);
		addToRowHead(MIN_ROW_NUM);
		addToView(MIN_ROW_NUM, MIN_LINE_NUM);
			
		//绑定视图同步闲书
		synScrollViews();
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
    		   	
    	mLineTv = new MyTextView(this);	
    	mLineTv.setLayoutParams(new android.widget.TableRow.
				LayoutParams(ROW_HEAD_WIDTH, ROW_HEAD_HEIGHT));
		mLineTv.setId(10087);										//注册ID
		mLineTv.setGravity(Gravity.CENTER);
		mLineTv.setTextColor(Color.parseColor("#095583"));			//文本颜色
		mLineTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);	//字体样式		
		
    	//初始化列名的数据
    	for(int i=0; i < number; ++i)
    	{
    		
    		MyTextView cloneLineTv = mLineTv.clone();	
    		TableRow tablerow = new TableRow(ExcelTable.this);
 		
    		cloneLineTv.setText(String.valueOf(i+1));				//文本信息	
    		cloneLineTv.setBackgroundResource(R.drawable.top02);	//背景图片
			tablerow.addView(cloneLineTv);							//添加到表格
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
    	
    	TableRow tablerow = new TableRow(ExcelTable.this);    	
    	
    	int columnWidth = 150;
    	mRowTv = new MyTextView(this);
    	mRowTv.setId(10086);										//注册ID	
		mRowTv.setGravity(Gravity.CENTER);
		mRowTv.setTextColor(Color.parseColor("#095583"));			//文本颜色
		mRowTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);	//字体样式
		
    	//初始化行名的数据
    	for(int i=0; i < number; ++i)
    	{
    		MyTextView cloneRowTv = mRowTv.clone();
    		String str = getRowName(i);    		    		
    		cloneRowTv.setLayoutParams(new android.widget.TableRow.
    				LayoutParams(columnWidth, COLUNM_HEAD_HEIGHT));
    		cloneRowTv.setBackgroundResource(R.drawable.top02);		//背景图片
    		cloneRowTv.setText(str);								//文本信息
			tablerow.addView(cloneRowTv);							//添加到表格
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
    	 	   	
    	mViewEt = new MyTextView(this);
    	mViewEt.setLayoutParams(new android.widget.TableRow.LayoutParams(DEFAULT_WIDTH, 
				DEFAULT_HEIGHT));
		mViewEt.setBackgroundResource(R.drawable.my_textview);
		mViewEt.setGravity(Gravity.CENTER);
		mViewEt.setTextSize(TEXT_SIZE);//字体大小
		mViewEt.setTextColor(Color.parseColor("#095583"));//文本颜色
	

		
    	//行
    	for(int i=1; i <= lineNum; ++i)
    	{
    		TableRow tablerow = new TableRow(ExcelTable.this);

    		//列
    		for(int j=1; j <= rowNum; ++j)
        	{
        		
    			MyTextView cloneViewTv = mViewEt.clone();
    			cloneViewTv.setText(String.valueOf(i*j));
                tablerow.addView(cloneViewTv);//添加到表格

        	}
    		mTable.addView(tablerow, tableLparams);
    		
    	}
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
    
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
