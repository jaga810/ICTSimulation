package ictsimulationpackage;

import java.util.*;

/**
 * ビルとリンクオブジェクトの生成と管理
 * コンストラクタを呼び出すことで通信ネットワークを構築
 * find系メソッドで特定のビルやリンクのオブジェクトを得る
 */
public class Network {
	//定数
	private final int groupNum = 3;
	private final int kunaiRelayBldgIdx[] = {0, 36, 71};
    private final int bldgNum = 102;//区外ビル含まない
    private final int kugaiRelayBldgId = 0;

	//データ読み込み
	private BuildingInfo bldgInfo[] = Settings.BldgInfo();
	private double[] scale;

	//various building lists
	private Building[] bldgList = new Building[bldgNum + 1];
	private Building[] kunaiRelayBldgList = new Building[bldgNum];
	private Building[] kunaiBldgList = new Building[bldgNum];
	private Building kugaiRelayBldg;

	//various link lists
	private ArrayList<Link> linkList = new ArrayList<Link>();
	private ArrayList<Link> exLinkList = new ArrayList<Link>();
	private ArrayList<Link> allLinkList = new ArrayList<Link>();

	//for search building from name
	private HashMap<String, Building> bldgIndex = new HashMap<String, Building>();

	//for search building from linkIndex
	private HashMap<Integer, Link> linkIndex = new HashMap<Integer, Link>();
	private Link outLink;


    /**
     * Bulidingオブジェクトの生成及びネットワークの形成を行う
     */
	Network() {
        initBuildings();

        //各リングの生成
        makeLocalRing();
		makeKunaiRelayRing();
		makeKugaiNodeAndLink(bldgList[kugaiRelayBldgId]);

		// リンク全体のリスト作成
		allLinkList.addAll(linkList);
		allLinkList.addAll(exLinkList);
		allLinkList.add(outLink);

		// ビルのインデックス（名前検索）の作成
		makeIndex();
		makeLinkIndex();

		//kosuの読み込み
		KosuDownloader.download(findBldg("区外"), this);
	}

    /**
     * ローカルリング上のビルをbidとbnameで初期化する
     */
    private void initBuildings() {
        for(int i = 0;i < bldgInfo.length; i++) {
            BuildingInfo info = bldgInfo[i];
            Building bldg = new Building(info);
            bldgList[bldg.getBid()] = bldg;
            kunaiBldgList[bldg.getBid()] = bldg;
        }
    }

    /**
     * ローカルリングトポロジーの作成
     */
    private void makeLocalRing() {
        for(int i = 0; i < groupNum;i++) {
            int kunaiIdx = kunaiRelayBldgIdx[i];
            Building kunaiBldg = bldgList[kunaiIdx];
            //この区内リングが最後ならば次の区内リングの開始idxを全体のビルの数に設定
            int nextKunaiIdx = i == groupNum - 1 ? bldgNum : kunaiRelayBldgIdx[(i + 1) % 3];

            for(int j = kunaiIdx ; j < nextKunaiIdx;j ++ ) {
                Building left  = bldgList[j];

                //リング上の23区内中継ビルを設定
                left.setKunaiRelayBuilding(kunaiBldg);

                //リング上で最後のビルならこのビルの右側を区内中継ビルに設定
                Building right = j == nextKunaiIdx - 1 ? bldgList[kunaiIdx]:bldgList[j + 1];

                //ビルの接続
                left. setBldgR(right);
                right.setBldgL(left);

                //ローカルリンクの作成
                Link ln = new Link(left);
                left.setLinkFor2Bldgs(ln);
                linkList.add(ln);
            }
        }
    }

    /**
     * 23区内中継リングトポロジーの作成
     */
    void makeKunaiRelayRing() {
        //区内中継ビルのリスト作成
        for(int i = 0;i < groupNum;i++) {
            kunaiRelayBldgList[i] = bldgList[kunaiRelayBldgIdx[i]];
        }

		//23区内中継リンクの作成とリングの接続
        for(int i = 0;i < groupNum;i++) {
            Building left  = kunaiRelayBldgList[i];
            Building right = kunaiRelayBldgList[(i + 1) % groupNum];

            left. setKunaiBldgR(right);
            right.setKunaiBldgL(left);

            Link ln = new Link( left, left.getBid() + 200);
            left.setKunaiLinks(ln);
            exLinkList.add(ln);
        }
	}

    /**
     * 仮想的な区外ビルとリンクの作成
     * @param bldg 区外ビルと区内の中継を行うビル
     */
	void makeKugaiNodeAndLink(Building bldg) {
		// 区外ビル（抽象）
		Building kugaiBldg = new Building("区外", bldgNum);
		kugaiBldg.setBldgL(bldg);
		bldgList[bldgNum] = kugaiBldg;
		this.kugaiRelayBldg = kugaiBldg;

		// 区外中継リンク
		Link ln = new Link(bldg, kugaiBldg);
		bldg.setKugaiLink(ln);
		outLink = ln;
	}

    /**
     * ビルオブジェクトの名前検索。見つからなければnullを返す
     * @param bname 検索したいビルの名前
     * @return
     */
	public Building findBldg(String bname) {
		Building bldg;
		try {
			bldg = bldgIndex.get(bname);
		} catch (Exception e) {
			e.printStackTrace();
			bldg = null;
		}

		return bldg;
	}

    /**
     * リンクオブジェクトのId検索。見つからなければnullを返す
     * @param id 欲しいLinkのLinkID
     * @return
     */
    public Link findLink(int id) {
        Link ln;
        try {
            ln = linkIndex.get(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ln;
    }

    private void makeIndex() {
        for (Building bldg : bldgList) {
            bldgIndex.put(bldg.getBname(), bldg);
        }
    }

	private  void makeLinkIndex() {
		for (Link ln : allLinkList) {
			linkIndex.put(ln.getId(), ln);
		}
	}

    /**
     * リンクは接続回線数を0に
     * ビルはbrokenフラグをfalseに
     */
	public void resetBroken(){
		for(Link ln : allLinkList){
			ln.repair();
		}
		for(Building bldg : bldgList){
			bldg.repair();
		}
	}

    /**
     * 中継ビルの中で、limitの数になるように破壊確率が高いものからビルを壊す
     * limit = 0なら確率に応じてビルを破壊。数はランダム
     * @param limit 破壊するビルの数
     */
	public void brokenByQuake(int limit) {
		Pair ps[] = new Pair[102];
		int idx = 0;
		for (Building bldg : kunaiBldgList) {
			ps[idx++] = new Pair((Math.pow(scale[bldg.getBid()], 4) / 30000) / Math.random(), bldg);
		}
		Arrays.sort(ps);
		if (limit == 0) {
			//破壊数無制限の場合
			for (int i = 0; i < ps.length; i++) {
				if (ps[i].prob > 1) {
					ps[i].bldg.makeBroken();
				}
			}
		} else {
			int cnt = 0;
			for (int i = 0; i < ps.length && cnt < limit; i++) {
				ps[i].bldg.makeBroken();
				cnt++;
			}
		}
	}

	/** getter */
	public ArrayList<Link> getLinkList() {
		return linkList;
	}

	public ArrayList<Link> getExLinkList() {
		return exLinkList;
	}

	public Link getOutLink() {
		return outLink;
	}

	public ArrayList<Link> getAllLinkList(){
		return allLinkList;
	}

	public Building getKugaiRelayBldg() {
		return kugaiRelayBldg;
	}

	public Building[] getBldgList() {
		return bldgList;
	}

    public void getScale() {
        scale = Settings.getScale(this);
    }

	/** etc */
	private class Pair implements Comparable {
		double prob;
		Building bldg;

		Pair(double a, Building b) {
			prob = a;
			bldg = b;
		}

		public int compareTo(Object other) {
			Pair p1 = (Pair) other;
//            return this.prob - ((Pair) other).first; // IDの値に従い昇順で並び替えたい場合
//         return -(this.prob - ((Pair) other).prob); // IDの値に従い降順で並び替えたい場合
			double val = (this.prob - ((Pair) other).prob);
			if (val == 0) {
				return 0;
			} else if (val > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}
