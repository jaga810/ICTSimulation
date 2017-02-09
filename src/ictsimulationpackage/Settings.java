package ictsimulationpackage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Settings {
    static final String dataDir = "/Users/jaga/Documents/domain_project/data/";
	static final String sheetName[] = { "練馬区内中継リンク", "荏原区内中継リンク", "墨田区内中継リンク" };
	static final int localRingNum = 3;
    static final int relayBldgNum = 102;

	static BuildingInfo[] BldgInfo() {
        BuildingInfo info[] = new BuildingInfo[relayBldgNum];
        XSSFWorkbook book = null;

        //エクセルファイルの読み込み
        try {
			FileInputStream fi = new FileInputStream(dataDir + "NTT-ver2.xlsx");
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
		double[] scale = new double[relayBldgNum];
		try {
			FileInputStream fi = new FileInputStream(dataDir + "scale_data.xls");
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

}
