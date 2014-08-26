package com.example.mytable;



import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.radaee.excel.CellInfo;
import com.radaee.reader.R;

public class EditPop {

	private Activity mActivity;
	private View mPopView;
	private PopupWindow mPopWindow = null;
	private EditText mEditText;
	private boolean isChanged = false;
	private String pre_text = null;
	private String last_text = null;
	private TextView tv = null;
	private List<CellInfo> textChangedCellList = new ArrayList<CellInfo>();		//记录被修改的单元格
	private CellInfo cellInfo = null;
	private int mRowIndex =0;
	private int mColunmIndex = 0;
	public EditPop(Activity _act) {
		// TODO Auto-generated constructor stub
		mActivity = _act;
		initView();
	}
	
	public void initView()
	{
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		this.mPopView = inflater.inflate(R.layout.edit_pop, null);
		this.mEditText = (EditText) this.mPopView.findViewById(R.id.excel_edit);
		this.mEditText.requestFocus();	
		
		this.mPopWindow = new PopupWindow(mPopView, 320, 200, true);
	//	this.mPopWindow.setFocusable(true);
		this.mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		this.mPopWindow.setOnDismissListener(new OnDismissListener() {
			
			public void onDismiss() {
				// TODO Auto-generated method stub
				last_text = mEditText.getText().toString();
				Log.e("pop", last_text);
				if(!pre_text.equals(last_text))
				{
					isChanged = true;
					tv.setText(last_text);
					cellInfo = new CellInfo();
					cellInfo.setRowIndex(mRowIndex);
					cellInfo.setColumnIndex(mColunmIndex);
					cellInfo.setContent(last_text);
					textChangedCellList.add(cellInfo);	
				}
			}
		});
	}
	
	public void setPopWidth(int width)
	{
		this.mPopWindow.setWidth(width);
	}
	
	public void setPopHeight(int height)
	{
		this.mPopWindow.setHeight(height);
	}
	
	public void setText(String text)
	{
		this.mEditText.setText(text);
	}
	
	public String getText()
	{
		return this.mEditText.getText().toString();
	}
	
	public void showAtLocation(View v, int x, int y)
	{		
		this.mPopWindow.showAtLocation(v, Gravity.NO_GRAVITY, x, y);
		last_text = pre_text = mEditText.getText().toString();		
		isChanged = false;
	}
	
	public void dismiss()
	{
		this.mPopWindow.dismiss();
	}
	
	public boolean hasChanged()
	{
		return isChanged;
	}
	
	public View getChildView()
	{
		return this.mEditText;
	}
	
	public String getLastText()
	{
		return last_text;
	}
	
	public void setView(View _tv)
	{
		tv = (TextView) _tv;
	}
	
	public void setRowIndex(int index)
	{
		mRowIndex = index;
	}
	
	public void setColunmIndex(int index)
	{
		mColunmIndex = index;
	}
	
	public List<CellInfo> getTextChangedList()
	{
		return textChangedCellList;
	}
}
