package ictsimulationpackage;

import java.io.*;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

public class Output {
	static void doubleArrayToExcel(double[] array, String path, String sheetName) {
		File file = new File(path);
		Workbook wb = new HSSFWorkbook();
		wb = getWorkbook(file, wb);

		Sheet sheet;
		sheet = wb.createSheet(sheetName);
		Row row;
		for (int i = 0; i < array.length; i++) {
			sheet.createRow(i).createCell(0).setCellValue(array[i]);
		}

		output(file, wb);
	}

	//与えられたfileにwbの内容をoutputするメソッド
	public static void output(File file, Workbook wb) {
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

	static void areaDevidedKosu(File folder) {
		String path = folder  + "/areaDevidedKosu.xls";
		String sheetName = "areaKosu";
		long array[] = Call.areaKosu;

		File file = new File(path);
		Workbook wb = new HSSFWorkbook();
		wb = getWorkbook(file, wb);

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

		output(file, wb);
	}

	//現在存在するxlsファイルを読みだす
	public static Workbook getWorkbook(File file, Workbook wb) {
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
		return wb;
	}

	public static void magDevidedOutput(int hour, int timeLength, File timedir, int roop, int mag, int[] callExist, int[] callOccur, int[] callLoss, double[] callLossRate, int[] callDeleted, double[] avgHoldTime) {
		String path  = timedir + "/magDevidedOutput.xls";
		File file = new File(path);
		Workbook wb = new HSSFWorkbook();
		wb = Output.getWorkbook(file,wb);
		Sheet sheet;
		if (roop > 0) {
			sheet = wb.createSheet("呼量" + mag + "倍_broken");
		} else {
			sheet = wb.createSheet("呼量" + mag + "倍");
		}

		Row row = sheet.createRow(0);
		// 絡むインデックス
		Cell cell = row.createCell(0);
		cell.setCellValue("time");
		cell = row.createCell(1);
		cell.setCellValue("occur");
		cell = row.createCell(2);
		cell.setCellValue("lost");
		cell = row.createCell(3);
		cell.setCellValue("rate");
		cell = row.createCell(4);
		cell.setCellValue("exists");
		cell = row.createCell(5);
		cell.setCellValue("deleted");
		cell = row.createCell(6);
		cell.setCellValue("avg holding time");

		for (int t = 0; t < timeLength; t++) {
			row = sheet.createRow(t + 1);
			cell = row.createCell(0);
			cell.setCellValue(t + 1);
			cell = row.createCell(1);
			cell.setCellValue(callOccur[t]);
			cell = row.createCell(2);
			cell.setCellValue(callLoss[t]);
			cell = row.createCell(3);
			cell.setCellValue(callLossRate[t]);
			cell = row.createCell(4);
			cell.setCellValue(callExist[t]);
			cell = row.createCell(5);
			cell.setCellValue(callDeleted[t]);
			cell = row.createCell(6);
			cell.setCellValue(avgHoldTime[t]);
		}

		// １時間単位のデータ
		if (roop > 0) {
			sheet = wb.createSheet("呼量" + mag + "倍_byHour_broken");
		} else {
			sheet = wb.createSheet("呼量" + mag + "倍_byHour");
		}

		row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue("time");
		cell = row.createCell(1);
		cell.setCellValue("occur");
		cell = row.createCell(2);
		cell.setCellValue("lost");
		cell = row.createCell(3);
		cell.setCellValue("rate");
		cell = row.createCell(4);
		cell.setCellValue("exists");
		cell = row.createCell(5);
		cell.setCellValue("deleted");
		cell = row.createCell(6);
		cell.setCellValue("avg holding time");

		double callOccurH[] = arrayIntoHour(callOccur);
		double callLossH[] = arrayIntoHour(callLoss);
		double callLossRateH[] = arrayIntoHourRate(callLossRate);
		double callExistH[] = arrayIntoHour(callExist);
		double callDeletedH[] = arrayIntoHour(callDeleted);
		double avgHoldTimeH[] = arrayIntoHourRate(avgHoldTime);

		for (int h = 0; h < hour; h++) {
			row = sheet.createRow(h + 1);
			row.createCell(0).setCellValue(h + 1);
			row.createCell(1).setCellValue(callOccurH[h]);
			row.createCell(2).setCellValue(callLossH[h]);
			row.createCell(3).setCellValue(callLossRateH[h]);
			row.createCell(4).setCellValue(callExistH[h]);
			row.createCell(5).setCellValue(callDeletedH[h]);
			row.createCell(6).setCellValue(avgHoldTimeH[h]);
		}
		double worst_loss_rate = 0;
		for (int i = 0; i < callLossRateH.length; i++) {
			if (worst_loss_rate < callLossRateH[i]) {
				worst_loss_rate = callLossRateH[i];
			}
		}
		sheet.createRow(26).createCell(0).setCellValue("最大呼損率/h");
		sheet.createRow(27).createCell(0).setCellValue(worst_loss_rate);

		Output.output(file,wb);
	}

	public static void summaryOutput(File timedir, int mag, int[] brokenLink, String[] brokenBuilding, double ammount) {
		try {
			File file = new File(timedir + "/summary.txt");
			file.createNewFile();
			FileWriter filewriter = new FileWriter(file);
			filewriter.write("需要" + mag + "倍");
			filewriter.write(" ");
			filewriter.write("破壊リンク：");
			for (int i = 0; i < brokenLink.length; i++) {
				filewriter.write(brokenLink[i]);
			}
			filewriter.write(" ");
			filewriter.write("破壊ビル");
			for (int i = 0; i < brokenBuilding.length; i++) {
				filewriter.write(brokenBuilding[i]);
			}
			filewriter.write(" ");
			filewriter.write("破壊リンク容量" + ammount + "倍");
			filewriter.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void StandardOutput(int timeLength, File timedir, double[] callLossRate, double[][] capHis) {
		File file = new File(timedir + "/standardData.xls");
		Workbook wb = new HSSFWorkbook();
		wb = Output.getWorkbook(file, wb);

		Sheet sheet = wb.createSheet("capHis");
		Row row;
		row = sheet.createRow(0);
		for (int i = 0; i < 102; i++) {
			row.createCell(i + 1).setCellValue(i);
		}
		for (int t = 0; t < timeLength; t++) {
			row = sheet.createRow(t + 1);
			row.createCell(0).setCellValue(t);
			for (int i = 0; i < 102; i++) {
				row.createCell(i + 1).setCellValue(capHis[t][i]);
			}
		}
		;
		sheet = wb.createSheet("lossRate");
		for (int t = 0; t < timeLength; t++) {
			sheet.createRow(t).createCell(0).setCellValue(callLossRate[t]);
		}
		Output.output(file, wb);
	}


	//リンクを準に破壊していった場合の結果をアウトプットするもの
	public static void BreakLinkInOrderOutput(int roopNum, String date, double[] worstCallLossRate, File folder) {
		//ファイルの作成
		String fileName = folder + "/BreakLinkInOrder.xls";
		File file = new File(fileName);
		Workbook wb = new HSSFWorkbook();
		wb = Output.getWorkbook(file, wb);

		//sheetの作成
		Sheet sheet;
		sheet = wb.createSheet("worstCallLossRate");
		Row row;
		for (int i = 0; i < roopNum; i++) {
			sheet.createRow(i).createCell(0).setCellValue(worstCallLossRate[i]);
		}
		Output.output(file, wb);
	}

	static double[] arrayIntoHour(double[] array) {
		int hour = array.length / 60;
		double val[] = new double[hour];
		for (int h = 0; h < hour; h++) {
			for (int i = 0; i < 60; i++) {
				int index = 60 * h + i;
				val[h] += array[index];
			}
		}
		return val;
	}

	static double[] arrayIntoHour(int[] array) {
		int hour = array.length / 60;
		double val[] = new double[hour];
		for (int h = 0; h < hour; h++) {
			for (int i = 0; i < 60; i++) {
				int index = 60 * h + i;
				val[h] += array[index];
			}
		}
		return val;
	}

	static double[] arrayIntoHourRate(double[] array) {
		int hour = array.length / 60;
		double val[] = new double[hour];
		for (int h = 0; h < hour; h++) {
			for (int i = 0; i < 60; i++) {
				int index = 60 * h + i;
				val[h] += array[index];
			}
			val[h] /= 60;
		}
		return val;
	}

	public static double maxInArray(double[] array) {
		if (array.length == 0) {
			return 0;
		}
		double val = 0;
		for (int i = 0; i < array.length; i++) {
			if (val < array[i]) {
				val = array[i];
			}
		}
		return val;
	}
}
