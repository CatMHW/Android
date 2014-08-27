package com.example.mytable;

import android.content.Context;
import android.widget.TextView;

public class MyTextView extends TextView implements Cloneable{

	
	public MyTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyTextView clone()
	{
		MyTextView editText = null;
		try
		{
			editText = (MyTextView)super.clone();			
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return editText;
		
	}

}
