package ictsimulationpackage;

import java.util.ArrayList;

public class Call {
    //変数
    Building start;
    Building dest;
    int EndTime;
    ArrayList<Link> linkList;
    boolean success = false;
    CallList group;
    static int timeLength;

    static void setTimeLength(int time){
        timeLength = time;
    }

    /**
     * assess the possibility of call and store the result to the group
     * @param start the start building
     * @param dest the destination building of this call
     * @param time when this call occurs
     * @param group which call list this call belongs to
     */
    Call(Building start, Building dest, int time, CallList group) {
        //発着ビルの設定
        this.start = start;
        this.dest = dest;

        //呼のグループへの所属
        this.group = group;

        // 終了時刻
        int holdTime = group.holdingTime();
        this.EndTime = time + holdTime;
        group.addSumHoldTime(time,holdTime);

        //経路探索
        if (start == dest) {
            // 出発ビルと到着ビルが同じ場合
            if (!start.isBroken()) {
                //ビルが壊れていなければ
                success = true;
            }
        } else {
            // 使用するリンク
            linkList = group.route(start, dest);
            if (linkList != null) {
                // 接続に成功した場合 null = 失敗
                for (Link ln : linkList) {
                    ln.addCap();
                }
                success = true;
            }
        }

        //呼が生成に成功した場合
        if (success && EndTime < timeLength) {
            group.addToLimitList(EndTime, this);
            // 呼の発生種別をリンクに選り分ける=>区内呼のシミュレーション用
            if (start.getKunaiRelayBldg() != null && dest.getKunaiRelayBldg() != null && start != dest &&
                    time < 14 * 60 && time > 12 * 60) {
                if ((start.getKunaiRelayBldg() == dest.getKunaiRelayBldg())) {
                    for (Link ln : linkList) {
                        if (ln.getLinkId() < 102) {
                            group.addAreaKosu(ln.getLinkId());
                        }
                    }
                } else {
                    for (Link ln : linkList) {
                        if (ln.getLinkId() < 102) {
                            group.addExKosu(ln.getLinkId());
                        }
                    }
                }
            }
        }
    }

    void delete() {
        if (linkList == null) {
            return;
        }
        for (Link ln : linkList) {
            ln.subCap();
        }
    }
}