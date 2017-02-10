package ictsimulationpackage;

/**
 * ネットワークのリンク
 * 基本的に回線数の管理のみを行う
 */
public class Link {
    private int hourLength = 24;

	private int occupiedCap;// 時刻tの接続回線数
	private int capacity;// 初期設計回線数
	private int capHis[];
	private int linkId;

	// 区内リンク：leftのビルのbidと一致
	// 区内中継リンク: 練馬荏原:200 荏原墨田:236 墨田練馬:271
	// 区外中継リンク:1000
	private Building right;
	private Building left;
	private boolean broken = false;

	//アーランB用
	private double trrafic[] = new double[hourLength];

	public Link(Building bldg) {
		// 区内リンク
		right = bldg.getBldgR();
		left = bldg;
		linkId = bldg.getBid();
	}

	public Link(Building bldg, int id) {
		//区内中継リンク
		left = bldg;
		right = bldg.getKunaiBldgR();
		this.linkId = id;
	}

	public Link(Building bldg1, Building bldg2) {
		// 区外中継リンク
		left = bldg1;
		right = bldg2;
		linkId = 1000;
	}

	// 回線数関連の初期化
	public void iniCap(int time, int cap) {
		occupiedCap = 0;
		capacity = cap;
		capHis = new int[time];
	}

    /**
     * ammount倍にリンクのキャパシティを変更する
     * 0の場合はリンクの設計回線数を0に
     * @param ammount 0 <= ammount < 1で想定
     */
	public void broken(double ammount) {
		if (0 < ammount && ammount < 1) {
			capacity *= ammount;
		} else if (ammount == 0) {
			broken = true;
		}else{
            System.out.println("Link capacity ammount error");
            System.out.println(1/0); //エラーはいて止める
        }
	}

    /**
     * もし使用回線数が設計回線数をうわ待っていた場合はエラー
     * @return 回線数に余裕があればfalse,なければtrue
     */
	public boolean isMaxCap() {
		if (occupiedCap == capacity) {
			return true;
		} else if (occupiedCap > capacity) {
            System.out.println("Link class isMaxCap Error");
            System.out.println(1/0);
        }
		return false;
	}

    /**
     * 使用回線数の増加
     * capacity <= occupiedCapとなったらエラー（一応）
     */
	public void addCap() {
		if (occupiedCap < capacity) {
			occupiedCap++;
		} else {
            System.out.println("Link class addCap error");
            System.out.println(1 / 0);
		}
	}

    /**
     * 使用回線数を減らす
     * 既にoccupiedCapが0以下の場合はエラー
     */
	public void subCap() {
		if (occupiedCap < 1) {
            System.out.println("Link calls subCap error");
            System.out.println(1/0);
        } else {
			occupiedCap--;
		}
	}

    /**
     * 現在の時刻のoccupiedcapを保存する
     * @param t
     */
    public void saveCap(int t) {
        capHis[t] = occupiedCap;
    }

    public void repair() {
        broken = false;
    }

	/** getter */
	public int getLinkId() {
		return linkId;
	}



	public int getOccupiedCap() {
		return occupiedCap;
	}

	public int[] getCapHis() {
		return capHis;
	}



	public int getCapacity() {
		return capacity;
	}

	double capHis() {
		
		double res  = -1;
		double ratio = (double) occupiedCap / (double) capacity;
		res = ratio * 100;
		
		return res;
	}

    public boolean isBroken() {
        return broken;
    }

    /**
     * リンクが使用可能かどうか返す
     * @return
     */
    public boolean isAvail() {
        return !isBroken() && !isMaxCap();
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
