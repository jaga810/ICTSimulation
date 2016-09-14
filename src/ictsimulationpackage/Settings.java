package ictsimulationpackage;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Settings {
	static String sheetName[] = { "練馬区内中継リンク", "荏原区内中継リンク", "墨田区内中継リンク" };
	static int sheetNum = 3;
    static String address1 = "/Users/jaga/Documents/domain_project/data/NTT-ver2.xlsx";

	static ArrayList<Integer> LinkListPlus() {
		// 0,...,101,0,...,100までの数を収容
		int[] LinkListPlusH = new int[203];
		for (int i = 0; i < 102; i++) {
			LinkListPlusH[i] = i;
		}
		for (int i = 102; i < 203; i++) {
			LinkListPlusH[i] = i - 102;
		}
		ArrayList<Integer> LinkListPlus = new ArrayList<Integer>();
		for (int i = 0; i < LinkListPlusH.length; i++) {
			LinkListPlus.add(LinkListPlusH[i]);
		}
		return LinkListPlus;
	}

	static ArrayList<Integer> LinkListMinus() {
		// 101, ..., 0, 101, ..., 1 までの数を収容
		int[] LinkListMinusH = new int[203];
		for (int i = 0; i < 102; i++) {
			LinkListMinusH[i] = 101 - i;
		}
		for (int i = 102; i < 203; i++) {
			LinkListMinusH[i] = 203 - i;
		}
		ArrayList<Integer> LinkListMinus = new ArrayList<Integer>();
		for (int i = 0; i < LinkListMinusH.length; i++) {
			LinkListMinus.add(LinkListMinusH[i]);
		}
		return LinkListMinus;
	}

	static String[] BldgName() {
		System.out.println("loading Bldg name ....");
		// 各ビルの名前
		int sheetNum = 3;
		String sheetName[] = { "練馬区内中継リンク", "荏原区内中継リンク", "墨田区内中継リンク" };
		String[] BldgName = new String[102];
		try {
			FileInputStream fi = new FileInputStream(address1);
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();

			Sheet sheet = null;
			int rowNum = 0;
			int index = 0;

			for (int i = 0; i < sheetNum; i++) {
				System.out.println("sheet->" + i);
				sheet = book.getSheet(sheetName[i]);
				rowNum = (int) sheet.getRow(1).getCell(10).getNumericCellValue();
				for (int k = 0; k < rowNum; k++) {
					Row row = sheet.getRow(k + 1);
					BldgName[index] = row.getCell(1).toString();

//					System.out.println(index + " : " + BldgName[index]);
					index++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return BldgName;
	}

	static int[] BldgLinkL() {
		System.out.println("loading BldgLinkL ....");
		int[] link = new int[103];
		int dataNum;
		try {
			FileInputStream fi = new FileInputStream(address1);
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();
			// データの数の読み込み
			Sheet sheet = null;
			int rowNum = 0;
			int index = 0;

			for (int i = 0; i < sheetNum; i++) {
				System.out.println("sheet->" + i);
				sheet = book.getSheet(sheetName[i]);
				rowNum = (int) sheet.getRow(1).getCell(10).getNumericCellValue();
				for (int k = 0; k < rowNum; k++) {
					Row row = sheet.getRow(k + 1);
					link[index] = (int) row.getCell(8).getNumericCellValue();

//					System.out.println(index + " : " + link[index]);
					index++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return link;
	}

	static int[] BldgLinkR() {
		System.out.println("loading BldgLinkR ....");
		int[] link = new int[103];
		int dataNum;
		int sheetNum = 3;
		try {
			FileInputStream fi = new FileInputStream(address1);
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();
			// データの数の読み込み
			Sheet sheet = null;
			int rowNum = 0;
			int index = 0;

			for (int i = 0; i < sheetNum; i++) {
				System.out.println("sheet->" + i);
				sheet = book.getSheet(sheetName[i]);
				rowNum = (int) sheet.getRow(1).getCell(10).getNumericCellValue();
				for (int k = 0; k < rowNum; k++) {
					Row row = sheet.getRow(k + 1);
					link[index] = (int) row.getCell(9).getNumericCellValue();

//					System.out.println(index + " : " + link[index]);
					index++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return link;
	}

	static int[] exchangeID() {
		System.out.println("downloading exchangeID ...");
		// 各ビルの中継局ID
		int[] exchangeID = new int[102];
		try {
			FileInputStream fi = new FileInputStream(address1);
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();
			// データの数の読み込み
			Sheet sheet = null;
			int rowNum = 0;
			int index = 0;

			for (int i = 0; i < sheetNum; i++) {
				System.out.println("sheet->" + i);
				sheet = book.getSheet(sheetName[i]);
				rowNum = (int) sheet.getRow(1).getCell(10).getNumericCellValue();
				for (int k = 0; k < rowNum; k++) {
					Row row = sheet.getRow(k + 1);
					exchangeID[index] = (int) row.getCell(6).getNumericCellValue();

//					System.out.println(index + " : " + exchangeID[index]);
					index++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return exchangeID;
	}
}
