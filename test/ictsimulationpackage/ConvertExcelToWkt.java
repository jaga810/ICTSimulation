package ictsimulationpackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ConvertExcelToWkt {
	static BuildingList list;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		list = new BuildingList(102);
		download();
		outputText();
	}
	
	static void download(){
		System.out.println("start downloading GIS data...");
		try {
			FileInputStream fi = new FileInputStream(
					"/Users/jaga/Documents/domain_project/data/building_for_qgis_2.xlsx");
			XSSFWorkbook book = new XSSFWorkbook(fi);
			fi.close();
			Sheet sheet;
			Row row;
			int lineNum = 0;
			String bname;
			Building bldg;
			double latitude;
			double longitude;
			System.out.println("called");
			
			String[] sheetStr = {"Sheet2", "Sheet3", "Sheet4"};
			
			for(int i = 0; i < 3; i++){
				sheet = book.getSheet(sheetStr[i]);
				System.out.println(sheet.getRow(0).getCell(3).toString());
				lineNum = (int)(sheet.getRow(0).getCell(3).getNumericCellValue());
				for(int k = 0; k < lineNum ; k ++){
					row = sheet.getRow(k);
					bname = row.getCell(0).toString();
					bldg = list.findBldg(bname);
					latitude = Double.parseDouble(row.getCell(4).toString());
					longitude = Double.parseDouble(row.getCell(5).toString());
					bldg.setGisData(latitude, longitude);
//					System.out.println("bname:" + bname + ", latitude:" + latitude + ",lognigtude:" + longitude);
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		System.out.println("finished");
	}
	static String[] outputText(){
		System.out.println("writing GIS data...");
		String[] output = new String[3];
		String start[] = {"練馬", "荏原", "墨田"};
		Building bldg ;
		String text;
		
		for(int i = 0; i < start.length;i++){
			text = "LINESTRING ((, ";
			bldg = list.findBldg(start[i]);
			int num = 0;
			do{
				// 111 2222,
				num ++;
//				System.out.println(num + ":"+bldg.bname);
				
				text = text + " " + bldg.latitude +" " + bldg.longitude +",";
				bldg = bldg.bldgR;
				
			}while(!bldg.bname.equals(start[i]));
			text = text + " " + bldg.latitude +" " + bldg.longitude +",))";
			System.out.println(text);
			output[i] = text;
		}
		text = "LINESTRING ((, ";
		bldg = list.findBldg("練馬");
		int num = 0;
		do{
			// 111 2222,
			num ++;
//			System.out.println(num + ":"+bldg.bname);
			
			text = text + " " + bldg.latitude +" " + bldg.longitude +",";
			bldg = bldg.exBldgR;
			
		}while(!bldg.bname.equals("練馬"));
		text = text + " " + bldg.latitude +" " + bldg.longitude +",))";
		System.out.println(text);
		
		
		System.out.println("finished");
		return output;
	}
	static void outputToExecel(String str[]){
		String fileName;
		fileName = "/Users/jaga/Documents/domain_project/data/building_for_qgis_3.xls";
		
		File file = new File(fileName);
		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet();
		Row row;
		for(int i = 0; i < str.length;i++){
			sheet.createRow(i).createCell(0).setCellValue(str[i]);
		}
	}
}
