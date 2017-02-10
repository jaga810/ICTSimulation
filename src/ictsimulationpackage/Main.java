package ictsimulationpackage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main implements Runnable {

    private int brokenBldglimit;
    private File dateDir;
    private int bldgNum;

    public static void main(String args[]) {
        /**各種設定変数**/
        final int bldgNum = 102; //使用する中継ビルの個数（県外除く）
        final int minBrokenBldgLimit = 1;
        final int maxBrokenBldgLimit = 10;
        final int maxThreadsNum = 3;//同時に走らせるスレッドの最大数（2GBメモリ振って4,5くらいが限界)

        /**出力関連**/
        File dateDir = IOHelper.getDateDir();

        /**シミュレーション**/
        double sTime = System.nanoTime();

        simulation(bldgNum, minBrokenBldgLimit, maxBrokenBldgLimit, maxThreadsNum, dateDir);

        System.out.println((System.nanoTime() - sTime) / 1.0e9 + "s");

        /** 全体のサマリーの出力 **/
        IOHelper.limitRegulationPoint(dateDir, minBrokenBldgLimit, maxBrokenBldgLimit);
    }

    /**
     * スレッドごとの変数を初期化
     *
     * @param brokenBldglimit シナリオ使用時におけるビル破壊数
     * @param dateDir         シミュレーションで生成したデータを突っ込むディレクトリの作成
     * @param bldgNum         中継ビルの数
     */
    Main(final int brokenBldglimit, final File dateDir, final int bldgNum) {
        this.bldgNum = bldgNum;
        this.brokenBldglimit = brokenBldglimit;
        this.dateDir = dateDir;
    }

    public void run() {
        /****各種設定****/

        // ループの回数
        final int loopNum = 4 * 1;

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
        final int mag = 5;

        //IOHelper 0:stanndard 1:areaDevidedKosu 2:magDevidedKosu 3:regulationDevided 4:BreakInorder 5:summary 6:pointSum
        final int outputMethod[] = {3, 6};

        //規制の書け方 0:規制なし/手動規制 1: 4角規制方針の比較
        final int regMethod = 1;

        /*** 初期化関連 ***/

        //４つの規制方針の比較につかう non -> time -> amm -> bothの順番 [][0] = time [][1] = amm
        final int[][] regulationMethod = {{0, 0}, {1, 0}, {0, 1}, {1, 1}};

        // 時間ループ毎の最大呼損率
        double[] worstcallLossRate = new double[loopNum];

        //ループ毎の平均呼損率
        double[] avecallLossRate = new double[loopNum];

        //ループ毎の呼損率の合計
        double[] sumcallLossRate = new double[loopNum];

        //ループ毎の呼損率の合計
        double[] sumqtyLostCalls = new double[loopNum];

        //ループ毎の呼損率のmax-min
        double[] minMaxcallLossRate = new double[loopNum];

        // 計算時間の算出
        double calcTime = System.nanoTime();

        //何時間分のシミュレーションを行うか
        final int hour = 24;

        //タイムステップ ( hour * 60分)
        final int timeLength = hour * 60;

        // 各リンク設計回線数
        final int localLinkCapacity = 11200 * 2;
        final int kunaiLinkCapacity = 16300 * 2;
        final int kugaiLinkCapacity = 33200 * 2;

        //このスレッドで使うディレクトリの作成
        File timeDir = getTimeDir();

        /** 初期化　**/
        Network bldgs = new Network();
        CallList callList = new CallList(timeLength, bldgs);
        IOHelper IOHelper = new IOHelper();
        ArrayList<Link> allLinks = bldgs.getAllLinkList();
        Building[] bldgList = bldgs.getBldgList();
        bldgs.getScale();//シナリオの震度分布の読み込み

        loop:
        for (int loop = 0; loop < loopNum; loop++) {
            /***通信規制の設定***/
            int timeRegulation = 0; //通信時間規制 0:規制なし 1:一分間に限定
            int ammountRegulation = 0; //通信量規制   0:規制なし, 1:規制有り
            switch (regMethod) {
                case 0: //ループによって規制方針を切り替えない
                    timeRegulation = 0;
                    ammountRegulation = 0;
                    break;
                case 1: //ループによって規制方針を切り替える
                    timeRegulation = regulationMethod[loop % 4][0];
                    ammountRegulation = regulationMethod[loop % 4][1];
                    break;
            }

            /***initialization***/
            // Callの持続時間の方針: 0:制限なし 1:１分まで
            callList.init(timeRegulation);

            // リンクとビルのbroken状態を回復する(シナリオでないときと、シナリオでかつループが4の倍数のとき)
            if (scenario == 0 || loop % 4 == 0) {
                bldgs.resetBroken();
            }

            // qtyExistingCalls[t] = 時刻tに存在する呼の数 ;  qty = quantitiy(量・数）の意味で使う
            int qtyExistingCalls[] = new int[timeLength];

            // qtyOccurredCalls[t] = 時刻tに生起した呼の数
            int qtyOccurredCalls[] = new int[timeLength];

            // qtyLostCalls[t] = 時刻tに損失した呼の数
            int qtyLostCalls[] = new int[timeLength];

            // 削除された呼の数
            int qtyDeletedCalls[] = new int[timeLength];

            // callLossRate[t] = 時刻tの呼損率
            double callLossRate[] = new double[timeLength];

            // 保留時間の平均
            double[] avgHoldTime = new double[timeLength];

            // timeOfLastCall.get(t) = 時刻tにおいて発生した呼の中で最も終了時刻の遅いものの値
            ArrayList<Integer> timeOfLastCall = new ArrayList<>();

            //リンクの回線数を初期化
            initializeLinks(timeLength, localLinkCapacity, kunaiLinkCapacity, kugaiLinkCapacity, bldgs);


            //existingCallList[t] = 時刻tに現存する呼のリスト
            ArrayList<Call> existingCallList[] = new ArrayList[timeLength];
            initArrayList(existingCallList);

            // endCallList[t] = 時刻tに終了する呼のリスト
            ArrayList<Call> endCallList[] = new ArrayList[timeLength + 100];
            initArrayList(endCallList);

            //シナリオ|手動による施設の破壊を行う
            breakNetwork(scenario, brokenLink, brokenBuilding, ammount, outputMethod, bldgs, loop);

            System.out.println("limit : " + brokenBldglimit + " loop : " + (loop + 1) + " , mag : " + mag + " ");
            double loopStartTime = System.nanoTime();

            // 時間ループの開始（シミュレーション一回分)
            for (int t = 0; t < timeLength; t++) {
                int latestTimeOfCalls = t;// 此度発生した呼の終了時刻の最遅値
                // capacityと現存呼は保存される
                if (t > 0) {
                    for (Link ln : allLinks) {
                        ln.saveCap(t);
                    }
                    qtyExistingCalls[t] = qtyExistingCalls[t - 1];
                }

                //呼の生起
                ArrayList<Call> generatedCallList = occurrenceOfCalls(mag, bldgs, bldgList, ammountRegulation, qtyOccurredCalls, t, callList);
                Collections.shuffle(generatedCallList);

                //ルーティング及び接続可能性評価
                latestTimeOfCalls = routing(qtyExistingCalls, qtyLostCalls, existingCallList, t, latestTimeOfCalls, generatedCallList);
                timeOfLastCall.add(latestTimeOfCalls);

                //時刻tに終了する呼の消去
                deleteCalls(callList, qtyExistingCalls, qtyDeletedCalls, t);

                // 有る時間帯tにおける生起呼の最長終了時間が訪れたらそのリストのメモリを解放する
                releaseExisitingCallList(timeOfLastCall, existingCallList, t);

                // 呼損率の算出
                callLossRate[t] = ((double) qtyLostCalls[t] * 100) / (double) qtyOccurredCalls[t];
                avgHoldTime[t] = Double.valueOf(callList.getSumHoldTime(t)) / Double.valueOf(qtyOccurredCalls[t]);
            }

            worstcallLossRate[loop]  = IOHelper.maxInArray(callLossRate);
            avecallLossRate[loop]    = IOHelper.aveInArray(callLossRate);
            sumcallLossRate[loop]    = IOHelper.sumInArray(callLossRate);
            sumqtyLostCalls[loop]    = IOHelper.sumInArray(qtyLostCalls);
            minMaxcallLossRate[loop] = IOHelper.minMaxInArray(callLossRate);

            output(loopNum, criterion, criNum, brokenLink, brokenBuilding, ammount, mag, outputMethod, hour, timeLength, timeDir, bldgs, callList, IOHelper, loop, timeRegulation, ammountRegulation, qtyExistingCalls, qtyOccurredCalls, qtyLostCalls, qtyDeletedCalls, callLossRate, avgHoldTime);
            double loopDur = (System.nanoTime() - loopStartTime) * 1.0e-9;
            System.out.println("limit-" + brokenBldglimit + ":loop-" + (loop + 1) + " time :" + loopDur);
        }


        //リンクを順に破壊するときのためのExcel出力
        if (contain(outputMethod, 4)) {
            double arr[] = null;
            switch (criterion) {
                case 0:
                    arr = worstcallLossRate;
                    break;
                case 1:
                    arr = avecallLossRate;
                    break;
                case 2:
                    arr = sumcallLossRate;
                    break;
                case 3:
                    arr = sumqtyLostCalls;
                    break;
                case 4:
                    arr = minMaxcallLossRate;
            }
            IOHelper.BreakLinkInOrderOutput(loopNum, arr, timeDir, criterion);
        }


        calcTime = System.nanoTime() - calcTime;
        System.out.println("計算時間：" + (calcTime * (Math.pow(10, -9))) + "s");
    }

    private void output(int loopNum, int criterion, int criNum, int[] brokenLink, String[] brokenBuilding, double ammount, int mag, int[] outputMethod, int hour, int timeLength, File timeDir, Network bldgs, CallList callList, IOHelper IOHelper, int loop, int timeRegulation, int ammountRegulation, int[] qtyExistingCalls, int[] qtyOccurredCalls, int[] qtyLostCalls, int[] qtyDeletedCalls, double[] callLossRate, double[] avgHoldTime) {
        // summary
        if (contain(outputMethod, 5)) {
            IOHelper.summaryOutput(timeDir, mag, brokenLink, brokenBuilding, ammount, timeRegulation, ammountRegulation, brokenBldglimit);
        }

        // standard outputを行う
        if (contain(outputMethod, 0)) {
            IOHelper.StandardOutput(timeLength, timeDir, callLossRate, loop, bldgs);
        }


        //通信規制の方針を比較する
        if (contain(outputMethod, 3)) {
            if (loop == 0) {
                IOHelper.regulaitonMethodDevidedInitialize(criNum);
            }

            IOHelper.regulationMethodDevided(hour, timeLength, timeDir, loop, mag, qtyExistingCalls,
                    qtyOccurredCalls, qtyLostCalls, callLossRate, qtyDeletedCalls, avgHoldTime, loopNum, timeRegulation, ammountRegulation, criNum, bldgs.getBldgList());
        }


        //通信制限欠けた場合と欠けない場合を連続でデータ取った後のポイントのデータ
        if (contain(outputMethod, 6) && loop == loopNum - 1 && criterion == 5) {
            IOHelper.regulationPointOutput(timeDir, criNum, bldgs, brokenBldglimit);

        } else if (contain(outputMethod, 6) && loop == loopNum - 1) {
            IOHelper.regulationPointOutput(timeDir, 0, bldgs, brokenBldglimit);
        }

        //呼量の倍率を変えた場合＊破壊非破壊のパターン別データ
        if (contain(outputMethod, 2)) {
            IOHelper.magDevidedOutput(hour, timeLength, timeDir, loop, mag, qtyExistingCalls, qtyOccurredCalls, qtyLostCalls,
                    callLossRate, qtyDeletedCalls, avgHoldTime);
        }

        //発生area別に呼数を出力する
        if (contain(outputMethod, 1)) {
            IOHelper.areaDevidedKosu(timeDir, loop, callList);
        }
    }


    private void releaseExisitingCallList(ArrayList<Integer> timeOfLastCall, ArrayList<Call>[] existingCallList, int t) {
        for (int i = 0; i < timeOfLastCall.size(); i++) {
            if (timeOfLastCall.get(i) == t) {
                if (existingCallList[i] != null) {
                    existingCallList[i].clear();
                }
            }
        }
    }

    private void deleteCalls(CallList callList, int[] qtyExistingCalls, int[] qtyDeletedCalls, int t) {
        for (Call call : callList.getCallsToEndList(t)) {
            call.releaseCapacityOfLink();
            qtyExistingCalls[t]--;
            qtyDeletedCalls[t]++;
        }
        callList.clearCallsToEndList(t);
    }

    private int routing(int[] qtyExistingCalls, int[] qtyLostCalls, ArrayList<Call>[] existingCallList, int t, int latestTimeOfCalls, ArrayList<Call> generatedCallList) {
        for (Call call : generatedCallList) {
            if (call.routing()) {
                existingCallList[t].add(call);
                qtyExistingCalls[t]++;
                // 終了時刻の最遅値の更新
                if (latestTimeOfCalls < call.getEndTime()) {
                    latestTimeOfCalls = call.getEndTime();
                }
            } else {
                qtyLostCalls[t]++;
            }
        }
        return latestTimeOfCalls;
    }

    private ArrayList<Call> occurrenceOfCalls(int mag, Network bldgs, Building[] bldgList, int ammountRegulation, int[] qtyOccurredCalls, int t, CallList callList) {
        ArrayList<Call> candiCallList = new ArrayList<>();
        for (Building start : bldgList) {
            for (Building dest : bldgList) {
                //区外間の通信は考えない
                if (start == dest && dest.isKugai()) {
                    continue;
                }
                int occur = start.generateTraffic(t, dest, mag);
                //区外発信呼の切断シミュレーション用
                if (ammountRegulation == 1 && start.isKugai()) {
                    int limit = start.generateTraffic(t, dest, 1) * 2;
                    if (bldgs.getOutLink().getOccupiedCap() + occur > limit) {
                        //現在県外からかかってきている呼 + 今回生じる可能性のある呼数　> 平常時の二倍　ならば、超過分を削除
                        occur = limit - bldgs.getOutLink().getOccupiedCap();
                    }
                }
                for (int i = 0; i < occur; i++) {
                    Call call = new Call(start, dest, t, callList);
                    candiCallList.add(call);
                    qtyOccurredCalls[t]++;
                }
            }
        }
        return candiCallList;
    }

    private void initArrayList(ArrayList<Call>[] existingCallList) {
        for (int i = 0; i < existingCallList.length; i++) {
            existingCallList[i] = new ArrayList<>();
        }
    }

    private void breakNetwork(int scenario, int[] brokenLink, String[] brokenBuilding, double ammount, int[] outputMethod, Network bldgs, int loop) {
        //地震による影響で壊れるシナリオで使用
        if (scenario == 1 && loop % 4 == 0) {
            //破壊
            bldgs.brokenByQuake(brokenBldglimit);
        }

        //ビル・リンクの破壊
        if (brokenLink.length > 0) {
            for (int i = 0; i < brokenLink.length; i++) {
                bldgs.findLink(brokenLink[i]).broken(ammount);
            }

        }
        if (brokenBuilding.length > 0) {
            for (String bname : brokenBuilding) {
                bldgs.findBldg(bname).makeBroken();
            }
        }

        if (contain(outputMethod, 4)) {
            // 区内リンクを順に破壊する用

            bldgs.findLink(loop).broken(ammount);

            // 区内中継リンクを破壊する
            // bldgs.exLinkList.get(loop).makeBroken(ammount);

            // 区外リンクを破壊する
            // bldgs.outLink.makeBroken(ammount);
        }
    }

    private void initializeLinks(int timeLength, int localLinkCapacity, int kunaiLinkCapacity, int kugaiLinkCapacity, Network bldgs) {
        // 区内リンクの回線数設定
        for (Link ln : bldgs.getLinkList()) {
            ln.iniCap(timeLength, localLinkCapacity);
        }

        // 区内中継リンクの回線数設定
        for (Link ln : bldgs.getKunaiRelayLinkList()) {
            ln.iniCap(timeLength, kunaiLinkCapacity);
        }

        // 区外中継リンクの回線数設定
        bldgs.getOutLink().iniCap(timeLength, kugaiLinkCapacity);
    }

    private File getTimeDir() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH_mm_ss");
        String time = sdf.format(c.getTime());

        File timeDir = new File(dateDir + "/" + time + "/");
        if (!timeDir.exists()) {
            timeDir.mkdir();
        }
        return timeDir;
    }

    private static void simulation(int bldgNum, int minBrokenBldgLimit, int maxBrokenBldgLimit, int maxThreadsNum, File dateDir) {
        ArrayDeque<Thread> threads = new ArrayDeque<>();
        Thread[] runThreads = new Thread[maxBrokenBldgLimit - minBrokenBldgLimit + 1];
        ThreadGroup group = new ThreadGroup("simulation");

        prepareThreads(bldgNum, minBrokenBldgLimit, maxBrokenBldgLimit, dateDir, threads, group);

        runThreads(maxThreadsNum, threads, runThreads, group);

        waitForThreadsEnd(runThreads);
    }

    private static void runThreads(int maxThreadsNum, ArrayDeque<Thread> threads, Thread[] runThreads, ThreadGroup group) {
        int tmpRunningThreads = 0;
        while (!threads.isEmpty()) {
            if (maxThreadsNum > group.activeCount()) {
                Thread th = threads.remove();
                System.out.println(th.getName() + " is running");
                th.start();
                runThreads[tmpRunningThreads++] = th;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void waitForThreadsEnd(Thread[] runThreads) {
        try {
            for (int i = 0; i < runThreads.length; i++) {
                runThreads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void prepareThreads(int bldgNum, int minBrokenBldgLimit, int maxBrokenBldgLimit, File dateDir, ArrayDeque<Thread> threads, ThreadGroup group) {
        for (int i = minBrokenBldgLimit; i <= maxBrokenBldgLimit; i++) {
            Runnable run = new Main(i, dateDir, bldgNum);
            Thread thread = new Thread(group, run, "limit:" + i);
            threads.offer(thread);
        }
    }

    private static void monitor(String x, int i, int i1, int callLos, double v, int i2, double v1, double ntime) {
        System.out.println(x);
        System.out.println("生起：" + i1);
        System.out.println("損失：" + callLos);
        System.out.println("呼損率：" + v);
        System.out.println("存在：" + i);
        System.out.println("deleted calls ..." + i2);
        // System.out.println("sumHoldTIme:" + Call.sumHoldTime[t]);
        // System.out.println("call occur:" + qtyOccurredCalls[t]);
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
