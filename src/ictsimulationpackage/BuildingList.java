package ictsimulationpackage;

import java.util.*;

public class BuildingList {
	//定数
	private final int groupNum = 3;

	//データ読み込み
	String[] bldgName = Settings.BldgName();
	double[] scale;

	//various building lists
	private Building[] bldgList = new Building[103];
	private Building[] preBldgList = new Building[103];
	private Building[] exBldgList = new Building[3];
	private Building[] startBldgList = new Building[102];

	//various link lists
	private ArrayList<Link> linkList = new ArrayList<Link>();
	private ArrayList<Link> exLinkList = new ArrayList<Link>();
	private ArrayList<Link> allLinkList = new ArrayList<Link>();

	//for search building from name
	private HashMap<String, Building> bldgIndex = new HashMap<String, Building>();

	//for search building from linkIndex
	private HashMap<Integer, Link> linkIndex = new HashMap<Integer, Link>();
	private Link outLink;
	private Building first[] = new Building[3];
	private Building last[] = new Building[3];
	private int bldgNumNow = 0;// 現在格納しているビルデータ数
	private int exBldgIndex[] = {0, 36, 71};

	// bldgNum=使用するビルの数
	BuildingList(int bldgNum) {
		// 双方向リスト作成用変数の初期化
		for (int i = 0; i < groupNum; i++) {
			first[i] = new Building();
			last[i] = new Building();
			first[i].setBldgR(last[i]);
			last[i].setBldgL(first[i]);
		}
		// ビルの初期化（双方向リスト）
		int roop = bldgNum;
		for (int i = bldgNumNow; i < roop; i++) {
			if (i < 36) {
				// 練馬区内リンク add(index , 区識別ナンバー)
				addBldg(i, 0);
			} else if ( 36 <= i && i < 71) {
				// 荏原区内リンク
				addBldg(i, 1);
			} else {
				// 墨田区内リンク
				addBldg(i, 2);
			}
		}
		//first,lastを削除して環状リストに
		for(int i = 0; i < groupNum;i ++) {
			first[i].getBldgR().setBldgL(last[i].getBldgL());
			last[i].getBldgL().setBldgR(first[i].getBldgR());
		}

		// areaBldgセット
		for (int i = 0; i < roop; i++) {
			if (i < 36) {
				// 練馬区内リンク add(index , 区識別ナンバー)
				setArea(i, 0);
			} else if (36 <= i && i < 71) {
				// 荏原区内リンク
				setArea(i, 1);
			} else {
				// 墨田区内リンク
				setArea(i, 2);
			}
		}

		// 区内リンク：リンク生成
		for (int i = 0; i < groupNum; i++) {
			Building bldg = first[i].getBldgR();
			do {
				Link ln = new Link(bldg);
				bldg.setLinks(ln);
				linkList.add(ln);

				bldg = bldg.getBldgR();
			} while (bldg != first[i].getBldgR());
		}

		// 区内中経リンク作成
		exBuilding();

		// 区外中継リンク作成
		outBuilding(bldgList[0]);

		// リンク全体のリスト作成
		allLinkList.addAll(linkList);
		allLinkList.addAll(exLinkList);
		allLinkList.add(outLink);

		// ビルのインデックス（名前検索）の作成
		makeIndex();
		makeLinkIndex();

		//呼数のセット
		preBldgList = bldgList;

		//kosuの読み込み
		KosuDownloader.download(findBldg("区外"), this);
	}

	// areaBldgを
	void setArea(int index, int group) {
		// group 0:練馬区 1:荏原区 2:墨田区
		Building bldg = bldgList[index];
		// System.out.println(index+":"+bldg.bname);
		Building area = null;
		switch (group) {
		case 0:
			area = bldgList[exBldgIndex[0]];
			break;
		case 1:
			area = bldgList[exBldgIndex[1]];
			break;
		case 2:
			area = bldgList[exBldgIndex[2]];
			break;
		}
		bldg.setArea(area);
	}

	// idを引数としてビルを後ろへと追加 group=区内接続リンク
	void addBldg(int index, int group) {
		// group 0:練馬区 1:荏原区 2:墨田区
		Building bldg = new Building(index, this, bldgName);
		//インデックスに追加
		bldgList[index] = bldg;
		startBldgList[index] = bldg;
		bldgNumNow++;
		//ビルの挿入
		last[group].getBldgL().insertBldgR(bldg);
	}

	// 区内中継リンクの作成 練馬=0, 荏原=40, 墨田=71
	void exBuilding() {
		// 区内中継リンクフラグ=true
		Building bldg0 = bldgList[exBldgIndex[0]];
		Building bldg40 = bldgList[exBldgIndex[1]];
		Building bldg71 = bldgList[exBldgIndex[2]];

		bldg0.external();
		bldg40.external();
		bldg71.external();

		// 左右のビルの入力
		bldg0.setExBldg(bldg40, bldg71);
		bldg40.setExBldg(bldg71, bldg0);
		bldg71.setExBldg(bldg0, bldg40);

		// 左右のリンク作成
		Building bldg = bldg0;
		do {
			Link ln = new Link( bldg, bldg.getBid() + 200);
			bldg.setExLinks(ln);
			exLinkList.add(ln);

			bldg = bldg.getExBldgR();
		} while (bldg != bldg0);
		// リストへ追加
		exBldgList[0] = bldg0;
		exBldgList[1] = bldg40;
		exBldgList[2] = bldg71;
	}

	// 区外中継ビルの設定
	void outBuilding(Building bldg) {
		// 区外中継ビルフラグ
		bldg.setOutBuilding(true);
		// 区外ビル（抽象）
		Building outBldg = new Building("区外", bldgNumNow++);
		outBldg.setBldgL(bldg);
		bldgList[102] = outBldg;
		// 区外中継リンク
		Link ln = new Link(bldg, outBldg);
		bldg.setOutLink(ln);
		outLink = ln;
	}

	void makeIndex() {
		for (Building bldg : bldgList) {
			bldgIndex.put(bldg.getBname(), bldg);
		}
	}

	// ビルの名前検索。見つからなければnullを返す
	Building findBldg(String bname) {
		Building bldg;
		try {
			bldg = bldgIndex.get(bname);
		} catch (Exception e) {
			e.printStackTrace();
			bldg = null;
		}

		return bldg;
	}

	void makeLinkIndex() {
		Link link;
		for (Link ln : allLinkList) {
			linkIndex.put(ln.getId(), ln);
		}
	}

	Link findLink(int i) {
		Link ln;
		try {
			ln = linkIndex.get(i);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ln;
	}
	
	void resetBroken(){
		for(Link ln : allLinkList){
			ln.repair();
		}
		for(Building bldg : bldgList){
			bldg.repair();
		}
	}

	public void getScale() {
		scale = Settings.getScale(this);
	}

	public void brokenByQuake(int limit) {
		Pair ps[] = new Pair[102];
		int idx = 0;
		for (Building bldg :startBldgList) {
			ps[idx++] = new Pair((Math.pow(scale[bldg.getBid()], 4) / 30000) / Math.random(), bldg);
		}
		Arrays.sort(ps);
		if (limit == 0) {
			//破壊数無制限の場合
			for (int i = 0; i < ps.length; i++) {
				if (ps[i].prob > 1) {
					ps[i].bldg.broken();
				}
			}
		} else {
			int cnt = 0;
			for (int i = 0; i < ps.length && cnt < limit; i++) {
				ps[i].bldg.broken();
				cnt++;
			}
		}
	}

	public Building[] getStartBldgList() {
		return startBldgList;
	}

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

	public Building[] getBldgList() {
		return bldgList;
	}
	class Pair implements Comparable {
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
