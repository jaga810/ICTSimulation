package ictsimulationpackage;

import java.util.ArrayList;

/**
 * 呼のクラス
 * コンストラクタで必要な情報を受け取り生起する
 * routing()でルーティングと接続可能性評価を行う
 * 2段階に分けることで生起後のルーティングをランダム順序で行うことができる
 */
public class Call {
    static private int timeLength;

    private Building start;
    private Building dest;

    private int EndTime;

    private ArrayList<Link> usedLinkList;
    private CallList callList;


    /**
     * assess the possibility of call and store the result to the callList
     *
     * @param start    the start building
     * @param dest     the destination building of this call
     * @param sTime    when this call occurs
     * @param callList which call list this call belongs to
     */
    Call(Building start, Building dest, int sTime, CallList callList) {
        this.start = start;
        this.dest = dest;

        this.callList = callList;

        // 終了時刻
        int holdTime = callList.calcHoldingTime();
        this.EndTime = sTime + holdTime;
        callList.addSumHoldTime(sTime, holdTime);
    }

    /**
     * ルーティング及び接続可能性評価を行う
     *
     * @return ルーティングが成功し、接続したらtrue 失敗したらfalse
     */
    public boolean routing() {
        //start==destのルーティングはここで処理
        if (start == dest){
            if (start.isAvail()) {
                return true;
            } else {
                return false;
            }
        }

        //start != destが保証される必要有り
        usedLinkList = callList.routing(start, dest);
        if (usedLinkList == null) return false;//接続失敗

        // 接続に成功した場合
        for (Link ln : usedLinkList) {
            ln.addCap();
        }

        // 呼の発生種別をリンクに選り分ける=>区内呼のシミュレーション用
        sortOutKosuInLocalRing();

        //シミュレーション時間内に終わる呼をリストに追加
        if (EndTime < timeLength) {
            callList.addCallsToEndList(EndTime, this);
        }
        return true;
    }

    /**
     * 呼の発生種別をリンクに選り分ける=>区内呼のシミュレーション用
     */
    private void sortOutKosuInLocalRing() {
        if ((start.isOnSameLocalRing(dest))) {
            for (Link ln : usedLinkList) {
                callList.addKosuInLocalRing(ln.getLinkId());

            }
        } else {
            for (Link ln : usedLinkList) {
                callList.addKosuThroughKunaiRelayRing(ln.getLinkId());
            }
        }
    }

    /**
     * 使用リンクの回線を解放する
     */
    public void releaseCapacityOfLink() {
        if (usedLinkList == null) {
            return;
        }
        for (Link ln : usedLinkList) {
            ln.subCap();
        }
    }

    /** getter setter*/
    public int getEndTime() {
        return EndTime;
    }

    static public void setTimeLength(int time) {
        timeLength = time;
    }
}