package ictsimulationpackage;

public class CallUtility {
    /**
     * 与えられた呼数に従って、ポアソン分布に基づき生起呼数を決定
     * @param avgKosu 1分単位での平均呼数
     * @return     ポアソン分布に従った呼数
     */
	public static int occurredCallsNum(double avgKosu) {
		int poissonKosu = 0;
		if (avgKosu < 600) {
			poissonKosu = calcPoissonRandomNum(avgKosu);
		} else {
            //なぜこの場合分けが必要なのかわからなかったが残してある...
            //kosuをさらに60で割って1s単位の平均呼数にし、それを1分に渡って足し合わせているが...
			avgKosu /= 60;
			for (int i = 0; i < 60; i++) {
				poissonKosu += calcPoissonRandomNum(avgKosu);
			}
		}
		return poissonKosu;
	}

    /**
     * 平均lamdaのポアソン乱数を発生させる
     * 参考 http://www.ishikawa-lab.com/montecarlo/4shou.html#4.3
     * @param lamda 平均
     * @return
     */
	private static int calcPoissonRandomNum(double lamda) {
		double xp = Math.random();
		int k = 0;
		while (xp >= Math.exp(-lamda)) {
			xp *=  Math.random();
			k = k + 1;
		}
		return (k);
	}

}
