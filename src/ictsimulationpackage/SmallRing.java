package ictsimulationpackage;

import java.util.*;

public class SmallRing {
	static ArrayList<Link> route(Building start, Building dest) {
		ArrayList<Link> link = new ArrayList<Link>();
		boolean isRight = false;
		if(Math.random() > 0.5){
			isRight = true;
		}
		
		//区外へのリンク
		if((start.bid == 102 || dest.bid ==102)){
			if(start.bid == 0){
				//練馬 -> 区外
				link.add(start.outLink);
			} else if(dest.bid == 0){
				//区外 -> 練馬
				link.add(dest.outLink);
			}else{
				return null;
			}
		}
		
		if (start.exBuilding && dest.exBuilding) {
			//中継ビル同士の探索
			if (isRight) {
				link = areaRightSearch(start, dest);
			} else {
				link = areaLeftSearch(start, dest);
			}
		}else{
			//同じ地区内ビル同士の探索
			if (isRight) {
				link = rightSearch(start, dest);
			} else {
				link = leftSearch(start, dest);
			}
		}
		//探索で見つからなかった場合（破壊ビルが紛れていた場合||接続回線数が上限に達していた場合）
		if(link == null){
			if (start.exBuilding && dest.exBuilding) {
				//中継ビル同士の探索
				if (!isRight) {
					link = areaRightSearch(start, dest);
				} else {
					link = areaLeftSearch(start, dest);
				}
			}else{
				//同じ地区内ビル同士の探索
				if (!isRight) {
					link = rightSearch(start, dest);
				} else {
					link = leftSearch(start, dest);
				}
			}
		}
		return link;
	}

	// 右回り探索
	static ArrayList<Link> rightSearch(Building start, Building dest) {
		// test:右回り優先探索でやる
		ArrayList<Link> link = new ArrayList<Link>();
		Building bldg;
		bldg = start;
		boolean broken = false;
		do {
			if(bldg.linkR.broken || bldg.broken || bldg.linkR.maxCap()){
				//破壊リンクが有る場合||破壊ビル||リンクが埋まっている
				return null;
			}
			link.add(bldg.linkR);
			bldg = bldg.bldgR;
		} while (bldg != dest && bldg != start && bldg != null);
		
		// destが存在しなかった場合
		if (bldg == start) {
			link = null;
		}
		return link;
	}

	// 左回り探索
	static ArrayList<Link> leftSearch(Building start, Building dest) {
		ArrayList<Link> link = new ArrayList<Link>();
		Building bldg;
		bldg = start;
		boolean broken = false;
		do {
			if(bldg.linkL.broken || bldg.broken || bldg.linkL.maxCap()){
				//破壊リンクがある場合
				return null;
			}
			link.add(bldg.linkL);
			bldg = bldg.bldgL;
		} while (bldg != dest && bldg != start);

		// destが存在しなかった場合
		if (bldg == start) {
			link = null;
		}
		return link;
	}
	
	// 中継ビル右回り探索
	static ArrayList<Link> areaRightSearch(Building start, Building dest) {
		// test:右回り優先探索でやる
		ArrayList<Link> link = new ArrayList<Link>();
		Building bldg;
		bldg = start;
		boolean broken = false;
		do {
			if(bldg.exLinkR.broken || bldg.broken| bldg.exLinkR.maxCap()){
				//リンクが破壊されている場合
				return null;
			}
			link.add(bldg.exLinkR);
			bldg = bldg.exBldgR;
		} while (bldg != dest && bldg != start && bldg != null);

		// destが存在しなかった場合
		if (bldg == start) {
			link = null;
		}
		return link;
	}

	// 中継ビル左回り探索
	static ArrayList<Link> areaLeftSearch(Building start, Building dest) {
		ArrayList<Link> link = new ArrayList<Link>();
		Building bldg;
		bldg = start;
		boolean broken = false;
		do {
			if(bldg.exLinkL.broken || bldg.broken || bldg.exLinkL.maxCap()){
//				リンクが破壊されている
				return null;
			}
			link.add(bldg.exLinkL);
			bldg = bldg.exBldgL;
		} while (bldg != dest && bldg != start);
		
		// destが存在しなかった場合
		if (bldg == start) {
			link = null;
		}
		return link;
	}
	
}
