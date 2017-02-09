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

    /**
     * 1/2の確率でtrueを返す
     * @return
     */
    public static boolean halfProb() {
        return Math.random() > 0.5;
    }

    /**
     * とりあえずエラーが出したい時
     */
    public static void error() {
        System.out.println(1/0);
    }
}
