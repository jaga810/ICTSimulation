package ictsimulationpackage;

import java.util.HashMap;

/**
 * Created by jaga on 2/9/17.
 */
public class Utility {
    public static void initHashMap(HashMap[] map) {
        for(int i = 0; i < map.length;i++) {
            map[i] = new HashMap();
        }
    }
}
