package ictsimulationpackage;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Main2 {
    public static void main(String argv[]) {
        // public static void practice(){
        // ループの回数
        int roopNum = 2;

        // ループ毎の最大呼損率
        double[] worstCallLossRate = new double[roopNum];
        // 計算時間の算出
        double calcTime;
        calcTime = System.nanoTime();
        // 24時間
        int hour = 24;
        // 一時間=60分
        int timeLength = hour * 60;

        // ファイル出力
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd");
        String date = sdf.format(c.getTime());
        sdf = new SimpleDateFormat("hh_mm_ss");
        String time = sdf.format(c.getTime());
        // 出力ファイル
        int filenum = 0;
        // 出力フォルダ
        String rootFolder = "/Users/jaga/Documents/domain_project/output/";
        String folder = rootFolder + "/" + date;// root/yyyy_MMdd/hh_mm_ss/
        // date階層のdirectoryの作成（当日に既に実行している場合はエスケープ）
        File datedir = new File(folder);
        if (!datedir.exists()) {
            datedir.mkdir();
        }
        File timedir = new File(folder + "/" + time + "/");
        if (!timedir.exists()) {
            timedir.mkdir();
        }

        // 引数の番号で通話時間規制方法が変わる 0:規制なし 1:一分間に限定
        HoldingTime iniHold = new HoldingTime(0);

        // Callの初期化
        Call initialize = new Call(timeLength);

        // ビルディングリストオブジェクトの作成
        BuildingList bldgs = new BuildingList(102);

        // 全てのリンク情報の取得
        ArrayList<Link> allLinks = bldgs.allLinkList;

        // 全てのビル情報の取得
        Building[] bldgList = bldgs.bldgList;

        // 出発ビル情報の取得
        Building[] startBldgList = bldgs.startBldgList;

        // roopとは？
        roop:
        for (int roop = 1; roop < roopNum; roop++) {
            // bai: 需要を5倍、10倍にする
            for (int bai = 1; bai < 2; bai++) {
                // Callの持続時間の方針: 0:制限なし 1:１分まで
                Call.reset(timeLength);
                // リンクとビルのbroken状態を回復する
                bldgs.resetBroken();

                int bai2 = 5;// 需要の倍数の基数？

                int mag = bai * bai2; // 需要が何倍か

                // CallExist[t] = 時刻tに存在する呼の数
                int callExist[] = new int[timeLength];

                // CallOccur[t] = 時刻tに生起した呼の数
                int callOccur[] = new int[timeLength];

                // CallLoss[t] = 時刻tに損失した呼の数
                int callLoss[] = new int[timeLength];

                // CallLossRate[t] = 時刻tの呼損率
                double callLossRate[] = new double[timeLength];

                // 削除された呼の数
                int callDeleted[] = new int[timeLength];

                // average holding time
                double[] avgHoldTime = new double[timeLength];

                // capHis[t][i] = 時刻tのid = iのリンクのキャパシティの充足度
                double[][] capHis = new double[timeLength][102];

                // 1リンク設計回線数
                // int kaisensu = (int) (221486 * 0.5); // 区内
                // int outKaisensu = 78276; // 区外
                // int kaisensu = 14200 * 2; // 区内
                int kaisensu = 10000 * 2;
                int outKaisensu = 14200 * 2; // 区内中継リンク
                int exKaisensu = 27100 * 2;// 区外中継リンク

                // 区内リンクの回線数設定
                for (Link ln : bldgs.linkList) {
                    ln.iniCap(timeLength, kaisensu);
                }
                // 区内中継リンクの回線数設定
                for (Link ln : bldgs.exLinkList) {
                    ln.iniCap(timeLength, outKaisensu);
                }
                // 区外中継リンクの回線数設定
                bldgs.outLink.iniCap(timeLength, exKaisensu);

                // 破壊リンクの設定 idで設定
                int brokenLink[] = {51};
                // 破壊ビルの設定
                String brokenBuilding[] = {"馬込", ""};

                double ammount = 0; // 元の容量の何倍か

                if (roop > 0) {

                    for (int i = 0; i < brokenLink.length; i++) {
                        bldgs.findLink(brokenLink[i]).broken(ammount);
                    }
                    // for (String bname : brokenBuilding) {
                    // bldgs.findBldg(bname).broken();
                    // }
                }
                // 区内リンクを順に破壊する用
                // bldgs.findLink(roop).broken(ammount);

                // 区内中継リンクを破壊する
                // bldgs.exLinkList.get(roop).broken(ammount);

                // 区外リンクを破壊する
                // bldgs.outLink.broken(ammount);

                // listofcalllist[t] = 時刻tにおいて生起した呼のリスト
                ArrayList<Call> callList[] = new ArrayList[timeLength];
                for (int i = 0; i < callList.length; i++) {
                    // 初期化
                    callList[i] = new ArrayList<Call>();
                }
                // max.get(t) = 時刻tにおいて発生した呼の中で最も終了時刻の遅いものの値
                ArrayList<Integer> max = new ArrayList<Integer>();

                // callETlist[t] = 時刻tに終了する呼のリスト
                @SuppressWarnings("unchecked")
                ArrayList<Call> callETlist[] = new ArrayList[timeLength + 100];// なんで+100?
                for (int i = 0; i < callETlist.length; i++) {
                    // 初期化
                    callETlist[i] = new ArrayList<Call>();
                }

                // 時間ループの開始
                for (int t = 0; t < timeLength; t++) {
                    // capacityと現存呼は保存される
                    if (t > 0) {
                        for (Link ln : allLinks) {
                            ln.saveCap(t);
                        }
                        callExist[t] = callExist[t - 1];
                    }

                    // この時間における呼のリスト
                    ArrayList<Call> calllist = new ArrayList<Call>();
                    // 呼の生成.
                    // System.out.println(t + ":generating candidate of
                    // calls...");
                    int M = t;// 此度発生した呼の終了時刻の最遅値
                    int candiNum = 0;// 標準出力用

                    // 呼数の計算。及び呼候補の生成
                    ArrayList<CandiCall> candiCallList = new ArrayList();
                    int occur;
                    CandiCall candidate;
                    for (Building start : bldgList) {
                        for (Building dest : bldgList) {
                            if (start.bname == dest.bname && dest.bname == "区外") {
                                continue;
                            }
                            occur = start.occurence(t, dest, mag);
                            for (int i = 0; i < occur; i++) {
                                candidate = new CandiCall(start, dest, t);
                                candiCallList.add(candidate);
                                candiNum++;
                                callOccur[t]++;
                                // System.out.println(t + ":generating candidate
                                // of calls..." + candiNum);
                            }
                        }
                    }
                    // 候補呼リストのシャッフル
                    Collections.shuffle(candiCallList);

                    // 候補呼リストに従って呼の生成。及び接続可能性評価
                    int callNum = 0;
                    // System.out.println(t + ":generating calls...");
                    for (CandiCall can : candiCallList) {
                        Call call = can.generateCall();
                        if (call.success) {
                            callList[t].add(call);
                            callExist[t]++;
                            // System.out.println(t + ":generating calls..." +
                            // callOccur[t]);
                            // 終了時刻の最遅値の更新
                            if (M < call.EndTime) {
                                M = call.EndTime;
                            }
                        } else {
                            // System.out.println("lostcall:" + call.start.bname
                            // + " -> " + call.dest.bname);
                            callLoss[t]++;
                            call = null;
                            // System.out.println("roop:" + roop);
                            // System.out.println(1/0);
                            // break roop;
                        }
                    }

                    max.add(M);

                    // 回線のhold割合を導出
                    for (int i = 0; i < 102; i++) {
                        capHis[t][i] = bldgs.findLink(i).capHis();
                    }

                    // 時刻tに終了する呼の消去
                    // System.out.println("deleteing calls ...");
                    int deleteNum = 0;
                    for (Call call : Call.limitList[t]) {
                        call.delete();
                        callExist[t]--;
                        deleteNum++;
                        callDeleted[t]++;
                        // System.out.println("deleteing calls ..." +
                        // deleteNum);
                    }
                    Call.limitList[t].clear();
                    // System.out.println("deletingListNum:" +
                    // callETlist[t].size());

                    // 有る時間帯iにおける生起呼の最長終了時間が訪れたらそのリストを破壊する
                    for (int i = 0; i < max.size(); i++) {
                        // System.out.println(max.get(i));
                        if (max.get(i) == t) {// 0時台以外から始める場合、+60*(開始時間-1)
                            if (callList[i] != null) {
                                callList[i].clear();
                            }
                        }
                    }

                    // 呼損率の算出
                    callLossRate[t] = ((double) callLoss[t] * 100) / (double) callOccur[t];
                    avgHoldTime[t] = Double.valueOf(Call.sumHoldTime[t]) / Double.valueOf(callOccur[t]);
                    double ntime = System.nanoTime() - calcTime;

                    System.out.println("----------------roop:" + (roop) + "---time:" + t + "----mag:" + mag
                            + "------------------");
                    System.out.println("生起：" + callOccur[t]);
                    System.out.println("損失：" + callLoss[t]);
                    System.out.println("呼損率：" + callLossRate[t]);
                    System.out.println("存在：" + callExist[t]);
                    System.out.println("deleted calls ..." + callDeleted[t]);
                    // System.out.println("sumHoldTIme:" + Call.sumHoldTime[t]);
                    // System.out.println("call occur:" + callOccur[t]);
                    System.out.println("average Holding Time:" + avgHoldTime[t]);
                    System.out.println("passed time:" + ntime + "ns");
                    // 呼損率が100%をこえることはない
                    if (callLossRate[t] > 100) {
                        int aa = 1 / 0;
                    }
                    // //回線数設計用
                    // if(callLossRate[t] > 0){
                    // int aa = 1/0;
                    // }

                }
                // 呼損率の最大値格納
                worstCallLossRate[roop] = Output.maxInArray(callLossRate);

                System.out.println("-----------------OUTPUT start-------------------");
                // summary
//                summaryOutput(timedir, mag, brokenLink, brokenBuilding, ammount);

                // エクセルデータ作成
                Output.StandardOutput(timeLength, timedir, callLossRate, capHis);

                //呼量の倍率を変えた場合＊破壊非破壊のパターン別データ
                Output.magDevidedOutput(hour, timeLength, timedir, roop, mag, callExist, callOccur, callLoss, callLossRate, callDeleted, avgHoldTime);


                System.out.println("-----------------OUTPUT finished-------------------");
            }
        }

        // //リンクを順に破壊するときのためのExcel出力
        Output.BreakLinkInOrderOutput(roopNum, date, worstCallLossRate, timedir);


        //発生area別に呼数を出力する
        Output.areaDevidedKosu(timedir);

        calcTime = System.nanoTime() - calcTime;
        System.out.println("計算時間：" + (calcTime * (Math.pow(10, -9))) + "s");
        System.out.println("計算時間：" + calcTime + "ns");
    }



}
