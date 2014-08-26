package com.radaee.excel;


public class CellInfo 
{
	private int columnIndex = 0;	
	private int rowIndex;
	private int fontSize = 0;
	
	private boolean isItalic = false;
	private boolean isBoldweight = false;
	
	private String comment = null;
	private String content = null;
	
	public CellInfo()
	{
		
	}
	
	public void setItalic(boolean is)
	{
		isItalic = is;
	}
	
	public void setBoldweight(boolean is)
	{
		isBoldweight = is;
	}
	
	public void setFontSize(int size)
	{
		fontSize = size;
	}
	
	public void setColumnIndex(int index)
	{
		columnIndex = index;
	}
	
	public void setComment(String str)
	{
		comment = str;
	}
	
	public void setRowIndex(int index)
	{
		rowIndex = index;
	}
	
	public void setContent(String str)
	{
		content = str;
	}
	
	public boolean getItalic()
	{
		return isItalic;
	}
	
	public boolean getBoldweight()
	{
		return isBoldweight;
	}
	
	public int getFontSize()
	{
		return fontSize;
	}
	
	public int getColumnIndex()
	{
		return columnIndex;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public int getRowIndex()
	{
		return rowIndex;
	}
	
	public String getContent()
	{
		return content;
	}
	
}
