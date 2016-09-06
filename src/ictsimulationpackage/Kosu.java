package ictsimulationpackage;

import java.util.ArrayList;

//
public class Kosu {
	static ArrayList<ArrayList<String>> Kosu = KosuHairetsu.ExceltoHairetsu();
	String hatsu_ku;
	String chaku_ku;
	String hatsu_bldg;
	String chaku_bldg;
	int jikan;//3時代など
	double kosu;

	Kosu(int index, int time) {
		//
		this.hatsu_ku = Kosu.get(index).get(0);
		this.chaku_ku = Kosu.get(index).get(2);
		switch (Kosu.get(index).get(0)) {
		case "多摩地区":
			this.hatsu_bldg = "多摩地区";
			break;
		case "他県":
			this.hatsu_bldg = "他県";
			break;
		default:
			this.hatsu_bldg = Kosu.get(index).get(1);
		}
		switch (Kosu.get(index).get(2)) {
		case "多摩地区":
			this.chaku_bldg = "多摩地区";
			break;
		case "他県":
			this.chaku_bldg = "他県";
			break;
		default:
			this.chaku_bldg = Kosu.get(index).get(3);
		}
		//呼数の受け取り
		this.jikan = (time / 60) % 24;
		if (Kosu.get(index).get(4) != "") {
			this.kosu = Double.parseDouble(Kosu.get(index).get(jikan + 4));
			//jikan + 4という引数は?->発信情報をエスケープするための措置
		} else {
			this.kosu = 0;
		}
	}

	int Occurrence(double m) {
		//ポアソン分布に従って呼が生起した数を返す
		int n;

		if (this.hatsu_ku.equals("多摩地区")) {
			if (this.kosu * m < 2815.00707107201 * 2) {
				n = OccurrenceOfCalls.Occurrence(this.kosu * m / 60);
			} else {
				n = OccurrenceOfCalls.Occurrence(2815.00707107201 * 2 / 60);
			}
		}
		if (this.hatsu_ku.equals("他県")) {
			if (this.kosu * m < 5908.49092777896 * 2) {
				n = OccurrenceOfCalls.Occurrence(this.kosu * m / 60);
			} else {
				n = OccurrenceOfCalls.Occurrence(5908.49092777896 * 2 / 60);
			}
		}else {
			n = OccurrenceOfCalls.Occurrence(this.kosu * m / 60);
		}
		return n;
	}

}
