package ictsimulationpackage;

import java.util.*;

/**
 * SmallRingオブジェクトでは
 * localRingSearch      : ローカルリング上のルート探索
 * kunaiRelayRingSearch : 23区内中継リング上のルート探索
 * を行う。
 */
public class SmallRing {

    /**
     * this method is used for the localRingSearch exploring in the same ring
     * or in the areaLinks.
     * startかdestが壊れている場合はここでnull出してルート探索の失敗を保証
     * start == destは受け付けない
     */
    public ArrayList<Link> localRingSearch(Building start, Building dest) {
        if (start == dest) {
            System.out.println("smallring routing error start == dest");
            Utility.error();
        }
        ArrayList<Link> usedLinkList;

        //探索を右回りで行うかどうかはランダム
        boolean isRight = Utility.halfProb();

        //同じ地区内ビル同士の探索
        if (isRight) {
            usedLinkList = localRingLightSearch(start, dest);
        } else {
            usedLinkList = localRingLeftSearch(start, dest);
        }

        //ルートが見つかったらおわり
        if (usedLinkList != null) {
            return usedLinkList;
        }

        //ルートが見つからなければ逆回りで探索
        if (!isRight) {
            usedLinkList = localRingLightSearch(start, dest);
        } else {
            usedLinkList = localRingLeftSearch(start, dest);
        }

        return usedLinkList;
    }

    /**
     * 区内中継リング上のルートを探索する
     * start == destは受け付けない
     * @param start
     * @param dest
     * @return
     */
    public ArrayList<Link> kunaiRelayRingSearch(Building start, Building dest) {
        if (start == dest) {
            System.out.println("smallring routing error start == dest");
            Utility.error();
        }

        ArrayList usedLinkList;

        //探索を右回りで行うかどうかはランダム
        boolean isRight = Utility.halfProb();
        if (isRight) {
            usedLinkList = kunaiRelayRingRightSearch(start, dest);
        } else {
            usedLinkList = kunaiRelayRingLeftSearch(start, dest);
        }

        //ルートが見つかったら終わり
        if (usedLinkList != null) {
            return usedLinkList;
        }

        //見つからなければ逆回り
        //ALERT:isRightに!がついてなかった気がする
        if (!isRight) {
            usedLinkList = kunaiRelayRingRightSearch(start, dest);
        } else {
            usedLinkList = kunaiRelayRingLeftSearch(start, dest);
        }
        return usedLinkList;
    }


    /**
     * ローカルリング上を右回りに探索
     * start と destのリングが同じである必要あり
     * start == dest はlocalRingRouteで潰す
     * @param start
     * @param dest
     * @return
     */
    private ArrayList<Link> localRingLightSearch(Building start, Building dest) {
        //同じローカルリング上になければエラー
        if (!start.isOnSameLocalRing(dest)) {
            System.out.println("SmallRing local ring light search error");
            Utility.error();
        }
        //startかdestinationのどちらかが破壊
        if (!start.isAvail() || !dest.isAvail()) {
            return null;
        }

        ArrayList<Link> usedLinkList = new ArrayList();
        ArrayList<Building> usedBldgList = new ArrayList();

        //ルートの探索
        {
            Building bldg = start;
            while (bldg != dest) {
                usedBldgList.add(bldg);
                usedLinkList.add(bldg.getLinkR());

                bldg = bldg.getBldgR();
            }
        }

        //ルートが使用可能か確認
        if (checkRouteAvail(usedLinkList, usedBldgList)) return null;

        return usedLinkList;
    }

    private ArrayList<Link> localRingLeftSearch(Building start, Building dest) {
        //同じローカルリング上になければエラー
        if (!start.isOnSameLocalRing(dest)) {
            System.out.println("SmallRing local ring light search error");
            Utility.error();
        }
        //startかdestinationのどちらかが破壊
        if (!start.isAvail() || !dest.isAvail()) {
            return null;
        }

        ArrayList<Link> usedLinkList = new ArrayList();
        ArrayList<Building> usedBldgList = new ArrayList();

        //ルートの探索
        {
            Building bldg = start;
            while (bldg != dest) {
                usedBldgList.add(bldg);
                usedLinkList.add(bldg.getLinkL());

                bldg = bldg.getBldgL();
            }
        }

        //ルートが使用可能か確認
        if (checkRouteAvail(usedLinkList, usedBldgList)) return null;

        return usedLinkList;
    }

    /**
     * ルーティングの際に通った要素の内、使用不可能なものがあればtrueを返す
     * @param usedLinkList
     * @param usedBldgList
     * @return
     */
    private boolean checkRouteAvail(ArrayList<Link> usedLinkList, ArrayList<Building> usedBldgList) {
        for(int i = 0; i < usedLinkList.size();i++) {
            Building bldg = usedBldgList.get(i);
            Link ln = usedLinkList.get(i);
            if(!bldg.isAvail() || !ln.isAvail()){
                return true;
            }
        }
        return false;
    }

    /**
     * 区内中継リング上のルーティング
     * 区内中継ビル以外は受け付けない
     * @param start
     * @param dest
     * @return
     */
    private ArrayList<Link> kunaiRelayRingRightSearch(Building start, Building dest) {
        if (!start.isKunaiRelayBuilding() || !dest.isKunaiRelayBuilding()) {
            Utility.error();
        }
        //startかdestinationのどちらかが破壊
        if (!start.isAvail() || !dest.isAvail()) {
            return null;
        }

        ArrayList<Link> usedLinkList = new ArrayList();
        ArrayList<Building> usedBldgList = new ArrayList();

        //ルートの探索
        {
            Building bldg = start;
            while (bldg != dest) {
                usedBldgList.add(bldg);
                usedLinkList.add(bldg.getKunaiLinkR());

                bldg = bldg.getKunaiBldgR();
            }
        }

        //ルートが使用可能か確認
        if (checkRouteAvail(usedLinkList, usedBldgList)) return null;

        return usedLinkList;
    }

    // 中継ビル左回り探索
    private ArrayList<Link> kunaiRelayRingLeftSearch(Building start, Building dest) {
        if (!start.isKunaiRelayBuilding() || !dest.isKunaiRelayBuilding()) {
            Utility.error();
        }
        //startかdestinationのどちらかが破壊
        if (!start.isAvail() || !dest.isAvail()) {
            return null;
        }

        ArrayList<Link> usedLinkList = new ArrayList();
        ArrayList<Building> usedBldgList = new ArrayList();

        //ルートの探索
        {
            Building bldg = start;
            while (bldg != dest) {
                usedBldgList.add(bldg);
                usedLinkList.add(bldg.getKunaiLinkL());

                bldg = bldg.getKunaiBldgL();
            }
        }

        //ルートが使用可能か確認
        if (checkRouteAvail(usedLinkList, usedBldgList)) return null;

        return usedLinkList;
    }


    /**
     * アーランbで使う
     * 以下リファクタリングしてない
     * @param start
     * @param dest
     * @param isRight
     * @return
     */
    ArrayList<Link> localRingSearch(Building start, Building dest, boolean isRight) {
        ArrayList<Link> linkList;
        if (start.isKunaiRelayBuilding() && dest.isKunaiRelayBuilding()) {
            //中継ビル同士の探索
            if (isRight) {
                linkList = kunaiRelayRingRightSearch(start, dest);
            } else {
                linkList = kunaiRelayRingLeftSearch(start, dest);
            }
        } else {
            //同じ地区内ビル同士の探索
            if (isRight) {
                linkList = localRingLightSearch(start, dest);
            } else {
                linkList = localRingLeftSearch(start, dest);
            }
        }
        return linkList;
    }
}
