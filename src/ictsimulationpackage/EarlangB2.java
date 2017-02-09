package ictsimulationpackage;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaga on 1/18/17.
 */
public class EarlangB2 {
    public static void main(String args[]) {
        EarlangB2 e = new EarlangB2();
        Network bldgs = new Network(102);



        //Linkの初期capacityを無限にしておく（そうしないとルート探索に失敗する）
        e.iniCap(bldgs);

        //ビルをExcelの配列に並び直す
        Building[] list = e.getBldgInOrder(bldgs);

        //リンクのトラフィックをロード
        e.download(list, bldgs);

//        HashMap<Double,Double> clr = e.calc(bldgs, 20);
        HashMap<Double,BigDecimal> clr = e.calc2(bldgs, 20);

//        for(int i = 0; i < list.length;i++) {
//            System.out.println(i +":" + list[i].getBname());
//        }

        //Excelにアウトプット
        e.output(clr);
    }

    public void output(HashMap<Double, ?> clr) {
        File outputDir = Output.getTimeDir();
        File file = new File(outputDir + "/earlangB.xls");
        Workbook wb = Output.getWorkbook(file, new HSSFWorkbook());
        Sheet sh = wb.createSheet("earlangB");

        int i = 0;
        for(double key : clr.keySet()) {
            Row row = sh.createRow(i++);
            row.createCell(0).setCellValue(key);
            row.createCell(1).setCellValue(clr.get(key).toString());
        }
        Output.output(file, wb);
    }


    public void download(Building[] list, Network bldgs) {
        String path = "/Users/jaga/Documents/domain_project/data/ネットワーク構成例及びトラヒックの調査/交流トラヒックマトリックス(呼量表示)_140930.xlsx";
        File file = new File(path);
        FileInputStream input = null;
        LargeRing ring = new LargeRing(bldgs);

        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            XSSFWorkbook book = new XSSFWorkbook(input);
            int areaNum = 103;
            for (int s = 0; s < 24; s++) {
                Sheet sh = book.getSheet(s + "時台");
                //時間毎に集計
                for (int i = 0; i < areaNum; i++) {
                    Row row = sh.getRow(i + 3);
                    Building start = list[i];
                    for (int k = 0; k < areaNum; k++) {

                        Building dest = list[k];
                        if (start == dest) continue;
                        double traffic = row.getCell(k + 2).getNumericCellValue();

                        //右回り固定と左回り固定でそれぞれ探索。通る可能性のあるリンクを洗い出す
                        ArrayList<Link> lnListR = ring.route(start, dest, true);
                        ArrayList<Link> lnListL = ring.route(start, dest, false);

                        if (lnListR == null) {
                            System.out.println("null");
                        }

                        for (Link ln : lnListR) {
                            ln.addTrrafic(s, traffic);
                        }
                        for (Link ln : lnListL) {
                            ln.addTrrafic(s, traffic);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Link ln : bldgs.getAllLinkList()) {
            System.out.println(ln.getId() + " : " + ln.getMaxTrrafic());
        }
    }

    public Building[] getBldgInOrder(Network bldgs) {
        String path = "/Users/jaga/Documents/domain_project/data/ネットワーク構成例及びトラヒックの調査/交流トラヒックマトリックス(呼量表示)_140930.xlsx";
        File file = new File(path);
        FileInputStream input = null;
        Building[] list = new Building[103];

        try {
            input = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            XSSFWorkbook book = new XSSFWorkbook(input);

            Sheet sh = book.getSheet("0時台");
            Row row = sh.getRow(2);
            for (int i = 0; i < list.length - 1; i++) {
                Building bldg = bldgs.findBldg(row.getCell(i + 2).getStringCellValue());
                list[i] = bldg;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        list[list.length - 2] = bldgs.getKugaiRelayBldg();
        list[list.length - 1] = bldgs.getKugaiRelayBldg();
        return list;
    }




    public HashMap<Double,Double> calc(Network bldgs, int steps) {
        //全てのリンク
        ArrayList<Link> allLinks = bldgs.getAllLinkList();

        //mag別の最大呼損率
        HashMap<Double,Double> clrByMag = new HashMap<>();

        for(int m = 0; m < steps;m++) {
            //需要の倍率
            double mag = 1.0 + 0.5 * (double)m;

            //時間帯別の呼損率
            double[] clrList = new double[24];

            //リンク別の呼損率を時間帯別に収納
            HashMap<Link, double[]> CLRByLink = new HashMap<>();

            //リンク別の損失呼を時間帯別に収納
            HashMap<Link, double[]> CLByLink = new HashMap<>();

            //全体の呼損量（時間帯別)
            double clList[] = new double[24];

            //全体の生起呼量(時間帯別) generated calls
            double gcList[] = new double[24];

            //リンク別の呼損率の計算
            for (Link ln : allLinks) {
                int kaisen = ln.getIniCap();
                double clr[] = new double[24];
                double cl[] = new double[24];

                for (int h = 0; h < 24; h++) {
                    //h=時間帯
                    double trrafic = ln.getTrrafic(h) * mag;
                    //wikiに従った算出
                    for (int i = 0; i < kaisen; i++) {
                        clr[h] = calcEarlangB(i, trrafic, clr[h]);
                    }

                    cl[h] = clr[h] * trrafic;
                    gcList[h] += trrafic;
                    clList[h] += cl[h];
                }
                CLRByLink.put(ln, clr);
                CLByLink.put(ln, cl);
            }

            //全体の呼損率の計算
            for (int h = 0; h < 24; h++) {
                clrList[h] = clList[h] / gcList[h];
//                System.out.println(m + "倍: " + h + "時台:" + clList[h]);
            }
            clrByMag.put(mag,Output.maxInArray(clrList) * 100.0);
            System.out.println(mag + "倍: " +clrByMag.get(mag));
        }
        return clrByMag;
    }
    public HashMap<Double,BigDecimal> calc2(Network bldgs, int steps) {
        //全てのリンク
        ArrayList<Link> allLinks = bldgs.getAllLinkList();

        //bigDemacil野丸め
        int mal = 5;

        //mag別の最大呼損率
        HashMap<Double,BigDecimal> clrByMag = new HashMap<>();

        for(int m = 0; m < steps;m++) {
            //需要の倍率
            double mag = 1.0 + 0.5 * (double)m;

            //時間帯別の呼損率
            BigDecimal[] clrList = new BigDecimal[24];

            //リンク別の呼損率を時間帯別に収納
            HashMap<Link, double[]> CLRByLink = new HashMap<>();

            //リンク別の損失呼を時間帯別に収納
            HashMap<Link, double[]> CLByLink = new HashMap<>();

            //全体の呼損量（時間帯別)
            BigDecimal clList[] = new BigDecimal[24];

            //全体の生起呼量(時間帯別) generated calls
            BigDecimal gcList[] = new BigDecimal[24];

            //リンク別の呼損率の計算
            for (Link ln : allLinks) {
                int kaisen = ln.getIniCap();
                BigDecimal clr[] = new BigDecimal[24];
                BigDecimal cl[] = new BigDecimal[24];
                int h = 10;
//                for (int h = 0; h < 24; h++) {
                    //h=時間帯
                    double trrafic = ln.getTrrafic(h) * mag;

                    //先行研究に従った算出
                    BigDecimal dem;
                    BigDecimal nur = BigDecimal.ZERO;

                    dem = BigDecimal.valueOf(trrafic).pow(kaisen).divide(getFactorial(kaisen), mal, BigDecimal.ROUND_HALF_UP);
                    for(int i = 0; i < kaisen;i++) {
                        nur.add(BigDecimal.valueOf(trrafic).pow(i).divide(getFactorial(i), mal, BigDecimal.ROUND_HALF_UP));
                        if (i % 1000 == 0) {
                            System.out.println(i);
                        }
                    }

                    clr[h] = dem.divide(nur, mal, BigDecimal.ROUND_HALF_UP);

                    cl[h] = clr[h].multiply(BigDecimal.valueOf(trrafic));
                    gcList[h].add(BigDecimal.valueOf(trrafic));
                    clList[h].add(cl[h]);

                    System.out.println(ln.getId() +",h:" + h + " cl:" + cl[h]);
//                }
            }

            //全体の呼損率の計算
            for (int h = 0; h < 24; h++) {
                clrList[h] = clList[h].divide(gcList[h], mal, BigDecimal.ROUND_HALF_UP);
//                System.out.println(m + "倍: " + h + "時台:" + clList[h]);
            }
            clrByMag.put(mag,Output.maxInArray(clrList).multiply(BigDecimal.valueOf(100)));
            System.out.println(mag + "倍: " +clrByMag.get(mag));
        }
        return clrByMag;
    }

    public void iniCap(Network bldgs) {
        int kaisen = 22400;
        int exKaisen = 32600;
        int outKaisen = 66400;

        for (Link ln : bldgs.getAllLinkList()) {
            int val = 0;
            int id = ln.getId();

            if (ln.getId() < 200) {
                val = kaisen;
            } else if (id < 1000) {
                val = exKaisen;
            } else if (id == 1000) {
                val = outKaisen;
            } else {
                int a = 1 / 0;
            }

            ln.iniCap(24, val);
            System.out.println(ln.getId() + " link is ok");
        }
    }

    private double calcEarlangB(double kaisen, double traffic, double preEarlang) {
        //再帰にするとstackOverflowするので注意
        if (kaisen == 0) {
            return 1;
        }
        double earlang = traffic * preEarlang / (kaisen + traffic * preEarlang);
        return earlang;
    }

    private double calcEarlangB2(double kaisen, double traffic, double preEarlang) {
        //階乗のテーブル
        long table[];
        double res = 0;
        for(int n = 0; n < kaisen;n++) {
            //n = 回線数
            res += Math.pow(traffic,n);
        }
        return 0;
    }

    public BigDecimal getFactorial(int val) {
        BigDecimal res = BigDecimal.ONE;
        for(int i = 0; i <= val ;i++) {
            if(i == 0)continue;
            else res = res.multiply(BigDecimal.valueOf(i));
        }
        return res;
    }
}
