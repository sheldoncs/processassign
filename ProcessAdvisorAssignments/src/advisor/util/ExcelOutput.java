package advisor.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelOutput {

	// private WritableCellFormat times;
	private CellFormat times;
	private ArrayList assignList;
	WritableWorkbook wworkbook;
	WritableSheet wsheet;
	String[] header = { "STUDENT ID", "STUDENT FIRSTNAME", "STUDENT LASTNAME", "STUDENT TYPE", "ADVISOR ID",
			"ADVISOR FIRSTNAME","ADVISOR LASTNAME" ,"LEVEL", "PROGRAM", "DEPARTMENT" };

	String[] headerConcentrations = { "STUDENT ID", "STUDENT FIRSTNAME","STUDENT LASTNAME", "STUDENT TYPE",
			"ADVISOR ID", "ADVISOR FIRSTNAME","ADVISOR LASTNAME" ,"LEVEL", "PROGRAM", "CONCENTRATION",
			"DEPARTMENT" };

	private String filename;

	public void setAssignList(ArrayList list) {
		this.assignList = list;
	}

	public void setFileName(String f) {
		this.filename = f;
	}

	public void setupWorkBook() throws IOException {
		wworkbook = Workbook.createWorkbook(new File("c:/temp/" + filename
				+ ".xls"));
		wsheet = wworkbook.createSheet("First Sheet", 0);

	}

	public void writeOut(boolean isConcentration) throws IOException,
			RowsExceededException, WriteException, BiffException {

		int row = 0;
		if (!isConcentration) {
			for (int i = 0; i < header.length; i++) {
				addLabel(wsheet, i, row, header[i]);
			}
		} else {
			for (int j = 0; j < headerConcentrations.length; j++) {
				addLabel(wsheet, j, row, headerConcentrations[j]);
			}
		}

		Iterator iterator = assignList.iterator();
		int col = 0;
		
		while (iterator.hasNext()) {

			
			col = 0;
			Assignments assignment = (Assignments) iterator.next();
			row++;

			addLabel(wsheet, col, row, assignment.getStudentId());
			
			String[] words = assignment.getStudentName().split(" ");
            String fname = words[0];
            String lname = words[1];
            
            col++;
			addLabel(wsheet, col, row, fname);
			
			col++;
			addLabel(wsheet, col, row, lname);
			
			col++;
			addLabel(wsheet, col, row, assignment.getStudentType());
			
			col++;
			addLabel(wsheet, col, row, assignment.getAdvisorId());
			
			
			words = assignment.getAdvisorName().split(" ");
            fname = words[0];
            lname = words[1];
            
			col++;
			addLabel(wsheet, col, row, fname);
			
			col++;
			addLabel(wsheet, col, row, lname);
			
			col++;
			addLabel(wsheet, col, row, assignment.getClas());
			col++;
			addLabel(wsheet, col, row, assignment.getProgram());
			if (isConcentration){
				col++;
				addLabel(wsheet, col, row, assignment.getConcentration());
			}
			col++;
			addLabel(wsheet, col, row, assignment.getDepartment());
			
			
			

		}

		wworkbook.write();

	}

	private void addNumber(WritableSheet sheet, int column, int row,
			Integer integer) throws WriteException, RowsExceededException {

		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		times = new WritableCellFormat(times10pt);

		Number number;

		number = new Number(column, row, 0, times);

		sheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {

		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
		times = new WritableCellFormat(times10pt);

		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}

	public void closeWorkBook() throws WriteException, IOException {
		wworkbook.close();

	}
}
