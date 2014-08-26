package com.radaee.reader;

import com.radaee.pdf.Page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PDFGridItem extends LinearLayout
{
	private ImageView m_image;
	private TextView m_name;
	private String m_path;
	private Bitmap m_bmp;
	private Page m_page;
	private boolean m_cancel = false;
	static Bitmap m_def_pdf_icon = null;
	static Bitmap m_def_dir_icon = null;
	static Bitmap m_def_up_icon = null;
	static int TEXT_COLOR = 0xFFCCCCCC;
	public PDFGridItem(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		if( m_def_pdf_icon == null )
			m_def_pdf_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.file03);
		if( m_def_dir_icon == null )
			m_def_dir_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder0);
		if( m_def_up_icon == null )
			m_def_up_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.folder1);
		this.setBackgroundColor(0);
		this.setOrientation(VERTICAL);
	}
	protected String get_path()
	{
		return m_path;
	}
	public String get_path_for_open()
	{
		wait_close();
		notify_close();
		return m_path;
	}
	public boolean is_dir()
	{
		return (m_bmp == m_def_dir_icon || m_bmp == m_def_up_icon);
	}
	public String get_name()
	{
		return (String) m_name.getText();
	}
	protected int get_imagew()
	{
		return m_bmp.getWidth();
	}
	protected int get_imageh()
	{
		return m_bmp.getHeight();
	}
	protected synchronized void page_set()
	{
		this.removeAllViews();
		m_image = new ImageView(getContext());
		m_image.setImageBitmap(m_bmp);
		m_image.setPadding(2, 2, 2, 2);
		this.addView(m_image);
		this.addView(m_name);
	}
	protected synchronized boolean page_is_cancel()
	{
		return m_cancel;
	}
	protected synchronized void page_start( Page page )
	{
		m_page = page;
	}
	protected synchronized boolean page_close( Bitmap bmp )
	{
		boolean ret = false;
		if( !m_page.RenderIsFinished() )
		{
			bmp.recycle();
			bmp = null;
		}
		m_page.Close();
		m_page = null;
		if( bmp != null )
		{
			m_bmp = bmp;
			ret = true;
		}
		return ret;
	}
	public synchronized void page_destroy()
	{
		m_cancel = true;
		if( m_page != null )
			m_page.RenderCancel();
		if( m_bmp != m_def_pdf_icon && m_bmp != m_def_dir_icon && m_bmp != m_def_up_icon && m_bmp != null )
		{
			m_bmp.recycle();
			m_bmp = null;
		}
	}
	public void set_dir( String name, String path )
	{
		m_path = path;
		m_name = new TextView(getContext());
		m_name.setText(name);
		m_name.setSingleLine(true);
		m_name.setGravity(Gravity.CENTER_HORIZONTAL);
		m_name.setTextColor(TEXT_COLOR);
		m_image = new ImageView(getContext());
		if( name == ".." )
			m_bmp = m_def_up_icon;
		else
			m_bmp = m_def_dir_icon;
		m_image.setImageBitmap(m_bmp);
		m_image.setPadding(2, 2, 2, 2);
		this.addView(m_image);
		this.addView(m_name);
		notify_close();
	}
	public void set_file( PDFGridThread thread, String name, String path )
	{
		m_path = path;
		m_name = new TextView(getContext());
		m_name.setText(name);
		m_name.setSingleLine(true);
		m_name.setGravity(Gravity.CENTER_HORIZONTAL);
		m_name.setTextColor(TEXT_COLOR);
		m_image = new ImageView(getContext());
		m_bmp = m_def_pdf_icon;
		m_image.setImageBitmap(m_bmp);
		m_image.setPadding(2, 2, 2, 2);
		this.addView(m_image);
		this.addView(m_name);
		thread.start_render( this );
	}
	private boolean is_notified = false;
	private boolean is_waitting = false;
	protected synchronized void wait_close()
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
	protected synchronized void notify_close()
	{
		if( is_waitting )
			notify();
		else
			is_notified = true;
	}
}
