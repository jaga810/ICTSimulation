package ictsimulationpackage;

import java.util.ArrayList;

/**
 * Created by jaga on 12/28/16.
 */
public class CallList {
    // ある１つの呼のデータ
    private ArrayList<Call> limitList[];// 有る時間に終了する呼のリスト
    private int sumHoldTime[];

    //区内で発生した呼の生成数と呼損数（累計：時間全体で）
    private long areaKosu[] = new long[102];
    private long exLossKosu[] = new long[102];

    //区外で発生した呼の生成数と呼損数（累計）
    private long exKosu[] = new long[102];
    private long areaLossKosu[] = new long[102];
    private int timeLength;

    //このsimulationで使用しているもの
    private Network bldgList;
    private LargeRing largeRing;

    //このloopでのtimeregulationとHoldingTimeオブジェクト
    private int timeRegulation;
    private HoldingTime holdingTime;

    /**
     * reset method for 1 day loop
     * if the 24h simulation ends, this method is called
     */
    void reset(int regulation) {
        //各配列の初期化
        limitList = new ArrayList[timeLength];
        for (int i = 0; i < timeLength; i++) {
            // 初期化
            limitList[i] = new ArrayList<>();
            sumHoldTime[i] = 0;
        }
        areaKosu = new long[102];
        exLossKosu = new long[102];
        areaLossKosu = new long[102];
        exKosu = new long[102];
        sumHoldTime = new int[timeLength];
        timeRegulation = regulation;
        holdingTime = new HoldingTime();
    }

    public int holdingTime() {
        return holdingTime.OneHoldingTime(timeRegulation);
    }

    /**
     * the initialization of callList
     *
     * @param tLength the length of time steps. ex) 24h * 60min = 1440 steps
     */
    CallList(int tLength, Network bldgList) {
        sumHoldTime = new int[tLength];
        limitList = new ArrayList[tLength];
        for (int i = 0; i < tLength; i++) {
            // 初期化
            limitList[i] = new ArrayList<>();
        }
        timeLength = tLength;
        Call.setTimeLength(tLength);
        this.bldgList = bldgList;
        this.largeRing = new LargeRing(bldgList);
    }

    public ArrayList<Link> route(Building start, Building dest) {
        return largeRing.route(start, dest);
    }

    void addSumHoldTime(int time, int holdTime) {
        sumHoldTime[time] += holdTime;
    }

    public void addToLimitList(int endTIme, Call call) {
        limitList[endTIme].add(call);
    }

    /**
     * @param id the id of used link
     */
    public void addAreaKosu(int id) {
        areaKosu[id]++;
    }

    /**
     * @param id the id of used link
     */
    public void addExKosu(int id) {
        exKosu[id]++;
    }

    public long[] getAreaKosu() {
        return areaKosu;
    }

    public long[] getAreaLossKosu() {
        return areaLossKosu;
    }

    public long[] getExKosu() {
        return exKosu;
    }

    public long[] getExLossKosu() {
        return exLossKosu;
    }

    /**
     * return the calls which ends time t
     *
     * @param t the temp time of this simulation
     */
    public ArrayList<Call> getLimitList(int t) {
        return limitList[t];
    }

    public void clearLimitList(int t) {
        limitList[t].clear();
    }

    public double getSumHoldTime(int t) {
        return sumHoldTime[t];
    }

    public Network getBldgList() {
        return bldgList;
    }
}
