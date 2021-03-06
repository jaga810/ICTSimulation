package ictsimulationpackage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 殆どリファクタリングしてない魔境なので書き直したほうが理解早い気がします
 * 評価基準を変えてoutputできるようになってますが全ていっぺんに回したほうが楽なので分化は必要ないかも
 */
public class IOHelper {
    //outputするルートとなるフォルダ
    private static final String outputRootPath = Path.OUTPUT_PATH.get();

    //一日分のデータの保存
    private ArrayList<double[]> allCallLossRate = new ArrayList<>();
    private ArrayList<double[]> allCallLoss = new ArrayList<>();

    //loop毎のデータの保存
    private ArrayList<Double> timePointList[];
    private ArrayList<Double> ammPointList[];
    private ArrayList<Double> bothPointList[];
    private ArrayList<Integer> brokenBldgNum = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> brokenBldgId = new ArrayList<>();

    //スレッド毎のデータ保存。keyはbrokenBldgLimit
    private static HashMap<Integer, ArrayList<Double>[]> limitTimePointList = new HashMap();
    private static HashMap<Integer, ArrayList<Double>[]> limitAmmPointList = new HashMap();
    private static HashMap<Integer, ArrayList<Double>[]> limitBothPointList = new HashMap();


    //与えられたfileにwbの内容をoutputするメソッド
    public static void output(File file, Workbook wb) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            wb.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }

    void areaDevidedKosu(File folder, int loop, CallList group) {
        String path = folder + "/areaDevidedKosu.xls";
        String sheetName = "areaKosu_" + loop;
        long array[] = group.getKosuInLocalRing();

        File file = new File(path);
        Workbook wb = new HSSFWorkbook();
        wb = getWorkbook(file, wb);

        Sheet sheet;

        // areaKosu
        sheet = wb.createSheet(sheetName);
        for (int i = 0; i < array.length; i++) {
            sheet.createRow(i).createCell(0).setCellValue(array[i]);
        }
        // areaLossKosu
        sheetName = "areaLossKosu_" + loop;
        array = group.getLossKosuThroughKunaiRelayRing();
        sheet = wb.createSheet(sheetName);
        for (int i = 0; i < array.length; i++) {
            sheet.createRow(i).createCell(0).setCellValue(array[i]);
        }

        // exKosu
        sheetName = "exKosu_" + loop;
        array = group.getKosuThroughKunaiRelayRing();
        sheet = wb.createSheet(sheetName);
        for (int i = 0; i < array.length; i++) {
            sheet.createRow(i).createCell(0).setCellValue(array[i]);
        }

        // exLossKosu
        sheetName = "exLossKosu_" + loop;
        array = group.getLossKosuInLocalRing();
        sheet = wb.createSheet(sheetName);
        for (int i = 0; i < array.length; i++) {
            sheet.createRow(i).createCell(0).setCellValue(array[i]);
        }

        output(file, wb);
    }

    //現在存在するxlsファイルを読みだす
    public static Workbook getWorkbook(File file, Workbook wb) {
        if (file.exists()) {
            try {
                InputStream in = new FileInputStream(file);
                wb = WorkbookFactory.create(in);
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wb;
    }

    public void magDevidedOutput(int timeLength, File timedir, int loop, int mag, int[] callExist, int[] callOccur, int[] callLoss, double[] callLossRate, int[] callDeleted, double[] avgHoldTime) {
        String path = timedir + "/magDevidedOutput.xls";
        File file = new File(path);
        Workbook wb = new HSSFWorkbook();
        wb = IOHelper.getWorkbook(file, wb);
        Sheet sheet;
        if (loop > 0) {
            sheet = wb.createSheet("呼量" + mag + "倍_broken");
        } else {
            sheet = wb.createSheet("呼量" + mag + "倍");
        }

        Row row = sheet.createRow(0);
        // カラムインデックス
        Cell cell = row.createCell(0);
        cell.setCellValue("time");
        cell = row.createCell(1);
        cell.setCellValue("occurredCallsNum");
        cell = row.createCell(2);
        cell.setCellValue("lost");
        cell = row.createCell(3);
        cell.setCellValue("rate");
        cell = row.createCell(4);
        cell.setCellValue("exists");
        cell = row.createCell(5);
        cell.setCellValue("deleted");
        cell = row.createCell(6);
        cell.setCellValue("avg holding time");

        for (int t = 0; t < timeLength; t++) {
            row = sheet.createRow(t + 1);
            cell = row.createCell(0);
            cell.setCellValue(t + 1);
            cell = row.createCell(1);
            cell.setCellValue(callOccur[t]);
            cell = row.createCell(2);
            cell.setCellValue(callLoss[t]);
            cell = row.createCell(3);
            cell.setCellValue(callLossRate[t]);
            cell = row.createCell(4);
            cell.setCellValue(callExist[t]);
            cell = row.createCell(5);
            cell.setCellValue(callDeleted[t]);
            cell = row.createCell(6);
            cell.setCellValue(avgHoldTime[t]);
        }

        // １時間単位のデータ
        if (loop > 0) {
            sheet = wb.createSheet("呼量" + mag + "倍_byHour_broken");
        } else {
            sheet = wb.createSheet("呼量" + mag + "倍_byHour");
        }

        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("time");
        cell = row.createCell(1);
        cell.setCellValue("occurredCallsNum");
        cell = row.createCell(2);
        cell.setCellValue("lost");
        cell = row.createCell(3);
        cell.setCellValue("rate");
        cell = row.createCell(4);
        cell.setCellValue("exists");
        cell = row.createCell(5);
        cell.setCellValue("deleted");
        cell = row.createCell(6);
        cell.setCellValue("avg holding time");

        double callOccurH[] = Utility.arrayIntoHour(callOccur);
        double callLossH[] = Utility.arrayIntoHour(callLoss);
        double callLossRateH[] = Utility.arrayIntoHourRate(callLossRate);
        double callExistH[] = Utility.arrayIntoHour(callExist);
        double callDeletedH[] = Utility.arrayIntoHour(callDeleted);
        double avgHoldTimeH[] = Utility.arrayIntoHourRate(avgHoldTime);

        for (int h = 0; h < Setting.SIMULATION_HOUR.get(); h++) {
            row = sheet.createRow(h + 1);
            row.createCell(0).setCellValue(h + 1);
            row.createCell(1).setCellValue(callOccurH[h]);
            row.createCell(2).setCellValue(callLossH[h]);
            row.createCell(3).setCellValue(callLossRateH[h]);
            row.createCell(4).setCellValue(callExistH[h]);
            row.createCell(5).setCellValue(callDeletedH[h]);
            row.createCell(6).setCellValue(avgHoldTimeH[h]);
        }
        double worst_loss_rate = 0;
        for (int i = 0; i < callLossRateH.length; i++) {
            if (worst_loss_rate < callLossRateH[i]) {
                worst_loss_rate = callLossRateH[i];
            }
        }
        sheet.createRow(26).createCell(0).setCellValue("最大呼損率/h");
        sheet.createRow(27).createCell(0).setCellValue(worst_loss_rate);

        IOHelper.output(file, wb);
    }

    public void regulaitonMethodDevidedInitialize(int criNum) {
        timePointList = new ArrayList[criNum];
        ammPointList = new ArrayList[criNum];
        bothPointList = new ArrayList[criNum];
        //リストの初期化
        for (int i = 0; i < criNum; i++) {
            timePointList[i] = new ArrayList<>();
            ammPointList[i] = new ArrayList<>();
            bothPointList[i] = new ArrayList<>();
        }

    }

    public void regulationMethodDevided(int timeLength, File timedir, int loop, int mag, int[] callExist, int[] callOccur,
                                        int[] callLoss, double[] callLossRate, int[] callDeleted, double[] avgHoldTime, int loopNum,
                                        int timeRegulation, int ammountRegulation, int criNum, Building[] bldgList) {

        //通信規制の方針を比較する
        String path = timedir + "/regulationMethodDevidedOutput_" + (loop / 4) + ".xls";
        File file = new File(path);
        Workbook wb = new HSSFWorkbook();
        wb = IOHelper.getWorkbook(file, wb);
        Sheet sheet;
        Row row;
        Cell cell;
//		if (loop > 0) {
//			sheet = wb.createSheet(mag + "倍_regulated");
//		} else {
//			sheet = wb.createSheet(mag + "倍");
//		}
//		row = sheet.createRow(0);
//		// カラムインデックス
//		cell = row.createCell(0);
//		cell.setCellValue("time");
//		cell = row.createCell(1);
//		cell.setCellValue("occurredCallsNum");
//		cell = row.createCell(2);
//		cell.setCellValue("lost");
//		cell = row.createCell(3);
//		cell.setCellValue("rate");
//		cell = row.createCell(4);
//		cell.setCellValue("exists");
//		cell = row.createCell(5);
//		cell.setCellValue("deleted");
//		cell = row.createCell(6);
//		cell.setCellValue("avg holding time");
//
//		for (int t = 0; t < timeLength; t++) {
//			row = sheet.createRow(t + 1);
//			cell = row.createCell(0);
//			cell.setCellValue(t + 1);
//			cell = row.createCell(1);
//			cell.setCellValue(callOccur[t]);
//			cell = row.createCell(2);
//			cell.setCellValue(callLoss[t]);
//			cell = row.createCell(3);
//			cell.setCellValue(callLossRate[t]);
//			cell = row.createCell(4);
//			cell.setCellValue(callExist[t]);
//			cell = row.createCell(5);
//			cell.setCellValue(callDeleted[t]);
//			cell = row.createCell(6);
//			cell.setCellValue(avgHoldTime[t]);
//		}
        // １時間単位のデータ
        String sheetName = mag + "倍_";
        if (ammountRegulation == 1) {
            sheetName += "ammReg";
        }
        if (timeRegulation == 1) {
            sheetName += "_timeReg";
        }

        sheet = wb.createSheet(sheetName);

        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("time");
        cell = row.createCell(1);
        cell.setCellValue("occurredCallsNum");
        cell = row.createCell(2);
        cell.setCellValue("lost");
        cell = row.createCell(3);
        cell.setCellValue("rate");
        cell = row.createCell(4);
        cell.setCellValue("exists");
        cell = row.createCell(5);
        cell.setCellValue("deleted");
        cell = row.createCell(6);
        cell.setCellValue("avg holding time");


        double callOccurH[] = Utility.arrayIntoHour(callOccur);
        double callLossH[] = Utility.arrayIntoHour(callLoss);
        double callLossRateH[] = Utility.arrayIntoHourRate(callLossRate);
        double callExistH[] = Utility.arrayIntoHour(callExist);
        double callDeletedH[] = Utility.arrayIntoHour(callDeleted);
        double avgHoldTimeH[] = Utility.arrayIntoHourRate(avgHoldTime);

        for (int h = 0; h < Setting.SIMULATION_HOUR.get(); h++) {
            row = sheet.createRow(h + 1);
            row.createCell(0).setCellValue(h + 1);
            row.createCell(1).setCellValue(callOccurH[h]);
            row.createCell(2).setCellValue(callLossH[h]);
            row.createCell(3).setCellValue(callLossRateH[h]);
            row.createCell(4).setCellValue(callExistH[h]);
            row.createCell(5).setCellValue(callDeletedH[h]);
            row.createCell(6).setCellValue(avgHoldTimeH[h]);
        }
        double worst_loss_rate = 0;
        for (int i = 0; i < callLossRateH.length; i++) {
            if (worst_loss_rate < callLossRateH[i]) {
                worst_loss_rate = callLossRateH[i];
            }
        }
        sheet.createRow(26).createCell(0).setCellValue("最大呼損率/h");
        sheet.createRow(27).createCell(0).setCellValue(worst_loss_rate);

        allCallLossRate.add(callLossRateH);
        allCallLoss.add(callLossH);

        if (loop % 4 == 3) {
            //最後のループの時
            for (int criterion = 0; criterion < criNum; criterion++) {
                //変数初期化
                String criText = "";
                double[] rateNonReg;
                double[] rateTimeReg;
                double[] rateAmmReg;
                double[] rateBothReg;
                double difTimeSum;
                double difAmmSum;
                double difBothSum;
                double timeDif;
                double ammDif;
                double bothDif;
                switch (criterion) {
                    case 0:
                        criText = "_maxCLR";
                        sheet = wb.createSheet("summary" + criText);

                        rateNonReg = allCallLossRate.get(0);
                        rateTimeReg = allCallLossRate.get(1);
                        rateAmmReg = allCallLossRate.get(2);
                        rateBothReg = allCallLossRate.get(3);

                        row = sheet.createRow(0);

                        row.createCell(0).setCellValue("noRegulated");
                        row.createCell(1).setCellValue("timeReg");
                        row.createCell(2).setCellValue("ammReg");
                        row.createCell(3).setCellValue("bothReg");
                        row.createCell(4).setCellValue("timeDif");
                        row.createCell(5).setCellValue("ammDif");
                        row.createCell(6).setCellValue("bothDif");
                        difTimeSum = 0;
                        difAmmSum = 0;
                        difBothSum = 0;
                        //出力
                        row = sheet.createRow(1);
                        row.createCell(0).setCellValue(Utility.maxInArray(rateNonReg));
                        row.createCell(1).setCellValue(Utility.maxInArray(rateTimeReg));
                        row.createCell(2).setCellValue(Utility.maxInArray(rateAmmReg));
                        row.createCell(3).setCellValue(Utility.maxInArray(rateBothReg));

                        //差分の導出
                        timeDif = Utility.maxInArray(rateNonReg) - Utility.maxInArray(rateTimeReg);
                        ammDif = Utility.maxInArray(rateNonReg) - Utility.maxInArray(rateAmmReg);
                        bothDif = Utility.maxInArray(rateNonReg) - Utility.maxInArray(rateBothReg);

                        difTimeSum += timeDif;
                        difAmmSum += ammDif;
                        difBothSum += bothDif;

                        row.createCell(4).setCellValue(timeDif);
                        row.createCell(5).setCellValue(ammDif);
                        row.createCell(6).setCellValue(bothDif);

                        //ポイントの合計
                        row.createCell(4).setCellValue(difTimeSum);
                        row.createCell(5).setCellValue(difAmmSum);
                        row.createCell(6).setCellValue(difBothSum);

                        //ポイントの集計
                        timePointList[criterion].add(difTimeSum);
                        ammPointList[criterion].add(difAmmSum);
                        bothPointList[criterion].add(difBothSum);
                        break;
                    case 1:
                        criText = "_aveCLR";
                        sheet = wb.createSheet("summary" + criText);

                        rateNonReg = allCallLossRate.get(0);
                        rateTimeReg = allCallLossRate.get(1);
                        rateAmmReg = allCallLossRate.get(2);
                        rateBothReg = allCallLossRate.get(3);

                        row = sheet.createRow(0);

                        row.createCell(0).setCellValue("noRegulated");
                        row.createCell(1).setCellValue("timeReg");
                        row.createCell(2).setCellValue("ammReg");
                        row.createCell(3).setCellValue("bothReg");
                        row.createCell(4).setCellValue("timeDif");
                        row.createCell(5).setCellValue("ammDif");
                        row.createCell(6).setCellValue("bothDif");
                        difTimeSum = 0;
                        difAmmSum = 0;
                        difBothSum = 0;

                        //出力
                        row = sheet.createRow(1);
                        row.createCell(0).setCellValue(Utility.aveInArray(rateNonReg));
                        row.createCell(1).setCellValue(Utility.aveInArray(rateTimeReg));
                        row.createCell(2).setCellValue(Utility.aveInArray(rateAmmReg));
                        row.createCell(3).setCellValue(Utility.aveInArray(rateBothReg));

                        //差分の導出
                        timeDif = Utility.aveInArray(rateNonReg) - Utility.aveInArray(rateTimeReg);
                        ammDif = Utility.aveInArray(rateNonReg) - Utility.aveInArray(rateAmmReg);
                        bothDif = Utility.aveInArray(rateNonReg) - Utility.aveInArray(rateBothReg);

                        difTimeSum += timeDif;
                        difAmmSum += ammDif;
                        difBothSum += bothDif;

                        row.createCell(4).setCellValue(timeDif);
                        row.createCell(5).setCellValue(ammDif);
                        row.createCell(6).setCellValue(bothDif);

                        //ポイントの合計
                        row.createCell(4).setCellValue(difTimeSum);
                        row.createCell(5).setCellValue(difAmmSum);
                        row.createCell(6).setCellValue(difBothSum);

                        //ポイントの集計
                        timePointList[criterion].add(difTimeSum);
                        ammPointList[criterion].add(difAmmSum);
                        bothPointList[criterion].add(difBothSum);
                        break;
                    case 2:
                        //シート作成
                        criText = "_sumCLR";
                        sheet = wb.createSheet("summary" + criText);

                        rateNonReg = allCallLossRate.get(0);
                        rateTimeReg = allCallLossRate.get(1);
                        rateAmmReg = allCallLossRate.get(2);
                        rateBothReg = allCallLossRate.get(3);

                        row = sheet.createRow(0);

                        row.createCell(0).setCellValue("noRegulated");
                        row.createCell(1).setCellValue("timeReg");
                        row.createCell(2).setCellValue("ammReg");
                        row.createCell(3).setCellValue("bothReg");
                        row.createCell(4).setCellValue("timeDif");
                        row.createCell(5).setCellValue("ammDif");
                        row.createCell(6).setCellValue("bothDif");
                        difTimeSum = 0;
                        difAmmSum = 0;
                        difBothSum = 0;
                        for (int r = 0; r < rateNonReg.length; r++) {
                            //出力
                            row = sheet.createRow(r + 1);
                            row.createCell(0).setCellValue(rateNonReg[r]);
                            row.createCell(1).setCellValue(rateTimeReg[r]);
                            row.createCell(2).setCellValue(rateAmmReg[r]);
                            row.createCell(3).setCellValue(rateBothReg[r]);

                            //差分の導出
                            timeDif = rateNonReg[r] - rateTimeReg[r];
                            ammDif = rateNonReg[r] - rateAmmReg[r];
                            bothDif = rateNonReg[r] - rateBothReg[r];

                            difTimeSum += timeDif;
                            difAmmSum += ammDif;
                            difBothSum += bothDif;

                            row.createCell(4).setCellValue(timeDif);
                            row.createCell(5).setCellValue(ammDif);
                            row.createCell(6).setCellValue(bothDif);
                        }
                        //ポイントの合計
                        row.createCell(4).setCellValue(difTimeSum);
                        row.createCell(5).setCellValue(difAmmSum);
                        row.createCell(6).setCellValue(difBothSum);

                        //ポイントの集計
                        timePointList[criterion].add(difTimeSum);
                        ammPointList[criterion].add(difAmmSum);
                        bothPointList[criterion].add(difBothSum);
                        break;
                    case 3:
                        criText = "_sumCL";
                        sheet = wb.createSheet("summary" + criText);

                        rateNonReg = allCallLoss.get(0);
                        rateTimeReg = allCallLoss.get(1);
                        rateAmmReg = allCallLoss.get(2);
                        rateBothReg = allCallLoss.get(3);
                        row = sheet.createRow(0);

                        row.createCell(0).setCellValue("noRegulated");
                        row.createCell(1).setCellValue("timeReg");
                        row.createCell(2).setCellValue("ammReg");
                        row.createCell(3).setCellValue("bothReg");
                        row.createCell(4).setCellValue("timeDif");
                        row.createCell(5).setCellValue("ammDif");
                        row.createCell(6).setCellValue("bothDif");
                        difTimeSum = 0;
                        difAmmSum = 0;
                        difBothSum = 0;
                        for (int r = 0; r < rateNonReg.length; r++) {
                            //出力
                            row = sheet.createRow(r + 1);
                            row.createCell(0).setCellValue(rateNonReg[r]);
                            row.createCell(1).setCellValue(rateTimeReg[r]);
                            row.createCell(2).setCellValue(rateAmmReg[r]);
                            row.createCell(3).setCellValue(rateBothReg[r]);

                            //差分の導出
                            timeDif = rateNonReg[r] - rateTimeReg[r];
                            ammDif = rateNonReg[r] - rateAmmReg[r];
                            bothDif = rateNonReg[r] - rateBothReg[r];

                            difTimeSum += timeDif;
                            difAmmSum += ammDif;
                            difBothSum += bothDif;

                            row.createCell(4).setCellValue(timeDif);
                            row.createCell(5).setCellValue(ammDif);
                            row.createCell(6).setCellValue(bothDif);
                        }
                        //ポイントの合計
                        row.createCell(4).setCellValue(difTimeSum);
                        row.createCell(5).setCellValue(difAmmSum);
                        row.createCell(6).setCellValue(difBothSum);

                        //ポイントの集計
                        timePointList[criterion].add(difTimeSum);
                        ammPointList[criterion].add(difAmmSum);
                        bothPointList[criterion].add(difBothSum);
                        break;
                    case 4:
                        criText = "_minMaxCLR";
                        sheet = wb.createSheet("summary" + criText);

                        rateNonReg = allCallLossRate.get(0);
                        rateTimeReg = allCallLossRate.get(1);
                        rateAmmReg = allCallLossRate.get(2);
                        rateBothReg = allCallLossRate.get(3);
                        row = sheet.createRow(0);

                        row.createCell(0).setCellValue("noRegulated");
                        row.createCell(1).setCellValue("timeReg");
                        row.createCell(2).setCellValue("ammReg");
                        row.createCell(3).setCellValue("bothReg");
                        row.createCell(4).setCellValue("timeDif");
                        row.createCell(5).setCellValue("ammDif");
                        row.createCell(6).setCellValue("bothDif");
                        difTimeSum = 0;
                        difAmmSum = 0;
                        difBothSum = 0;

                        //出力
                        row = sheet.createRow(1);
                        row.createCell(0).setCellValue(Utility.minMaxInArray(rateNonReg));
                        row.createCell(1).setCellValue(Utility.minMaxInArray(rateTimeReg));
                        row.createCell(2).setCellValue(Utility.minMaxInArray(rateAmmReg));
                        row.createCell(3).setCellValue(Utility.minMaxInArray(rateBothReg));

                        //差分の導出
                        timeDif = Utility.minMaxInArray(rateNonReg) - Utility.minMaxInArray(rateTimeReg);
                        ammDif = Utility.minMaxInArray(rateNonReg) - Utility.minMaxInArray(rateAmmReg);
                        bothDif = Utility.minMaxInArray(rateNonReg) - Utility.minMaxInArray(rateBothReg);

                        difTimeSum += timeDif;
                        difAmmSum += ammDif;
                        difBothSum += bothDif;

                        row.createCell(4).setCellValue(timeDif);
                        row.createCell(5).setCellValue(ammDif);
                        row.createCell(6).setCellValue(bothDif);

                        //ポイントの合計
                        row.createCell(4).setCellValue(difTimeSum);
                        row.createCell(5).setCellValue(difAmmSum);
                        row.createCell(6).setCellValue(difBothSum);

                        //ポイントの集計
                        timePointList[criterion].add(difTimeSum);
                        ammPointList[criterion].add(difAmmSum);
                        bothPointList[criterion].add(difBothSum);
                        break;
                }

            }

            //ビル破壊情報の収集
            if (brokenBldgId.size() <= loop) {
                Building[] list = bldgList;
                int idx = 0;

                //上の表記の二行あとから
                row = sheet.createRow(allCallLossRate.get(0).length + 2);
                row.createCell(idx++).setCellValue("破壊ビル");
                int brokenNum = 0;
                ArrayList<Integer> tmpIdList = new ArrayList<>();
                for (int i = 0; i < list.length; i++) {
                    if (list[i].isBroken()) {
                        row.createCell(idx++).setCellValue(list[i].getBname());
                        brokenNum++;
                        tmpIdList.add(list[i].getBid());
                    }
                }

                brokenBldgId.add(tmpIdList);
                brokenBldgNum.add(brokenNum);
            }

            //1シナリオの比較が終わったら初期化
            allCallLossRate.clear();
            allCallLoss.clear();
        }
        IOHelper.output(file, wb);
    }


    public void summaryOutput(File timedir, int mag, int[] brokenLink, String[] brokenBuilding, double ammount, int timeReg, int amReg, int limit) {
//        Building[] list = Network.bldgList;
        try {
            File file = new File(timedir + "/summary.txt");
            file.createNewFile();
            FileWriter filewriter = new FileWriter(file);
            filewriter.write("需要" + mag + "倍\n");
//            filewriter.write("破壊リンク：");
//            for (int i = 0; i < brokenLink.length; i++) {
//                filewriter.write(brokenLink[i]);
//                filewriter.write("\n");
//            }

//            filewriter.write("破壊ビル\n");
//            for (int i = 0; i < list.length; i++) {
//                if (list[i].makeBroken) {
//                    filewriter.write(list[i].bname);
//                    filewriter.write("\n");
//                }
//            }
            filewriter.write("破壊リンク容量" + ammount + "倍");
            String reg = "";
            if (timeReg == 1) {
                reg += "時間規制 ";
            }
            if (amReg == 1) {
                reg += "通信量規制";
            }
            filewriter.write("規制方針 : " + reg + "\n");
            filewriter.write("破壊ビル数limit = " + limit);

            filewriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void StandardOutput(int timeLength, File timedir, double[] arr, int loop, Network bldgs) {
        double capHis[][] = new double[102][timeLength];
        ArrayList<Link> links = bldgs.getAllLinkList();

        for (int i = 0; i < 102; i++) {
            Link link = links.get(i);
            for (int t = 0; t < timeLength; t++) {
                capHis[i][t] = link.getCapHis()[t];
            }
        }

        File file = new File(timedir + "/standardData.xls");
        Workbook wb = new HSSFWorkbook();
        wb = IOHelper.getWorkbook(file, wb);

        Sheet sheet = wb.createSheet("capHis_" + loop);
        Row row;
        row = sheet.createRow(0);
        for (int i = 0; i < 102; i++) {
            row.createCell(i + 1).setCellValue(i);
        }
        for (int t = 0; t < timeLength; t++) {
            row = sheet.createRow(t + 1);
            row.createCell(0).setCellValue(t);
            for (int i = 0; i < 102; i++) {
                row.createCell(i + 1).setCellValue(capHis[i][t]);
            }
        }
        ;
        sheet = wb.createSheet("lossRate_" + loop);
        for (int t = 0; t < timeLength; t++) {
            sheet.createRow(t).createCell(0).setCellValue(arr[t]);
        }
        IOHelper.output(file, wb);
    }


    //リンクを準に破壊していった場合の結果をアウトプットするもの
    public void BreakLinkInOrderOutput(int loopNum, double[] array, File folder, int criterion) {
        //ファイルの作成
        String cri = "";
        String sheetName = "";
        switch (criterion) {
            case 0:
                cri = "_maxCLR";
                sheetName = "maxCLR";
                break;
            case 1:
                cri = "_aveCLR";
                sheetName = "aveCLR";
                break;
            case 2:
                cri = "_sumCLR";
                sheetName = "sumCLR";
                break;
            case 3:
                cri = "_sumCL";
                sheetName = "sumCL";
                break;
            case 4:
                cri = "_minMaxCLR";
                sheetName = "minMaxCLR";
                break;
        }
        String fileName = folder + "/BreakLinkInOrde" + cri + ".xls";
        File file = new File(fileName);
        Workbook wb = new HSSFWorkbook();
        wb = IOHelper.getWorkbook(file, wb);

        //sheetの作成
        Sheet sheet;
        sheet = wb.createSheet(sheetName);
        for (int i = 0; i < loopNum; i++) {
            sheet.createRow(i).createCell(0).setCellValue(array[i]);
        }
        IOHelper.output(file, wb);
    }

    /**
     *
     * @param folder
     * @param criNum
     * @param bldgs
     * @param brokenBldgLimit
     */
    public synchronized void regulationPointOutput(File folder, int criNum, Network bldgs, int brokenBldgLimit) {
        //ファイルの作成
        String fileName = folder + "/regulationPointOutput.xls";
        File file = new File(fileName);
        Workbook wb = new HSSFWorkbook();
        wb = IOHelper.getWorkbook(file, wb);

        //sheetの作成
        Sheet s = wb.createSheet("init");
        Row r;
        Building[] bList = bldgs.getBldgList();
        int[] brokenBldgCnt = new int[103];

        for (int criterion = 0; criterion < criNum; criterion++) {
            String cri = "";
            switch (criterion) {
                case 0:
                    cri = "_maxCLR";
                    break;
                case 1:
                    cri = "_aveCLR";
                    break;
                case 2:
                    cri = "_sumCLR";
                    break;
                case 3:
                    cri = "_sumCL";
                    break;
                case 4:
                    cri = "_minMaxCLR";
                    break;
            }
            s = wb.createSheet("summary" + cri);

            r = s.createRow(0);
            r.createCell(0).setCellValue("");
            r.createCell(1).setCellValue("timePoint");
            r.createCell(2).setCellValue("ammPoint");
            r.createCell(3).setCellValue("bothPoint");
            r.createCell(4).setCellValue("brokenBldgNum");

            //評価値の出力
            for (int i = 0; i < timePointList[criterion].size(); i++) {
                r = s.createRow(i + 1);
                r.createCell(0).setCellValue(i);
                r.createCell(1).setCellValue(timePointList[criterion].get(i));
                r.createCell(2).setCellValue(ammPointList[criterion].get(i));
                r.createCell(3).setCellValue(bothPointList[criterion].get(i));
                r.createCell(4).setCellValue(brokenBldgNum.get(i));

                ArrayList<Integer> list = brokenBldgId.get(i);
                for (int k = 0; k < list.size(); k++) {
                    Building bldg = bList[list.get(k)];
                    r.createCell(5 + k).setCellValue(bldg.getBname());
                    brokenBldgCnt[bldg.getBid()]++;
                }
            }
        }

        //スタートする位置
        int st = timePointList[0].size() + 1;

        r = s.createRow(st);
        r.createCell(0).setCellValue("ビル名");
        r.createCell(1).setCellValue("壊れた回数");

        for (int i = 0; i < brokenBldgCnt.length; i++) {
            r = s.createRow(st + i + 1);
            r.createCell(0).setCellValue(bList[i].getBname());
            r.createCell(1).setCellValue(brokenBldgCnt[i]);
        }
        IOHelper.output(file, wb);

        //limit毎の記録
        ArrayList<Double> cpTimeList[] = new ArrayList[timePointList.length];
        ArrayList<Double> cpAmmList[] = new ArrayList[timePointList.length];
        ArrayList<Double> cpBothList[] = new ArrayList[timePointList.length];

        for (int i = 0; i < timePointList.length; i++) {
            cpTimeList[i] = new ArrayList(timePointList[i]);
            cpAmmList[i] = new ArrayList(ammPointList[i]);
            cpBothList[i] = new ArrayList(bothPointList[i]);
        }


        limitTimePointList.put(brokenBldgLimit, cpTimeList);
        limitAmmPointList.put(brokenBldgLimit, cpAmmList);
        limitBothPointList.put(brokenBldgLimit, cpBothList);

        //初期化
        allCallLossRate.clear();
        allCallLoss.clear();
        for (int i = 0; i < timePointList.length; i++) {
            timePointList[i].clear();
            ammPointList[i].clear();
            bothPointList[i].clear();
            brokenBldgNum.clear();
            brokenBldgId.clear();
        }
    }

    /**
     * 首都直下型地震シナリオ使用時におけるビル破壊数別の規制効果を、各種評価基準を使用してアウトプットするために使う
     * @param minBrokenBldgNum
     * @param maxBrokenBldgNum
     */
    public static void limitRegulationPoint(File dateDir,int minBrokenBldgNum, int maxBrokenBldgNum) {
        //ファイルの作成
        Calendar cl = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
        String time = sdf.format(cl.getTime());
        String fileName = getTimeDir(dateDir) + "pointOutputSummary_" + time + ".xls";
        File file = new File(fileName);
        Workbook wb = new HSSFWorkbook();
        wb = IOHelper.getWorkbook(file, wb);

        //シートの作成->criterion別
        Sheet sheets[] = new Sheet[5];
        sheets[0] = wb.createSheet("maxCLR");
        sheets[1] = wb.createSheet("aveCLR");
        sheets[2] = wb.createSheet("sumCLR");
        sheets[3] = wb.createSheet("sumCL");
        sheets[4] = wb.createSheet("minMaxCLR");

        //シートのカラム名の追加
        for (int i = 0; i < sheets.length; i++) {
            Row r = sheets[i].createRow(0);
            r.createCell(0).setCellValue("timePoint");
            r.createCell(1).setCellValue("ammPoint");
            r.createCell(2).setCellValue("bothPoint");
            r.createCell(3).setCellValue("brokenBuildingNum");
        }


        //limitとciriterionのサイズ取得
        int criterionNum = limitBothPointList.get(minBrokenBldgNum).length;
        int numPerBrokenBldg = limitAmmPointList.get(minBrokenBldgNum)[0].size();

        for (int l = minBrokenBldgNum; l <= maxBrokenBldgNum; l++) {
            //limit別のデータ取得
            int startRow = 1 + numPerBrokenBldg * l;
            for (int c = 0; c < criterionNum; c++) {
                Sheet s = sheets[c];
                ArrayList<Double> timeList = limitTimePointList.get(l)[c];
                ArrayList<Double> ammList = limitAmmPointList.get(l)[c];
                ArrayList<Double> bothList = limitBothPointList.get(l)[c];

                for (int i = 0; i < timeList.size(); i++) {
                    Row r = s.createRow(startRow + i);
                    r.createCell(0).setCellValue(timeList.get(i));
                    r.createCell(1).setCellValue(ammList.get(i));
                    r.createCell(2).setCellValue(bothList.get(i));
                    r.createCell(3).setCellValue(l);
                }
            }
        }
        output(file, wb);
    }

    /**
     * output用のDateディレクトリを作成する
     * ex) $outputDir/2017_0210/
     * プロジェクトのアウトプットディクレトリ下の日付ディレクトリ(2017_0210)
     * 既にディレクトリが存在する場合は作成しない
     *
     * @return
     */
    public static File getDateDir() {
        /**出力関連**/
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd");
        String date = sdf.format(c.getTime());

        // date階層のdirectoryの作成（当日に既に実行している場合はエスケープ）
        String folder = outputRootPath + date;// root/yyyy_MMdd/hh_mm_ss/
        File datedir = new File(folder);

        if (!datedir.exists()) {
            datedir.mkdir();
        }

        return datedir;
    }

    /**
     * output用のTimeディレクトリを作成する
     * ex) $outputDir/2017_0210/11_52_43
     * プロジェクトのアウトプットディクレトリ下の日付ディレクトリ(2017_0210)
     * の下に(11_54_43) 11h52m43sのようなディレクトリを作成してFileオブジェクトを返す
     * ディレクトリが存在する場合は作成しない
     *
     * @return
     */
    public static File getTimeDir() {
        // ファイル出力
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
        String time = sdf.format(c.getTime());

        File timedir = new File(getDateDir() + "/" + time + "/");
        if (!timedir.exists()) {
            timedir.mkdir();
        }
        return timedir;
    }

    /**
     * dateDirを指定してtimeDirを作成
     * @param dateDir
     * @return
     */
    public static File getTimeDir(File dateDir) {
        // ファイル出力
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
        String time = sdf.format(c.getTime());

        File timedir = new File(dateDir + "/" + time + "/");
        if (!timedir.exists()) {
            timedir.mkdir();
        }
        return timedir;
    }

    /**
     * pathに存在するエクセルファイルをXSSFWorkbook型のオブジェクトに読み込む
     *
     * @param path 絶対パス
     * @return パスのエクセルファイルが存在しなければnull
     */
    public static XSSFWorkbook importExcelToWorkBook(String path) {
        XSSFWorkbook book = null;
        try {
            FileInputStream f = new FileInputStream(path);
            book = new XSSFWorkbook(f);
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return book;
    }

    /**
     * Double型の配列をExcelに一行ずつ出力する
     *
     * @param array     出力するDouble型配列
     * @param path      Excelを出力するパス
     * @param sheetName シートの名前
     */
    public static void doubleArrayToExcel(double[] array, String path, String sheetName) {
        File file = new File(path);
        Workbook wb = new HSSFWorkbook();
        wb = getWorkbook(file, wb);

        Sheet sheet;
        sheet = wb.createSheet(sheetName);
        for (int i = 0; i < array.length; i++) {
            sheet.createRow(i).createCell(0).setCellValue(array[i]);
        }

        output(file, wb);
    }
}
