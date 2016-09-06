package ictsimulationpackage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.StringTokenizer;

//�ۗ�����
public class HoldingTime {
	static int method;

	HoldingTime(int method) {
		this.method = method;
	}

	static int OneHoldingTime() {
		int regulation = 0; // �ʐM���ԋK���F�ő啪���i�O�Ȃ�΋K���Ȃ��j
		double tau;
		double lambda = 0.02166911;
		tau = -1.0 / lambda * Math.log(1.0 - Math.random());
		int time = (int) Math.round(tau / 60);

		// �ʐM���ԋK��
		if (method == 1) {
			if (regulation > 0 && time > regulation) {
				time = 1;
			}
		}

		return (time);
	}
}
