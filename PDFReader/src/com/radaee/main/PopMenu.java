package com.radaee.main;

import java.util.ArrayList;

import com.radaee.reader.R;
import com.radaee.rotate.MyAnimations;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


public class PopMenu {
	private ArrayList<String> itemList;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;
	private int popHeight = 0;
	// private OnItemClickListener listener;
	private View mBtn = null; //���ͼƬ�İ�ť

	public PopMenu(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		
		
		itemList = new ArrayList<String>(5);
		
		View view = LayoutInflater.from(context)
				.inflate(R.layout.popmenu, null);
		
		//POPWINDOW�߶�
//		LinearLayout layout1 = (LinearLayout) getResources().getLayout(R.id.popup_view_cont);
//		layout1.measure(ww, hh);  	
//		popHeight = layout1.getMeasuredHeight(); 
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		LinearLayout layout1 = (LinearLayout) view.findViewById(R.id.popup_view_cont);
		layout1.measure(w, h);  
		popHeight = layout1.getMeasuredHeight();  
		Log.e("popHeight", ":"+popHeight);
		
		// ���� listview
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setAdapter(new PopAdapter());
		listView.setFocusableInTouchMode(true);
		listView.setFocusable(true);

		popupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow = new PopupWindow(view, context.getResources()
				.getDimensionPixelSize(R.dimen.popmenu_width),
				LayoutParams.WRAP_CONTENT);

		// �����Ϊ�˵��������Back��Ҳ��ʹ����ʧ�����Ҳ�����Ӱ����ı�����������ģ�
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		
			
	}

	
	// ���ò˵�����������
	public void setOnItemClickListener(OnItemClickListener listener) {
		// this.listener = listener;
		listView.setOnItemClickListener(listener);
	}

	// ������Ӳ˵���
	public void addItems(String[] items) {
		for (String s : items)
			itemList.add(s);
	}

	// ������Ӳ˵���
	public void addItem(String item) {
		itemList.add(item);
	}

	// ����ʽ ���� pop�˵� parent ���½�
	public void showAsDropDown(View parent) {		
		popupWindow.showAsDropDown(parent,
				10,
				// ��֤�ߴ��Ǹ�����Ļ�����ܶ�����
				context.getResources().getDimensionPixelSize(
						R.dimen.popmenu_yoff));

		// ʹ��ۼ�
		popupWindow.setFocusable(true);
		// ����������������ʧ
		popupWindow.setOutsideTouchable(true);
		// ˢ��״̬
		popupWindow.update();
	}

	public void showAsLocation(View parent, int x, int y)
	{		
		popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, x, y-popHeight);
		// ʹ��ۼ�
				popupWindow.setFocusable(true);
				// ����������������ʧ
				popupWindow.setOutsideTouchable(true);
				popupWindow.setAnimationStyle(R.style.my_pop_style);
				// ˢ��״̬
				popupWindow.update();
				
	}
	
	// ���ز˵�
	public void dismiss() {
	
		popupWindow.dismiss();
	}

	// ������
	private final class PopAdapter extends BaseAdapter {

		
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.pomenu_item, null);
				holder = new ViewHolder();

				convertView.setTag(holder);

				holder.groupItem = (TextView) convertView
						.findViewById(R.id.textView);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.groupItem.setText(itemList.get(position));

			return convertView;
		}

		private final class ViewHolder {
			TextView groupItem;
		}
	}
}
