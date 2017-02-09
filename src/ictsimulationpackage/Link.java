package ictsimulationpackage;

public class Link {
	private int capacity;// 時刻tの接続回線数
	private int iniCap;// 初期設計回線数
	private int capHis[];
	private int id;
	// 区内リンク：leftのビルのbidと一致
	// 区内中継リンク: 練馬荏原:200 荏原墨田:236 墨田練馬:271
	// 区外中継リンク:1000
	private Building right;
	private Building left;
	private boolean broken = false;

	//アーランB用
	private double trrafic[] = new double[24];

	Link(Building bldg) {
		// 区内リンク
		right = bldg.getBldgR();
		left = bldg;
		id = bldg.getBid();
	}

	Link(Building bldg, int id) {
		//区内中継リンク
		left = bldg;
		right = bldg.getKuaniBldgR();
		this.id = id;
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

	public int getId() {
		return id;
	}

	public void repair() {
		broken = false;
	}

	public boolean isBroken() {
		return broken;
	}

	public int getCapacity() {
		return capacity;
	}

	public int[] getCapHis() {
		return capHis;
	}

	void saveCap(int t) {
		capHis[t] = capacity;
	}

	public int getIniCap() {
		return iniCap;
	}

	double capHis() {
		
		double res  = -1;
		double ratio = (double)capacity / (double)iniCap;
		res = ratio * 100;
		
		return res;
	}


	//引数の時間帯のトラフィックを返す
	public double getTrrafic(int h) {
		return trrafic[h];
	}


	public double addTrrafic(int time,double val) {
		trrafic[time] += val;
		return trrafic[time];
	}

	public double getMaxTrrafic() {
		return Output.maxInArray(trrafic);
	}
}
