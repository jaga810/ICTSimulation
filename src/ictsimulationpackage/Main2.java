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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class Main2 {
    /**連続でシミュレーションを回すときに使う**/
    public static void main(String args[]) {
        /**各種設定**/
        //outputするルートとなるフォルダ
        final String outputRootFolder = "/Users/jaga/Documents/domain_project/output/";

        /**出力関連**/
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd");
        String date = sdf.format(c.getTime());
        sdf = new SimpleDateFormat("HH_mm_ss");


        // date階層のdirectoryの作成（当日に既に実行している場合はエスケープ）
        String folder = outputRootFolder + "/" + date;// root/yyyy_MMdd/hh_mm_ss/
        File datedir = new File(folder);
        if (!datedir.exists()) {
            datedir.mkdir();
        }


        /**シミュレーション**/
        BuildingList bldgs = new BuildingList(102);
        for(int i = 13 ; i < 16;i++) {
            System.out.println("brokenBldgLimit : " + i);
            run(i, bldgs, datedir);
        }

        /**全体のサマリーの出力**/
        Output.limitRegulationPoint(datedir);
    }
    public static void run(final int brokenBldglimit, final BuildingList bldgs, final File datedir) {

        /****各種設定****/

        // ループの回数
        final int loopNum = 20;

        //東京湾直下型地震シナリオによる破壊の有無 0:mu 1:ari
        final int scenario = 1;

        //直下型シナリオにおいて、ビルの破壊数を制限するか->0:制限しない
//        final int brokenBldglimit = 2;

        //評価基準 0:呼損率の最大値 1:呼損率の平均 2:時間積分した呼損率 3:全損失呼の数 4:呼損率のMax-Min 5:全て
        final int criterion = 5;

        //評価基準全体の呼数
        final int criNum = 5;

        // 破壊リンクの設定 idで設定
        final int brokenLink[] = {};

        // 破壊ビルの設定
        final String brokenBuilding[] = {};

        //破壊時に元の容量の何倍に設定するか
        final double ammount = 0;

        //需要を元の呼の発生量の何倍に設定するか
        int mag = 1;

        //output 0:stanndard 1:areaDevidedKosu 2:magDevidedKosu 3:regulationDevided 4:BreakInorder 5:summary 6:pointSum
        int output[] = {2};

        //規制の書け方 0:規制なし/手動規制 1: 4角規制方針の比較
        int regMethod = 0;

        /*** 初期化関連 ***/

        //４つの規制方針の比較につかう non -> time -> amm -> bothの順番 [][0] = time [][1] = amm
        final int[][] method = {{0, 0}, {1, 0}, {0, 1}, {1, 1}};

        // ループ毎の最大呼損率
        double[] worstCallLossRate = new double[loopNum];

        //ループ毎の平均呼損率
        double[] aveCallLossRate = new double[loopNum];

        //ループ毎の呼損率の合計
        double[] sumCallLossRate = new double[loopNum];

        //ループ毎の呼損率の合計
        double[] sumCallLoss = new double[loopNum];

        //ループ毎の呼損率のmax-min
        double[] minMaxCallLossRate = new double[loopNum];

        // 計算時間の算出
        double calcTime = System.nanoTime();

        // 24時間
        final int hour = 24;

        // 一時間=60分
        final int timeLength = hour * 60;

        // 1リンク設計回線数
        final int kaisensu = 11200 * 2;
        final int outKaisensu = 16300 * 2; // 区内中継リンク
        final int exKaisensu = 33200 * 2;// 区外中継リンク

        //outputするルートとなるフォルダ
        final String outgputRootFolder = "/Users/jaga/Documents/domain_project/output/";

        // ファイル出力
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
        String time = sdf.format(c.getTime());

        File timedir = new File(datedir + "/" + time + "/");
        if (!timedir.exists()) {
            timedir.mkdir();
        }

        // Callの初期化
        new Call(timeLength);

        // ビルディングリストオブジェクトの作成
//        BuildingList bldgs = new BuildingList(102);

        // 全てのリンク情報の取得
        ArrayList<Link> allLinks = bldgs.allLinkList;

        // 全てのビル情報の取得
        Building[] bldgList = bldgs.bldgList;

        //シナリオの読み込み
        Building.getScale();
        loop:
        for (int loop = 0; loop < loopNum; loop++) {
            /***通信規制の設定***/
            // 引数の番号で通話時間規制方法が変わる 0:規制なし 1:一分間に限定
            int timeRegulation = 0;
            //通信量規制 0:規制なし, 1:規制有り
            int ammountRegulation = 0;
            switch (regMethod) {
                case 0:
                    HoldingTime.method = 0;
                    ammountRegulation = 0;
                    break;
                case 1:
                    timeRegulation = method[loop % 4][0];
                    HoldingTime.method = timeRegulation;
                    ammountRegulation = method[loop % 4][1];
                    break;
            }

            /***initialization***/
            // Callの持続時間の方針: 0:制限なし 1:１分まで
            Call.reset();

            // リンクとビルのbroken状態を回復する(シナリオでないときと、ループが木数回目の時)
            if (scenario == 0 || loop % 4 == 0) {
                bldgs.resetBroken();
            }

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

            // max.get(t) = 時刻tにおいて発生した呼の中で最も終了時刻の遅いものの値
            ArrayList<Integer> max = new ArrayList<>();

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

            //現存する呼のリスト
            ArrayList<Call> callList[] = new ArrayList[timeLength];
            for (int i = 0; i < callList.length; i++) {
                callList[i] = new ArrayList<>();
            }

            // endCallList[t] = 時刻tに終了する呼のリスト
            ArrayList<Call> endCallList[] = new ArrayList[timeLength + 100];
            for (int i = 0; i < endCallList.length; i++) {
                endCallList[i] = new ArrayList<>();
            }

            /***施設の破壊関連***/
            //地震による影響で壊れるシナリオで使用
            if (scenario == 1 && loop % 4 == 0) {
                //破壊
                Building.brokenByQuake(brokenBldglimit);
            }

            //ビル・リンクの破壊
            if (brokenLink.length > 0) {
                for (int i = 0; i < brokenLink.length; i++) {
                    bldgs.findLink(brokenLink[i]).broken(ammount);
                }

            }
            if (brokenBuilding.length > 0) {
                for (String bname : brokenBuilding) {
                    bldgs.findBldg(bname).broken();
                }
            }

            if (contain(output, 4)) {
                // 区内リンクを順に破壊する用

                bldgs.findLink(loop).broken(ammount);

                // 区内中継リンクを破壊する
                // bldgs.exLinkList.get(loop).broken(ammount);

                // 区外リンクを破壊する
                // bldgs.outLink.broken(ammount);
            }


            System.out.print("loop : " + (loop + 1) + " , mag : " + mag + " ");
            double loopStartTime = System.nanoTime();
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
                int M = t;// 此度発生した呼の終了時刻の最遅値

                // 呼数の計算。及び呼候補の生成
                ArrayList<CandiCall> candiCallList = new ArrayList();
                int occur;
                int limit;
                CandiCall candidate;
                for (Building start : bldgList) {
                    for (Building dest : bldgList) {
                        if (start.bname.equals(dest.bname) && dest.bname.equals("区外")) {
                            continue;
                        }
                        occur = start.occurence(t, dest, mag);
                        //区外発信呼の切断シミュレーション用
                        if (ammountRegulation == 1 && start.bname.equals("区外")) {
                            limit = start.occurence(t, dest, 1) * 2;
                            if (BuildingList.outLink.capacity + occur > limit) {
                                //現在県外からかかってきている呼 + 今回生じる可能性のある呼数　> 平常時の二倍　ならば、超過分を削除
                                occur = limit - BuildingList.outLink.capacity;
                            }
                        }
                        for (int i = 0; i < occur; i++) {
                            candidate = new CandiCall(start, dest, t);
                            candiCallList.add(candidate);
                            callOccur[t]++;
                        }
                    }
                }

                // 候補呼リストのシャッフル
                Collections.shuffle(candiCallList);

                // 候補呼リストに従って呼の生成。及び接続可能性評価
                for (CandiCall can : candiCallList) {
                    Call call = can.generateCall();
                    if (call.success) {
                        callList[t].add(call);
                        callExist[t]++;
                        // 終了時刻の最遅値の更新
                        if (M < call.EndTime) {
                            M = call.EndTime;
                        }
                    } else {
                        callLoss[t]++;
                    }
                }

                max.add(M);

                // 時刻tに終了する呼の消去
                for (Call call : Call.limitList[t]) {
                    call.delete();
                    callExist[t]--;
                    callDeleted[t]++;
                }
                Call.limitList[t].clear();

                // 有る時間帯tにおける生起呼の最長終了時間が訪れたらそのリストを破壊する
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

                if (t < timeLength && t % 100 == 0) {
                    System.out.print(".");
                }
                //モニタ
//                monitor("----------------loop:" + (loop + 1) + "---time:" + t + "----mag:" + mag + "------------------", callExist[t], callOccur[t], callLoss[t], callLossRate[t], callDeleted[t], avgHoldTime[t], ntime);
            }


            switch (criterion) {
                case 0:
                    // 呼損率の最大値格納
                    worstCallLossRate[loop] = Output.maxInArray(callLossRate);
                    break;
                case 1:
                    //平均呼損率の格納
                    aveCallLossRate[loop] = Output.aveInArray(callLossRate);
                    break;
                case 2:
                    //呼損率の合計の格納
                    sumCallLossRate[loop] = Output.sumInArray(callLossRate);
                    break;
                case 3:
                    //全損失呼の数の格納
                    sumCallLoss[loop] = Output.sumInArray(callLoss);
                    break;
                case 4:
                    //呼損率のmin-max
                    minMaxCallLossRate[loop] = Output.minMaxInArray(callLossRate);
                    break;
                case 5:
                    //全て記録
                    worstCallLossRate[loop] = Output.maxInArray(callLossRate);
                    aveCallLossRate[loop] = Output.aveInArray(callLossRate);
                    sumCallLossRate[loop] = Output.sumInArray(callLossRate);
                    sumCallLoss[loop] = Output.sumInArray(callLoss);
                    minMaxCallLossRate[loop] = Output.minMaxInArray(callLossRate);
                    break;
            }


//            System.out.println("-----------------OUTPUT start-------------------");
            // summary
            if (contain(output, 5)) {
                Output.summaryOutput(timedir, mag, brokenLink, brokenBuilding, ammount, timeRegulation, ammountRegulation, brokenBldglimit);
            }

            // standard outputを行う
            if (contain(output, 0)) {
                Output.StandardOutput(timeLength, timedir, callLossRate, loop);
            }


            //通信規制の方針を比較する
            if (contain(output, 3)) {
                if (loop == 0) {
                    Output.regulaitonMethodDevidedInitialize(criNum);
                }

                Output.regulationMethodDevided(hour, timeLength, timedir, loop, mag, callExist,
                        callOccur, callLoss, callLossRate, callDeleted, avgHoldTime, loopNum, timeRegulation, ammountRegulation, criNum);


            }


            //通信制限欠けた場合と欠けない場合を連続でデータ取った後のポイントのデータ
            if (contain(output, 6) && loop == loopNum - 1 && criterion == 5) {
                Output.regulationPointOutput(timedir, criNum);

            } else if (contain(output, 6) && loop == loopNum - 1) {
                Output.regulationPointOutput(timedir, 0);
            }

            //呼量の倍率を変えた場合＊破壊非破壊のパターン別データ
            if (contain(output, 2)) {
                Output.magDevidedOutput(hour, timeLength, timedir, loop, mag, callExist, callOccur, callLoss,
                        callLossRate, callDeleted, avgHoldTime);
            }

            //発生area別に呼数を出力する
            if (contain(output, 1)) {
                Output.areaDevidedKosu(timedir, loop);
            }

//            System.out.println("-----------------OUTPUT finished-------------------");
            double loopDur = (System.nanoTime() - loopStartTime) * 1.0e-9;
            System.out.println(loopDur);
        }


        // //リンクを順に破壊するときのためのExcel出力
        if (contain(output, 4)) {
            double arr[] = null;
            switch (criterion) {
                case 0:
                    arr = worstCallLossRate;
                    break;
                case 1:
                    arr = aveCallLossRate;
                    break;
                case 2:
                    arr = sumCallLossRate;
                    break;
                case 3:
                    arr = sumCallLoss;
                    break;
                case 4:
                    arr = minMaxCallLossRate;
            }
            Output.BreakLinkInOrderOutput(loopNum, arr, timedir, criterion);
        }


        calcTime = System.nanoTime() - calcTime;
        System.out.println("計算時間：" + (calcTime * (Math.pow(10, -9))) + "s");
    }

    private static void monitor(String x, int i, int i1, int callLos, double v, int i2, double v1, double ntime) {
        System.out.println(x);
        System.out.println("生起：" + i1);
        System.out.println("損失：" + callLos);
        System.out.println("呼損率：" + v);
        System.out.println("存在：" + i);
        System.out.println("deleted calls ..." + i2);
        // System.out.println("sumHoldTIme:" + Call.sumHoldTime[t]);
        // System.out.println("call occur:" + callOccur[t]);
        System.out.println("average Holding Time:" + v1);
        System.out.println("passed time:" + ntime + "ns");
    }

    private static boolean contain(int[] arr, int val) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == val) {
                return true;
            }
        }
        return false;
    }

}
