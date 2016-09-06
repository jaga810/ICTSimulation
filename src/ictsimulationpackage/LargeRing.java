package ictsimulationpackage;

import java.util.ArrayList;
import java.util.Collections;

public class LargeRing {
	// startとdestにビルを入れるとそのルート上のリンクのリストを返す
	static ArrayList<Link> route(Building start, Building dest) {
		ArrayList<Link> link = new ArrayList<Link>();

		boolean isOutBldg = isOutBldg(start, dest);
		if (isOutBldg) {
			link = outRoute(start, dest);
			return link;
		}

		boolean isSameArea = isSameArea(start, dest);
		if (isSameArea) {
			link = inRoute(start, dest);
		} else {
			link = exRoute(start, dest);
		}
		return link;
	}

	// 区外を含んだルートを決定する
	static ArrayList<Link> outRoute(Building start, Building dest) {
		// 変数初期化
		ArrayList<Link> link = new ArrayList<Link>();
		Building dAreaBldg = null;
		Building bldg = null;
		Building nerima = BuildingList.findBldg("練馬");
		// 変数代入
		dAreaBldg = dest.areaBldg;
		if( BuildingList.outLink.maxCap()){
			return null;
		}

		// 区外 -> 区内
		if (start.bid == 102) {
			// 区外から練馬
			if (nerima.outLink.broken || start.broken || nerima.broken) {
				// リンクまたはビル壊れている
				return null;
			} else {
				// 健全
				link.add(nerima.outLink);
			}
			// ゴールが練馬ならここでおわり
			if (dest == nerima) {
				return link;
			}
			// 練馬からゴール ルートがnullで例外発生
			try {
				link.addAll(route(nerima, dest));
			} catch (Exception e) {
				// System.out.println("outRoute->route(nerima, dest) is null");
				return null;
			}
		} else {
			// 区内 -> 区外
			// 区内から練馬
			if (start == nerima) {
				link.add(nerima.outLink);
			} else {
				link = route(start, nerima);
				if (link == null ) {
					// ルートが見つからない場合の処理
					return null;
				}
				// 練馬から区外
				if (nerima.outLink.broken || start.broken || nerima.broken) {
					// リンクまたはビル壊れている
					return null;
				} else {
					link.add(nerima.outLink);
				}
			}
		}
		return link;
	}

	// 違う区に向けてルートを決定する
	static ArrayList<Link> exRoute(Building start, Building dest) {
		// 変数初期化
		ArrayList<Link> link = new ArrayList<Link>();
		Building sAreaBldg = null;
		Building dAreaBldg = null;
		Building bldg = null;
		// 変数代入
		sAreaBldg = start.areaBldg;
		dAreaBldg = dest.areaBldg;

		// ルートにnullがあると例外発生
		try {
			// スタートからスタートエリア
			if (start != start.areaBldg) {
				// startが中継ビルの場合のエスケープ
				link.addAll(inRoute(start, sAreaBldg));
			}
			// スタートエリアからゴールエリア
			link.addAll(inRoute(sAreaBldg, dAreaBldg));
			// ゴールエリアからゴール
			if (dest != dest.areaBldg) {
				link.addAll(inRoute(dAreaBldg, dest));
			}
		} catch (Exception e) {
			return null;
		}
		return link;
	}

	// 同じくないのビル間、exRoopのビル間、各リングの一周で済む区間の探索
	static ArrayList<Link> inRoute(Building start, Building dest) {
		ArrayList<Link> link = new ArrayList<Link>();
		link = SmallRing.route(start, dest);
		return link;
	}

	// 同じ区内に出発地と目的地が存在するか
	static boolean isSameArea(Building start, Building dest) {
		boolean isSameArea = false;
		if (start.areaBldg == dest.areaBldg) {
			isSameArea = true;
		}
		return isSameArea;
	}

	// 区外かいなか
	static boolean isOutBldg(Building start, Building dest) {
		boolean isOutBldg = false;
		// System.out.println("the bid = dest:" + dest.bid + " start:" +
		// start.bid);
		if (start.bid == 102 || dest.bid == 102) {
			isOutBldg = true;
		}
		return isOutBldg;
	}
}
