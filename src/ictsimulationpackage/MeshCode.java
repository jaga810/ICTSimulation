package ictsimulationpackage;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * メッシュコードから各中継ビルの震度分布をエクセルデータとして出力する
 */
public class MeshCode {
    private static final String dataPath = Path.DATA_PATH.get();

    public static void main(String args[]) {
        makeScaleExcelWithBldg();
    }

    public static void makeScaleExcelWithBldg() {
        Pair scale[] = getScaleData();
        XSSFWorkbook book = IOHelper.importExcelToWorkBook(
                dataPath + "building_for_qgis_2.xlsx" //中継ビルに対する緯度経度
        );
        for (int s = 2; s <= 4; s++) {
            //シートの取得
            Sheet sheet;
            Row row;
            sheet = book.getSheet(("Sheet" + s));
            int rowNum = (int) sheet.getRow(0).getCell(3).getNumericCellValue();
            for (int r = 0; r < rowNum; r++) {
                //各行についてmeshcodeを取得し書き込み
                row = sheet.getRow(r);
                double lon = row.getCell(1).getNumericCellValue();
                double lat = row.getCell(2).getNumericCellValue();

                //全てのメッシュを網羅していないことに注意（ない場合は近いもの）
                double sc = lowerBound(scale, getMeshCode(lon, lat));
                row.createCell(6).setCellValue(sc);
            }
        }
        File file = new File(dataPath + "scale_data.xls");
        IOHelper.output(file, book);

    }

    /**
     * 四次メッシュの直下型地震の震度分布を読み込んで三次メッシュに落としてreturn
     * 三次メッシュで同じメッシュコードになるもので平均を撮る
     * meshcodeで昇順に整列済み
     * @return Pair = (meshcode, 平均震度）
     */
    private static Pair[] getScaleData() {
        ArrayList<Pair> res = new ArrayList<>();
        XSSFWorkbook book = IOHelper.importExcelToWorkBook(
                dataPath + "tokyowan_chokkagata.xlsx"
        );

        //シートの取得
        Sheet sheet = book.getSheet("sheet1");
        int rowNum = (int) sheet.getRow(0).getCell(2).getNumericCellValue() - 1;//行数
        Pair ps[] = new Pair[rowNum];

        System.out.println("loading.....");
        for (int r = 0; r < rowNum; r++) {
            //各行についてmeshcodeを取得
            Row row = sheet.getRow(r + 1);
            long meshCode = (long) row.getCell(0).getNumericCellValue() / 100; //３次メッシュにしておく
            double scale = row.getCell(1).getNumericCellValue();
            ps[r] = new Pair(meshCode, scale);
//                System.out.println(meshCode + " : " + scale);
        }

        Arrays.sort(ps);

        //メッシュコードがかぶったデータの圧縮
        Pair prev = ps[0];
        int cnt = 1;//何個同じモノが連続したか
        double sum = prev.second;
        for (int i = 1; i < ps.length; i++) {
            Pair p = ps[i];
            if (p.first == prev.first && i != ps.length - 1) {
                //前回と同じ時
                sum += p.second;
                cnt++;
            } else {
                //前回と違う値の時
                res.add(new Pair(prev.first, sum / cnt));
                System.out.println(res.get(res.size() - 1).first + " : " + res.get(res.size() - 1).second);

                //次回に向けて
                sum = p.second;
                cnt = 1;
                prev = p;
            }
        }
        //ArrayListを配列にしてreturn
        return res.toArray(new Pair[0]);
    }

    /**
     * 緯度経度を元に三次メッシュコードをreturnする
     * @param lon 経度
     * @param lat 緯度
     * @return
     */
    private static int getMeshCode(double lon, double lat) {
        String[] latMesh = getLatMesh(lat);
        String[] lonMesh = getLonMesh(lon);

        String res = latMesh[0] + lonMesh[0] + latMesh[1] + lonMesh[1] + latMesh[2] + lonMesh[2];
        System.out.println(res);
        return Integer.parseInt(res);
    }

    /**
     * 経度を元にメッシュコードの経度部分を生成
     * @param lat　経度
     * @return メッシュコードの要素
     */
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

        return res;
    }

    /**
     * 緯度を元に三次メッシュコードの緯度部分を作成
     * @param lon 緯度
     * @return メッシュコードの要素
     */
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

        return res;
    }

    /**
     * 処理に使う
     * firrst要素でComparableであり、Arrays.sort()が使える
     */
    private static class Pair implements Comparable {
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

    /**
     * lowerBoundを求める
     * @param arr 対象の配列
     * @param val　閾値
     * @return 初めてarrの中でvalがPair.firstとなるときのsecond
     */
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
