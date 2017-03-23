package ictsimulationpackage;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Utilityクラス
 * 汎用性のある処理をまとめたクラス。
 * 全てのメソッドはクラスメソッドになっている
 */
public class Utility {
    public static void initHashMap(HashMap[] map) {
        for (int i = 0; i < map.length; i++) {
            map[i] = new HashMap();
        }
    }

    /**
     * 1/2の確率でtrueを返す
     *
     * @return
     */
    public static boolean halfProb() {
        return Math.random() > 0.5;
    }

    /**
     * とりあえずエラーが出したい時につかう
     */
    public static void error() {
        System.out.println(1 / 0);
    }

    public static double[] arrayIntoHour(double[] array) {
        int hour = array.length / 60;
        double val[] = new double[hour];
        for (int h = 0; h < hour; h++) {
            for (int i = 0; i < 60; i++) {
                int index = 60 * h + i;
                val[h] += array[index];
            }
        }
        return val;
    }

    public static double[] arrayIntoHour(int[] array) {
        int hour = array.length / 60;
        double val[] = new double[hour];
        for (int h = 0; h < hour; h++) {
            for (int i = 0; i < 60; i++) {
                int index = 60 * h + i;
                val[h] += array[index];
            }
        }
        return val;
    }

    public static double[] arrayIntoHourRate(double[] array) {
        int hour = array.length / 60;
        double val[] = new double[hour];
        for (int h = 0; h < hour; h++) {
            for (int i = 0; i < 60; i++) {
                int index = 60 * h + i;
                val[h] += array[index];
            }
            val[h] /= 60;
        }
        return val;
    }

    public static double maxInArray(double[] array) {
        if (array.length == 0) {
            return 0;
        }
        double val = 0;
        for (int i = 0; i < array.length; i++) {
            if (val < array[i]) {
                val = array[i];
            }
        }
        return val;
    }

    public static BigDecimal maxInArray(BigDecimal[] array) {
        if (array.length == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal val = BigDecimal.ZERO;

        for (int i = 0; i < array.length; i++) {
            if (val.compareTo(array[i]) < 0) {
                val = array[i];
            }
        }
        return val;
    }

    public static double minInArray(double[] array) {
        if (array.length == 0) {
            return 0;
        }
        double val = Double.MAX_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (val > array[i]) {
                val = array[i];
            }
        }
        return val;
    }

    public static double aveInArray(double[] array) {
        if (array.length == 0) {
            return 0;
        }
        double sum = sumInArray(array);

        return sum / array.length;
    }

    public static double sumInArray(double[] array) {
        if (array.length == 0) {
            return 0;
        }
        double sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static int sumInArray(int[] array) {
        if (array.length == 0) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static double minMaxInArray(double array[]) {
        if (array.length == 0) {
            return 0;
        }

        return maxInArray(array) - minInArray(array);
    }


    public class Timer {
        double start;

        Timer() {
            start = System.nanoTime();
        }

        public double getSec() {
            return (System.nanoTime() - start) * 1.0e9;
        }

        public String get() {
            return refineTimeExpression((int) getSec());
        }
    }

    public static String refineTimeExpression(int dur) {
        int days = dur / (60 * 60 * 24);
        dur %= 60 * 60 * 24;

        int hour = dur / (60 * 60);
        dur %= 60 * 60;

        int min = dur / 60;
        int sec = dur % 60;

        return (days + "d " + hour + "h " + min + "m " + sec + "s");
    }
}
