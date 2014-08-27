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

	//��С������������
	private final int MIN_ROW_NUM = 10;
	private final int MIN_LINE_NUM = 40;
	
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
	
	//�Զ���TextView
	private MyTextView mLineTv = null;							//������ʾ�к�
	private MyTextView mViewEt = null;							//������ʾExcel����
	private MyTextView mRowTv = null;							//������ʾ�к�
	
	private CustomHScrollView mHRowHeadScrollView;				//ˮƽ������ͼ��������ʾ�к�
	private CustomScrollView mLineScrollView;					//��ֱ������ͼ��������ʾ�к�
	
	//������View�໥Ƕ�ף�������ʾ����
	private CustomHScrollView mHViewScroll;						
	private CustomScrollView mViewScroll;			
	
	private GestureDetector mGestureDetector = null;		//����
	private TableLayout mTable = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_table);
		
		mGestureDetector = new GestureDetector(this); 
	
		//��ʼ����ͼ
		addToLineHead(MIN_LINE_NUM);
		addToRowHead(MIN_ROW_NUM);
		addToView(MIN_ROW_NUM, MIN_LINE_NUM);
			
		//����ͼͬ������
		synScrollViews();
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
    		   	
    	mLineTv = new MyTextView(this);	
    	mLineTv.setLayoutParams(new android.widget.TableRow.
				LayoutParams(ROW_HEAD_WIDTH, ROW_HEAD_HEIGHT));
		mLineTv.setId(10087);										//ע��ID
		mLineTv.setGravity(Gravity.CENTER);
		mLineTv.setTextColor(Color.parseColor("#095583"));			//�ı���ɫ
		mLineTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);	//������ʽ		
		
    	//��ʼ������������
    	for(int i=0; i < number; ++i)
    	{
    		
    		MyTextView cloneLineTv = mLineTv.clone();	
    		TableRow tablerow = new TableRow(ExcelTable.this);
 		
    		cloneLineTv.setText(String.valueOf(i+1));				//�ı���Ϣ	
    		cloneLineTv.setBackgroundResource(R.drawable.top02);	//����ͼƬ
			tablerow.addView(cloneLineTv);							//��ӵ����
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
    	
    	TableRow tablerow = new TableRow(ExcelTable.this);    	
    	
    	int columnWidth = 150;
    	mRowTv = new MyTextView(this);
    	mRowTv.setId(10086);										//ע��ID	
		mRowTv.setGravity(Gravity.CENTER);
		mRowTv.setTextColor(Color.parseColor("#095583"));			//�ı���ɫ
		mRowTv.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);	//������ʽ
		
    	//��ʼ������������
    	for(int i=0; i < number; ++i)
    	{
    		MyTextView cloneRowTv = mRowTv.clone();
    		String str = getRowName(i);    		    		
    		cloneRowTv.setLayoutParams(new android.widget.TableRow.
    				LayoutParams(columnWidth, COLUNM_HEAD_HEIGHT));
    		cloneRowTv.setBackgroundResource(R.drawable.top02);		//����ͼƬ
    		cloneRowTv.setText(str);								//�ı���Ϣ
			tablerow.addView(cloneRowTv);							//��ӵ����
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
    	 	   	
    	mViewEt = new MyTextView(this);
    	mViewEt.setLayoutParams(new android.widget.TableRow.LayoutParams(DEFAULT_WIDTH, 
				DEFAULT_HEIGHT));
		mViewEt.setBackgroundResource(R.drawable.my_textview);
		mViewEt.setGravity(Gravity.CENTER);
		mViewEt.setTextSize(TEXT_SIZE);//�����С
		mViewEt.setTextColor(Color.parseColor("#095583"));//�ı���ɫ
	

		
    	//��
    	for(int i=1; i <= lineNum; ++i)
    	{
    		TableRow tablerow = new TableRow(ExcelTable.this);

    		//��
    		for(int j=1; j <= rowNum; ++j)
        	{
        		
    			MyTextView cloneViewTv = mViewEt.clone();
    			cloneViewTv.setText(String.valueOf(i*j));
                tablerow.addView(cloneViewTv);//��ӵ����

        	}
    		mTable.addView(tablerow, tableLparams);
    		
    	}
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
