package ictsimulationpackage;

import java.util.ArrayList;

//
public class Kosu {
	static ArrayList<ArrayList<String>> Kosu = KosuHairetsu.ExceltoHairetsu();
	String hatsu_ku;
	String chaku_ku;
	String hatsu_bldg;
	String chaku_bldg;
	int jikan;//3??????
	double kosu;

	Kosu(int index, int time) {
		//
		this.hatsu_ku = Kosu.get(index).get(0);
		this.chaku_ku = Kosu.get(index).get(2);
		switch (Kosu.get(index).get(0)) {
		case "?????n??":
			this.hatsu_bldg = "?????n??";
			break;
		case "????":
			this.hatsu_bldg = "????";
			break;
		default:
			this.hatsu_bldg = Kosu.get(index).get(1);
		}
		switch (Kosu.get(index).get(2)) {
		case "?????n??":
			this.chaku_bldg = "?????n??";
			break;
		case "????":
			this.chaku_bldg = "????";
			break;
		default:
			this.chaku_bldg = Kosu.get(index).get(3);
		}
		//????????
		this.jikan = (time / 60) % 24;
		if (Kosu.get(index).get(4) != "") {
			this.kosu = Double.parseDouble(Kosu.get(index).get(jikan + 4));
			//jikan + 4????????????->???M?????G?X?P?[?v???????[?u
		} else {
			this.kosu = 0;
		}
	}

	int Occurrence(double m) {
		//?|?A?\?????z??]?????????N??????????
		int n;

		if (this.hatsu_ku.equals("?????n??")) {
			if (this.kosu * m < 2815.00707107201 * 2) {
				n = OccurrenceOfCalls.Occurrence(this.kosu * m / 60);
			} else {
				n = OccurrenceOfCalls.Occurrence(2815.00707107201 * 2 / 60);
			}
		}
		if (this.hatsu_ku.equals("????")) {
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
