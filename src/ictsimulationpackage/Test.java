package ictsimulationpackage;

import org.apache.commons.codec.binary.StringUtils;

import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * Created by jaga on 10/3/16.
 */
public class Test {
    static public void main(String args[]) {
        int loop = 1000000;
        long hist[] = new long[10];
        for(int i = 0; i < loop;i++) {
            int time = HoldingTime.OneHoldingTime();
            if (time < hist.length - 1) {
                hist[time]++;
            }else{
                hist[hist.length - 1]++;
            }
        }
        for(int i = 0; i < hist.length;i++) {
            System.out.println(hist[i]);
        }
    }
}
