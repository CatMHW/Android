package com.radaee.reader;

import java.util.ArrayList;
import java.util.List;

import com.radaee.main.SoftWareCup;
import com.radaee.netdisk.BaiduCloud;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;


//package com.radaee.reader;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//import android.app.LocalActivityManager;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.os.Bundle;
//import android.support.v4.view.PagerAdapter;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class ViewPagerActivity extends Activity {
//
//	private ViewPager viewPager;//ҳ������
//	private ImageView imageView;// ����ͼƬ
//	private TextView textView1,textView2,textView3;
//	private List<View> views;// Tabҳ���б�
//	private int offset = 0;// ����ͼƬƫ����
//	private int currIndex = 0;// ��ǰҳ�����
//	private int bmpW;// ����ͼƬ���
//	private View view1,view2,view3;//����ҳ��
//	private Context context = null;
//	private LocalActivityManager manager = null;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.viewpager);
//		
//		context = ViewPagerActivity.this;
//		manager = new LocalActivityManager(this , true);
//		manager.dispatchCreate(savedInstanceState);
//		
//		InitImageView();
//		InitTextView();
//		InitViewPager();
//	}
//
//	private void InitViewPager() {
//		viewPager=(ViewPager) findViewById(R.id.vPager);
//		views=new ArrayList<View>();
//		LayoutInflater inflater=getLayoutInflater();
//	
//		Intent intent = new Intent(context, Historydemo.class);
//		views.add(getView("Historydemo", intent));
//		Intent intent2 = new Intent(context, Search.class);
//		views.add(getView("Search", intent2));
//		Intent intent3 = new Intent(context, Filedemo.class);
//		views.add(getView("Filedemo", intent3));
//
//		views.add(view1);
//		views.add(view2);
//		views.add(view3);
//		
//		viewPager.setAdapter(new MyViewPagerAdapter(views));
//		viewPager.setCurrentItem(0);
//		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
//	}
//	 /**
//	  *  ��ʼ��ͷ��
//	  */
//
//	private void InitTextView() {
//		textView1 = (TextView) findViewById(R.id.text1);
//		textView2 = (TextView) findViewById(R.id.text2);
//		textView3 = (TextView) findViewById(R.id.text3);
//
//		textView1.setOnClickListener(new MyOnClickListener(0));
//		textView2.setOnClickListener(new MyOnClickListener(1));
//		textView3.setOnClickListener(new MyOnClickListener(2));
//	}
//
//	/**
//	 2      * ��ʼ������
//	 3 */
//
//	private void InitImageView() {
//		imageView= (ImageView) findViewById(R.id.cursor);
//		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a).getWidth();// ��ȡͼƬ���
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
//		offset = (screenW / 3 - bmpW) / 2;// ����ƫ����
//		Matrix matrix = new Matrix();
//		matrix.postTranslate(offset, 0);
//		imageView.setImageMatrix(matrix);// ���ö�����ʼλ��
//	}
//
//	/** 
//	 *     
//	 * ͷ�������� 3 */
//	private class MyOnClickListener implements OnClickListener{
//        private int index=0;
//        public MyOnClickListener(int i){
//        	index=i;
//        }
//		public void onClick(View v) {
//			viewPager.setCurrentItem(index);			
//		}
//		
//	}
//	
//	/**
//	 * ͨ��activity��ȡ��ͼ
//	 * @param id
//	 * @param intent
//	 * @return
//	 */
//	private View getView(String id, Intent intent) {
//		return manager.startActivity(id, intent).getDecorView();
//	}
//	
//	public class MyViewPagerAdapter extends PagerAdapter{
//		private List<View> mListViews;
//		
//		public MyViewPagerAdapter(List<View> mListViews) {
//			this.mListViews = mListViews;
//		}
//
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) 	{	
//			container.removeView(mListViews.get(position));
//		}
//
//
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {			
//			 container.addView(mListViews.get(position), 0);
//			 return mListViews.get(position);
//				ViewPager pViewPager = ((ViewPager) arg0);
//				pViewPager.addView(list.get(arg1));
//				return list.get(arg1);	
//		}
//
//public Object instantiateItem(View arg0, int arg1) {
//	ViewPager pViewPager = ((ViewPager) arg0);
//	pViewPager.addView(list.get(arg1));
//	return list.get(arg1);
//}
//		@Override
//		public int getCount() {			
//			return  mListViews.size();
//		}
//		
//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1) {			
//			return arg0==arg1;
//		}
//	}
//
//    public class MyOnPageChangeListener implements OnPageChangeListener{
//
//    	int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����
//		int two = one * 2;// ҳ��1 -> ҳ��3 ƫ����
//		public void onPageScrollStateChanged(int arg0) {
//			
//			
//		}
//
//		public void onPageScrolled(int arg0, float arg1, int arg2) {
//			
//			
//		}
//
//		public void onPageSelected(int arg0) {
//			
//			Animation animation = null;
//			switch (arg0) {
//			case 0:
//				if (currIndex == 1) {
//					animation = new TranslateAnimation(one, 0, 0, 0);
//				} else if (currIndex == 2) {
//					animation = new TranslateAnimation(two, 0, 0, 0);
//				}
//				break;
//			case 1:
//				if (currIndex == 0) {
//					animation = new TranslateAnimation(offset, one, 0, 0);
//				} else if (currIndex == 2) {
//					animation = new TranslateAnimation(two, one, 0, 0);
//				}
//				break;
//			case 2:
//				if (currIndex == 0) {
//					animation = new TranslateAnimation(offset, two, 0, 0);
//				} else if (currIndex == 1) {
//					animation = new TranslateAnimation(one, two, 0, 0);
//				}
//				break;
//				
//			}
//			
//			//Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);
//			currIndex = arg0;
//			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
//			animation.setDuration(300);
//			imageView.startAnimation(animation);
//			Toast.makeText(ViewPagerActivity.this, "��ѡ����"+ viewPager.getCurrentItem()+"ҳ��", Toast.LENGTH_SHORT).show();
//		}
//    	
//    }
//}
public class ViewPagerActivity extends Activity {

	Context context = null;
	LocalActivityManager manager = null;
	ViewPager pager = null;
	TabHost tabHost = null;
	ImageView t1,t2,t3,t4;
	
	private int offset = 0;// ����ͼƬƫ����
	private int currIndex = 0;// ��ǰҳ�����
	private int bmpW;// ����ͼƬ���
	private ImageView cursor;// ����ͼƬ

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.viewpager);

		context = ViewPagerActivity.this;
		manager = new LocalActivityManager(this , true);
		manager.dispatchCreate(savedInstanceState);

		InitImageView();
		initTextView();
		initPagerViewer();

	}
	/**
	 * ��ʼ������
	 */
	private void initTextView() {
		t1 = (ImageView) findViewById(R.id.text1);
		t2 = (ImageView) findViewById(R.id.text2);
		t3 = (ImageView) findViewById(R.id.text3);
		t4 = (ImageView) findViewById(R.id.text4);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
		t4.setOnClickListener(new MyOnClickListener(3));
		
	}
	/**
	 * ��ʼ��PageViewer
	 */
	private void initPagerViewer() {
		pager = (ViewPager) findViewById(R.id.vPager);
		final ArrayList<View> views = new ArrayList<View>();
		Intent intent = new Intent(context, Historydemo.class);
		views.add(getView("Historydemo", intent));
		Intent intent2 = new Intent(context, Search.class);
		views.add(getView("Search", intent2));
		Intent intent3 = new Intent(context, Filedemo.class);
		views.add(getView("Filedemo", intent3));
		Intent intent4 = new Intent(context, BaiduCloud.class);
		views.add(getView("BaiduCloud", intent4));

		pager.setAdapter(new MyPagerAdapter(views));
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	/**
	 * ��ʼ������
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
		.getWidth();// ��ȡͼƬ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// ��ȡ�ֱ��ʿ��
		offset = (screenW / 4 - bmpW) / 2;// ����ƫ����
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// ���ö�����ʼλ��
	}


	/**
	 * ͨ��activity��ȡ��ͼ
	 * @param id
	 * @param intent
	 * @return
	 */
	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	/**
	 * Pager������
	 */
	public class MyPagerAdapter extends PagerAdapter{
		List<View> list =  new ArrayList<View>();
		public MyPagerAdapter(ArrayList<View> list) {
			this.list = list;
		}

		@Override
		public void destroyItem(ViewGroup container, int position,
				Object object) {
			ViewPager pViewPager = ((ViewPager) container);
			pViewPager.removeView(list.get(position));
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return list.size();
		}
		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ViewPager pViewPager = ((ViewPager) arg0);
			pViewPager.addView(list.get(arg1));
			return list.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
			
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	/**
	 * ҳ���л�����
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// ҳ��1 -> ҳ��2 ƫ����
		int two = one * 2;// ҳ��1 -> ҳ��3 ƫ����
		int three = one*3;

		
		public void onPageSelected(int arg0) {
			//Animation animation = null;
			Log.e("offset:",""+one+"-"+two+"-"+three);
//			switch (arg0) {
//			case 0:
//				if (currIndex == 1) {
//					animation = new TranslateAnimation(one, 0, 0, 0);
//				} else if (currIndex == 2) {
//					animation = new TranslateAnimation(two, 0, 0, 0);
//				}
//				else if(currIndex == 3)
//				{
//					animation = new TranslateAnimation(three, 0, 0, 0);
//				}
//				Log.e("offset_page:","  from:"+arg0  +"  to:"+0 +"  currIndex:" +currIndex);
//				break;
//			case 1:
//				if (currIndex == 0) {
//					animation = new TranslateAnimation(offset, one, 0, 0);
//				} else if (currIndex == 2) {
//					animation = new TranslateAnimation(two, one, 0, 0);	
//				} else if (currIndex == 3) {
//					animation = new TranslateAnimation(three, one, 0, 0);	
//				}
//				Log.e("offset_page:","  to:"+arg0  +"  to:"+0 +"  currIndex:" +currIndex);
//				break;
//			case 2:
//				if (currIndex == 0) {
//					animation = new TranslateAnimation(offset, two, 0, 0);
//				} else if (currIndex == 1) {
//					animation = new TranslateAnimation(one, two, 0, 0);
//				} else if (currIndex == 3) {
//					animation = new TranslateAnimation(three, two, 0, 0);	
//				}
//				Log.e("offset_page:","  from:"+arg0  +"  to:"+0 +"  currIndex:" +currIndex);
//			case 3:
//				if (currIndex == 0) {
//					animation = new TranslateAnimation(offset, three, 0, 0);
//				} else if (currIndex == 1) {
//					animation = new TranslateAnimation(one, three, 0, 0);
//				} else if (currIndex == 2) {
//					animation = new TranslateAnimation(two, three, 0, 0);	
//				}
//				Log.e("offset_page:","  from:"+arg0  +"  to:"+0 +"  currIndex:" +currIndex);
//				break;
//			}
			Animation animation = new TranslateAnimation(one*currIndex, one*arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:ͼƬͣ�ڶ�������λ��
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

	
		public void onPageScrollStateChanged(int arg0) {
			
		}

	
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
	}
	/**
	 * ͷ��������
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		
		public void onClick(View v) {
			pager.setCurrentItem(index);
		}
	};
	 /*���������ذ�ť*/
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	   	switch(keyCode)
	   	{
	   	case KeyEvent.KEYCODE_BACK:

	   		this.backToMain();
	   		
	   		break;
	   	case KeyEvent.KEYCODE_MENU:
	   		break;
	   	}
		return false;	
	 }

	private void backToMain()
	{
		Intent intent = new Intent(ViewPagerActivity.this, SoftWareCup.class);
		this.startActivity(intent);
		this.finish();
		
		this.overridePendingTransition(R.anim.main_right_in,
				R.anim.main_right_out);
	}
}