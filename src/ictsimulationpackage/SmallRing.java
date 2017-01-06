package ictsimulationpackage;

import java.util.*;

public class SmallRing {

	/**
	 * this method is used for the route exploring in the same ring
	 * or in the areaLinks.
	 */
	ArrayList<Link> route(Building start, Building dest) {
		ArrayList<Link> linkList;

		//探索を右回りで行うかどうかはランダムにキマる
		boolean isRight = false;
		if(Math.random() > 0.5){
			isRight = true;
		}
		
		if (start.isAreaBldg() && dest.isAreaBldg()) {
			//中継ビル同士の探索
			if (isRight) {
				linkList = areaRightSearch(start, dest);
			} else {
				linkList = areaLeftSearch(start, dest);
			}

			//ルートが見つからなかったら逆回りで
			if (linkList.isEmpty()) {
				if (isRight) {
					linkList = areaRightSearch(start, dest);
				}else{
					linkList = areaLeftSearch(start, dest);
				}
			}
		}else{
			//同じ地区内ビル同士の探索
			if (isRight) {
				linkList = rightSearch(start, dest);
			} else {
				linkList = leftSearch(start, dest);
			}
			//ルートが見つからなかったら逆回りで
			if (linkList == null) {
				if (!isRight) {
					linkList = rightSearch(start, dest);
				} else {
					linkList = leftSearch(start, dest);
				}
			}
		}
		return linkList;
	}

	// 右回り探索（同じエリア内）
	ArrayList<Link> rightSearch(Building start, Building dest) {
		// test:右回り優先探索でやる
		ArrayList<Link> linkList = new ArrayList<Link>();
		Building bldg;
		bldg = start;

		//start かdestinationのどちらかが破壊されていたら無理
		if (start.isBroken() || dest.isBroken()) {
			return null;
		}

		//ルートの探索
		do {
			if(bldg.canGoRight()){
				//破壊リンクが有る場合||破壊ビル||リンクが埋まっている
				return null;
			}
			linkList.add(bldg.getLinkR());
			bldg = bldg.getBldgR();
		} while (bldg != dest && bldg != start && bldg != null);
		
		// destが存在しなかった場合
		if (bldg == start) {
			linkList = null;
		}
		return linkList;
	}

	// 左回り探索
	ArrayList<Link> leftSearch(Building start, Building dest) {
		ArrayList<Link> linkList = new ArrayList<Link>();
		Building bldg;
		bldg = start;

		//start かdestinationのどちらかが破壊されていたら無理
		if (start.isBroken() || dest.isBroken()) {
			return null;
		}

		//ルートの探索
		do {
			if(bldg.canGoLeft()){
				//破壊リンクがある場合
				return null;
			}
			linkList.add(bldg.getLinkL());
			bldg = bldg.getBldgL();
		} while (bldg != dest && bldg != start);

		// destが存在しなかった場合
		if (bldg == start) {
			linkList = null;
		}
		return linkList;
	}
	
	// 中継ビル右回り探索
	static ArrayList<Link> areaRightSearch(Building start, Building dest) {
		// test:右回り優先探索でやる
		ArrayList<Link> linkList = new ArrayList<Link>();
		Building bldg;
		bldg = start;
		boolean broken = false;

		//start かdestinationのどちらかが破壊されていたら無理
		if (start.isBroken() || dest.isBroken()) {
			return null;
		}

		do {
			if(bldg.canGoExRight()){
				//リンクが破壊されている場合
				return null;
			}
			linkList.add(bldg.getExLinkR());
			bldg = bldg.getExBldgR();
		} while (bldg != dest && bldg != start && bldg != null);

		// destが存在しなかった場合
		if (bldg == start) {
			linkList = null;
		}
		return linkList;
	}

	// 中継ビル左回り探索
	static ArrayList<Link> areaLeftSearch(Building start, Building dest) {
		ArrayList<Link> linkList = new ArrayList<Link>();
		Building bldg;
		bldg = start;

		//start かdestinationのどちらかが破壊されていたら無理
		if (start.isBroken() || dest.isBroken()) {
			return null;
		}

		do {
			if(bldg.canGoExLeft() ){
//				リンクが破壊されている
				return null;
			}
			linkList.add(bldg.getExLinkL());
			bldg = bldg.getExBldgL();
		} while (bldg != dest && bldg != start);
		
		// destが存在しなかった場合
		if (bldg == start) {
			linkList = null;
		}
		return linkList;
	}
	
}
