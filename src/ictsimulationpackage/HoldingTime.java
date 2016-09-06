package ictsimulationpackage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;

//保留時間
public class HoldingTime {
	static int method;

	HoldingTime(int method) {
		this.method = method;
	}

	static int OneHoldingTime() {
		int regulation = 0; // 通信時間規制：最大分数（０ならば規制なし）
		double tau;
		double lambda = 0.02166911;
		tau = -1.0 / lambda * Math.log(1.0 - Math.random());
		int time = (int) Math.round(tau / 60);

		// 通信時間規制
		if (method == 1) {
			if (regulation > 0 && time > regulation) {
				time = 1;
			}
		}

		return (time);
	}
}
