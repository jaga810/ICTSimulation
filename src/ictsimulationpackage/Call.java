package ictsimulationpackage;

import java.util.ArrayList;

public class Call {
    static int timeLength;

    private Building start;
    private Building dest;

    private int EndTime;
    private int sTime;

    private ArrayList<Link> usedLinkList;
    private boolean isSuccess = false;
    private CallList callList;


    /**
     * assess the possibility of call and store the result to the callList
     * @param start the start building
     * @param dest  the destination building of this call
     * @param sTime  when this call occurs
     * @param callList which call list this call belongs to
     */
    Call(Building start, Building dest, int sTime, CallList callList) {
        this.start = start;
        this.dest = dest;

        this.callList = callList;
        this.sTime = sTime;

        // 終了時刻
        int holdTime = callList.holdingTime();
        this.EndTime = sTime + holdTime;
        callList.addSumHoldTime(sTime,holdTime);
    }

    /**
     * ルーティング及び接続可能性評価を行う
     * @return ルーティングが成功し、接続したらtrue 失敗したらfalse
     */
    public boolean routing(){
        //経路探索
        if (start == dest) {
            if (start.isAvail()) {
                isSuccess = true;
            }
        } else {
            usedLinkList = callList.routing(start, dest);
            if (usedLinkList != null) {
                // 接続に成功した場合
                for (Link ln : usedLinkList) {
                    ln.addCap();
                }
                isSuccess = true;
            }
        }

        //呼が生成に成功した場合(シミュレーション時間内に終わらない呼は無視
        if (isSuccess && EndTime < timeLength) {
            callList.addToLimitList(EndTime, this);
            // 呼の発生種別をリンクに選り分ける=>区内呼のシミュレーション用
            if (start.getKunaiRelayBldg() != null && dest.getKunaiRelayBldg() != null && start != dest &&
                    sTime < 14 * 60 && sTime > 12 * 60) {
                if ((start.getKunaiRelayBldg() == dest.getKunaiRelayBldg())) {
                    for (Link ln : usedLinkList) {
                        if (ln.getLinkId() < 102) {
                            callList.addAreaKosu(ln.getLinkId());
                        }
                    }
                } else {
                    for (Link ln : usedLinkList) {
                        if (ln.getLinkId() < 102) {
                            callList.addExKosu(ln.getLinkId());
                        }
                    }
                }
            }
            return true;
        }
        return false;
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

    public int getEndTime() {
        return EndTime;
    }

    static void setTimeLength(int time){
        timeLength = time;
    }
}