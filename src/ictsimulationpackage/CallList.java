package ictsimulationpackage;

import java.util.ArrayList;

/**
 * 全てのcallオブジェクトを管理するクラス
 * ある時間に終了する呼の管理
 * callオブジェクトで使用するリソースの確保
 * 統計情報の管理
 */
public class CallList {
    private final int bldgNum = Setting.BUILDING_NUM.get();
    
    //callsToEndList[t]有る時間に終了する呼のリスト
    private ArrayList<Call> callsToEndList[];

    //sumHoldTime[t] 時間tに於ける保留時間の合計
    private int sumHoldTime[];

    //区内で発生した呼の生成数と呼損数（累計：時間全体で）
    private long kosuInLocalRing[];
    private long lossKosuInLocalRing[];

    //区外で発生した呼の生成数と呼損数（累計）
    private long kosuThroughKunaiRelayRing[];
    private long lossKosuThroughKunaiRelayRing[];
    private int timeLength;

    //このスレッドで使用するオブジェクト
    private Network network;
    private LargeRing largeRing;

    //時間規制を行う >= 1, 行わない = 0
    private int timeRegulation;
    
    /**
     * the initialization of callList
     * @param tLength the length of time steps. ex) 24h * 60min = 1440 steps
     * @param network 本スレッドで使用しているnetworkリソース
     */
    CallList(int tLength, Network network) {
        this.timeLength = tLength;
        this.network = network;
        this.largeRing = new LargeRing(network);
        Call.setTimeLength(tLength);
    }

    /**
     * init method for 1 day loop
     * if the 24h simulation ends, this method is called
     * @param timeRegulation >=0 しか受け付けない。0じゃなければ保留時間の最大値を
     *                       timeregulation分数分にして時間規制をかける
     */
    void init(int timeRegulation) {
        sumHoldTime = new int[timeLength];
        callsToEndList = new ArrayList[timeLength];
        for (int i = 0; i < timeLength; i++) {
            // 初期化
            callsToEndList[i] = new ArrayList<>();
            sumHoldTime[i] = 0;
        }

        kosuInLocalRing = new long[bldgNum];
        lossKosuInLocalRing = new long[bldgNum];
        lossKosuThroughKunaiRelayRing = new long[bldgNum];
        kosuThroughKunaiRelayRing = new long[bldgNum];


        //regulation>=0で
        if(timeRegulation < 0)Utility.error();
        this.timeRegulation = timeRegulation;
    }




    public ArrayList<Link> routing(Building start, Building dest) {
        return largeRing.routing(start, dest);
    }

    void addSumHoldTime(int time, int holdTime) {
        sumHoldTime[time] += holdTime;
    }

    public void addCallsToEndList(int endTIme, Call call) {
        callsToEndList[endTIme].add(call);
    }

    /**
     * @param id the id of used link
     */
    public void addKosuInLocalRing(int id) {
        kosuInLocalRing[id]++;
    }

    /**
     * @param id the id of used link
     */
    public void addKosuThroughKunaiRelayRing(int id) {
        if(id < kosuThroughKunaiRelayRing.length)
        kosuThroughKunaiRelayRing[id]++;
    }

    /**
     * return the calls which ends time t
     *
     * @param t the temp time of this simulation
     */
    public ArrayList<Call> getCallsToEndList(int t) {
        return callsToEndList[t];
    }

    /**
     * t時間帯のcallsToEndArrayの要素を削除する。
     * @param t
     */
    public void clearCallsToEndList(int t) {
        callsToEndList[t].clear();
    }

    /**
     * timeRegulationを考慮して保留時間を計算する
     * @return
     */
    public int calcHoldingTime() {
        double tau;
        double lambda = 0.02166911;
        tau = -1.0 / lambda * Math.log(1.0 - Math.random());
        int time = (int) Math.round(tau / 60);

        // 通信時間規制
        if (timeRegulation != 0) {
            if (timeRegulation > 0 && time > timeRegulation) {
                time = 1;
            }
        }
        return (time);
    }
    
    /** getter */
    public double getSumHoldTime(int t) {
        return sumHoldTime[t];
    }
    
    public long[] getKosuInLocalRing() {
        return kosuInLocalRing;
    }

    public long[] getLossKosuThroughKunaiRelayRing() {
        return lossKosuThroughKunaiRelayRing;
    }

    public long[] getKosuThroughKunaiRelayRing() {
        return kosuThroughKunaiRelayRing;
    }

    public long[] getLossKosuInLocalRing() {
        return lossKosuInLocalRing;
    }
}
