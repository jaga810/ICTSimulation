package ictsimulationpackage;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by jaga on 1/18/17.
 */
public class EarlangB2 {
    public static void main(String args[]) {
        EarlangB2 e = new EarlangB2();
        e.download();
    }

    public void download() {
        String path = "/Users/jaga/Documents/domain_project/data/ネットワーク構成例及びトラヒックの調査/交流トラヒックマトリックス(呼量表示)_140930.xlsx";
        File file = new File(path);
        FileInputStream input = null;
        double[] traffic = new double[24];

        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            XSSFWorkbook book = new XSSFWorkbook(input);

            int areaNum = 104;
            for(int s = 0; s < 24;s++) {
                Sheet sh = book.getSheet(s + "時台");
                //時間毎に集計
                for(int i = 3;i < areaNum;i++) {
                    Row row = sh.getRow(i+ 2);
                    for(int k = 0; k < areaNum;k++) {
                        traffic[s] += row.getCell(k + 2).getNumericCellValue();
                    }
                }
                System.out.println(s + "時台:" + traffic[s] );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int maxMag = 1000;
        double allKaisen = 22400*102 + 32600*3 + 66400*1;
        double earlangB[][] = new double [maxMag + 1][24];

        for(int m = 1; m <= maxMag;m++) {
            for(int i = 0;i < 24;i++) {
                for(int k = 0;k < allKaisen;k++) {
                    earlangB[m][i] = calcEarlangB(k, traffic[i] * m * 30,earlangB[m][i]);
//                    if (k == allKaisen / 1000) {
//                        System.out.println("半分");
//                    }
                }
                System.out.println("倍率:" + m + "倍、" + i + "時台 : " +  (earlangB[m][i] * 100)+"%");
            }
        }
    }

    private double calcEarlangB(double kaisen,  double traffic, double preEarlang) {
        //再帰にするとstackOverflowするので注意
        if (kaisen == 0) {
            return 1;
        }
        double earlang = traffic * preEarlang / (kaisen + traffic * preEarlang);
        return earlang;
    }
}
