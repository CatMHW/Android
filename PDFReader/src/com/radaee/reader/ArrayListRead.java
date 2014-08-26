package com.radaee.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ArrayListRead {
	private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private String Path = null;
	public ArrayListRead(ArrayList<Map<String, Object>> list ,String Path) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.Path = Path;
		writeObject();
		
			
		
	}
	
	public ArrayListRead()
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
		 
		 public  ArrayList<Map<String, Object>> readObject(String Path){
		  FileInputStream freader;
		  try {
		   freader = new FileInputStream(Path);
		   ObjectInputStream objectInputStream = new ObjectInputStream(freader);
		    this.list = (ArrayList<Map<String, Object>>) objectInputStream.readObject();
		    
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
