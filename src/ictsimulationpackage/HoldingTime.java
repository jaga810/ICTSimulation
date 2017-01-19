package ictsimulationpackage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;

//•Û—¯ŠÔ
public class HoldingTime {
    int OneHoldingTime(int regulation) {
        int limit = 1; // ’ÊMŠÔ‹K§FÅ‘å•ª”i‚O‚È‚ç‚Î‹K§‚È‚µj
        double tau;
        double lambda = 0.02166911;
        tau = -1.0 / lambda * Math.log(1.0 - Math.random());
        int time = (int) Math.round(tau / 60);

        // ’ÊMŠÔ‹K§
        if (regulation == 1) {
            if (limit > 0 && time > limit) {
                time = 1;
            }
        }

        return (time);
    }
}
