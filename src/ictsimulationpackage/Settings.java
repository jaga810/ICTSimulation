package ictsimulationpackage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Settings {
    static final String dataPath = "/Users/jaga/Documents/domain_project/data/";
	static final String sheetName[] = { "練馬区内中継リンク", "荏原区内中継リンク", "墨田区内中継リンク" };
	static final int localRingNum = 3;
    static final int bldgNum = 102;

	static BuildingInfo[] BldgInfo() {
        BuildingInfo info[] = new BuildingInfo[bldgNum];
        XSSFWorkbook book = null;

        //エクセルファイルの読み込み
        try {
			FileInputStream fi = new FileInputStream(dataPath + "NTT-ver2.xlsx");
			book = new XSSFWorkbook(fi);
			fi.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        Sheet sheet;
        int rowNum;
        int index = 0;

        for (int s = 0; s < localRingNum; s++) {
            sheet = book.getSheet(sheetName[s]);
            rowNum = (int) sheet.getRow(1).getCell(10).getNumericCellValue();
            for (int r = 0; r < rowNum; r++) {
                Row row = sheet.getRow(r + 1);
                String bname = row.getCell(1).toString();
                int bid =(int)row.getCell(0).getNumericCellValue();
                int kunaiRelayBldgId = (int) row.getCell(6).getNumericCellValue();
                info[index++] = new BuildingInfo(bid, bname, kunaiRelayBldgId);
            }
        }

        return info;
    }

	static double[] getScale(Network bldgList) {
		double[] scale = new double[bldgNum];
		try {
			FileInputStream fi = new FileInputStream(dataPath + "scale_data.xls");
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();

			// データの数の読み込み
			Sheet sheet;
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

    static void importExcelData(Building kugai, Network network) {
        Building[] list = sortBldgList(network);
        int bldgNumIncKugai = bldgNum + 2;

        //Excelデータの取得
        XSSFWorkbook book = null;
        try {
            FileInputStream fi = new FileInputStream(
                    dataPath + "ネットワーク構成例及びトラヒックの調査/交流トラヒックマトリックス(呼数表示)_140930.xlsx");
            book = new XSSFWorkbook(fi);
            fi.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < bldgNumIncKugai; i++) {
            // 発信元ビルのindex
            Building start;
            if (i < bldgNum) {
                start = list[i];
            } else {
                start = kugai;
            }
            for (int hour = 0; hour < 24; hour++) {
                // 時間帯でシートが分かれている
                Sheet sheet = book.getSheetAt(hour);
                Row row = sheet.getRow(i + 3);
                for (int k = 0; k < bldgNumIncKugai; k++) {
                    // 着信ビルの取得
                    Building dest;
                    String destName = sheet.getRow(2).getCell(k + 2).toString();
                    if (destName.equals("-")) {
                        dest = kugai;
                    } else {
                        dest = network.findBldg(destName);
                    }

                    // 呼数の読み込み
                    if (start.isKugai() && dest.isKugai()) {
                        // 区外同士の時
                        continue;
                    }

                    //呼数の更新
                    double kosu = Double.parseDouble(row.getCell(k + 2).toString());
                    if (i == 103) {
                        // start == 県外
                        start.setKosuTaken(hour, dest, kosu);
                    } else {
                        // start == 区内or多摩地区
                        start.setKosu(hour, dest, kosu);
                    }
                }
            }
        }
    }

    // Excelで読み込む時のindexに従うリストに変更 ->
    static Building[] sortBldgList(Network network) {
        String bname;
        Building bldg;
        Building list[] = new Building[bldgNum + 1];
        try {
            FileInputStream fi = new FileInputStream(
                    dataPath + "/ネットワーク構成例及びトラヒックの調査/交流トラヒックマトリックス(呼数表示)_140930.xlsx");
            XSSFWorkbook book = new XSSFWorkbook(fi);
            fi.close();
            Sheet sheet = book.getSheetAt(0);
            Row row = sheet.getRow(2);
            for (int i = 0; i < bldgNum; i++) {
                bname = row.getCell(i + 2).toString();
                bldg = network.findBldg(bname);
                list[i] = bldg;
            }
            list[bldgNum] = network.getBldgList()[bldgNum];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
