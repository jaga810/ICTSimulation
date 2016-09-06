package ictsimulationpackage;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RouteExchange {
	static String[] BldgName = Settings.BldgName();
	static int[] exchangeID = Settings.exchangeID();

	//リンクを通った個数の数。中継器を使用　どこのビルを何回通ったかをreturn
	static int[] route(String hatsu, String chaku, int[] broken, double ammount) {
		//hatsu:発信ビル chaku:着信ビル broken:壊れたリンク ammount:破壊リンクのキャパシティ

		//変数の初期化
		int[] links = new int[103]; //書くリンクの使用回数
		int[] hatsu_HatsuEx = new int[103];//Ex嫁いていたら区内中継ビルまでのルート
		int[] HatsuEx_ChakuEx = new int[103];//102は練馬から　　　　　　　　　　　　　　　　　　　　　　　　　　　　　区外中継ノード
		int[] ChakuEx_chaku = new int[103];

		//発着区に区外が含まれていた場合
		if (hatsu.equals("多摩地区") || hatsu.equals("他県")) {
			hatsu = "練馬";
			links[102] += 1;
		}
		if (chaku.equals("多摩地区") || chaku.equals("他県")) {
			chaku = "練馬";
			links[102] += 1;
		}
		//ビルディングの名前の保存
		String HatsuBldgName = BldgName[0];
		String ChakuBldgName = BldgName[0];
		String HatsuExchange = BldgName[0];
		String ChakuExchange = BldgName[0];

		int i = 0;
		while (HatsuBldgName.equals(hatsu) == false) {
			HatsuBldgName = BldgName[i];
			HatsuExchange = BldgName[exchangeID[i]];
			i++;//index管理だからこんな面倒なことに...
		}
		i = 0;
		while (ChakuBldgName.equals(chaku) == false) {
			ChakuBldgName = BldgName[i];
			ChakuExchange = BldgName[exchangeID[i]];
			i++;
		}
		//破壊されているかどうかか？
		boolean check = false;
		
		if (!hatsu.equals(chaku)){
			//出発点から区内中継ビルまで
			hatsu_HatsuEx = RouteStraight.route(hatsu, HatsuExchange, broken, ammount);
			if (hatsu_HatsuEx[0] == -1) {//RouteStraight.routeは-1とらないんだが...
				check = true;
			}
			//出発点側区内中継ビルから到着点側区内中継ビル
			HatsuEx_ChakuEx = RouteStraight.route(HatsuExchange, ChakuExchange, broken, ammount);
			if (HatsuEx_ChakuEx[0] == -1) {
				check = true;
			}
			//区内中継ビルから到着ビルまで
			ChakuEx_chaku = RouteStraight.route(ChakuExchange, chaku, broken, ammount);
			if (ChakuEx_chaku[0] == -1) {
				check = true;
			}
		}
		
		if (check) {
			for (int j = 0; j < 103; j++) {
				links[j] = -1;
			}

		} else {
			for (int j = 0; j < 103; j++) {
				links[j] = links[j] + hatsu_HatsuEx[j] + HatsuEx_ChakuEx[j] + ChakuEx_chaku[j];
			}
		}
		return links;
	}

}
