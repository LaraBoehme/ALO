package de.htwg.alo.campingplatz.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
	public static final int anzahlStellplaetze = 20;

	public void exportToExcel(int monat, int jahr, Campingplatz cp,
			String speicherOrt) {

		int stellplatzNummer = 0;
		String[] myCalendar = new String[100];
		String[] myBelegungen = new String[100];
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(jahr, (monat - 1), 01);
		int currentMonth = gc.get(GregorianCalendar.MONTH);
		String tempMonat = new SimpleDateFormat("MMMM", Locale.GERMAN)
				.format(gc.getTime());

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(tempMonat);

		Map<Integer, Object[]> data = new HashMap<Integer, Object[]>();

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
		myCalendar[0] = "Stellplatz-NR:";

		while (gc.get(GregorianCalendar.MONTH) == currentMonth) {
			myCalendar[zaehler] = DateUtil.getInstance().formatDate(
					gc.getTime());
			zaehler++;

			// Tag wird um 1 erh�ht
			gc.add(GregorianCalendar.DAY_OF_MONTH, 1);

		}

		// Alle Tage des Monats werden in die erste Zeile (Kopfzeile) der
		// Excel-Datei geschrieben
		data.put(1, myCalendar);

		for (int i = 0; i < anzahlStellplaetze; i++) {
			myBelegungen = cp.getBelegungsPlan(stringMonat, jahr,
					stellplatzNummer);

			data.put((i + 2), myBelegungen);

			stellplatzNummer++;

		}

		Set<Integer> keyset = data.keySet();
		int rownum = 0;
		for (Integer key : keyset) {
			Row row = sheet.createRow(rownum++);

			Object[] objArr = data.get(key);
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

			Row rowEmpty = sheet.createRow(rownum++);									// *NEU* WS14/15

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
