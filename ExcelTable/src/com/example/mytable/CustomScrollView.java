package com.example.mytable;



import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView{
	 private GestureDetector mGestureDetector; 
	 private View mView;
	 private int mVerticalOffset = 0;
	
	public CustomScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(new VScrollDetector()); 
	    setFadingEdgeLength(0); 
	}
	
	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(new VScrollDetector()); 
	    setFadingEdgeLength(0); 
	}
	
	public CustomScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mGestureDetector = new GestureDetector(new VScrollDetector()); 
	    setFadingEdgeLength(0); 
	}

	
	 @Override
	 public boolean onInterceptTouchEvent(MotionEvent ev) { 
	     return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev); 
	 } 
	  
	    // Return false if we're scrolling in the x direction   
	 class VScrollDetector extends SimpleOnGestureListener { 
	        @Override
	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {   	
	         if(Math.abs(distanceY) > Math.abs(distanceX)) { 
	                return true; 
	         } 
	        	
	         return false; 
	    } 
	 } 
	 
	 protected void onScrollChanged(int l, int t, int oldl, int oldt)
	 {    
		 super.onScrollChanged(l, t, oldl, oldt);    
	   	 if(mView!=null)
	   	 {  
	   		  mView.scrollTo(l, t);  
	   	 }    
	 } 

		/**
	   	 * @param view
	   	 * 实现同步显示
	   	 */
	   	public void setScrollView(View view)
	   	{  
	   		  mView = view;    
	   	} 
	   	
	   	public int computeVerticalScrollOffset () 
	   	{
	   		mVerticalOffset = super.computeVerticalScrollOffset();
	   		Log.e("Scroll", ":"+mVerticalOffset);
			return mVerticalOffset;
	   	}
}
