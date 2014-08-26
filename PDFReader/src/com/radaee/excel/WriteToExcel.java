package com.radaee.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;



public class WriteToExcel {

	private Sheet mSheet = null;
	WriteToExcel(Sheet sheet)
	{
		mSheet = sheet;
	}
	
	public void write(int rowIndex, int colunmIndex)
	{
		Row row = mSheet.getRow(rowIndex);
		Cell cell = row.getCell(colunmIndex);
		if(cell == null)
		{
			cell = row.createCell(colunmIndex);
		}
		
		cell.setCellType(Cell.CELL_TYPE_STRING);
		cell.setCellValue("123");
	}
	
}
