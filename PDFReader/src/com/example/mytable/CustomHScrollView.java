package com.example.mytable;



import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.HorizontalScrollView;

public class CustomHScrollView extends HorizontalScrollView{
	 private GestureDetector mGestureDetector; 
	 private View mView;

        /**
         * @function CustomHScrollView constructor
         * @param context  Interface to global information about an application environment. 
         * 					
         */
        public CustomHScrollView(Context context) {
                super(context);
                // TODO Auto-generated constructor stub
            	mGestureDetector = new GestureDetector(new HScrollDetector()); 

        	    setFadingEdgeLength(0); 
        }
        
        
        /**
         * @function CustomHScrollView constructor  
         * @param context Interface to global information about an application environment. 
         * @param attrs A collection of attributes, as found associated with a tag in an XML document.
         */
        public CustomHScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
            // TODO Auto-generated constructor stub
        	mGestureDetector = new GestureDetector(new HScrollDetector()); 

    	    setFadingEdgeLength(0); 
        }

        /**
         * @function  CustomHScrollView constructor  
         * @param context Interface to global information about an application environment. 
         * @param attrs A collection of attributes, as found associated with a tag in an XML document.
         * @param defStyle style of view
         */
        public CustomHScrollView(Context context, AttributeSet attrs,
                        int defStyle) {
                super(context, attrs, defStyle);
                // TODO Auto-generated constructor stub
            	mGestureDetector = new GestureDetector(new HScrollDetector()); 
        	    setFadingEdgeLength(0); 
        }
        
       @Override
   	 public boolean onInterceptTouchEvent(MotionEvent ev) { 
    	 //  Log.e("HScroll", "onInterceptTouchEvent");
   	     return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev); 
   	 } 
   	  
   	    // Return false if we're scrolling in the y direction   
   	 class HScrollDetector extends SimpleOnGestureListener { 
   	        @Override
   	    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {        
   	         if(Math.abs(distanceX) > Math.abs(distanceY)) { 
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
   		 
   		// Log.e("HScroll", ":"+(oldl-l));
   	}  
   	
   	
   	/**
   	 * @param view
   	 * 实现同步显示
   	 */
   	public void setScrollView(View view)
   	{  
   		 mView = view;    
   	} 
   	
   	public int computeHorizontalScrollOffset()
   	{   		
   		return super.computeHorizontalScrollOffset();
   	}
	
}

	
