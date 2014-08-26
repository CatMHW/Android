package com.radaee.reader;

import com.radaee.pdf.Document;
import com.radaee.pdf.Matrix;
import com.radaee.pdf.Page;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class PDFGridThread extends Thread
{
	private Handler m_hand = null;
	private Handler m_handUI = null;
	private boolean is_notified = false;
	private boolean is_waitting = false;
	private synchronized void wait_init()
	{
		try
		{
			if( is_notified )
				is_notified = false;
			else
			{
				is_waitting = true;
				wait();
				is_waitting = false;
			}
		}
		catch(Exception e)
		{
		}
	}
	private synchronized void notify_init()
	{
		if( is_waitting )
			notify();
		else
			is_notified = true;
	}
	protected PDFGridThread(Handler hand_ui)
	{
		super();
		m_handUI = hand_ui;
	}
	@Override
    public void start()
	{
		super.start();
		wait_init();
	}
	@Override
    public void run()
	{
		Looper.prepare();
		m_hand = new Handler(Looper.myLooper())
		{
			public void handleMessage(Message msg)
			{
				if( msg != null )
				{
					if( msg.what == 0 )//render function
					{
						PDFGridItem item = (PDFGridItem)msg.obj;
						if( !item.page_is_cancel() )
						{
							Document doc = new Document();
							if( doc.Open(item.get_path(), null) == 0 )
							{
								Page page = doc.GetPage(0);
								item.page_start( page );
								float w = doc.GetPageWidth(0);
								float h = doc.GetPageHeight(0);
								int iw = item.get_imagew();
								int ih = item.get_imageh();
								Bitmap bmp = Bitmap.createBitmap( iw, ih, Bitmap.Config.ARGB_8888 );
								bmp.eraseColor(0);
								float ratiox = iw/w;
								float ratioy = ih/h;
								if( ratiox > ratioy ) ratiox = ratioy;
								Canvas canvas = new Canvas(bmp);
								Paint paint = new Paint();
								paint.setARGB(255, 255, 255, 255);
								canvas.drawRect((iw - w * ratiox)/2, (ih - h * ratiox)/2,
										(iw + w * ratiox)/2, (ih + h * ratiox)/2, paint);
								Matrix mat = new Matrix( ratiox, -ratiox, (iw - w * ratiox)/2, (ih + h * ratiox)/2 );
								page.RenderToBmp(bmp, mat);
								mat.Destroy();
								boolean ret = item.page_close( bmp );
								doc.Close();
								if( ret ) m_handUI.sendMessage(m_handUI.obtainMessage(0, item));
							}
						}
						item.notify_close();
						super.handleMessage(msg);
					}
					else//quit
					{
						super.handleMessage(msg);
						getLooper().quit();
					}
				}
				else
					getLooper().quit();
			}
		};
		notify_init();
		Looper.loop();
	}
	public synchronized void start_render( PDFGridItem item )
	{
		m_hand.sendMessage(m_hand.obtainMessage(0, item));
	}
	public synchronized void destroy()
	{
		try
		{
			m_hand.sendEmptyMessage(100);
			join();
			m_hand = null;
			m_handUI = null;
		}
		catch(InterruptedException e)
		{
		}
	}
}
