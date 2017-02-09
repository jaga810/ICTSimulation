package ictsimulationpackage;

/**
 * Created by jaga on 2/9/17.
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
