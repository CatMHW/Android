package com.radaee.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.radaee.reader.Filedemo;
import com.radaee.reader.HashMapRead;
import com.radaee.reader.PDFReaderAct;
import com.radaee.reader.R;
import com.radaee.rotate.MyAnimations;
import com.radaee.excel.FileManager;
import com.radaee.main.CreatPdf;
import com.reader.setting.Clothes;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;


public class Scaning extends Activity 
{
	private static int SELECT_PICTURE = 1; 
	private static int TAKE_PICTURE = 2;
	
	@SuppressWarnings("deprecation")
	private Gallery mGallery = null;
	private List<Map<String, String>> mList = new ArrayList<Map<String, String>>();
	private HashMap<Integer, Bitmap> imgCache = new HashMap<Integer, Bitmap>();
	private ArrayList<String> mImgPathList = new ArrayList<String>();
	private ImageAdapter mSimpleAdapter = null;
	private ImageSwitcher mImageSwitcher = null;
	private DisplayMetrics mPhoneSize = null;
	//private Rect rect = null;
	private int mStatusBarHeight = 0;
	private Button mAddBtn;
	private Button mTakePhotoBtn = null;
	private Button mScanPhotoBtn = null;
	private Intent mIntent = null;
	private Bitmap bm = null;
	private View mMakePdfBtn = null;
	private EditText mPdfNameEdit = null;	
	private PopupWindow mPdfNamePop = null;
	private PopupWindow mPopWindow = null;
	private PopupWindow mTipPop = null;
	private View mPopView = null;
	private Document mDocument = null;
	private String mPdfName = null;
	private String mPdfPath = null;
	private String mImagePath = null;
	private Button mSureBtn = null;
	private Button mCancelBtn = null;
	private PopMenu popMenu;
	private Context context;
	private ImageView mBack = null;
	private Handler mHandler = null;
	
	private static boolean deleteHappen = false;
	boolean isPopWindowShow = false;
	boolean isPopWindowShow1 = false;
	
	private int screenWidth;
	private int screenHeight;
	private int galleryHeight = 0;
	private int titleHeight = 0;
	private int tipPopHeight = 0;
	private int selectPosition = 0;
	private float downX;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.scaning);
		
		mHandler = new Handler();
		
		//添加按钮,动画初始化
		MyAnimations.initOffset(this);
		
		//得到屏幕的大小
		mPhoneSize = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mPhoneSize);
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		context = Scaning.this;
		this.mAddBtn = (Button)findViewById(R.id.AddBtn);
		
		popMenu = new PopMenu(context);
		popMenu.addItems(new String[] { "拍   照", "浏   览"});
		// 菜单项点击监听器
		popMenu.setOnItemClickListener(popmenuItemClickListener);
				
		initWidget();
		
		dm = new DisplayMetrics();    
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
				
	}

	private void initTipPop()
	{
		View view = LayoutInflater.from(this)
				.inflate(R.layout.pop_tip, null);
		
		int ww = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		int hh = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.tip_pop_layout);
		layout.measure(ww, hh);
		tipPopHeight = layout.getMeasuredHeight();
		
		mTipPop = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mTipPop.setBackgroundDrawable(new BitmapDrawable());
		mTipPop.setOutsideTouchable(true);
		
		Runnable runnable = new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				if(null != Scaning.this.getWindow().getDecorView().getWindowToken())
				{
					mTipPop.showAtLocation(mAddBtn, Gravity.NO_GRAVITY,
							screenWidth-120 , screenHeight-galleryHeight-tipPopHeight);
				//	mTipPop.setAnimationStyle(R.style.my_pop_style);
					
					//停止检测
					mHandler.removeCallbacks(this);  
				}
				else
				{
					// 如果activity没有初始化完毕则等待5毫秒再次检测  
                    mHandler.postDelayed(this, 5);  
				}
			}
		};
		// 开始检测  
        mHandler.post(runnable);  
	}
	/**
	 * 
	 */
	private void initWidget()
	{
		int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
				
		RelativeLayout rLayout = (RelativeLayout) Scaning.this.findViewById(R.id.rLayout);
		rLayout.measure(w, h);  
		titleHeight = rLayout.getMeasuredHeight();  

		Log.e("titleHeight", ":"+titleHeight);
		//制作PDF按钮
		this.mMakePdfBtn = (View)findViewById(R.id.makePDFBtn);
		this.mMakePdfBtn.setOnClickListener(new MakePdf());
		this.mMakePdfBtn.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //更改为按下时的背景图片     
                     v.setBackgroundResource(R.drawable.pdf_btn_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //改为抬起时的图片     
	                      v.setBackgroundResource(R.drawable.pdf_btn);   
	              }     
				return false;
			}
		});
		
		//初始化适配器
		this.initAdapter();
		this.mGallery = (Gallery)findViewById(R.id.photoGallery);
		
		int ww = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		int hh = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout2);		
		
		//画廊高度 
		layout.measure(ww, hh);  				
		galleryHeight =layout.getMeasuredHeight();  		
		Log.e("galleryHeight", ":"+galleryHeight);
		
		this.mImageSwitcher = (ImageSwitcher)findViewById(R.id.photoView);
		this.mImageSwitcher.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch(event.getAction()) {  
                case MotionEvent.ACTION_DOWN:  
                     downX = event.getX();
                     Log.e("downx", ":"+downX);
                    break;  
                case MotionEvent.ACTION_UP:  
                    int totalCount = mImgPathList.size();
                    
                    
                    float upX = event.getX();
                    Log.e("upx", ":"+upX);
                    Log.e("downx1", ":"+downX);
                    if (upX - downX > 100 && selectPosition > 0) 
                    {
                    //	Log.e("TO_LEFT", "TO_LEFT");
                    	setSwitcherPic(mImgPathList.get(--selectPosition), false);
                    } 
                    else if (downX - upX > 100 && selectPosition < totalCount-1)              
                    { 
                    //	Log.e("TO_RIGHT", "TO_RIGHT");
                    	setSwitcherPic(mImgPathList.get(++selectPosition), true);
                    } 

                    break;  
                }  
                return true;  
			}
		});
		
		
		this.mAddBtn.setOnClickListener((OnClickListener) new AddPhotoBtnClicked());
		this.mAddBtn.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //更改为按下时的背景图片     
                     v.setBackgroundResource(R.drawable.addphoto_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //改为抬起时的图片     
	                      v.setBackgroundResource(R.drawable.c_1);   	                      
	              }     
				return false;
			}
		});
		
		//设置图片工厂
		this.mImageSwitcher.setFactory(new ViewFactoryImpl());
		
		//设置图片集
		this.mGallery.setAdapter(this.mSimpleAdapter);
		this.mGallery.setOnItemClickListener(new OnItemClickListenerImpl());
		this.mGallery.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long id) {
				// TODO Auto-generated method stub

				AlertDialog.Builder build = new Builder(Scaning.this);
				build.setTitle("确定要删除图片吗")
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Log.e("position", ":"+position);
						onDelBtnDown(position);
					}				
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}					
				});
				build.create().show();
				return false;
			}
		});
		
		mBack = (ImageView)findViewById(R.id.back);		
		mBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Scaning.this.backToMain();
				
			}
		});
		mBack.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if(event.getAction() == MotionEvent.ACTION_DOWN){     
                     //更改为按下时的背景图片     
                     v.setBackgroundResource(R.drawable.back_pressed);     
	              }else if(event.getAction() == MotionEvent.ACTION_UP){     
	                      //改为抬起时的图片     
	                      v.setBackgroundResource(R.drawable.back);   	                      
	              }     
				return false;
			}
		});
		
		initTipPop();
	}
	
	
 

	private void onDelBtnDown(int pos) 
	{
		// TODO Auto-generated method stub
		deleteHappen = true;
		Log.e("scaning","the pos will deleted is " + pos);		
		File current = new File(mImgPathList.get(pos));
		current.delete();
		
		mImgPathList.remove(pos);		
		this.mSimpleAdapter.notifyDataSetChanged();
		mGallery.setAdapter(this.mSimpleAdapter);
		
		if(pos == 0){
			mImageSwitcher.setImageDrawable(null);			
		}
		else if(pos != 0)
		{
			setSwitcherPic(mImgPathList.get(pos-1));
		}
		Toast.makeText(this, "图片删除成功",
                Toast.LENGTH_SHORT).show();
	}	
	
		
	private void backToMain()
	{
		Intent intent = new Intent(this, SoftWareCup.class);
		this.startActivity(intent);
		this.finish();
		
		this.overridePendingTransition(R.anim.main_right_in,
				R.anim.main_right_out);
	}
	
	// 弹出菜单监听器
	OnItemClickListener popmenuItemClickListener = new OnItemClickListener() {
			
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch(position)
			{
			case 0:
				Scaning.this.TakePhoto();
				break;
			case 1:
				Scaning.this.ScanPhoto();
				break;
			default:
				break;
			}
		}
	};
		
	public void showPdfPopWindow()
	{
//		LayoutInflater inflater = LayoutInflater.from(this);
//		this.mPopView = inflater.inflate(R.layout.pdf_dialog, null);
//		this.mPdfNamePop = new PopupWindow(mPopView, 300, 220, true);
//		this.mPdfNamePop.setFocusable(true);
//		this.mPdfNamePop.setBackgroundDrawable(new BitmapDrawable());
//		
//		this.mPdfNameEdit = (EditText)mPopView.findViewById(R.id.pdfNameEditText);
//		mSureBtn = (Button)mPopView.findViewById(R.id.sureBtn);
//		mSureBtn.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//				mPdfName = mPdfNameEdit.getText().toString();
//				Log.e("PDF-MAKE", mPdfName);
//				
//				if(mPdfName == null)
//				{
//					Toast.makeText(Scaning.this, "请输入文件名", Toast.LENGTH_SHORT).show();
//				}
//				
//				mPdfNamePop.dismiss();
//				
//				isPopWindowShow = false;
//				
//				Log.e("PDF-MAKE", "9");
//				if(mPdfName != null)
//				{
//					startMakePdf();
//				}
//			}
//		});
//		
//		Button cancelBtn = (Button)mPopView.findViewById(R.id.cancelBtn);
//		cancelBtn.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mPdfNamePop.dismiss();
//				isPopWindowShow = false;
//			}
//		});
//		
//		//显示弹出窗口
//		this.mPdfNamePop.showAtLocation(this.mMakePdfBtn, Gravity.CENTER, 0, 0);
//		
//		isPopWindowShow = true;
		this.mPdfNameEdit = new EditText(this);
		Dialog dialog = new AlertDialog.Builder(this)
//							.setIcon(R.drawable.home)
							.setView(this.mPdfNameEdit)
							.setTitle("请输入PDF的名字")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									mPdfName = Scaning.this.mPdfNameEdit.getText().toString();
									
									if(!mPdfName.equals(null))
									{
										startMakePdf();
									}
									else
									{
										Toast.makeText(Scaning.this, "请输入PDF的文件名", Toast.LENGTH_SHORT).show();
									}
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							}).create();
		Window window = dialog.getWindow();
		window.setWindowAnimations(R.style.my_dialog_style);
		dialog.show();
		
	}
	private DisplayMetrics dm = null;
	
	/**
	 * 开始制作PDF
	 */
	public void startMakePdf()
	{
		//CreatPdf pdf = new CreatPdf();
		
		int screenHeight = dm.heightPixels;
		int screenWidth = dm.widthPixels;
		FileManager pdf = new FileManager();
		Document mDocument = new Document();
		Log.e("PDF-MAKE", "8");
		pdf.makeDir("MakedPDF");
			
		Log.e("PDF-MAKE", "1");
		//创建PDF文件的路径
		try 
		{
			if(mPdfName != null)
			{ 
				mPdfPath = pdf.makeFilePath(mPdfName, "pdf");
			}				
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		Log.e("PDF-MAKE", "2");
		//得到一个PdfWriter实例
		try 
		{
			PdfWriter.getInstance(mDocument,
					new FileOutputStream(mPdfPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("PDF-MAKE", "3");
		mDocument.open();
		
		//往PDF加入一个段落
//		try {
//			mDocument.add(new Paragraph("Hello World"));
//			mDocument.add(new Paragraph("Hi~"));
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Log.e("PDF-MAKE", "4");
		
		//加入图像
		for(int i=0; i < mImgPathList.size(); ++i)
		{
			Image img = null;
			try {
				img = Image.getInstance(mImgPathList.get(i));
			} catch (BadElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			img.scaleToFit(screenWidth, screenHeight - 72);
			img.setAlignment(Image.ALIGN_MIDDLE);
			//img.setAbsolutePosition(0, 200);
			
			try {
				mDocument.add(img);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
						
		mDocument.close();		
		
		viewMakedPdf(mPdfPath);
		//Toast.makeText(Scaning.this, "PDF制作成功", Toast.LENGTH_SHORT).show();	
	}
	
	/**
	 * @author CatM
	 * 制作PDF按钮响应函数
	 */
	private class MakePdf implements OnClickListener
	{		
	
		
		public void onClick(View v) 
		{
			if(!mImgPathList.isEmpty())
			{
				showPdfPopWindow();
			}
			//Log.e("PDF-MAKE", "5");			
		}	
		
	}	
	
	//Gallery的监听事件
	private class OnItemClickListenerImpl implements OnItemClickListener
	{
	//	@SuppressWarnings("unchecked")		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) 
		{
			//取出map
		//	position = position % mList.size();
//			Map<String, String> map = (Map<String, String>)Scaning
//					.this.mSimpleAdapter.getItem(position);
				
			//设置显示图片
			//Scaning.this.mImageSwitcher.setBackgroundResource(map.get("img"));
			//String imgPath = map.get("img");
//			String imgPath = mImgPathList.get(position);
//			Uri imgUri = Uri.parse(imgPath);
//			Scaning.this.mImageSwitcher.setImageURI(imgUri);
			//Drawable drawable = new BitmapDrawable();
			//drawable.
			
			//Scaning.this.mImageSwitcher.setImageDrawable(drawable);
			
			String imgPath = mImgPathList.get(position);
			
			setSwitcherPic(imgPath);
	
			releaseBitmap();
			
			selectPosition = position;
				
		}		
	}
//	private Map<String, String> history = new HashMap<String,String>();
//	private File sdFile = android.os.Environment.getExternalStorageDirectory();
//	private String DirPath = sdFile.getAbsolutePath() + File.separator + 
//			"UIT-MAX"+ File.separator+"data"+File.separator;
//	private String hisPath = DirPath + "history.txt";
	
	public void viewMakedPdf(String path)
	{
		Date date =new Date(System.currentTimeMillis());				//获取系统时间
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	//设置时间格式
		String time = f.format(date);	
		
		Intent i = new Intent();
		i.setClass(Scaning.this, PDFReaderAct.class);
		PDFReaderAct.path = path;
		PDFReaderAct.isMaked = true;

//		history.put(path,time);
//		new HashMapRead(history, hisPath);
//    
		startActivity(i);
		finish();
	}
	private void setSwitcherPic(String imgPath, boolean toRight)
	{
		
			if(toRight) 
			{  
                mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(Scaning.this,  
                        R.anim.push_left_in));  
                mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(Scaning.this,  
                        R.anim.push_left_out));  
                setSwitcherPic(imgPath);
                
				
            } 
			else
			{  
            	mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(Scaning.this,  
            			R.anim.push_right_in));  
            	mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(Scaning.this,  
            			R.anim.push_right_out));  
            	setSwitcherPic(imgPath);
            }  
		
	}
	/**
	 * @param imgPath 图片路径
	 * 将图片缩放后显示在switcher上
	 */
	private void setSwitcherPic(String imgPath)
	{
		InputStream is = null;
		try 
		{
			 is = new FileInputStream(imgPath);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inSampleSize = 2;
		
		bm = BitmapFactory.decodeFile(imgPath, options);			
		BitmapDrawable bmDrawable = new BitmapDrawable(bm);
		Scaning.this.mImageSwitcher.setImageDrawable(bmDrawable);
	}
	private void releaseBitmap() {
		// TODO Auto-generated method stub
		int begin = mGallery.getFirstVisiblePosition();
		int end = mGallery.getLastVisiblePosition();
		
		for(int i = 0 ; i < begin ; i++){
			Bitmap delBitmap = imgCache.get(i);
			if(delBitmap != null){
				imgCache.remove(i);
				delBitmap.recycle(); 
				mSimpleAdapter.notifyDataSetChanged();
			}
		}
		
		for(int i = end + 1 ; i < imgCache.size() ; i++){
			Bitmap delBitmap = imgCache.get(i);
			if(delBitmap != null){
				imgCache.remove(i);
				delBitmap.recycle();
			}
		}
		
	}
	

	
	/**
	 * 适配器的初始化
	 */
	public void initAdapter()
	{
		//String picPath = getIntent().getStringExtra("picturePath");
		String picPath = SoftWareCup.picPath;
	//	Log.e("MainActivity", picPath);
		//bm = BitmapFactory.decodeFile(picPath);
		
		if(null != picPath)
		{
			mImgPathList.add(picPath);
		}
				
		this.mSimpleAdapter = new ImageAdapter(this, mImgPathList);
	
	}
	
	public class ImageAdapter extends BaseAdapter{
		
		private Context mContext;
		private int mGalleryBackground;
		private ArrayList<String> mArrayList;
		
		public ImageAdapter(Context c, ArrayList<String> list){
			mContext = c;
			mArrayList = list;
			
			//use attrs.xml to specify the Gallery's attributes
//			TypedArray mTypeArray = obtainStyledAttributes(R.styleable.Gallery);
//			mGalleryBackground = mTypeArray.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
//			mTypeArray.recycle();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			//new a ImageView for the image displaying
			Log.d("Adapter","getView--->and pos = " + position);
			ImageView imgView = new ImageView(mContext);
		//	Bitmap current = imgCache.get(position);
			String picPath = mArrayList.get(position);
			
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
		//	int scale = countScale(options.outWidth, options.outHeight);			
			options.inSampleSize = 10;
			Bitmap bm = imgCache.get(position);	
			
			if(bm == null)
			{
				bm = BitmapFactory.decodeStream(is, null, options);
				Log.e("Adapter", "width"+options.outWidth+"\n"+options.outWidth);
				imgCache.put(position, bm);
			}
		//	bm = BitmapFactory.decodeFile(picPatch, options);
			
			imgView.setImageBitmap(bm);
			imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imgView.setLayoutParams(new Gallery.LayoutParams(100, 100));
//			if(!bm.isRecycled())
//			{
//				bm.recycle();
//				System.gc();
//			}	
//			if(deleteHappen || current == null){
//				current = BitmapFactory.decodeFile(mArrayList.get(position));
//				
//				//Log.e(LOG_TAG,"decodeFile path = " + mArrayList.get(position));
//				
//				imgCache.put(position, current);
//			}
			
			return imgView;
		}
		
	}
	/**
	 * @author Administrator
	 *
	 */
	private class AddPhotoBtnClicked implements OnClickListener
	{

		public void onClick(View v)
		{		
			popMenu.showAsLocation(v, screenWidth-120 , screenHeight-galleryHeight);				
		}		
	}
	
	//视图工厂类
	private class ViewFactoryImpl implements ViewFactory
	{
		
		public View makeView()
		{

			//得到状态栏高度
		//	rect = new Rect();
		//	getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
			mStatusBarHeight = getStatusHeight(Scaning.this);
		//	Log.e("ssss", "mStatusBarHeight"+mStatusBarHeight);
			
			ImageView img = new ImageView(Scaning.this);
			img.setBackgroundResource(R.drawable.g_bg);
			img.setScaleType(ImageView.ScaleType.FIT_CENTER);
		//	img.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,
		//			LayoutParams.FILL_PARENT-100));
			//设置IMAGEVIEW的大小

			img.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,
					mPhoneSize.heightPixels-galleryHeight-mStatusBarHeight-titleHeight));
//			img.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT,
//					mPhoneSize.heightPixels-150));
			return img;
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 
	        if (resultCode == RESULT_OK) 
	        { 
	            if (requestCode == SELECT_PICTURE) 
	            { 
	               // mAddBtn.setBackgroundDrawable(Drawable.createFromPath(tempFile 
	                      //  .getAbsolutePath())); 
	            	//获得图片的uri 
	            	Uri selectedImgUri = data.getData();
	            	
	            	String[] filePathColumn = { MediaStore.Images.Media.DATA };

	            	
	            	Cursor cursor = getContentResolver().query(selectedImgUri,
	    					filePathColumn, null, null, null);
	            	cursor.moveToFirst();
	            	
	            	int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	    			String picturePath = cursor.getString(columnIndex);
	    			cursor.close();
	    			Log.e("picturePath", picturePath);
	    			
	    			//将图片加入到画廊里
	    			addPhoto(picturePath);
	    			setSwitcherPic(picturePath);
	    			//Scaning.this.popMenu.dismiss();
	            	isPopWindowShow1 = false;
	    		
	            //	mAddBtn.setText("Ok!");
	            } 
	            
	            if(requestCode == TAKE_PICTURE)
	            {
	            	addPhoto(mImagePath);
	            	setSwitcherPic(mImagePath);
	            	//Scaning.this.popMenu.dismiss();
	            	isPopWindowShow1 = false;
	            }
	        } 
	   } 
	
	private boolean addPhoto(String picPath)
	{
		mImgPathList.add(picPath);
		mSimpleAdapter.notifyDataSetChanged();
		selectPosition = mImgPathList.size()-1;
		return true;		
	}
	
	public static Bitmap createFitinBitmap(String path, int fitinWidth, int fitinHeight) 
	{
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		int sampleSize1 = opts.outWidth / fitinWidth;
		int sampleSize2 = opts.outHeight / fitinHeight;
		opts.inSampleSize = sampleSize1>sampleSize2? sampleSize1 : sampleSize2;
		opts.inJustDecodeBounds = false;
        opts.inDither = false;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, opts);
	}
	
	public void showPop()
	{
		Log.e("1", "1");
		LayoutInflater inflater = LayoutInflater.from(Scaning.this);
		Scaning.this.mPopView = inflater.inflate(R.layout.scan_popwindow, null);
		Scaning.this.mPopWindow = new PopupWindow(mPopView, 300, 220, true);
		Scaning.this.mPopWindow.setFocusable(true);
		Scaning.this.mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		
		Scaning.this.mTakePhotoBtn = (Button)mPopView.findViewById(R.id.takePhotoBtn);
		Scaning.this.mTakePhotoBtn.setOnClickListener(new onTakePhotoBtnClicked());
		
		Scaning.this.mScanPhotoBtn = (Button)mPopView.findViewById(R.id.scanPhotoBtn);
		Scaning.this.mScanPhotoBtn.setOnClickListener(new onScanPhotoBtnClicked());
		
		//显示弹出窗口
		Scaning.this.mPopWindow.showAtLocation(Scaning.this.mAddBtn, Gravity.CENTER, 0, 0);
		
		isPopWindowShow1 = true;
	}
	
	/**
	 * @author Administrator
	 *
	 */
	private class onTakePhotoBtnClicked implements OnClickListener
	{

		public void onClick(View v) 
		{
			mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//mIntent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(mIntent, 1);
			Scaning.this.mPopWindow.dismiss();
			isPopWindowShow = false;
		//	SoftWareCup.this.finish();
		}		
	}
	
	/**
	 * @author Administrator
	 *
	 */
	private class onScanPhotoBtnClicked implements OnClickListener
	{

		public void onClick(View v)
		{
			mIntent = new Intent(Intent.ACTION_GET_CONTENT); 
			mIntent.setType("image/*");
			startActivityForResult(mIntent, 2);
			Scaning.this.mPopWindow.dismiss();
			isPopWindowShow = false;
		}
		
	}
	
	private void TakePhoto()
	{
		mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		//mIntent.addCategory(Intent.CATEGORY_OPENABLE);
		
		//Scaning.this.mPopWindow.dismiss();
		Scaning.this.popMenu.dismiss();
		isPopWindowShow1 = false;
		  
		  SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      String filename = timeStampFormat.format(new Date());
	      ContentValues values = new ContentValues();
	      values.put(Media.TITLE, filename);
	      Uri photoUri = getContentResolver().insert(
	                                         MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	      mImagePath = this.getRealPathFromURI(photoUri,
	                                         getContentResolver());
	      mIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
	      startActivityForResult(mIntent, TAKE_PICTURE);
	}
	
	/**
	 * @param uri
	 * @param resolver
	 * @return 返回camera路径
	 */
	public String getRealPathFromURI(Uri uri, ContentResolver resolver) 
	{
		 
        String[] proj = { MediaStore.Images.Media.DATA };
         Cursor cursor = resolver.query(uri, proj, null, null, null);
         int column_index = cursor
                         .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
         cursor.moveToFirst();
         String str = cursor.getString(column_index);
         cursor.close();

        return str;

	}
	
	private void ScanPhoto()
	{
		mIntent = new Intent(Intent.ACTION_GET_CONTENT); 
		mIntent.setType("image/*");
		startActivityForResult(mIntent, SELECT_PICTURE);
		Scaning.this.popMenu.dismiss();
		isPopWindowShow1 = false;
	}
	
	/*处理点击返回按钮*/
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	   	switch(keyCode)
	   	{
	   	case KeyEvent.KEYCODE_BACK:
	   		if(isPopWindowShow)
	   		{
	   			mPdfNamePop.dismiss();
	   			isPopWindowShow = false;
	   			return true;
	   		}
	   		else if(isPopWindowShow1)
	   		{
	   			popMenu.dismiss();
	   			isPopWindowShow = false;
	   			return true;
	   		}
	   		else
	   		{
	   			this.backToMain();
	   		}
	   		break;
	   	case KeyEvent.KEYCODE_MENU:
	   		break;
	   	}
		return false;	
	 }
	
	//得到状态栏高度
	public static int getStatusHeight(Activity activity){
	        int statusHeight = 0;
	        Rect localRect = new Rect();
	        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
	        statusHeight = localRect.top;
	        if (0 == statusHeight){
	            Class<?> localClass;
	            try {
	                localClass = Class.forName("com.android.internal.R$dimen");
	                Object localObject = localClass.newInstance();
	                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
	                statusHeight = activity.getResources().getDimensionPixelSize(i5);
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            } catch (IllegalAccessException e) {
	                e.printStackTrace();
	            } catch (InstantiationException e) {
	                e.printStackTrace();
	            } catch (NumberFormatException e) {
	                e.printStackTrace();
	            } catch (IllegalArgumentException e) {
	                e.printStackTrace();
	            } catch (SecurityException e) {
	                e.printStackTrace();
	            } catch (NoSuchFieldException e) {
	                e.printStackTrace();
	            }
	        }
	        return statusHeight;
	    }

}
