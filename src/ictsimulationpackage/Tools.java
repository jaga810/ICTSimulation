package ictsimulationpackage;

public class Tools {
	public static void main(String args[]){
		BuildingList list = new BuildingList(102);
		Building bldg = list.findBldg("墨田");
		double[]sum  = new double[100];
		int index = 0;
		do{
			sum[index] = areaKosu(bldg);
			bldg = bldg.bldgR;
			index ++ ;
		}while(!bldg.bname.equals("墨田"));
		String path = "/Users/jaga/Documents/domain_project/kosuS";
		String sheetName = "ebara";
		
		Output.doubleArrayToExcel(sum, path, sheetName);
	}
	
	//有るビルに区内から発着する呼の計上を時間合計に返す
	public static double areaKosu(Building dest){
		Building bldg;
		double sum = 0;
		for(int t = 0; t <24; t++){
			bldg = dest.bldgR;
			sum += bldg.kosu[t].get(dest);
			while(bldg != dest){
				sum += bldg.kosu[t].get(dest);
				sum += dest.kosu[t].get(bldg);
				
				bldg = bldg.bldgR;
			}
		}
		return sum;
	}
}