package com.radaee.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.Data;
import android.util.Log;

public class ContactInfo{
	
	private Activity mActivity;
	private Cursor cur;
	private String disPlayName = null;
	private String emailType = null;
	private String emailValue = null;
	private String contactId = null;
	private String phoneNumber = null;
	private String email = null;
	
	//第一个是邮箱，第二个是联系人
	private List<Map<String,Info>> mailList = new ArrayList<Map<String,Info>>();
	private Map<String,Info> mailMap;
	private Info info = null;
	public ContactInfo(Activity _act, String _email)
	{
		mActivity = _act;
		email = _email;
		getContactInfo(email);
	}
	
	public void getContactInfo(String email)
	{		// 获得所有的联系人
			cur = mActivity.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					null,
					null,
					null,
					ContactsContract.Contacts.DISPLAY_NAME
							+ " COLLATE LOCALIZED ASC");
			// 循环遍历
			if (cur.moveToFirst()) 
			{
				
				
				int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);

				int displayNameColumn = cur
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				
				
				do {
					// 获得联系人的ID号
						contactId = cur.getString(idColumn);
						// 获得联系人姓名
						disPlayName = cur.getString(displayNameColumn);
						
						// 查看该联系人有多少个电话号码。如果没有这返回值为0
						int phoneCount = cur
								.getInt(cur
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
						Log.i("username", disPlayName);
						if (phoneCount > 0) 
						{
							// 获得联系人的电话号码
							Cursor phones = mActivity.getContentResolver().query(
									ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
									null,
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID
											+ " = " + contactId, null, null);
							if (phones.moveToFirst()) 
							{
								do 
								{
									// 遍历所有的电话号码
									phoneNumber = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									String phoneType = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
									Log.i("phoneNumber", phoneNumber);
									Log.i("phoneType", phoneType);
								} while (phones.moveToNext());
								
								phones.close();
							}
						}

					// 获取该联系人邮箱
					Cursor emails = mActivity.getContentResolver().query(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
					if (emails.moveToFirst()) {
						do {
							// 遍历所有的电话号码
							emailType = emails
									.getString(emails
											.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
							emailValue = emails
									.getString(emails
											.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
							
							Log.i("emailType", emailType);
							Log.i("emailValue", emailValue);
						} while (emails.moveToNext());
						
						emails.close();
					};
					Log.e("1", "1");
					if(email.equals(emailValue))
					{
						is = true;
						break;
					}
					
					if(!is)
					{
						phoneNumber = null;
					}
					
				} while (cur.moveToNext());
				
				cur.close();
			}
	}
	
	boolean is = false;

	public String getName()
	{
		return disPlayName;
	}
	
	public String getPhone()
	{
		return phoneNumber;
	}
}
