package ictsimulationpackage;

import java.io.*;
import java.util.*;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class ForAnalysis {
	 public static void main(String[] args){
		 	int filenum = 2;
		    Workbook wb = new XSSFWorkbook();
		    String folder = "C:\\Users\\watanabe\\Documents\\����\\File\\��Q�V�i���I�ʐM�K��\\";
		    String[] name = {"�Ă̐��N","�đ���","���݌Đ�","������"};
		    for(int i = 0; i < 4; i++){
		    Sheet sheet = wb.createSheet(name[i]);
		    Row[] rowlist = new Row[1441];
		    Cell[][] celllist = new Cell[1441][filenum];
		    for(int index = 0; index < 1441; index++){
		    	rowlist[index] = sheet.createRow(index);
		    }
		    for(int column = 0; column < filenum; column++){
		    	for(int row = 0; row< 1441; row++){
		    		celllist[row][column] = rowlist[row].createCell(column);
		    	}
		    }
		    
		    for(int file = 0; file < filenum; file++){
		    	
		    try{
		    	FileReader frsummary=new FileReader(folder + file + "\\summary.txt");
		    	BufferedReader brsummary = new BufferedReader(frsummary);
		    	
		    	Cell cell0 = celllist[0][file];
		    	cell0.setCellValue(brsummary.readLine());
		    	
		    	double list[] = new double[1440];
		    	FileReader fr=new FileReader(folder + file + "\\" + name[i] + "\\all.csv");
		    	BufferedReader br = new BufferedReader(fr);
		    	
		    	//�ǂݍ��񂾃t�@�C�����P�s����������
	            String line;
	            int k = 0;
	            while ((line = br.readLine()) != null) {
	                list[k] = Double.parseDouble(line);
	                k++;
	            }
	            
            for(int j = 1; j < 1441; j++){
	        Cell cell = celllist[j][file];
	        cell.setCellValue(list[j-1]);
            }
            br.close();
		    }
		    
		    catch (IOException ex) {
	            //��O����������
	            ex.printStackTrace();
	        }
		    
	 }
	 }
		    FileOutputStream out = null;
		    try{
		      out = new FileOutputStream(folder + "��Q�V�i���I�ʐM�K������.xlsx");
		      wb.write(out);
		    }catch(IOException e){
		      System.out.println(e.toString());
		    }finally{
		      try {
		        out.close();
		      }catch(IOException e){
		        System.out.println(e.toString());
		      }
		    }
		  }
}
