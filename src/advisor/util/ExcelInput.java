package advisor.util;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import advisor.db.AdvisorDb;
import advisor.db.BannerDb;

public class ExcelInput {

	private String inputFile;
	private HashMap map;
	private HashMap lawMap;
	private BannerDb bannerDb;
	private AdvisorDb advisorDb;

	public void setInputFile(String str) {
		inputFile = str;
	}

	public void setContinuousList(HashMap map) {
		this.map = map;
	}

	public void setBannerDb(BannerDb db) {
		this.bannerDb = db;
	}

	public void setAdvisorDb(AdvisorDb db) {
		advisorDb = db;
	}

	public void read() {

		File inputWorkbook = new File(inputFile);
		Workbook w;
		lawMap = new HashMap();
		int cnt = 0;
		try {

			w = Workbook.getWorkbook(inputWorkbook);
			// Get the second sheet
			Sheet sheet = w.getSheet(1);
			for (int j = 0; j < sheet.getRows(); j++) {

				for (int i = 0; i < sheet.getColumns(); i++) {

					Cell cell = sheet.getCell(i, j);
					if (i == 5)
						break;

					if ((i == 0) && (j > 0)) {
						MessageLogger.out.println(cell.getContents());

						if (map.containsKey(cell.getContents())) {
							if (!lawMap.containsKey(cell.getContents()))
							   lawMap.put(cell.getContents(), cell.getContents());
							else 
								MessageLogger.out.println(cell.getContents());
							
							String id = cell.getContents();
							long pidm = bannerDb.getPidm(cell.getContents());
							
							if (advisorDb.foundMatched(id)){
								advisorDb.updateMatched(true, id);
								cnt++;
							} 
							// bannerDb.removeAdvisor(pidm);

						}
					}

				}

			}
			MessageLogger.out.println("Total Continuous rows = " + cnt);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void findIDsNotInList() {
		int cnt = 0;
		MessageLogger.out.println(lawMap.size());
		Set set = map.keySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			String id = iterator.next().toString();
			if (!lawMap.containsKey(id)) {
				MessageLogger.out.println(id);
				cnt++;
			}
		}
	}

	public static void main(String[] args) {

		AdvisorDb advisorDb = new AdvisorDb("", "admin", "kentish",
				"systemsman04");
		advisorDb.changeSchema("advisors");
		
		BannerDb bannerDb = new BannerDb(0);
		bannerDb.openConn();
		HashMap map = advisorDb.getContinuousAssignments();

		ExcelInput ei = new ExcelInput();
		ei.setAdvisorDb(advisorDb);
		ei.setContinuousList(map);
		ei.setBannerDb(bannerDb);
		ei.setInputFile("C:/temp/continuous.xlt");
		ei.read();
		ei.findIDsNotInList();

		bannerDb.closeConn();
		advisorDb.closeDb();
	}

}
