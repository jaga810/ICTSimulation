package ictsimulationpackage;

import java.io.*;
import java.util.*;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class KosuDownloader {
	static int bldgNum = 104;
	static Building[] preList;

	static void download() {
		Building[] list = sortBldgList();
		try {
			FileInputStream fi = new FileInputStream(
					"/Users/jaga/Documents/domain_project/data/ネットワーク構成例及びトラヒックの調査/交流トラヒックマトリックス(呼数表示)_140930.xlsx");
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();
			Sheet sheet;
			Row row;
			Building start;
			Building dest;
			Building kugai = BuildingList.findBldg("区外");
			String destName;
			double[] kosu = new double[103];
			System.out.println("downlading '呼数'");
			for (int i = 0; i < bldgNum; i++) {
				System.out.println("downloading..." + " building" + i);
				// 発信元ビルのindex
				if (i < bldgNum - 2) {
					start = list[i];
				} else {
					start = kugai;
				}
				for (int t = 0; t < 24; t++) {
					// 時間帯
					kosu = new double[103];
					sheet = book.getSheetAt(t);
					row = sheet.getRow(i + 3);
					for (int k = 0; k < bldgNum; k++) {
						// 着信ビルの取得
						destName = sheet.getRow(2).getCell(k + 2).toString();
//						System.out.print(destName);
						if (destName.equals("-")) {
							dest = kugai;
						} else {
							dest = BuildingList.findBldg(destName);
						}
//						System.out.print(t+":"+start.bname + "->" + dest.bname + " : ");
						// 個数のロード
						if (start == kugai && dest == kugai) {
							// 区外同士の時
							continue;
						} else if (i == 103){
							// 県外
							double val;
							val = Double.parseDouble(row.getCell(k + 2).toString());
							start.setKosuTaken(t, dest, val);
//							System.out.print(val );
						}else{
							// 一般
							double val;
							val = Double.parseDouble(row.getCell(k + 2).toString());
//							System.out.println("start:" + start.bname + " dest:" + dest.bname);
							start.setKosu(t, dest, val);
//							System.out.println(val);
						}
					}
//					 test用
//					 if (t == 23 && i == 103) {
//					 System.out.println(bldgs.bname + ":time[" + t + "]");
//					 for (int u = 0; u < kosu.length; u++) {
//					 System.out.println(u + " : " + bldgs.kosuTaken[t][u]);
//					 }
//					 }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("download has finished successfully!");
	}

	// Excelで読み込む時のindexに従うリストに変更 ->
	static Building[] sortBldgList() {
		System.out.println("sorting Buildings...");
		String bname = null;
		Building bldg;
		Building list[] = new Building[103];
		try {
			FileInputStream fi = new FileInputStream(
					"/Users/jaga/Documents/domain_project/data/ネットワーク構成例及びトラヒックの調査/交流トラヒックマトリックス(呼数表示)_140930.xlsx");
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();
			Sheet sheet = book.getSheetAt(0);
			Row row = sheet.getRow(2);
			for (int i = 0; i < bldgNum - 2; i++) {
				bname = row.getCell(i + 2).toString();
				bldg = BuildingList.findBldg(bname);
				list[i] = bldg;
//				bldg.bid = i;
			}
			list[102] = BuildingList.bldgList[102];
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 破壊的メソッド化
//		preList = BuildingList.bldgList;
//		BuildingList.bldgList = list;
		System.out.println("sort has finished successfully!");
		return list;
	}
}
