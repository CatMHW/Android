package com.radaee.main;

import com.radaee.reader.R;

import android.app.Activity;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;


public class CaptureScreenActivity extends Activity {

	ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);             
        setContentView(R.layout.capture);								//显示布局
        
        imageView = (ImageView)findViewById(R.id.capture);
        
        Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String nameStr = bundle.getString("name");
		Uri uri = Uri.parse(nameStr);
		imageView.setImageURI(uri);
		Toast.makeText(this, "截屏成功", Toast.LENGTH_SHORT).show();
    }    
    
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{	
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{			
			this.finish();
		}
		return false;	
	}
}