package com.radaee.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

//自定义适配器，实现无限循环
public class MySimpleAdapter extends SimpleAdapter
{
	private int count;
	private Context mContext;
	private List<Map<String,String>> mList;
	//private Context mContext;
	public MySimpleAdapter(Context context,
			List<Map<String, String>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		mContext = context;
		mList = data;
	}
	
//	public int getCount()
//	{
//		//count = super.getCount();
//		return mList.size();
//	}

//	public Object getItem(int position) {
//		// TODO Auto-generated method stub
//		return position;
//	}
//
//	public long getItemId(int position) {
//		// TODO Auto-generated method stub
//		return position;
//	}
//	
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ImageView imgView = new ImageView(mContext);
		Map<String, String> map = mList.get((int) this.getItemId(position));
		String picPath = map.get(this.getItem(position));
		Log.e("AdapterPath", picPath);
		InputStream is = null;
		try 
		{
			 is = new FileInputStream(picPath);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		int scale = countScale(options.outWidth, options.outHeight);
		Log.e("Adapter", "width"+options.outWidth+"\n"+options.outWidth);
		options.inSampleSize = 30;
		Bitmap bm = null;
	//	bm = BitmapFactory.decodeFile(picPatch, options);
		bm = BitmapFactory.decodeStream(is, null, options);
		imgView.setImageBitmap(bm);
		if(!bm.isRecycled())
		{
			bm.recycle();
			System.gc();
		}		
		return imgView;
	//	return super.getView(position % count, convertView, parent);
	}
	
	public int countScale(int width, int height)
	{
		int scale = 0;
	//	switch()
		return scale;
	}
}

