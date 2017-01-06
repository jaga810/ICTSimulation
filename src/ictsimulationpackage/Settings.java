package ictsimulationpackage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Settings {
    static String address1 = "/Users/jaga/Documents/domain_project/data/NTT-ver2.xlsx";

	static String[] BldgName() {
		// 各ビルの名前
		int sheetNum = 3;
		String sheetName[] = { "練馬区内中継リンク", "荏原区内中継リンク", "墨田区内中継リンク" };
		String[] BldgName = new String[102];
		try {
			FileInputStream fi = new FileInputStream(address1);
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();

			Sheet sheet;
			int rowNum;
			int index = 0;

			for (int i = 0; i < sheetNum; i++) {
				sheet = book.getSheet(sheetName[i]);
				rowNum = (int) sheet.getRow(1).getCell(10).getNumericCellValue();
				for (int k = 0; k < rowNum; k++) {
					Row row = sheet.getRow(k + 1);
					BldgName[index] = row.getCell(1).toString();
					index++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return BldgName;
	}

	static double[] getScale(BuildingList bldgList) {
		//各IDに紐付いた配列にscaleを読み込む
		double[] scale = new double[102];
		try {
			FileInputStream fi = new FileInputStream("/Users/jaga/Documents/domain_project/data/scale_data.xls");
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();
			// データの数の読み込み
			Sheet sheet = null;
			Row row;

			for (int s = 2; s <= 4; s++) {
				//シートの取得
				sheet = book.getSheet(("Sheet" + s));
				int rowNum = (int) sheet.getRow(0).getCell(3).getNumericCellValue();
				for (int r = 0; r < rowNum; r++) {
					//各行についてmeshcodeを取得し書き込み
					row = sheet.getRow(r);
					String bname = row.getCell(0).getStringCellValue();
					int idx = bldgList.findBldg(bname).getBid();
					double val = row.getCell(6).getNumericCellValue();
					scale[idx] = val;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scale;
	}
}
