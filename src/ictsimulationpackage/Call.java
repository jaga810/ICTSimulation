package ictsimulationpackage;

import java.util.ArrayList;

public class Call {
    // ある１つの呼のデータ
    static ArrayList<Call> limitList[];// 有る時間に終了する呼のリスト
    static int sumHoldTime[];
    //区内で発生した呼の生成数と呼損数（累計：時間全体で）
    static long areaKosu[] = new long[102];
    static long exLossKosu[] = new long[102];
    //区外で発生した呼の生成数と呼損数（累計）
    static long exKosu[] = new long[102];
    static long areaLossKosu[] = new long[102];
    static int timeLength;

    Building start;
    Building dest;
    int EndTime;
    ArrayList<Link> LinkList;
    boolean success = false;

    static void reset() {
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
    }

    Call(int tLength) {
        sumHoldTime = new int[tLength];
        limitList = new ArrayList[tLength];
        for (int i = 0; i < tLength; i++) {
            // 初期化
            limitList[i] = new ArrayList<>();
        }
        timeLength = tLength;
        System.out.println("Call Class initialized with timelength = " + timeLength);
    }

    Call(Building start, Building dest, int time) {
        //発着ビルの設定
        this.start = start;
        this.dest = dest;

        // 終了時刻
        int holdTime = HoldingTime.OneHoldingTime();
        this.EndTime = time + holdTime;
        sumHoldTime[time] += holdTime;


        if (start == dest) {
            // 出発ビルと到着ビルが同じ場合
            if (!start.broken) {
                //ビルが壊れていなければ
                success = true;
            }
        } else {
            // 使用するリンク
            LinkList = LargeRing.route(start, dest);
            if (LinkList != null) {
                // 接続に成功した場合 null = 失敗
                for (Link ln : LinkList) {
                    ln.addCap();
                }
                success = true;
            }
        }

        //呼が生成に成功した場合
        if (success && EndTime < timeLength) {
            limitList[EndTime].add(this);
            // 呼の発生種別をリンクに選り分ける=>区内呼のシミュレーション用
            if (start.areaBldg != null && dest.areaBldg != null && start != dest &&
                    time < 14 * 60 && time > 12 * 60) {
                if ((start.areaBldg == dest.areaBldg)) {
                    for (Link ln : LinkList) {
                        if (ln.id < 102) {
                            areaKosu[ln.id]++;
                        }
                    }
                } else {
                    for (Link ln : LinkList) {
                        if (ln.id < 102) {
                            exKosu[ln.id]++;
                        }
                    }
                }
            }
        }
    }

    void delete() {
        if (LinkList == null) {
            return;
        }
        for (Link ln : LinkList) {
            if (!ln.subCap()) {
                System.out.println("capacity goes to 0");
                System.out.println(1 / 0);
            }
        }
    }
}