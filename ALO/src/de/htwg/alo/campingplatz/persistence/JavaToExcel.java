package de.htwg.alo.campingplatz.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;

import de.htwg.alo.campingplatz.model.Campingplatz;
import de.htwg.alo.campingplatz.util.DateUtil;

public class JavaToExcel {
	
	public void xmlExportToExcel(int monat, int jahr, Campingplatz cp,
			String speicherOrt, String path) {

		GregorianCalendar gc = new GregorianCalendar();
		gc.set(jahr, (monat - 1), 01);
		
		String tempMonat = new SimpleDateFormat("MMMM", Locale.GERMAN).format(gc.getTime());
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(tempMonat);

		int currentMonth = gc.get(GregorianCalendar.MONTH);
		
		ArrayList<String> kalender = new ArrayList<String>();
		ArrayList<String> leereZeileNachKopfzeile = new ArrayList<String>();
		ArrayList<String> zusatzInfos = new ArrayList<String>();
		ArrayList<String> namen = new ArrayList<String>();
		
		Map<Integer, ArrayList<String>> data = new HashMap<Integer, ArrayList<String>>();
		Map<Integer, ArrayList<String>> infos = new HashMap<Integer, ArrayList<String>>();

		kalender.add("Stellplatz-Nr. :");
		leereZeileNachKopfzeile.add("");

		while (gc.get(GregorianCalendar.MONTH) == currentMonth) {		//um das Kalenderblatt hinzuzufügen
			kalender.add(DateUtil.getInstance().formatDate(				
					gc.getTime()));
			leereZeileNachKopfzeile.add("");

			// Tag wird um 1 erhöht
			gc.add(GregorianCalendar.DAY_OF_MONTH, 1);

		}
		// Kopfzeile und eine leere Zeile nach der Kopfzeile
		data.put(1, kalender);
		infos.put(1, leereZeileNachKopfzeile);
		
		// Schleife läuft über alle Stellplätze
		for (int i = 0; i < cp.getAnzahlStellplaetze(); i++) {
			namen = PersistenceXml.readXmlForExcel(path, cp, gc.get(Calendar.YEAR),gc.get(Calendar.MONTH), (i+1) ,1);     // bei 1 werden die namen der belegungen geholt
			zusatzInfos = PersistenceXml.readXmlForExcel(path, cp, gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), (i+1), 2);		// bei 2 die zusatzinformationen der belegungen
			data.put((i + 2), namen);
			infos.put((i+2), zusatzInfos);

		}
		
		//ab hier wird Excel gebaut
		Set<Integer> keyset = data.keySet();
		int rownum = 0;
		for (Integer key : keyset) {
			Row row = sheet.createRow(rownum++);
			ArrayList<String> objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String) {
					cell.setCellValue((String) obj);
				} else if (obj instanceof Integer) {
					cell.setCellValue((Integer) obj);
				}
				HSSFCellStyle style = workbook.createCellStyle();						
				style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
				style.setFillPattern(CellStyle.SOLID_FOREGROUND);
				cell.setCellStyle(style);
			}

			Row kommentare = sheet.createRow(rownum++);
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
					+ "/" + fileName + "Belegungsplan.xls"));
			workbook.write(out);
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
