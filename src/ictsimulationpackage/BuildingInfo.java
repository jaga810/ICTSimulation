package ictsimulationpackage;

/**
 * Created by jaga on 2/9/17.
 */

/**
 * 中継びるの名前とID、及びリング上の中継ビルのIDを読み込むのに使用する
 */
public class BuildingInfo {
    private int bid;
    private String bname;
    private int kunaiRelayBldgId;

    public int getBid() {
        return bid;
    }

    public String getBname() {
        return bname;
    }

    public int getKunaiRelayBldgId() {
        return kunaiRelayBldgId;
    }

    public BuildingInfo(int bid, String bname, int kunaiRelayBldgId) {
        this.bid = bid;
        this.bname = bname;
        this.kunaiRelayBldgId = kunaiRelayBldgId;
    }
}
