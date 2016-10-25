package ictsimulationpackage;

import java.util.HashMap;

public class Building {
    //データ読み込み
    static String[] bldgName = Settings.BldgName();
    static double[] scale ;

    //変数
    boolean exBuilding = false;//区内中継３ビル
    boolean outBuilding = false;//練馬
    int bid;
    String bname;
    Building areaBldg;
    boolean broken = false;
    double prob = 0;

    HashMap<Building, Double> kosu[] = new HashMap[24];
    HashMap<Building, Double> kosuTaken[] = new HashMap[24];

    //左右方向のリンク
    Link linkR;
    Link linkL;

    //左右方向のビル;
    Building bldgR;
    Building bldgL;
    Building exBldgR;
    Building exBldgL;

    //区内中継リンク
    Link exLinkR;
    Link exLinkL;

    //区外中継リンク
    Link outLink;

    double latitude;//緯度
    double longitude;//経度

    Building() {
        //start用
        for (int i = 0; i < kosu.length; i++) {
            kosu[i] = new HashMap();
        }
        for (int i = 0; i < kosuTaken.length; i++) {
            kosuTaken[i] = new HashMap();
        }
    }

    Building(int id) {
        bid = id;
        bname = bldgName[bid];

        for (int i = 0; i < kosu.length; i++) {
            kosu[i] = new HashMap<Building, Double>();
        }
        for (int i = 0; i < kosuTaken.length; i++) {
            kosuTaken[i] = new HashMap<Building, Double>();
        }
    }

    static void getScale() {
        scale = Settings.getScale();
    }

    void brokenByQuake() {
        prob = scale[bid ] / 100;
        if (Math.random() < prob) {
            broken = true;
//            System.out.println(bname + " is broken!");
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

    //中継ビルであるかいなか
    void external() {
        exBuilding = true;
    }

    //ビルの破壊
    void broken() {
        broken = true;
    }

    void setKosu(int t, Building bldg, double kosu) {
        if (this.kosu[t].containsKey(bldg)) {
            double val = this.kosu[t].get(bldg);
            val += kosu;
            this.kosu[t].put(bldg, val);
        } else {
            this.kosu[t].put(bldg, kosu);
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
        if (bname.equals("区外")) {
            double kosuTaken = kosuTakenFinder(hour, dest);
            //多摩地区発信のトラフィック
            if (kosu * mag < 2815.00707107201 * 2) {
                val = OccurrenceOfCalls.Occurrence(kosu * mag / 60);
            } else {
                val = OccurrenceOfCalls.Occurrence(2815.00707107201 * 2 / 60);
            }

            //県外発信のトラフィック
            if (kosu * mag < 5908.49092777896 * 2) {
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
}
