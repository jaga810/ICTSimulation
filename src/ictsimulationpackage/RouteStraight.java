package ictsimulationpackage;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class RouteStraight {
	static ArrayList<Integer> LinkListPlus = Settings.LinkListPlus();
	static ArrayList<Integer> LinkListMinus = Settings.LinkListMinus();
	static String[] BldgName = Settings.BldgName();
	static int[] exchangeID = Settings.exchangeID();
	
	//route[i]：番号iのビルに呼が通った回数。中継器を使用しない場合
	static int[] route(String hatsu, String chaku, int[] broken, double ammount) {
		int hatsuID = 0;
		int chakuID = 0;
		// String hatsu = "練馬";
		// String chaku = "練馬";
		String HatsuBldgName = BldgName[0];
		String ChakuBldgName = BldgName[0];
		// System.out.println(HatsuBldgName);
		// System.out.println(ChakuBldgName);
		int id = 0;
		// 発信ビルのID取得
		while (HatsuBldgName.toString().equals(hatsu) == false) {
			HatsuBldgName = BldgName[id];
			hatsuID = id;
			id++;
		}
		id = 0;
		// 到着ビルのID取得
		while (ChakuBldgName.toString().equals(chaku) == false) {
			ChakuBldgName = BldgName[id];
			chakuID = id;
			id++;
		}

		// System.out.println(hatsuID + "," + chakuID);

		// long stop = System.currentTimeMillis();

		// 使用したリンクのリスト？
		ArrayList<Integer> OccupiedLinks = new ArrayList<Integer>();

		// iは発信ビルの次のビル。最終ビルだけエスケープ
		int i;
		if (hatsuID == 101) {
			i = 0;
		} else {
			i = hatsuID + 1;
		}
		if (chakuID == 101) {
			// 着地点がIDの最終項のとき
			while (LinkListPlus.get(i) != 0) {
				// 0→101になるまで＋方向にリンクをたどる
				OccupiedLinks.add(LinkListPlus.get(i));
				i++;
			}
		} else {
			// 着地点が途中にある時
			while (LinkListPlus.get(i) != chakuID + 1) {
				// 着地点に行くまでループを続ける。着地点がlinksの最後の要素になる
				OccupiedLinks.add(LinkListPlus.get(i));
				i++;
			}
		}

		// 破壊リンクが経路上にあるかどうかのチェック
		int checkID[] = new int[broken.length];
		boolean check = false;
		for (int j = 0; j < broken.length; j++) {
			// 壊れた端が経路に含まれるか
			checkID[j] = OccupiedLinks.indexOf(broken[j]);
			if (checkID[j] > -1) {
				check = true;
			}
		}

		// 破壊リンクがある場合
		if (check) {
			Random rnd = new Random();
			int rand = rnd.nextInt(10); // 1~10の間の一様乱数を発生
			// amountとは？？？ただし、この経路が破壊される確率を表してるみたい？
			if (rand >= ammount * 10) {
				OccupiedLinks.clear();
				i = 101 - hatsuID;
				while (LinkListMinus.get(i) != chakuID) {
					//逆経路の探索
					OccupiedLinks.add(LinkListMinus.get(i));
					i++;
				}
			}
		}
		// }

		int links[] = new int[103];
		/*
		 * check = false; for(int j = 0; j < broken.length; j++){ checkID[j] =
		 * OccupiedLinks.indexOf(broken[j]); if(checkID[j] > -1){ check = true;
		 * } } if(check){ for(int j = 0; j < 103; j++){ links[j] = -1; } }
		 */
		// else{
		for (int j = 0; j < OccupiedLinks.size(); j++) {
			links[OccupiedLinks.get(j)] += 1;
		}
		// }
		OccupiedLinks.clear();
		// System.out.print(OccupiedLinks);
		// System.out.println();
		// System.out.println(stop-start);
		
		return (links);
	}
}
