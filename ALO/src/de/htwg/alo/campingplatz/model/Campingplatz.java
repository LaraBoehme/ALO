package de.htwg.alo.campingplatz.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import de.htwg.alo.campingplatz.controller.ICheckAvailability;
import de.htwg.alo.campingplatz.gui.MainFrame;
import de.htwg.alo.campingplatz.persistence.PersistenceXml;

public class Campingplatz {

	private ArrayList<Stellplatz> stellplaetze = new ArrayList<Stellplatz>(); 
	private ICheckAvailability currentCheck;
	private int anzahlStellplaetze; 

	public Campingplatz(int anzahlStellplaetze, ICheckAvailability currentCheck) {
		this.anzahlStellplaetze = anzahlStellplaetze;
		for(int i = 0; i < anzahlStellplaetze; i++){
			stellplaetze.add(new Stellplatz());
		}
		this.currentCheck = currentCheck;
	}

	public int getAnzahlStellplaetze() {
		return anzahlStellplaetze;
	}

	public void setAnzahlStellplaetze(int anzahlStellplaetze) {
		this.anzahlStellplaetze = anzahlStellplaetze;
	}

	public boolean checkAvailability(Date datum, int dauer, int limit,
			String name,String zusatzInfos) {
		return currentCheck.checkAvailability(stellplaetze, datum, dauer,
				limit, name, zusatzInfos);
	}

	public boolean checkAvailability(Date datum, int dauer, int limit, //WiSe14/15
			String name, int stellplatzIndex, String zusatzInfos) {
		return currentCheck.checkAvailability(stellplaetze.get(stellplatzIndex),
				datum, dauer, limit, name, zusatzInfos);
	}

	public int checkAvailabilityTest(Date datum, int dauer, int limit,
			String name, String zusatzInfos) {
		return currentCheck.checkAvailabilityTest(stellplaetze, datum, dauer,
				limit, name, zusatzInfos);
	}

	public void belegeStellplatz(int stellplatzIndex, Date datum, int dauer,
			String name, String zusatzInfos) {
		currentCheck.belegeStellplatz(stellplaetze, stellplatzIndex, datum,
				dauer, name, zusatzInfos);
	}
	
	public Set<String> getAllBelegungen(String month) {										/* WiSe14/15 */
		return currentCheck.getAllBelegungenForMonth(stellplaetze, month);
	}

	public int removeDatum(int stellplatzNummer, String name, Date datum) {
		return currentCheck.removeBelegung(stellplaetze, stellplatzNummer,
				name, datum);
	}

	public void belegungToXml(String outPath) {
		PersistenceXml.belegungenToXml(stellplaetze, outPath);
	}

	// V1.1 SOSE 2014 - neu zur Ausgabe in die Oberfl�che
	public void xmlToBelegung(String inPath, String calendarDatum) {
		PersistenceXml.xmlToBelegung(this, inPath, calendarDatum);
	}

	public void printStatistic() {
		System.out.println("Anfragen abgelehnt: "
				+ currentCheck.getAnfragenAbgelehnt());
		System.out.println("Anfragen angenommen: "
				+ currentCheck.getAnfragenAngenommen());
	}

	// V1.1 SoSe 2014
	public void belegeStellplatzAufGUI(int stellplatzIndex, Date datumVon,
			int dauer, String name, String zusatzInfos, String calendarDatum) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(datumVon);
		int jahr = cal.get(Calendar.YEAR);
				
		GregorianCalendar first = new GregorianCalendar(jahr,
				GregorianCalendar.APRIL, 1);

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
		
		int col = 1;// +1 da die erste Spalte f�r das Label Datum
		int colZusatzInfos = col;							// verwendet wird
		
		int row = stellplatzIndex*2;
		int rowZusatzInfos = (stellplatzIndex*2) + 1;

		int length = 184;
	
		GregorianCalendar tempCal = new GregorianCalendar(jahr,
				GregorianCalendar.APRIL, 1);
		Date tempDate = tempCal.getTime();
		
		for (int i = 0; i < length; i++) {
			if (tempDate.equals(datumVon)) {
				col = col + i;
				colZusatzInfos = col;
				break;
			}
			tempCal.add(tempCal.DAY_OF_YEAR, 1);
			tempDate = tempCal.getTime();
		}

		for (int j = 0; j < dauer; j++) {
			if(stellplatzIndex > anzahlStellplaetze-1){
				break;
			}
			String datumKalender = calendarDatum;
			String datumRichtig = sdf.format(first.getTime());
			if(datumKalender.compareTo(datumRichtig)==0){
				MainFrame.dtm.setValueAt(name, row, col);
				MainFrame.dtm.setValueAt(zusatzInfos, rowZusatzInfos, colZusatzInfos);
				col++;
				if(col >= length){
					break;
				}
			}
		}
	}

	public void newStellplaetze(int anzahlStellplaetze) {
		stellplaetze.removeAll(stellplaetze);
		for (int i = 0; i < anzahlStellplaetze; i++) {
			stellplaetze.add(new Stellplatz());
		}
	}
	
	//Stellplätze hinzufügen oder löschen
	public void aendereAnzahlStellplaetze(int anzahlStellplaetze, String chosenXml){
		if(stellplaetze.size() >= anzahlStellplaetze){
			int anzahlLoeschen = stellplaetze.size()-anzahlStellplaetze;
			int loeschen = stellplaetze.size() - 1;
			for(int i = 0; i < anzahlLoeschen; i++){
				stellplaetze.remove(loeschen);
				loeschen--;
			}
		}else{
			int anzahlHinzufuegen = anzahlStellplaetze-stellplaetze.size();
			for(int i = 0; i < anzahlHinzufuegen; i++){
				stellplaetze.add(new Stellplatz());	
			}
		}
	}
}
