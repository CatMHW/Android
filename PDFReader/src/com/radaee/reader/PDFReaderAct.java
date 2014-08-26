package com.radaee.reader;

import java.io.File;

import com.radaee.main.Scaning;
import com.radaee.pdfex.*;
import com.radaee.pdf.*;
import com.radaee.rotate.RotatePop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PDFReaderAct extends Activity implements OnItemClickListener, OnClickListener, PDFView.PDFAnnotListener, PopupWindow.OnDismissListener
{
	public static String path;
	public static boolean isMaked = false;
	private PDFGridView m_vFiles = null;
	private Document m_doc = new Document();
	private PDFReader m_vPDF = null;
	private ThumbView m_vThumb = null;
	private PopupWindow m_pEdit = null;
	private PopupWindow m_pCombo = null;
    private Button btn_ink;
    private Button btn_rect;
    private Button btn_end;
    private Button btn_remove;
    private Button btn_save;
    private Button btn_prev;
    private Button btn_next;
    private Button btn_act;
    private Button btn_saveas;
    private Button btn_edit;
    private Button btn_close;
    private EditText txt_find;
    private String str_find;
    private int edit_type = 0;
    private int sel_index = -1;
    private boolean m_modified = false;
    private RotatePop mRotatePop = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Global.Init( this );
//		m_vFiles = new PDFGridView(this, null);
//		//m_vFiles.setOnChildClickListener(this); 
//		m_vFiles.PDFSetRootPath("/mnt");
//		m_vFiles.setOnItemClickListener(this);
//		m_pEdit = new PopupWindow(LayoutInflater.from(this).inflate(R.layout.pop_edit, null) );
//		m_pCombo = new PopupWindow(LayoutInflater.from(this).inflate(R.layout.pop_combo, null));
//		m_pEdit.setOnDismissListener(this);
//		m_pCombo.setOnDismissListener(this);
//		m_pEdit.setFocusable(true);
//		m_pEdit.setTouchable(true);
//		BitmapDrawable bitmap = new BitmapDrawable();//add back
//		m_pEdit.setBackgroundDrawable(bitmap);
//		m_pCombo.setFocusable(true);
//		m_pCombo.setTouchable(true);
//		m_pCombo.setBackgroundDrawable(bitmap);
		
   //    setContentView(R.layout.main);
        
		//界面崩溃
//        Intent intent = this.getIntent();
//		Bundle bundle = intent.getExtras();
//		String path = bundle.getString("name");
     //   Log.e("PDFReader", path);
	//	openPDF("/sdcard-ext/BlackBerry/documents/C++Primer第四版中文版.pdf", "");
        mRotatePop = new RotatePop(this);
        openPDF(path, "");
        mRotatePop.setPath(path);
    }
    
    private void openPDF(String path,String password)
    {
    	m_doc.Close();
    	//m_doc.Open(path, password);
    	
    	int ret = m_doc.Open(path, "");
		switch( ret )
		{
		case -1://need input password
			finish();
			break;
		case -2://unknown encryption
			finish();
			break;
		case -3://damaged or invalid format
			finish();
			break;
		case -10://access denied or invalid file path
			finish();
			break;
		case 0://succeeded, and continue
			break;
		default://unknown error
			finish();
			break;
		}
		/*
		Page page = m_doc.GetPage(0);
		page.ObjsStart();
		float[] rect = new float[4];
		rect[0] = 0;
		rect[1] = 0;
		rect[2] = 100;
		rect[3] = 100;
		page.AddAnnotBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.file03), false, rect);
		m_doc.Save();
		*/
		
        String msg = "file name:" + path;
        msg += "\nversion:";
        msg += m_doc.GetMeta("ver");//PDF-1.X
        msg += "\npage count:";
        msg += String.format("%d", m_doc.GetPageCount());
        msg += "\n";
        msg += "\nTitle:";
        msg += m_doc.GetMeta("Title");
        msg += "\nAuthor:";
        msg += m_doc.GetMeta("Author");
        msg += "\nCreator:";
        msg += m_doc.GetMeta("Producer");
        msg += "\nProducer:";
        msg += m_doc.GetMeta("Creator");
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
		RelativeLayout lout = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.main, null);
        setContentView( lout );
        m_vPDF = (PDFReader)lout.findViewById(R.id.PDFView);
        m_vThumb = (ThumbView)lout.findViewById(R.id.PDFThumb);
        m_vPDF.open(m_doc);
        m_vThumb.thumbOpen(m_vPDF.get_viewer(), m_vPDF);
        m_vPDF.setAnnotListener(this);
        m_vPDF.setViewListener(m_vPDF);
        m_vPDF.set_thumb(m_vThumb);
        LinearLayout bar_cmd = (LinearLayout)lout.findViewById(R.id.bar_cmd);
//        LinearLayout bar_find = (LinearLayout)lout.findViewById(R.id.bar_find);
        btn_ink = (Button)bar_cmd.findViewById(R.id.btn_ink);
        btn_rect = (Button)bar_cmd.findViewById(R.id.btn_rect);
        btn_end = (Button)bar_cmd.findViewById(R.id.btn_end);
        btn_remove = (Button)bar_cmd.findViewById(R.id.btn_remove);
        btn_save = (Button)bar_cmd.findViewById(R.id.btn_save);
        btn_act = (Button)bar_cmd.findViewById(R.id.btn_act);
        btn_edit = (Button)bar_cmd.findViewById(R.id.btn_edit);
        btn_close = (Button)bar_cmd.findViewById(R.id.btn_close);

//        txt_find = (EditText)bar_find.findViewById(R.id.txt_find);
//        btn_prev = (Button)bar_find.findViewById(R.id.btn_prev);
//        btn_next = (Button)bar_find.findViewById(R.id.btn_next);
//        btn_saveas = (Button)bar_find.findViewById(R.id.btn_saveas);
        
        btn_ink.setOnClickListener(this);
        btn_rect.setOnClickListener(this);
        btn_end.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_act.setOnClickListener(this);
//        btn_prev.setOnClickListener(this);
//        btn_next.setOnClickListener(this);
//        btn_saveas.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_close.setOnClickListener(this);

        if( !m_doc.CanSave() )
        {
        	btn_ink.setEnabled(false);
        	btn_rect.setEnabled(false);
        }
        btn_act.setEnabled(false);
        btn_end.setEnabled(false);
        btn_remove.setEnabled(false);
        btn_save.setEnabled(false);
        btn_edit.setEnabled(false);
//        btn_saveas.setEnabled(m_doc.IsEncrypted());
	
    }

    
    protected void onDestroy()
    {
    	//m_vFiles.close();
    	if( m_vThumb != null )
    		m_vThumb.thumbClose();
    	if( m_vFiles != null )
    	{
    		m_vFiles.close();
    		m_vFiles = null;
    	}
    	if( m_vPDF != null )
    	{
    		m_vPDF.set_thumb(null);
    		m_vPDF.close();
    		m_vPDF = null;
    	}
    	if( m_modified )
    	{
    		//check if need save?
    		//m_doc.Save();
    		m_modified = false;
    	}
    	if( m_doc != null )
    	{
    		m_doc.Close();
    		m_doc = null;
    	}
    	Global.RemoveTmp();
    	super.onDestroy();
    }
	public void onClick(View v)
	{
		switch( v.getId() )
		{
		case R.id.btn_ink:
			btn_ink.setEnabled(false);
			btn_rect.setEnabled(false);
			btn_remove.setEnabled(true);
            btn_end.setEnabled(true);
            btn_act.setEnabled(false);
            btn_save.setEnabled(false);
//            btn_prev.setEnabled(false);
//            btn_next.setEnabled(false);
			btn_edit.setEnabled(false);
			m_vPDF.annotInk();
			break;
		case R.id.btn_rect:
			btn_rect.setEnabled(false);
			btn_ink.setEnabled(false);
			btn_remove.setEnabled(true);
            btn_end.setEnabled(true);
            btn_act.setEnabled(false);
            btn_save.setEnabled(false);
//            btn_prev.setEnabled(false);
//            btn_next.setEnabled(false);
			btn_edit.setEnabled(false);
			m_vPDF.annotRect();
			break;
		case R.id.btn_end:
			btn_ink.setEnabled(true);
			btn_rect.setEnabled(true);
            btn_end.setEnabled(false);
            btn_remove.setEnabled(false);
            btn_act.setEnabled(false);
            btn_save.setEnabled(false);
			btn_edit.setEnabled(false);
            m_vPDF.annotEnd();
			break;
		case R.id.btn_remove:
			btn_ink.setEnabled(true);
			btn_rect.setEnabled(true);
            btn_end.setEnabled(false);
			btn_remove.setEnabled(false);
            btn_act.setEnabled(false);
            btn_save.setEnabled(false);
			btn_edit.setEnabled(false);
			m_vPDF.annotRemove();
			break;
		case R.id.btn_save:
			btn_ink.setEnabled(true);
			btn_rect.setEnabled(true);
            btn_end.setEnabled(false);
            btn_act.setEnabled(false);
            btn_save.setEnabled(false);
			btn_remove.setEnabled(false);
			btn_edit.setEnabled(false);
			m_vPDF.annotEnd();
			m_doc.Save();//save to file
			m_modified = false;
			break;
		case R.id.btn_act:
			btn_ink.setEnabled(true);
			btn_rect.setEnabled(true);
            btn_end.setEnabled(false);
            btn_act.setEnabled(false);
            btn_save.setEnabled(false);
			btn_remove.setEnabled(false);
			btn_edit.setEnabled(false);
			m_vPDF.annotPerform();
			break;
		case R.id.btn_edit:
			btn_ink.setEnabled(true);
			btn_rect.setEnabled(true);
            btn_end.setEnabled(false);
            btn_act.setEnabled(false);
            btn_save.setEnabled(false);
			btn_remove.setEnabled(false);
			btn_edit.setEnabled(false);
			{
				m_vPDF.lockResize(true);
				LinearLayout layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.dlg_note, null);
				final EditText subj = (EditText)layout.findViewById(R.id.txt_subj);
				final EditText content = (EditText)layout.findViewById(R.id.txt_content);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which)
					{
						String str_subj = subj.getText().toString();
						String str_content = content.getText().toString();
						m_vPDF.annotSetSubject(str_subj);
						m_vPDF.annotSetText(str_content);
						m_vPDF.annotEnd();
						dialog.dismiss();
						m_vPDF.lockResize(false);
					}});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which)
					{
						m_vPDF.annotEnd();
						dialog.dismiss();
						m_vPDF.lockResize(false);
					}});
				builder.setTitle("Note Content");
				builder.setCancelable(false);
				builder.setView(layout);
				AlertDialog dlg = builder.create();
				subj.setText(m_vPDF.annotGetSubject());
				content.setText(m_vPDF.annotGetText());
				dlg.show();
			}
			break;
//		case R.id.btn_prev:
//			{
//				String str = txt_find.getText().toString();
//				if( str_find != null )
//				{
//					if( str != null && str.compareTo(str_find) == 0 )
//					{
//						m_vPDF.find(-1);
//						break;
//					}
//				}
//				if( str != null && str.length() > 0 )
//				{
//					str_find = str;
//					m_vPDF.findStart(str, true, false);
//					m_vPDF.find(-1);
//				}
//			}
//			break;
//		case R.id.btn_next:
//			{
//				String str = txt_find.getText().toString();
//				if( str_find != null )
//				{
//					if( str != null && str.compareTo(str_find) == 0 )
//					{
//						m_vPDF.find(1);
//						break;
//					}
//				}
//				if( str != null && str.length() > 0 )
//				{
//					str_find = str;
//					m_vPDF.findStart(str, true, false);
//					m_vPDF.find(1);
//				}
//			}
//			break;
//		case R.id.btn_saveas:
//			{
//				//save unencrypted document
//				//if( m_doc.IsEncrypted() )
//				//	m_doc.SaveAs(Global.sdPath + "/ebook/unenc.pdf");
//			}
//			break;
		case R.id.btn_close:
	    	if( m_vThumb != null )
	    		m_vThumb.thumbClose();
	    	if( m_vPDF != null )
	    		m_vPDF.close();
	    	if( m_modified )
	    	{
	    		//check if need save?
	    		//m_doc.Save();
	    		m_modified = false;
	    	}
	    	if( m_doc != null )
	    	{
	    		m_doc.Close();
	    		str_find = null;
	    	}
	    	setContentView(m_vFiles);
			break;
		}
	}
	public void onAnnotDragStart(boolean has_goto, boolean has_popup)
	{
		btn_ink.setEnabled(false);
		btn_rect.setEnabled(false);
        btn_end.setEnabled(false);
        btn_save.setEnabled(false);
        btn_act.setEnabled(has_goto);
		btn_remove.setEnabled(true);
//        btn_prev.setEnabled(false);
//        btn_next.setEnabled(false);
        btn_edit.setEnabled(has_popup);
	}
	public void onAnnotEditBox(int type, String val, float text_size, float left, float top, float right, float bottom)
	{
		m_vPDF.lockResize(true);
		m_pEdit.setWidth((int)(right - left));
		m_pEdit.setHeight((int)(bottom - top));
		EditText edit = (EditText)m_pEdit.getContentView().findViewById(R.id.annot_text);
		edit.setBackgroundColor(0xFFFFFFC0);
		edit.setTextSize(text_size);
		edit.setPadding(2, 2, 2, 2);
		switch( type )
		{
		case 1:
			edit.setSingleLine();
			edit.setInputType(InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_NORMAL);
			break;
		case 2:
			edit.setSingleLine();
			edit.setInputType(InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD);
			break;
		case 3:
			edit.setSingleLine(false);
			edit.setInputType(InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_NORMAL);
			break;
		}
		edit.setText(val);
		edit_type = 1;
		m_pEdit.showAtLocation(m_vPDF, Gravity.LEFT|Gravity.TOP, (int)left, (int)bottom);
	}
	public void onAnnotComboBox(int sel, String[] opts, float left, float top, float right, float bottom)
	{
		m_vPDF.lockResize(true);
		edit_type = 2;
		sel_index = -1;
	}
	public void onAnnotEnd()
	{
		btn_ink.setEnabled(true);
		btn_rect.setEnabled(true);
        btn_end.setEnabled(false);
		btn_remove.setEnabled(false);
		btn_act.setEnabled(false);
        btn_save.setEnabled(m_modified);
//        btn_prev.setEnabled(true);
//        btn_next.setEnabled(true);
        btn_edit.setEnabled(false);
	}

	public void onAnnotUpdate()
	{
		m_modified = true;
		btn_ink.setEnabled(true);
		btn_rect.setEnabled(true);
        btn_end.setEnabled(false);
		btn_remove.setEnabled(false);
		btn_act.setEnabled(false);
        btn_save.setEnabled(m_modified);
//        btn_prev.setEnabled(true);
//        btn_next.setEnabled(true);
        btn_edit.setEnabled(false);
	}
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		if( arg0 == m_vFiles )
		{
			PDFGridItem item = (PDFGridItem)arg1;
			if( item.is_dir() )
			{
				m_vFiles.PDFGotoSubdir(item.get_name());
			}
			else
			{
				//String val = item.get_path_for_open();
				String val = "/sdcard-ext/BlackBerry/documents/C++Templates 侯捷简体中文版.pdf";
				if( val != null )
				{
					m_doc.Close();
					//to open encrypted document modify codes below:
					int ret = m_doc.Open(val, "");
					switch( ret )
					{
					case -1://need input password
						finish();
						break;
					case -2://unknown encryption
						finish();
						break;
					case -3://damaged or invalid format
						finish();
						break;
					case -10://access denied or invalid file path
						finish();
						break;
					case 0://succeeded, and continue
						break;
					default://unknown error
						finish();
						break;
					}
					/*
					Page page = m_doc.GetPage(0);
					page.ObjsStart();
					float[] rect = new float[4];
					rect[0] = 0;
					rect[1] = 0;
					rect[2] = 100;
					rect[3] = 100;
					page.AddAnnotBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.file03), false, rect);
					m_doc.Save();
					*/
					
		            String msg = "file name:" + val;
		            msg += "\nversion:";
		            msg += m_doc.GetMeta("ver");//PDF-1.X
		            msg += "\npage count:";
		            msg += String.format("%d", m_doc.GetPageCount());
		            msg += "\n";
		            msg += "\nTitle:";
		            msg += m_doc.GetMeta("Title");
		            msg += "\nAuthor:";
		            msg += m_doc.GetMeta("Author");
		            msg += "\nCreator:";
		            msg += m_doc.GetMeta("Producer");
		            msg += "\nProducer:";
		            msg += m_doc.GetMeta("Creator");
		            Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		            toast.show();
					RelativeLayout lout = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.main, null);
		            setContentView( lout );
		            m_vPDF = (PDFReader)lout.findViewById(R.id.PDFView);
		            m_vThumb = (ThumbView)lout.findViewById(R.id.PDFThumb);
		            m_vPDF.open(m_doc);
		            m_vThumb.thumbOpen(m_vPDF.get_viewer(), m_vPDF);
		            m_vPDF.setAnnotListener(this);
		            m_vPDF.setViewListener(m_vPDF);
		            m_vPDF.set_thumb(m_vThumb);
		            LinearLayout bar_cmd = (LinearLayout)lout.findViewById(R.id.bar_cmd);
		        //    LinearLayout bar_find = (LinearLayout)lout.findViewById(R.id.bar_find);
		            btn_ink = (Button)bar_cmd.findViewById(R.id.btn_ink);
		            btn_rect = (Button)bar_cmd.findViewById(R.id.btn_rect);
		            btn_end = (Button)bar_cmd.findViewById(R.id.btn_end);
		            btn_remove = (Button)bar_cmd.findViewById(R.id.btn_remove);
		            btn_save = (Button)bar_cmd.findViewById(R.id.btn_save);
		            btn_act = (Button)bar_cmd.findViewById(R.id.btn_act);
		            btn_edit = (Button)bar_cmd.findViewById(R.id.btn_edit);
		            btn_close = (Button)bar_cmd.findViewById(R.id.btn_close);
//
//		            txt_find = (EditText)bar_find.findViewById(R.id.txt_find);
//		            btn_prev = (Button)bar_find.findViewById(R.id.btn_prev);
//		            btn_next = (Button)bar_find.findViewById(R.id.btn_next);
//		            btn_saveas = (Button)bar_find.findViewById(R.id.btn_saveas);
		            
		            btn_ink.setOnClickListener(this);
		            btn_rect.setOnClickListener(this);
		            btn_end.setOnClickListener(this);
		            btn_remove.setOnClickListener(this);
		            btn_save.setOnClickListener(this);
		            btn_act.setOnClickListener(this);
//		            btn_prev.setOnClickListener(this);
//		            btn_next.setOnClickListener(this);
//		            btn_saveas.setOnClickListener(this);
		            btn_edit.setOnClickListener(this);
		            btn_close.setOnClickListener(this);

		            if( !m_doc.CanSave() )
		            {
		            	btn_ink.setEnabled(false);
		            	btn_rect.setEnabled(false);
		            }
		            btn_act.setEnabled(false);
		            btn_end.setEnabled(false);
		            btn_remove.setEnabled(false);
		            btn_save.setEnabled(false);
		            btn_edit.setEnabled(false);
//		            btn_saveas.setEnabled(m_doc.IsEncrypted());
				}
			}
		}
		else
		{
			sel_index = arg2;
			m_pCombo.dismiss();
		}
	}
	public void onDismiss()
	{
		if( edit_type == 1 )//edit box
		{
			EditText edit = (EditText)m_pEdit.getContentView().findViewById(R.id.annot_text);
			m_vPDF.annotSetEditText(edit.getText().toString());
			m_vPDF.lockResize(false);
		}
		if( edit_type == 2 )//combo
		{
			if( sel_index >= 0 )
				m_vPDF.annotSetChoice(sel_index);
			else
				m_vPDF.annotEnd();
			sel_index = -1;
			m_vPDF.lockResize(false);
		}
		edit_type = 0;
	}
	public void onLowMemory()
	{
		int iiii = 0;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
	   	switch(keyCode)
	   	{
	   	case KeyEvent.KEYCODE_BACK:
	   		if(!mRotatePop.hasShow())
	   		{
	   			if(!isMaked)
	   			{
	   				backToTab();
	   			}
	   			else
	   			{
	   				backToScaning();
	   			}
	   		}
	   		if(mRotatePop.hasBtnShow())
	   		{
	   			mRotatePop.restore();
	   			mRotatePop.setBtnShow(false);
	   		}
	   		else if(!mRotatePop.hasBtnShow() && mRotatePop.hasShow())
	   		{
	   			mRotatePop.dismiss();	
	   		}

	   		break;
	   	case KeyEvent.KEYCODE_MENU:
	   		if(!mRotatePop.hasShow())
	   		{
	   			mRotatePop.show();
	   		}	
	   		break;	
	   	}
		return false;	
	 }
	
	private void backToTab()
	{
		Intent intent = new Intent();
   		intent.setClass(PDFReaderAct.this, ViewPagerActivity.class);
   		startActivity(intent);
   		this.finish();
	}
	
	private void backToScaning()
	{
		Intent intent = new Intent();
   		intent.setClass(PDFReaderAct.this, Scaning.class);
   		startActivity(intent);
   		this.finish();
	}
}