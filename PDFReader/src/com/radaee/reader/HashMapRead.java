package com.radaee.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashMapRead {
	private Map<String, String> list = new HashMap<String,String>();
 	private String Path = null;
	public HashMapRead(Map<String, String> list ,String Path) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.Path = Path;
		
		writeObject();
		
	}
	
	public HashMapRead()
	{}
	

	
	public void writeObject() {
		  try {
			  
//			  Collections.reverse(list);
		   
		   FileOutputStream outStream = new FileOutputStream(Path);
		   ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
		   objectOutputStream.writeObject(list);
		   outStream.close();
		   System.out.println("successful");
		  } catch (FileNotFoundException e) {
		   e.printStackTrace();
		  } catch (IOException e) {
		   e.printStackTrace();
		  }
		 }
		 
		 public  Map<String, String> readObject(String Path){
		  FileInputStream freader;
		  try {
		   freader = new FileInputStream(Path);
		   ObjectInputStream objectInputStream = new ObjectInputStream(freader);
		    this.list = (Map<String, String>) objectInputStream.readObject();
		  } catch (FileNotFoundException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (ClassNotFoundException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		  return list;
		 }
		 


}
