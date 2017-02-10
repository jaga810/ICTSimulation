package ictsimulationpackage;

public class Tools {
	public static void main(String args[]){
		Network list = new Network();
		Building bldg = list.findBldg("墨田");
		double[]sum  = new double[100];
		int index = 0;
		do{
			sum[index] = areaKosu(bldg);
			bldg = bldg.getBldgR();
			index ++ ;
		}while(!bldg.getBname().equals("墨田"));
		String path = "/Users/jaga/Documents/domain_project/kosuS";
		String sheetName = "ebara";
		
		IOHelper.doubleArrayToExcel(sum, path, sheetName);
	}
	
	//有るビルに区内から発着する呼の計上を時間合計に返す
	public static double areaKosu(Building dest){
		Building bldg;
		double sum = 0;
		for(int t = 0; t <24; t++){
			bldg = dest.getBldgR();
			sum += bldg.getKosu(t).get(dest);
			while(bldg != dest){
				sum += bldg.getKosu(t).get(dest);
				sum += dest.getKosu(t).get(bldg);
				
				bldg = bldg.getBldgR();
			}
		}
		return sum;
	}
}
