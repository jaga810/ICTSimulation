package ictsimulationpackage;

/**
 * Created by jaga on 10/3/16.
 */
public class Test {
    static public void main(String args[]) {
        new BuildingList(102);
        Building[] list= BuildingList.bldgList;
        for (Building bldg : list) {
            System.out.println(bldg.bid + " : " + bldg.bname);
        }
        double scale[]  =Settings.getScale();
        for(int i = 0; i < scale.length;i++) {
            System.out.println(scale[i]);
        }
    }
}
