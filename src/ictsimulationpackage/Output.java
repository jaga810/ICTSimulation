package ictsimulationpackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Output {
	static void doubleArrayToExcel(double[] array, String path, String sheetName) {
		File file = new File(path);
		Workbook wb = new HSSFWorkbook();
		if (file.exists()) {
			try {
				InputStream in = new FileInputStream(file);
				wb = WorkbookFactory.create(in);
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Sheet sheet;
		sheet = wb.createSheet(sheetName);
		Row row;
		for (int i = 0; i < array.length; i++) {
			sheet.createRow(i).createCell(0).setCellValue(array[i]);
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			wb.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
	}

	static void areaDevidedKosu() {
		String path = "/Users/jaga/Documents/domain_project/areaDevidedKosu.xls";
		String sheetName = "areaKosu";
		long array[] = Call.areaKosu;

		File file = new File(path);
		Workbook wb = new HSSFWorkbook();
		if (file.exists()) {
			try {
				InputStream in = new FileInputStream(file);
				wb = WorkbookFactory.create(in);
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Sheet sheet;

		// areaKosu
		sheet = wb.createSheet(sheetName);
		for (int i = 0; i < array.length; i++) {
			sheet.createRow(i).createCell(0).setCellValue(array[i]);
		}
		// areaLossKosu
		sheetName = "areaLossKosu";
		array = Call.areaLossKosu;
		sheet = wb.createSheet(sheetName);
		for (int i = 0; i < array.length; i++) {
			sheet.createRow(i).createCell(0).setCellValue(array[i]);
		}

		// exKosu
		sheetName = "exKosu";
		array = Call.exKosu;
		sheet = wb.createSheet(sheetName);
		for (int i = 0; i < array.length; i++) {
			sheet.createRow(i).createCell(0).setCellValue(array[i]);
		}

		// exLossKosu
		sheetName = "exLossKosu";
		array = Call.exLossKosu;
		sheet = wb.createSheet(sheetName);
		for (int i = 0; i < array.length; i++) {
			sheet.createRow(i).createCell(0).setCellValue(array[i]);
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			wb.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
	}

}
