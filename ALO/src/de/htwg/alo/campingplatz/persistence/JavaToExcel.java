package de.htwg.alo.campingplatz.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import de.htwg.alo.campingplatz.model.Campingplatz;
import de.htwg.alo.campingplatz.util.DateUtil;

public class JavaToExcel {

	public void exportToExcel(int monat, int jahr, Campingplatz cp,
			String speicherOrt) {

		int stellplatzNummer = 0;
		ArrayList<String> kalender = new ArrayList<String>();
		ArrayList<String> belegungen = new ArrayList<String>();
		ArrayList<String> zusatzInfos = new ArrayList<String>();
		ArrayList<String> leereZeileNachKopfzeile = new ArrayList<String>();
		
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(jahr, (monat - 1), 01);
		int currentMonth = gc.get(GregorianCalendar.MONTH);
		String tempMonat = new SimpleDateFormat("MMMM", Locale.GERMAN)
				.format(gc.getTime());

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(tempMonat);

		Map<Integer, ArrayList<String>> data = new HashMap<Integer, ArrayList<String>>();
		Map<Integer, ArrayList<String>> infos = new HashMap<Integer, ArrayList<String>>();

		String stringMonat = "";
		switch (monat) {
		case 1:
			stringMonat = "01";
			break;
		case 2:
			stringMonat = "02";
			break;
		case 3:
			stringMonat = "03";
			break;
		case 4:
			stringMonat = "04";
			break;
		case 5:
			stringMonat = "05";
			break;
		case 6:
			stringMonat = "06";
			break;
		case 7:
			stringMonat = "07";
			break;
		case 8:
			stringMonat = "08";
			break;
		case 9:
			stringMonat = "09";
			break;
		default:
			stringMonat = monat + "";
		}

		int zaehler = 1;
		kalender.add("Stellplatz-Nr. :");
		leereZeileNachKopfzeile.add("");

		while (gc.get(GregorianCalendar.MONTH) == currentMonth) {
			kalender.add(DateUtil.getInstance().formatDate(
					gc.getTime()));
			leereZeileNachKopfzeile.add("");
			zaehler++;

			// Tag wird um 1 erh�ht
			gc.add(GregorianCalendar.DAY_OF_MONTH, 1);

		}

		// Alle Tage des Monats werden in die erste Zeile (Kopfzeile) der
		// Excel-Datei geschrieben
		data.put(1, kalender);
		infos.put(1, leereZeileNachKopfzeile);
		
		for (int i = 0; i < cp.getAnzahlStellplaetze(); i++) {
			belegungen = cp.getBelegungsPlan(stringMonat, jahr,stellplatzNummer, 1);     // bei 1 holt er die namen der belegungen 
			zusatzInfos = cp.getBelegungsPlan(stringMonat, jahr, stellplatzNummer, 2);
			data.put((i + 2), belegungen);
			infos.put((i+2), zusatzInfos);
		

			stellplatzNummer++;

		}

		Set<Integer> keyset = data.keySet();
		int rownum = 0;
		for (Integer key : keyset) {
			Row row = sheet.createRow(rownum++);
			System.out.println("Ich bin Zeile "+ rownum);

			ArrayList<String> objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Integer) {
					cell.setCellValue((Integer) obj);
				}
				HSSFCellStyle style = workbook.createCellStyle();						// *NEU* WS14/15
				style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(style);
				
			}

			Row kommentare = sheet.createRow(rownum++);
			System.out.println("Ich bin Zeile "+ rownum);// *NEU* WS14/15
				ArrayList<String> infosStellplatz = infos.get(key);
				int zellnr = 0;
				for (Object einzelneInfo : infosStellplatz) {
					Cell zelle = kommentare.createCell(zellnr++);
					zelle.setCellValue((String) einzelneInfo);
				}
				
				
			
		}

		try {
			String fileName = jahr + "_" + tempMonat + "_";
			FileOutputStream out = new FileOutputStream(new File(speicherOrt
					+ "\\" + fileName + "Belegungsplan.xls"));
			workbook.write(out);
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
