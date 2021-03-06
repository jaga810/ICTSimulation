package ictsimulationpackage;

import java.util.HashMap;

/**
 * 中継ビル
 * スレッド毎にNetwork型オブジェクトをもち、各Networkオブジェクトが
 * 103のBuildingオブジェクトを持つ
 */
public class Building {

    private int bid;
    private String bname;
    private Building kunaiRelayBuilding = null;//中継ビル
    private boolean broken = false;

    //呼数の分布 kosu.get[h](Building) = Buildingへのh時間帯での呼数分布(区外ビルの場合は多摩地区からの呼量)
    //          kosutTaken.get[h](dest) = 他県からdestへのh時間帯での呼数分布
    private HashMap<Building, Double> kosu[]      = new HashMap[Setting.SIMULATION_HOUR.get()];
    private HashMap<Building, Double> kosuTaken[] = new HashMap[Setting.SIMULATION_HOUR.get()];

    //ローカルリング
    private Link linkR;
    private Link linkL;
    private Building bldgR;
    private Building bldgL;

    //23区内中継リング
    private Building kunaiBldgR;
    private Building kunaiBldgL;
    private Link kunaiLinkR;
    private Link kunaiLinkL;

    //区外中継
    private Link kugaiLink;

    //経度、緯度 => 震度分布作成時に使用
    private double latitude;
    private double longitude;

    Building(String bname, int bid) {
        //区外ビル作成用
        this.bname = bname;
        this.bid = bid;

        Utility.initHashMap(kosu);
        Utility.initHashMap(kosuTaken);
    }

    Building(BuildingInfo info) {
        //区内ビル作成用
        this.bid = info.getBid();
        this.bname = info.getBname();

        Utility.initHashMap(kosu);
        Utility.initHashMap(kosuTaken);
    }

    /**
     * このオブジェクトのビルから、destビルへのtime時におけるトラフィックを取得
     * 区外->区外へのトラフィックは返らないので呼び出さない
     *
     * @param time シミュレーション中の時間t (min)
     * @param dest 呼の目的地
     * @param mag  需要量の倍率
     * @return トラフィック（生起する呼の数）
     */
    public int generateTraffic(int time, Building dest, int mag) {
        int traffic;
        int hour = time / 60 % 24;
        double kosu = this.kosu[hour].get(dest);
        if (isKugai()) {
            //区外オブジェクト以外で呼び出すとエラーが出るので注意
            double kosuTaken = this.kosuTaken[hour].get(dest);

            //多摩地区発信のトラフィック
            traffic =  CallUtility.occurredCallsNum(kosu * mag / 60);

            //県外発信のトラフィック
            traffic += CallUtility.occurredCallsNum(kosuTaken * mag / 60);
        } else {
            traffic =  CallUtility.occurredCallsNum(kosu * mag / 60);
        }
        return traffic;
    }

    /**
     * このオブジェクトのビルからdestビルへのhour時間帯における呼数をセット
     *
     * @param hour 時間帯
     * @param dest 目的地のビル
     * @param kosu 呼数
     */
    public void setKosu(int hour, Building dest, double kosu) {
        try {
            if (this.kosu[hour].containsKey(dest)) {
                double val = this.kosu[hour].get(dest);
                val += kosu;
                this.kosu[hour].put(dest, val);
            } else {
                this.kosu[hour].put(dest, kosu);
            }
        } catch (NullPointerException e) {
            System.out.println("start:" + bname + " t:" + hour);
            e.printStackTrace();
        }

    }

    /**
     * 他県への個数は別に管理する。多摩地区はkosu.get("区外")にセットする
     *
     * @param hour
     * @param bldg
     * @param kosu
     */
    void setKosuTaken(int hour, Building bldg, double kosu) {
        if (this.kosuTaken[hour].containsKey(bldg)) {
            double val = this.kosuTaken[hour].get(bldg);
            val += kosu;
            this.kosuTaken[hour].put(bldg, val);
        } else {
            this.kosuTaken[hour].put(bldg, kosu);
        }
    }

    public void setLinkFor2Bldgs(Link ln) {
        setLinkR(ln);
        bldgR.setLinkL(ln);
    }

    public void makeBroken() {
        broken = true;
    }

    public void repair() {
        broken = false;
    }

    /** getterとsetter */

    public void setKunaiRelayBuilding(Building bldg) {
        kunaiRelayBuilding = bldg;
    }

    public void setKunaiBldgR(Building bldg) {
        kunaiBldgR = bldg;
    }

    public void setKunaiBldgL(Building bldg) {
        kunaiBldgL  = bldg;
    }

    public Building getBldgR() {
        return bldgR;
    }

    public Building getBldgL() {
        return bldgL;
    }

    public void setBldgR(Building bldg) {
        bldgR = bldg;
    }

    public void setBldgL(Building bldg) {
        bldgL = bldg;
    }

    public int getBid() {
        return bid;
    }

    public void setLinkR(Link ln) {
        linkR = ln;
    }

    public void setLinkL(Link ln) {
        linkL = ln;
    }

    public Link getLinkR() {
        return linkR;
    }

    public Link getLinkL() {
        return linkL;
    }


    public Link getKunaiLinkR() {
        return kunaiLinkR;
    }

    public Link getKunaiLinkL() {
        return kunaiLinkL;
    }


    public Building getKunaiBldgR() {
        return kunaiBldgR;
    }

    public Building getKunaiBldgL() {
        return kunaiBldgL;
    }

    private void setKunaiLinkR(Link ln) {
        kunaiLinkR = ln;
    }

    private void setKunaiLinkL(Link ln) {
        kunaiLinkL = ln;
    }

    public void setKunaiLinks(Link ln) {
        setKunaiLinkR(ln);
        kunaiBldgR.setKunaiLinkL(ln);
    }


    public String getBname() {
        return bname;
    }

    public void setKugaiLink(Link ln) {
        kugaiLink = ln;
    }

    public boolean isBroken() {
        return broken;
    }

    public boolean isAvail() {
        return !broken;
    }

    public Building getKunaiRelayBldg() {
        return kunaiRelayBuilding;
    }

    public HashMap<Building, Double> getKosu(int t) {
        return kosu[t];
    }

    /** ビルの状態に関するbool値 */
    public boolean isKugai() {
        return bname.equals("区外");
    }

    public Link getOutLink() {
        return kugaiLink;
    }

    public boolean isOnSameLocalRing(Building bldg) {
        return this.kunaiRelayBuilding == bldg.kunaiRelayBuilding;
    }

    public boolean isKunaiRelayBuilding() {
        return kunaiRelayBuilding == this;
    }

    public boolean isAvailLinkR() {
        return linkR.isBroken() || isBroken() || linkR.isMaxCap();
    }

    public boolean isAvailLinkL() {
        return linkL.isBroken() || isBroken() || linkL.isMaxCap();
    }

    public boolean isAvailKunaiLinkR() {
        return kunaiLinkR.isBroken() || isBroken() || kunaiLinkR.isMaxCap();
    }

    public boolean isAvailKunaiLinkL() {
        return kunaiLinkL.isBroken() || isBroken() || kunaiLinkL.isMaxCap();
    }

    /**etc*/
    void setGisData(double la, double lon) {
        latitude = la;
        longitude = lon;
    }
}
