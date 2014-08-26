package com.radaee.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

import android.util.Log;

public class ExcelInfo 
{
	private int rowCount = 0;
    
    private int columnCount = 0;
    private int rowHeight = 0;
    private int columnWidth = 0;
    private int rowIndex = 0;
    private int columnIndex = 0;
   
    
  //  private String mfilePath = null;
    
    private List<Map<String, Integer>> mergedRegionMapList = new ArrayList<Map<String, Integer>>();
    private List<CellInfo> cellList = new ArrayList<CellInfo>();
    private List<Integer> columnWidthList = new ArrayList<Integer>();
    private List<Map<Integer, Integer>> rowHeightMapList = new ArrayList<Map<Integer,Integer>>();
    
    private Workbook mWb;
    private org.apache.poi.ss.usermodel.Sheet mSheet;
    
    private CellInfo mCellInfo = null;
    
    
    public ExcelInfo(String filePath) throws IOException, InvalidFormatException 
    {
    	//mfilePath = filePath;
    	InputStream inp = new FileInputStream(filePath);
    	mWb = WorkbookFactory.create(inp);
    	getCellContent();
    	getMergedCellAddress();
    	
    	inp.close();
    }
    
    /**
     * ��ȡ��Ԫ������ݺ͸�����Ϣ
     */
    public void getCellContent()
    {   	
    	for(int i=0; i < 1; ++i)
    	{
    		mSheet = mWb.getSheetAt(0);
    		//rowCount = mSheet.getPhysicalNumberOfRows();
    		 rowCount = mSheet.getLastRowNum() - mSheet.getFirstRowNum();		
    		 for (Row row: mSheet) 
    		 {	   			  
    			  //�õ�ȫ���е������������
	   	    	  int temp = row.getLastCellNum();
	   	    	  if(temp > columnCount)
	   	    	  {
	   	    		  columnCount = temp;
	   	    	  }
	   	    	  
	   	    	  Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   	    	  map.put(row.getRowNum(), (int) row.getHeight());
	   	    	  rowHeightMapList.add(map);
	   	    	  //�õ��и�
    			 // System.out.println("HeightInPoints:"+row.getHeightInPoints());
	    	      for (Cell cell : row)
	    	      {
	    	        // Do something here\
	    	    	  
	    	    	  mCellInfo = new CellInfo();	    	    	  	    	    	  
	    	    	  
	    	    	  //�������ڵ��У��е�ֵ
	    	    	  mCellInfo.setColumnIndex(columnIndex = cell.getColumnIndex());
	    	    	  mCellInfo.setRowIndex(rowIndex = cell.getRowIndex());
	    	    	  
	    	    	  //��������
	    	    	  mCellInfo.setContent(getText(cell));
	    	    	  
	    	    	  CellStyle style = cell.getCellStyle();	    	  
	    	    	  
	    	    	  //��Ԫ��ע��
	    	    	  Comment comment = cell.getCellComment();
	    	    	  if(comment != null)
	    	    	  {	 
	    	    		  RichTextString commentStr = comment.getString();
	    	    		  mCellInfo.setComment(commentStr.toString());
	    	    	  }
	    	    	  
	    	    	  //�õ���Ԫ���λ��
	    	    	  Font font = mWb.getFontAt(style.getFontIndex());
	    	    	  
	    	    	 // font.getColor();
	    	    	  
	    	    	  //�����б��
	    	    	  mCellInfo.setItalic(font.getItalic());
	    	    	  
	    	    	  
	    	    	  //����Ǵ���
	    	    	  if(font.getBoldweight() > HSSFFont.BOLDWEIGHT_NORMAL)
	    	    	  {
	    	    		  mCellInfo.setBoldweight(true);
	    	    	  }
	    	    	  
	    	    	  //�õ������С
	    	    	  int fontheight = font.getFontHeightInPoints();
	    	          if (fontheight == 9) 
	    	          {
	    	              //fix for stupid ol Windows
	    	              fontheight = 10;
	    	          }
	    	          mCellInfo.setFontSize(fontheight);
	    	          
	    	          //�õ��п��
	    	        //  columnWidth = mSheet.getColumnWidth(cell.getColumnIndex());
	    	              	         
	    	          cellList.add(mCellInfo);	         
	    	    	 //System.out.println("���϶˵�Ԫ�ǣ� " + getText(cell) +"�����С:" + fontheight);
	    	      }
	    	      
	    	    //  rowHeightMap.put(columnIndex, columnWidth);
    	    }
    	}
    }
    
  
   
    /**
	 * �õ��ϲ���Ԫ���λ��
	 */
	private void getMergedCellAddress()
	{
		 if(hasMerged())
         {
	       	  for(int i=0; i < mSheet.getNumMergedRegions(); ++i)
	       	  {
	       		  CellRangeAddress address = mSheet.getMergedRegion(i);
	       		  
	       		  int firstColumn = address.getFirstColumn();
	       		  int lastColumn = address.getLastColumn();
	       		  int firstRow = address.getFirstRow();
	       		  int lastRow = address.getLastRow();
	       		  
	       		  Map<String, Integer> map = new HashMap<String, Integer>();
	       		  map.put("FC", firstColumn);
	       		  map.put("LC", lastColumn);
	       		  map.put("FR", firstRow);
	       		  map.put("LR", lastRow);
	       		  
	       		  mergedRegionMapList.add(map);
	       		  System.out.println("�ϲ���Ԫ���ַ��"+address.formatAsString());
	       	  }
         }
	}
	
	/**
	 * @return �ж����޺ϲ���Ԫ��
	 */
	public boolean hasMerged() 
	{
        return mSheet.getNumMergedRegions() > 0 ? true : false;
    }
	
	 /**
	 * @param cell
	 * @return ��ȡ��Ԫ�������
	 */
	private String getText(Cell cell)
	 {
	    	switch(cell.getCellType())
	        {
	        case Cell.CELL_TYPE_BLANK:
	        	return "";
	        case Cell.CELL_TYPE_BOOLEAN:
	        	return String.valueOf(cell.getBooleanCellValue());
	        case Cell.CELL_TYPE_FORMULA:
	        	return cell.getCellFormula();        	
	        case Cell.CELL_TYPE_NUMERIC:
	        	if(DateUtil.isCellDateFormatted(cell)) 
	        	{
	        		return cell.getDateCellValue().toString();
	        	}
	        	else
	        	{
	        		String phone = Double.toString(cell.getNumericCellValue());
		        	
		        	String str[] = phone.split("[.]");
		        	phone = str[0];
		        	if(str[1].contains("E"))
		        	{
		        		String str1[] = str[1].split("[E]");
		        		phone += str1[0];	       
		        	}	        	
		        	
		        	//String phone = BigInteger.
		        	return phone;
	        	}
	        	
	        case Cell.CELL_TYPE_STRING:
	        	return cell.getStringCellValue();
	        case Cell.CELL_TYPE_ERROR:
	        default:
	        	return "";
	        }
	}
    
    /**
     * @return �õ�һ���п�ļ���
     */
    public List<Integer> getColumnsWidth()
    {
	      //�õ��п�
	  	  for(int i=0; i < columnCount; ++i)
	  	  {	    		  
	  		  int width = mSheet.getColumnWidth(i);
	  		 // Log.e("ExcelInfo", "width:" + width);
	  		  columnWidthList.add(width);
	  	  }
	  	  return columnWidthList;
    }
    
    /**
     * @return ����Ĭ�ϵ��п�
     */
    public int getDefaultColumnWidth()
    {
    	return mSheet.getDefaultColumnWidth();
    }
    
    /**
     * @return ����Ĭ�ϵ��и�
     */
    public int getDefaultRowHeight()
    {
    	return mSheet.getDefaultRowHeight();
    }
    
    /**
     * @return ÿ����Ԫ�����Ϣ
     */
    public List<CellInfo> getCellInfoList()
    {
    	return cellList;
    }
    
    /**
     * @return ��úϲ���Ԫ��ĵ�ַ�ļ���
     */
    public List<Map<String, Integer>> getMergedRegionMapList()
    {
    	return mergedRegionMapList;
    }
    
    /**
     * @return ����и��б�
     */
    public List<Map<Integer, Integer>> getRowHeightMapList()
    {
    	return rowHeightMapList;
    }

    /**
     * @return ��������
     */
    public int getColumnCount()
    {
    	return columnCount;
    }
    
    /**
     * @return ��������
     */
    public int getRowCount()
    {
    	return rowCount;
    }
    
    /**
     * 
     */
    public Sheet getMysheet()
    {
    	return mSheet;
    }
}   