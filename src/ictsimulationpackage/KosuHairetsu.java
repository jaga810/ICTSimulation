package ictsimulationpackage;

import java.io.*;
import java.util.*;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class KosuHairetsu extends ArrayList{
	// public static void main(String argv[]){
	static ArrayList<ArrayList<String>> ExceltoHairetsu() {
		//0発信区ー＞1発信ビルー＞2着信区ー＞3着信ビルー＞4以降..呼数が１t分のリスト(全時間帯)
		ArrayList<ArrayList<String>> KosuList = new ArrayList<ArrayList<String>>();
		// long start = System.currentTimeMillis();
		try {
			FileInputStream fi = new FileInputStream(
					"/Users/jaga/Documents/domain_project/data/ネットワーク構成例及びトラヒックの調査/交流トラヒックマトリックス(呼数表示)_140930.xlsx");
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();
			for (int i = 3; i < 107; i++) {
				System.out.println("loading : " + i + "data");
				for (int j = 2; j < 106; j++) {
					// i：行 j：列
					ArrayList<String> ODList = new ArrayList<String>();
					for (int sheetnum = 0; sheetnum < 24; sheetnum++) {
						Sheet sheet = book.getSheetAt(sheetnum);
						// １時間単位で入力
						if (sheetnum == 0) {
							// 発信の区
							Row row = sheet.getRow(i);
							Cell HatsuKu = row.getCell(0);
							//選択したセルが空→行を上にずらした再検索（セルぶち抜いてるため）
							if (HatsuKu.toString() == "") {
								int i2 = i;
								while (HatsuKu.toString() == "") {
									i2--;
									HatsuKu = sheet.getRow(i2).getCell(0);
								}
							}
							ODList.add(HatsuKu.toString());
							
							// 発信ビル
							Cell HatsuBldg = row.getCell(1);
							ODList.add(HatsuBldg.toString());
							
							// 着信の区
							Row row1 = sheet.getRow(1);
							Cell ChakuKu = row1.getCell(j);
							if (ChakuKu.toString() == "") {
								int j2 = j;
								while (ChakuKu.toString() == "") {
									j2--;
									ChakuKu = row1.getCell(j2);
								}
							}
							ODList.add(ChakuKu.toString());
							// 着信ビル
							Row row2 = sheet.getRow(2);
							Cell ChakuBldg = row2.getCell(j);
							ODList.add(ChakuBldg.toString());
							// 呼数
							Cell Kosu = row.getCell(j);
							ODList.add(Kosu.toString());
						} else {
							// 呼数
							Row row = sheet.getRow(i);
							Cell Kosu = row.getCell(j);
							ODList.add(Kosu.toString());
						}
					}
					//ijそのまま、つまり着信発信元は考慮されていない
					KosuList.add(ODList);
				}
			}

			/*
			 * for(int k = 0; k < KosuList.size(); k++){
			 * System.out.println(KosuList.get(k)); }
			 */
			/*
			 * try { //出力先を作成する FileWriter fw = new
			 * FileWriter("C:\\Users\\watanabe\\Documents\\研究\\File\\呼数配列.csv",
			 * true); //※１ PrintWriter pw = new PrintWriter(new
			 * BufferedWriter(fw));
			 * 
			 * //内容を指定する for(int k = 0; k < KosuList.size(); k++){ for(int l =
			 * 0; l < 6; l++){ pw.print(KosuList.get(k).get(l)); pw.print(",");
			 * } pw.println(); } //ファイルに書き出す pw.close();
			 * 
			 * //終了メッセージを画面に出力する System.out.println("出力が完了しました。");
			 * 
			 * } catch (IOException ex) { //例外時処理 ex.printStackTrace(); }
			 */

			/*
			 * for(Sheet sheet:book){ // 全シートをなめる
			 * sheet.setForceFormulaRecalculation(true);// 数式解決(※)
			 * System.out.println("--- "+sheet.getSheetName()+" ---"); for(Row
			 * row:sheet){ // 全行をなめる for(Cell cell:row){ // 全セルをなめる
			 * System.out.print(getStr(cell)+" "); } System.out.println(""); } }
			 */
		}

		catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
		// long stop = System.currentTimeMillis();
		// System.out.println(stop-start);
		return KosuList;
		// System.exit(0);
	}

	public static String getStr(Cell cell) { // データ型毎の読み取り
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		// return cell.getStringCellValue();(※）
		case Cell.CELL_TYPE_NUMERIC:
			return Double.toString(cell.getNumericCellValue());
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		}
		return "";// CELL_TYPE_BLANK,CELL_TYPE_ERROR
	}
}

// ※:数式を解決して参照したい場合
