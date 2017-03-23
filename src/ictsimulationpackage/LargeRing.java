package ictsimulationpackage;

import java.util.ArrayList;

/**
 * LargeRingでは任意の中継ビル間のルーティングを行う
 * routingメソッドを呼び出せば良い
 * ただしstart == destの入力は受け付けないことに注意
 */
public class LargeRing {
    private Network network;
    private Building kugaiRelayBldg;
    private Link kugaiRelayLink;
    private SmallRing smallRing;

    public LargeRing(Network network) {
        this.network = network;
        kugaiRelayBldg = network.getKugaiRelayBldg();
        kugaiRelayLink = kugaiRelayBldg.getOutLink();
        smallRing = new SmallRing();
    }

    /**
     * ルーティングのトップ層
     * 任意のビルをstartとdestに設定して良いが唯一
     * start == destは受け付けない
     * @param start 呼を発信するビル
     * @param dest  　呼を着信するビル
     * @return ルート上の全てのリンクのリスト　接続に失敗した場合nullが返る
     */
    public ArrayList<Link> routing(Building start, Building dest) {
        if (start == dest) {
            System.out.println("Largering routing error start == dest");
            Utility.error();
        }
        ArrayList<Link> usedLinkList;

        //発着ビルに区外が含まれる場合
        if (isWithKugai(start, dest)) {
            usedLinkList = routeWithKugai(start, dest);
            return usedLinkList;
        }

        //発着ビルに区外が含まれない場合
        if (start.isOnSameLocalRing(dest)) {
            usedLinkList = smallRing.localRingSearch(start, dest);
        } else {
            usedLinkList = routingThroughKunaiRelayRing(start, dest);
        }

        return usedLinkList;
    }


    /**
     * you can determine the localRingSearch considering the link capacity and makeBroken buildings
     *
     * @param start start building
     * @param dest  destination building
     * @return list of used links. if cant find the localRingSearch, return null
     */
    private ArrayList<Link> routeWithKugai(Building start, Building dest) {
        ArrayList<Link> usedLinkList = new ArrayList();

        //startを区内のビルに
        if (start.isKugai()) {
            start = dest;
        }

        // 区内から練馬
        if (start != kugaiRelayBldg) {
            usedLinkList = routing(start, kugaiRelayBldg);
            if (usedLinkList == null) {
                // ルートが見つからない場合
                return null;
            }
        }

        // 練馬から区外
        //ALERT: ↓でlinkがisbrokenしか見てなかったので怪しいぞ...
        if (kugaiRelayLink.isAvail() && kugaiRelayBldg.isAvail()) {
            usedLinkList.add(kugaiRelayLink);
        } else {
            return null;
        }
        return usedLinkList;
    }

    /**
     * ローカルリングをまたぐような探索
     *
     * @param start 　発信ビル
     * @param dest  　　着信ビル
     * @return ルート上のリンク
     */
    private ArrayList<Link> routingThroughKunaiRelayRing(Building start, Building dest) {
        ArrayList<Link> usedLinkList = new ArrayList();
        Building sKunaiRelayBldg = start.getKunaiRelayBldg();
        Building dKunaiRelayBldg = dest.getKunaiRelayBldg();

        // startが区内中継ビルでないなら中継ビルまでルーティング。失敗したらnull返す
        if (!start.isKunaiRelayBuilding() && !routingOnLocalRing(start, sKunaiRelayBldg, usedLinkList))
            return null;

        // スタートエリアからゴールエリアまでのルーティング。失敗したらnull
        if (!routingOnKunaiRelayRing(usedLinkList, sKunaiRelayBldg, dKunaiRelayBldg))
            return null;

        // destが区内中継ビルでないなら中継ビルまでルーティング。失敗したらnull返す
        if (!dest.isKunaiRelayBuilding() && !routingOnLocalRing(dKunaiRelayBldg, dest, usedLinkList))
            return null;

        return usedLinkList;
    }

    /**
     * sareabldg -> dAreaBldgで通ったリンクをusedLinkListに追加する
     * @param usedLinkList
     * @param sKunaiRelayBldg startとなる中継ビル
     * @param dKunaiRelayBldg destとなる中継ビル
     * @return 探索に成功したら true, 失敗したら false
     */
    private boolean routingOnKunaiRelayRing(ArrayList<Link> usedLinkList, Building sKunaiRelayBldg, Building dKunaiRelayBldg) {
        ArrayList list = smallRing.kunaiRelayRingSearch(sKunaiRelayBldg, dKunaiRelayBldg);
        if (list == null) return false;
        usedLinkList.addAll(list);
        return true;
    }

    /**
     * start -> dest で通ったリンクをusedLinkListに追加する
     * @param start
     * @param dest
     * @param usedLinkList
     * @return 探索に成功したら true, 失敗したら false
     */
    private boolean routingOnLocalRing(Building start, Building dest, ArrayList<Link> usedLinkList) {
        // startが中継ビルの場合のエスケープ
        ArrayList list = smallRing.localRingSearch(start, dest);
        if (list == null) return false;
        usedLinkList.addAll(list);

        return true;
    }

    boolean isWithKugai(Building start, Building dest) {
        return start.isKugai() || dest.isKugai();
    }

    /**
     * アーランB式を使う際に使用 isRightにより右回り固定で探索する
     * 以下リファクタリングしてないので汚い
     * @param start
     * @param dest
     * @param isRight
     * @return
     */
    public ArrayList<Link> routing(Building start, Building dest, boolean isRight) {
        ArrayList<Link> link;

        boolean isOutBldg = isWithKugai(start, dest);
        if (isOutBldg) {
            link = routeWithKugai(start, dest, isRight);
            return link;
        }

        if (start.isOnSameLocalRing(dest)) {
            link = inRoute(start, dest, isRight);
        } else {
            link = routingThroughKunaiRelayRing(start, dest, isRight);
        }
        return link;
    }

    private ArrayList<Link> routeWithKugai(Building start, Building dest, boolean isRight) {
        // 変数初期化
        ArrayList<Link> link = new ArrayList();
        // 変数代入
        if (network.getOutLink().isMaxCap()) {
            return null;
        }

        // 区外 -> 区内
        if (start.isKugai()) {
            // 区外から練馬
            if (kugaiRelayLink.isBroken() || start.isBroken() || kugaiRelayBldg.isBroken()) {
                // リンクまたはビル壊れている
                return null;
            } else {
                // 健全
                link.add(kugaiRelayLink);
            }
            // ゴールが練馬ならここでおわり
            if (dest == kugaiRelayBldg) {
                return link;
            }
            // 練馬からゴール ルートがnullで例外発生
            try {
                link.addAll(routing(kugaiRelayBldg, dest, isRight));
            } catch (NullPointerException e) {
                // System.out.println("routeWithKugai->localRingSearch(kugaiRelayBldg, dest) is null");
                return null;
            }
        } else {
            // 区内 -> 区外
            // 区内から練馬
            if (start != kugaiRelayBldg) {
                link = routing(start, kugaiRelayBldg, isRight);
                if (link == null) {
                    // ルートが見つからない場合の処理
                    return null;
                }
            }
            // 練馬から区外
            if (kugaiRelayLink.isBroken() || start.isBroken() || kugaiRelayBldg.isBroken()) {
                // リンクまたはビル壊れている
                return null;
            } else {
                link.add(kugaiRelayLink);
            }
        }
        return link;
    }

    // 同じくないのビル間、exRoopのビル間、各リングの一周で済む区間の探索
    private ArrayList<Link> inRoute(Building start, Building dest, boolean isRight) {
        ArrayList<Link> link;
        link = smallRing.localRingSearch(start, dest, isRight);
        return link;
    }

    //区内の別リングを含む探索
    ArrayList<Link> routingThroughKunaiRelayRing(Building start, Building dest, boolean isRight) {
        // 変数初期化
        ArrayList<Link> link = new ArrayList();
        Building sAreaBldg;
        Building dAreaBldg;
        // 変数代入
        sAreaBldg = start.getKunaiRelayBldg();
        dAreaBldg = dest.getKunaiRelayBldg();

        // ルートにnullがあると例外発生
        try {
            // スタートからスタートエリア
            if (!start.isKunaiRelayBuilding()) {
                // startが中継ビルの場合のエスケープ
                link.addAll(inRoute(start, sAreaBldg, isRight));
            }
            // スタートエリアからゴールエリア
            link.addAll(inRoute(sAreaBldg, dAreaBldg, isRight));
            // ゴールエリアからゴール
            if (!dest.isKunaiRelayBuilding()) {
                link.addAll(inRoute(dAreaBldg, dest, isRight));
            }
        } catch (Exception e) {
            return null;
        }
        return link;
    }
}
