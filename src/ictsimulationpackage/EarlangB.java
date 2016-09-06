package ictsimulationpackage;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EarlangB {
	public static void main(String argv[]){
	double a[] = new double[105];//åƒó 
	int S[] = new int[105];//âÒê¸êî
	double B[] = new double[105];//åƒëπó¶
	double CallLossRate = 0;
	
	
	try{
        FileInputStream fi=new FileInputStream("C:\\Users\\watanabe\\Documents\\å§ãÜ\\EarlangBóp.xlsx");
        XSSFWorkbook book =new XSSFWorkbook(fi);
        fi.close();
        Sheet sheet = book.getSheetAt(0);
        for(int i = 0; i < 105; i++){
        	Row row = sheet.getRow(i);
       	    a[i] = Double.parseDouble(row.getCell(0).toString())*10;
       	    S[i] = (int)Double.parseDouble(row.getCell(2).toString());
        }
	}
	 catch(Exception e){
         e.printStackTrace(System.err);
         System.exit(1);
         }
	/*
	for(int i = 0; i < 105; i++){
		System.out.print(a[i] + ",");
	}
	System.out.println();
	for(int i = 0; i < 105; i++){
		System.out.print(S[i] + ",");
	}
	System.out.println();
	*/
	for(int i = 0; i < 105; i++){
		double E[] = new double[S[i]];
		E[0] = 1;
		for(int j = 1; j < S[i]; j++){
			E[j] = (a[i] * E[j-1])/(S[i] + a[i] * E[j-1]);
		}
		B[i] = E[S[i]-1];
		System.out.print(B[i] + ",");
	}
	double sum = 0;
	double losssum = 0;
	double loss[] = new double[105];
	for(int i = 0; i < 105; i++){
		loss[i] = a[i] * B[i];
		losssum += loss[i];
		sum += a[i];
	}
	CallLossRate = (losssum/sum) * 100;
	System.out.println();
	System.out.println(CallLossRate);
	}
}
