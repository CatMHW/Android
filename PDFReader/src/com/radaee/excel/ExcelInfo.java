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
     * 获取单元格的内容和各个信息
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
    			  //得到全部行的列数的最大数
	   	    	  int temp = row.getLastCellNum();
	   	    	  if(temp > columnCount)
	   	    	  {
	   	    		  columnCount = temp;
	   	    	  }
	   	    	  
	   	    	  Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	   	    	  map.put(row.getRowNum(), (int) row.getHeight());
	   	    	  rowHeightMapList.add(map);
	   	    	  //得到行高
    			 // System.out.println("HeightInPoints:"+row.getHeightInPoints());
	    	      for (Cell cell : row)
	    	      {
	    	        // Do something here\
	    	    	  
	    	    	  mCellInfo = new CellInfo();	    	    	  	    	    	  
	    	    	  
	    	    	  //设置所在的行，列的值
	    	    	  mCellInfo.setColumnIndex(columnIndex = cell.getColumnIndex());
	    	    	  mCellInfo.setRowIndex(rowIndex = cell.getRowIndex());
	    	    	  
	    	    	  //设置内容
	    	    	  mCellInfo.setContent(getText(cell));
	    	    	  
	    	    	  CellStyle style = cell.getCellStyle();	    	  
	    	    	  
	    	    	  //单元格注释
	    	    	  Comment comment = cell.getCellComment();
	    	    	  if(comment != null)
	    	    	  {	 
	    	    		  RichTextString commentStr = comment.getString();
	    	    		  mCellInfo.setComment(commentStr.toString());
	    	    	  }
	    	    	  
	    	    	  //得到单元格的位置
	    	    	  Font font = mWb.getFontAt(style.getFontIndex());
	    	    	  
	    	    	 // font.getColor();
	    	    	  
	    	    	  //如果是斜体
	    	    	  mCellInfo.setItalic(font.getItalic());
	    	    	  
	    	    	  
	    	    	  //如果是粗体
	    	    	  if(font.getBoldweight() > HSSFFont.BOLDWEIGHT_NORMAL)
	    	    	  {
	    	    		  mCellInfo.setBoldweight(true);
	    	    	  }
	    	    	  
	    	    	  //得到字体大小
	    	    	  int fontheight = font.getFontHeightInPoints();
	    	          if (fontheight == 9) 
	    	          {
	    	              //fix for stupid ol Windows
	    	              fontheight = 10;
	    	          }
	    	          mCellInfo.setFontSize(fontheight);
	    	          
	    	          //得到列宽度
	    	        //  columnWidth = mSheet.getColumnWidth(cell.getColumnIndex());
	    	              	         
	    	          cellList.add(mCellInfo);	         
	    	    	 //System.out.println("左上端单元是： " + getText(cell) +"字体大小:" + fontheight);
	    	      }
	    	      
	    	    //  rowHeightMap.put(columnIndex, columnWidth);
    	    }
    	}
    }
    
  
   
    /**
	 * 得到合并单元格的位置
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
	       		  System.out.println("合并单元格地址："+address.formatAsString());
	       	  }
         }
	}
	
	/**
	 * @return 判断有无合并单元格
	 */
	public boolean hasMerged() 
	{
        return mSheet.getNumMergedRegions() > 0 ? true : false;
    }
	
	 /**
	 * @param cell
	 * @return 获取单元格的内容
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
     * @return 得到一个列宽的集合
     */
    public List<Integer> getColumnsWidth()
    {
	      //得到列宽
	  	  for(int i=0; i < columnCount; ++i)
	  	  {	    		  
	  		  int width = mSheet.getColumnWidth(i);
	  		 // Log.e("ExcelInfo", "width:" + width);
	  		  columnWidthList.add(width);
	  	  }
	  	  return columnWidthList;
    }
    
    /**
     * @return 返回默认的列宽
     */
    public int getDefaultColumnWidth()
    {
    	return mSheet.getDefaultColumnWidth();
    }
    
    /**
     * @return 返回默认的行高
     */
    public int getDefaultRowHeight()
    {
    	return mSheet.getDefaultRowHeight();
    }
    
    /**
     * @return 每个单元格的信息
     */
    public List<CellInfo> getCellInfoList()
    {
    	return cellList;
    }
    
    /**
     * @return 获得合并单元格的地址的集合
     */
    public List<Map<String, Integer>> getMergedRegionMapList()
    {
    	return mergedRegionMapList;
    }
    
    /**
     * @return 获得行高列表
     */
    public List<Map<Integer, Integer>> getRowHeightMapList()
    {
    	return rowHeightMapList;
    }

    /**
     * @return 返回列数
     */
    public int getColumnCount()
    {
    	return columnCount;
    }
    
    /**
     * @return 返回行数
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