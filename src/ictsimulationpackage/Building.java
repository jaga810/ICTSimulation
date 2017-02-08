package ictsimulationpackage;

import java.util.Arrays;
import java.util.HashMap;

public class Building {
    //変数
    private int bid;
    private String bname;
    private Building areaBldg;
    private boolean broken = false;

    private HashMap<Building, Double> kosu[] = new HashMap[24];
    private HashMap<Building, Double> kosuTaken[] = new HashMap[24];

    //bldgListオブジェクト（親オブジェクト
    private BuildingList bldgList;

    //左右方向のリンク
    private Link linkR;
    private Link linkL;

    //左右方向のビル;
    private Building bldgR;
    private Building bldgL;
    private Building exBldgR;
    private Building exBldgL;

    //区内中継リンク
    private Link exLinkR;
    private Link exLinkL;

    //区外中継リンク
    private Link outLink;

    //経度、緯度
    private double latitude;
    private double longitude;

    Building() {
        //start,last用
    }

    Building(String bname, int bid) {
        //区外ビル作成用
        this.bname = bname;
        this.bid = bid;
        this.areaBldg = this;

        for (int i = 0; i < kosu.length; i++) {
            kosu[i] = new HashMap();
        }
        for (int i = 0; i < kosuTaken.length; i++) {
            kosuTaken[i] = new HashMap();
        }
    }

    Building(int id, BuildingList bldgList, String bldgName[]) {
        bid = id;
        bname = bldgName[bid];
        this.bldgList = bldgList;

        for (int i = 0; i < kosu.length; i++) {
            kosu[i] = new HashMap();
        }
        for (int i = 0; i < kosuTaken.length; i++) {
            kosuTaken[i] = new HashMap();
        }
    }





    void setArea(Building bldg) {
        areaBldg = bldg;
    }

    void setLink(Link r, Link l) {
        linkR = r;
        linkL = l;
    }

    void setExLink(Link r, Link l) {
        linkR = r;
        linkL = l;
    }

    void setExBldg(Building r, Building l) {
        exBldgR = r;
        exBldgL = l;
    }

    //ひとつ前のbuildingを引数として、次のビルを返すメソッド
    Building next(Building prev) {
        Building bldg;
        if (prev == bldgR) {
            bldg = bldgL;
        } else if (prev == bldgL) {
            bldg = bldgR;
        } else {
            bldg = null;
        }
        return bldg;
    }

    //ビルの破壊
    void broken() {
        broken = true;
    }

    void setKosu(int t, Building bldg, double kosu) {
        try {
            if (this.kosu[t].containsKey(bldg)) {
                double val = this.kosu[t].get(bldg);
                val += kosu;
                this.kosu[t].put(bldg, val);
            } else {
                this.kosu[t].put(bldg, kosu);
            }
        } catch (NullPointerException e) {
            System.out.println("start:" + bname + " t:" + t );
            e.printStackTrace();
        }

    }

    //他県への個数は別に管理。多摩地区はkosu[t][102]
    void setKosuTaken(int t, Building bldg, double kosu) {
        if (this.kosuTaken[t].containsKey(bldg)) {
            double val = this.kosuTaken[t].get(bldg);
            val += kosu;
            this.kosuTaken[t].put(bldg, val);
        } else {
            this.kosuTaken[t].put(bldg, kosu);
//			System.out.println(bid +":" + this.kosuTaken[t].get(bldg));
        }
    }

    double[] addArray(double[] array1, double[] array2) {
        double val[] = new double[array1.length];
        if (array1.length != array2.length) {
            return null;
        }
        for (int i = 0; i < array1.length; i++) {
            val[i] = array1[i] + array2[i];
        }
        return val;
    }

    double kosuFinder(int t, Building dest) {
        double d = kosu[t].get(dest);
        return d;
    }

    double kosuTakenFinder(int t, Building dest) {
        if (dest.bname.equals("区外")) {
            System.out.println("kugai to kugai is incapable");
            return 0;
        }
        double d = kosuTaken[t].get(dest);
        return d;
    }

    //時間、目的地、普段と比べたトラフィックの倍率を引数として発生呼数を返す
    int occurence(int time, Building dest, int mag) {
        int val;
        int hour = time / 60 % 24;
        double kosu = this.kosu[hour].get(dest);
        if (isKugai()) {
            double kosuTaken = kosuTakenFinder(hour, dest);
            //多摩地区発信のトラフィック
            if (kosu * mag < 2815.00707107201 * 2) {
                val = OccurrenceOfCalls.Occurrence(kosu * mag / 60);
            } else {
                val = OccurrenceOfCalls.Occurrence(2815.00707107201 * 2 / 60);
            }

            //県外発信のトラフィック
            if (kosuTaken * mag < 5908.49092777896 * 2) {
                val += OccurrenceOfCalls.Occurrence(kosu * mag / 60);
            } else {
                val += OccurrenceOfCalls.Occurrence(5908.49092777896 * 2 / 60);
            }
        } else {
            val = OccurrenceOfCalls.Occurrence(kosu * mag / 60);
        }
        return val;
    }

    void setGisData(double la, double lon) {
        latitude = la;
        longitude = lon;
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

    public void insertBldgR(Building bldg) {
        //this -> (bldg) -> bldgR
        //bldgの右をbldgRに, 左をthisに
        bldg.setBldgR(bldgR);
        bldg.setBldgL(this);
        //bldgRの左をbldgに
        bldgR.setBldgL(bldg);
        //bldgの右をbldgに
        setBldgR(bldg);
    }

    public int getBid() {
        return bid;
    }

    public void setLinks(Link ln) {
        setLinkR(ln);
        bldgR.setLinkL(ln);
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

    public Building getExBldgR() {
        return exBldgR;
    }

    public Building getExBldgL() {
        return exBldgL;
    }

    private void setExLinkR(Link ln ) {
        exLinkR = ln;
    }

    private void setExLinkL(Link ln) {
        exLinkL = ln;
    }

    public void setExLinks(Link ln) {
        setExLinkR(ln);
        exBldgR.setExLinkL(ln);
    }

    public String getBname() {
        return bname;
    }

    public void setOutLink(Link ln) {
        outLink = ln;
    }

    public void repair() {
        broken = false;
    }

    public boolean isBroken() {
        return broken;
    }

    public Building getAreaBldg() {
        return areaBldg;
    }

    public boolean isKugai() {
        return bname.equals("区外");
    }

    public Link getOutLink() {
        return outLink;
    }

    public boolean isSameArea(Building bldg) {
        return this.areaBldg == bldg.areaBldg;
    }

    public boolean isAreaBldg() {
        return areaBldg == this;
    }

    public boolean canGoRight() {
        return linkR.isBroken()|| isBroken() || linkR.maxCap();
    }

    public boolean canGoLeft() {
        return linkL.isBroken()|| isBroken() || linkL.maxCap();
    }

    public boolean canGoExRight() {
        return exLinkR.isBroken()|| isBroken() || exLinkR.maxCap();
    }

    public boolean canGoExLeft() {
        return exLinkL.isBroken()|| isBroken() || exLinkL.maxCap();
    }

    public Link getExLinkR() {
        return exLinkR;
    }

    public Link getExLinkL() {
        return exLinkL;
    }

    public HashMap<Building,Double> getKosu(int t) {
        return kosu[t];
    }
}
