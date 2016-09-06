package ictsimulationpackage;

public class Link {
	int capacity;// 時刻tの接続回線数
	int iniCap;// 初期設計回線数
	int capHis[];
	int id;
	// 区内リンク：leftのビルのbidと一致
	// 区内中継リンク: 練馬荏原:200 荏原墨田:240 墨田練馬:271
	// 区外中継リンク:1000
	Building right;
	Building left;
	boolean broken = false;

	Link(Building r, Building l, int n) {
		// 区内リンク、区内中継リンク
		right = r;
		left = l;
		id = n;
	}

	Link(Building bldg1, Building bldg2) {
		// 区外中継リンク
		left = bldg1;
		right = bldg2;
		id = 1000;
	}

	// 回線数関連の初期化
	void iniCap(int time, int cap) {
		capacity = 0;
		iniCap = cap;
		capHis = new int[time];
	}

	void broken(double ammount) {
		if (0 < ammount && ammount < 1) {
			iniCap *= ammount;
		} else if (ammount == 0) {
			broken = true;
		} else {
			int i = 1 / 0;
		}
	}

	// capacityが上限に達して いるかどうか
	boolean maxCap() {
		boolean val = false;
		if (capacity == iniCap) {
			val = true;
		} else if (capacity > iniCap) {
			try {
				throw new Exception();
			} catch (Exception e) {
				System.out.println("capacity can't go over inicap");
				e.printStackTrace();
			}
		}
		return val;
	}

	boolean addCap() {
		boolean val;
		if (capacity < iniCap) {
			val = true;
			capacity++;
		} else {
			val = false;
			System.out.println(1 / 0);
		}
		return val;
	}

	boolean subCap() {
		boolean val;
		if (capacity < 1) {
			val = false;
		} else {
			val = true;
			capacity--;
		}
		return val;
	}

	void saveCap(int t) {
		capHis[t] = capacity;
	}

	double capHis() {
		
		double res  = -1;
		double ratio = (double)capacity / (double)iniCap;
		res = ratio * 100;
		
		return res;
	}
}
