package ictsimulationpackage;

import org.apache.poi.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jaga on 9/30/16.
 */
public class GetMeshCode {
    public static void get() {
        Pair scale[] = getScaleData();
        try {
            FileInputStream f = new FileInputStream(
                    "/Users/jaga/Documents/domain_project/data/building_for_qgis_2.xlsx"
            );
            XSSFWorkbook book = new XSSFWorkbook(f);
            Sheet sheet;
            Row row;
            for (int s = 2; s <= 4; s++) {
                //シートの取得
                sheet = book.getSheet(("Sheet" + s));
                int rowNum = (int) sheet.getRow(0).getCell(3).getNumericCellValue();
                for (int r = 0; r < rowNum; r++) {
                    //各行についてmeshcodeを取得し書き込み
                    row = sheet.getRow(r);
                    double lon = row.getCell(1).getNumericCellValue();
                    double lat = row.getCell(2).getNumericCellValue();

                    double sc = lowerBound(scale, getMeshCode(lon, lat));
                    row.createCell(6).setCellValue(sc);
                }
            }
            File file = new File("/Users/jaga/Documents/domain_project/data/scale_data.xls");
            Output.output(file, book);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //昇順に整列済みのデータを返す。
    //３次メッシュに落としたときに同じところに属するものは平均値を取る
    private static Pair[] getScaleData() {
        Pair ps[] = null;
        ArrayList<Pair> res = new ArrayList<>();
        //データの読み込み
        try {
            FileInputStream f = new FileInputStream(
                    "/Users/jaga/Documents/domain_project/data/tokyowan_chokkagata.xlsx"
            );
            XSSFWorkbook book = new XSSFWorkbook(f);
            Sheet sheet;
            Row row;

            //シートの取得
            sheet = book.getSheet("sheet1");
            int rowNum = (int) sheet.getRow(0).getCell(2).getNumericCellValue() - 1;//行数
            ps = new Pair[rowNum];

            System.out.println("loading.....");
            for (int r = 0; r < rowNum; r++) {
                //各行についてmeshcodeを取得
                row = sheet.getRow(r + 1);
                long meshCode = (long) row.getCell(0).getNumericCellValue() / 100; //３次メッシュにしておく
                double scale = row.getCell(1).getNumericCellValue();
                ps[r] = new Pair(meshCode, scale);
//                System.out.println(meshCode + " : " + scale);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Arrays.sort(ps);
        {
            //メッシュコードがかぶったデータの圧縮
            Pair prev = ps[0];
            int cont = 1;//何個同じモノが連続したか
            double sum = prev.second;
            Pair p;
            for (int i = 1; i < ps.length; i++) {
                p = ps[i];
                if (p.first == prev.first && i != ps.length - 1) {
                    //前回と同じ時
                    sum += p.second;
                    cont++;
                } else {
                    //前回と違う値の時
                    res.add(new Pair(prev.first, sum / cont));
                    System.out.println(res.get(res.size() - 1).first + " : "+res.get(res.size() - 1).second);

                    //次回に向けて
                    sum = p.second;
                    cont = 1;
                    prev = p;
                }
            }
        }

        return res.toArray(new Pair[0]);
    }

    private static int getMeshCode(double lon, double lat) {
        String[] latMesh = getLatMesh(lat);
        String[] lonMesh = getLonMesh(lon);

        String res = latMesh[0] + lonMesh[0] + latMesh[1] + lonMesh[1] + latMesh[2] + lonMesh[2];
        System.out.println(res);
        return Integer.parseInt(res);
    }

    private static String[] getLatMesh(double lat) {
        String[] res = new String[3];
        int p = (int) (lat * 60) / 40;
        double a = (lat * 60) % 40;
        int q = (int) a / 5;
        double b = a % 5;
        int r = (int) b * 2;

        res[0] = String.valueOf(p);
        res[1] = String.valueOf(q);
        res[2] = String.valueOf(r);

//        System.out.println(p);
//        System.out.println(q);
//        System.out.println(r);


        return res;
    }

    private static String[] getLonMesh(double lon) {
        String[] res = new String[3];
        int p = (int) lon - 100;
        double a = lon - p - 100;
        int q = (int) ((a * 60) / 7.5);
        double b = (a * 60) % 7.5;
        int r = (int) (b * 60) / 45;


        res[0] = String.valueOf(p);
        res[1] = String.valueOf(q);
        res[2] = String.valueOf(r);

//        System.out.println(p);
//        System.out.println(q);
//        System.out.println(r);


        return res;
    }

    public static void main(String args[]) {
//        get();
//        getLatMesh(35.7007777);
//        getLonMesh(139.71475);
//        getMeshCode(139.802784, 35.369137);
        getScaleData();
    }

    static class Pair implements Comparable {
        long first;
        double second;

        Pair(long a, double b) {
            first = a;
            second = b;
        }

        public int compareTo(Object other) {
            Pair p1 = (Pair) other;

            long cond = this.first - p1.first;
            // IDの値に従い昇順で並び替えたい場合
            if (cond == 0) {
                return 0;
            } else if (cond > 0) {
                return 1;
            } else {
                return -1;
            }
//         return -(this.first - ((Pair) other).first); // IDの値に従い降順で並び替えたい場合
        }
    }

    //(scale, meshcode ) =?
    private static final double lowerBound(final Pair[] arr, final int val) {
        int low = 0;
        int high = arr.length;
        int mid;
        while (low < high) {
            mid = ((high - low) >>> 1) + low;
            if (arr[mid].first < val) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return arr[low].second;
    }
}
