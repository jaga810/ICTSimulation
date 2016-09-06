package ictsimulationpackage;

import java.util.*;

public class BuildingList {
	static Building[] bldgList = new Building[103];
	static Building[] preBldgList = new Building[103];
	static Building[] exBldgList = new Building[3];
	static Building[] startBldgList = new Building[102];
	static ArrayList<Link> linkList = new ArrayList<Link>();
	static ArrayList<Link> exLinkList = new ArrayList<Link>();
	static ArrayList<Link> allLinkList = new ArrayList<Link>();
	static HashMap<String, Building> bldgIndex = new HashMap<String, Building>();
	static HashMap<Integer, Link> linkIndex = new HashMap<Integer, Link>();
	static Link outLink;
	static Building first[] = new Building[3];
	static Building last[] = new Building[3];
	static int bldgNumNow = 0;// 現在格納しているビルデータ数
	int exBldgIndex[] = {0, 36, 71};

	// bldgNum=使用するビルの数
	BuildingList(int bldgNum) {
		System.out.println("set up building data");
		// 双方向リスト作成用変数の初期化
		for (int i = 0; i < 3; i++) {
			first[i] = new Building();
			last[i] = new Building();
		}
		// ビルの初期化（環状双方向リスト）
		int roop = bldgNum;
		for (int i = bldgNumNow; i < roop; i++) {
			if (i < 36) {
				// 練馬区内リンク add(index , 区識別ナンバー)
				add(i, 0);
			} else if ( 36 <= i && i < 71) {
				// 荏原区内リンク
				add(i, 1);
			} else {
				// 墨田区内リンク
				add(i, 2);
			}
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
		// 区内リンク： 連結 + リンク生成
		for (int i = 0; i < 3; i++) {
			first[i].bldgR.bldgL = last[i].bldgL;
			last[i].bldgL.bldgR = first[i].bldgR;
			Building bldg;
			bldg = first[i].bldgR;
			do {
				Link ln = new Link(bldg.bldgR, bldg, bldg.bid);
				bldg.linkR = ln;
				bldg.bldgR.linkL = ln;
				bldg = bldg.bldgR;
				linkList.add(ln);
			} while (bldg != first[i].bldgR);
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
		KosuDownloader.download();
		//idを元に戻す
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
	void add(int index, int group) {
		// group 0:練馬区 1:荏原区 2:墨田区
		Building bldg = new Building(index);
		// System.out.println(index + ":" + bldg.bname);
		bldgList[index] = bldg;
		startBldgList[index] = bldg;
		bldgNumNow++;
		if (first[group].bldgR == null) {
			first[group].bldgR = last[group].bldgL = bldg;
			bldg.bldgL = first[group];
			bldg.bldgR = last[group];
		} else {
			bldg.bldgL = last[group].bldgL;
			last[group].bldgL.bldgR = bldg;
			last[group].bldgL = bldg;
			bldg.bldgR = last[group];
		}
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
			// System.out.println("called");
			Link ln = new Link(bldg.exBldgR, bldg, bldg.bid + 200);
			bldg.exLinkR = ln;
			bldg.exBldgR.exLinkL = ln;
			bldg = bldg.exBldgR;
			exLinkList.add(ln);
		} while (bldg != bldg0);
		// リストへ追加
		exBldgList[0] = bldg0;
		exBldgList[1] = bldg40;
		exBldgList[2] = bldg71;
	}

	// 区外中継ビルの設定
	void outBuilding(Building bldg) {
		// 区外中継ビルフラグ
		bldg.outBuilding = true;
		// 区外ビル（抽象）
		Building outBldg = new Building();
		outBldg.bldgL = bldg;
		outBldg.bname = "区外";
		outBldg.bid = bldgNumNow;
		outBldg.areaBldg = outBldg;
//		System.out.println("outBldg:" + outBldg.bname + "," + outBldg.bid);
		bldgList[102] = outBldg;
		bldgNumNow++;
		// 区外中継リンク
		Link ln = new Link(bldg, outBldg);
		bldg.outLink = ln;
		outLink = ln;
	}

	void makeIndex() {
		for (Building bldg : bldgList) {
			bldgIndex.put(bldg.bname, bldg);
		}
	}

	// ビルの名前検索。見つからなければnullを返す
	static Building findBldg(String bname) {
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
			linkIndex.put(ln.id, ln);
		}
	}

	static Link findLink(int i) {
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
			ln.broken = false;
		}
		for(Building bldg : bldgList){
			bldg.broken = false;
		}
	}
}
