package ictsimulationpackage;

import java.util.ArrayList;

public class Test {
	
	public static void main(String argv[]){
		
		/*
		int broken[] = {60, 70, 76, 86};
		for(int j = 0; j < 20; j++){
		int route[] = new int[103];
		route = RouteExchange.route("西練馬", "練馬", broken, 0);
		for(int i = 0; i < 103; i++){
			System.out.print(route[i] + ",");
		}
		System.out.println();
		}
		*/
		/*
		int u;
		double x;
		double a = 0;
		double b = 102;
			x = a + (b - a) * Math.random();
			u = (int)x;
			System.out.println(u);
		*/
		//int t = OccurrenceOfCalls.Occurrence(500);
		//System.out.println(t);
//		ArrayList<ArrayList<String>> Kosu = KosuHairetsu.ExceltoHairetsu();
//		for(int i = 0; i < Kosu.size(); i++){
//		System.out.println(Kosu.get(i));
//		}
		/*
		int[] link = new int[102];
		String hatsu = "東京綾瀬";
		String chaku = "東京綾瀬";
		
		link = RouteExchange.route(hatsu, chaku);
		for(int i = 0; i < 102; i++){
			System.out.print(link[i] + ",");
		}
		/*
	}
		ArrayList<ArrayList<String>> Kosu = KosuHairetsu.ExceltoHairetsu();
		int[] LinkListPlusH = new int[203];
		int[] LinkListMinusH = new int[203];
		for(int i = 0; i < 102; i++){
			LinkListPlusH[i] = i;
			LinkListMinusH[i] = 101-i;
		}
		for(int i = 102; i < 203; i++){
			LinkListPlusH[i] = i - 102;
			LinkListMinusH[i] = 203 - i;
		}
		ArrayList<Integer> LinkListPlus = new ArrayList<Integer>();
		ArrayList<Integer> LinkListMinus = new ArrayList<Integer>();
		
		
		for(int i = 0; i < LinkListPlusH.length ; i++){
			LinkListPlus.add(LinkListPlusH[i]);
			LinkListMinus.add(LinkListMinusH[i]);
		}
		for(int i = 0; i < Kosu.size(); i++){
			String hatsu = null;
			String chaku = null;
			switch(Kosu.get(i).get(0)){
			case "多摩地区":
				hatsu = "多摩地区";
				break;
			case "他県":
				hatsu = "他県";
				break;
			default:
				hatsu = Kosu.get(i).get(1);
			}
			switch(Kosu.get(i).get(2)){
			case "多摩地区":
				chaku = "多摩地区";
				break;
			case "他県":
				chaku = "他県";
				break;
			default:
				chaku = Kosu.get(i).get(3);
			}
		int[] test = new int[102];
		long start = System.currentTimeMillis();
		test = RouteExchange.route(hatsu,chaku,LinkListPlus,LinkListMinus);
		long stop = System.currentTimeMillis();
		System.out.println(hatsu + "," + chaku);
		 for(int j = 0; j < 102; j++){
				System.out.print(test[j] + ",");
				 }
				 System.out.println();
				 System.out.println("ルーティング実行時間は" + (stop - start));
		}
		
		/*
		for(int i = 0; i < 60; i++){
		int n = OccurrenceOfCalls.Occurrence(2);
		System.out.println(n);
		}
		*/
		
		System.out.println("start");
		int NUMBER = 21427;
		int f[] = new int[100];
		for(int j = 0; j < NUMBER; j++){
		int n = HoldingTime.OneHoldingTime();
		f[n] = f[n] + 1;
		}
		for(int i = 0; i < f.length; i++){
			System.out.println(f[i]);
		}
		
		/*
		ArrayList<ArrayList<String>> Kosu = KosuHairetsu.ExceltoHairetsu();
		for(int k = 0; k < Kosu.size(); k++){
        	System.out.println(Kosu.get(k));
        }
        */
	}
}
