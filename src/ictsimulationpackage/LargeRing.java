package ictsimulationpackage;

import java.util.ArrayList;
import java.util.Collections;

public class LargeRing {
    private BuildingList bldgList;
    private Building nerima;
    private Link outLink;
    private SmallRing smallRing;

    LargeRing(BuildingList bldgList) {
        this.bldgList = bldgList;
        nerima = bldgList.findBldg("練馬");
        outLink = nerima.getOutLink();
        smallRing = new SmallRing();
    }

    // startとdestにビルを入れるとそのルート上のリンクのリストを返す
    ArrayList<Link> route(Building start, Building dest) {
        ArrayList<Link> link;

        boolean isOutBldg = isOutBldg(start, dest);
        if (isOutBldg) {
            link = outRoute(start, dest);
            return link;
        }

        if (start.isSameArea(dest)) {
            link = inRoute(start, dest);
        } else {
            link = exRoute(start, dest);
        }
        return link;
    }

    /**
     * you can determine the route considering the link capacity and broken buildings
     * @param start start building
     * @param dest  destination building
     * @return list of used links, if cant find the route, return null
     */
    ArrayList<Link> outRoute(Building start, Building dest) {
        // 変数初期化
        ArrayList<Link> link = new ArrayList();
        // 変数代入
        if (bldgList.getOutLink().maxCap()) {
            return null;
        }

        // 区外 -> 区内
        if (start.isKugai()) {
            // 区外から練馬
            if (outLink.isBroken() || start.isBroken() || nerima.isBroken()) {
                // リンクまたはビル壊れている
                return null;
            } else {
                // 健全
                link.add(outLink);
            }
            // ゴールが練馬ならここでおわり
            if (dest == nerima) {
                return link;
            }
            // 練馬からゴール ルートがnullで例外発生
            try {
                link.addAll(route(nerima, dest));
            } catch (NullPointerException e) {
                // System.out.println("outRoute->route(nerima, dest) is null");
                return null;
            }
        } else {
            // 区内 -> 区外
            // 区内から練馬
            if (start != nerima) {
                link = route(start, nerima);
                if (link == null) {
                    // ルートが見つからない場合の処理
                    return null;
                }
            }
            // 練馬から区外
            if (outLink.isBroken() || start.isBroken() || nerima.isBroken()) {
                // リンクまたはビル壊れている
                return null;
            } else {
                link.add(outLink);
            }
        }
        return link;
    }

    //区内の別リングを含む探索
    ArrayList<Link> exRoute(Building start, Building dest) {
        // 変数初期化
        ArrayList<Link> link = new ArrayList();
        Building sAreaBldg ;
        Building dAreaBldg ;
        // 変数代入
        sAreaBldg = start.getAreaBldg();
        dAreaBldg = dest.getAreaBldg();

        // ルートにnullがあると例外発生
        try {
            // スタートからスタートエリア
            if (!start.isAreaBldg()) {
                // startが中継ビルの場合のエスケープ
                link.addAll(inRoute(start, sAreaBldg));
            }
            // スタートエリアからゴールエリア
            link.addAll(inRoute(sAreaBldg, dAreaBldg));
            // ゴールエリアからゴール
            if (!dest.isAreaBldg()) {
                link.addAll(inRoute(dAreaBldg, dest));
            }
        } catch (Exception e) {
            return null;
        }
        return link;
    }

    // 同じくないのビル間、exRoopのビル間、各リングの一周で済む区間の探索
    ArrayList<Link> inRoute(Building start, Building dest) {
        ArrayList<Link> link;
        link = smallRing.route(start, dest);
        return link;
    }

    /**
     * if start or destination buildings is kugai, return true
     * @param start
     * @param dest
     * @return
     */
    boolean isOutBldg(Building start, Building dest) {
        return start.isKugai()|| dest.isKugai();
    }
}
