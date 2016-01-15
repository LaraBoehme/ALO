package de.htwg.alo.campingplatz.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import de.htwg.alo.campingplatz.controller.ICheckAvailability;
import de.htwg.alo.campingplatz.gui.MainFrame;
import de.htwg.alo.campingplatz.persistence.PersistenceXml;
import de.htwg.alo.campingplatz.util.DateUtil;

public class Campingplatz {

	private ArrayList<Stellplatz> stellplaetze = new ArrayList<Stellplatz>();  //Lara geändert von Array zu ArrayList um hinzufügen und löschen zu ermöglichen
	private ICheckAvailability currentCheck;
	private int anzahlStellplaetze;  //Lara hinzugefügt um die anzahl der Stellplätze zu setzen

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
			String name) {
		return currentCheck.checkAvailability(stellplaetze, datum, dauer,
				limit, name);
	}

	public boolean checkAvailability(Date datum, int dauer, int limit, /*
																		 * WiSe14/
																		 * 15
																		 */
			String name, int stellplatzNummber) {
		return currentCheck.checkAvailability(stellplaetze.get(stellplatzNummber),
				datum, dauer, limit, name, stellplatzNummber);
	}

	public int checkAvailabilityTest(Date datum, int dauer, int limit,
			String name) {
		return currentCheck.checkAvailabilityTest(stellplaetze, datum, dauer,
				limit, name);
	}

	public void belegeStellplatz(int stellplatzNummer, Date datum, int dauer,
			String name) {
		currentCheck.belegeStellplatz(stellplaetze, stellplatzNummer, datum,
				dauer, name);
	}

	public String[] getBelegungsPlan(String monat, int jahr,
			int stellplatzNummer) {
		return currentCheck.getBelegungsPlan(stellplaetze, stellplatzNummer,
				monat, jahr);
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
	public void xmlToBelegung(String inPath) {
		PersistenceXml.xmlToBelegung(this, inPath);
	}

	public void printStatistic() {
		System.out.println("Anfragen abgelehnt: "
				+ currentCheck.getAnfragenAbgelehnt());
		System.out.println("Anfragen angenommen: "
				+ currentCheck.getAnfragenAngenommen());
	}

	// V1.1 SoSe 2014
	public void belegeStellplatzAufGUI(int stellplatzNr, Date datumVon,
			int dauer, String name) {

		GregorianCalendar first = new GregorianCalendar(2014,
				GregorianCalendar.APRIL, 1);
		GregorianCalendar last = new GregorianCalendar(2014,
				GregorianCalendar.SEPTEMBER, 30);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");
		int col = stellplatzNr + 1;// +1 da die erste Spalte f�r das Label Datum
									// verwendet wird
		int row = 0;
		long result = last.getTimeInMillis() - first.getTimeInMillis();
		result = result / (1000 * 60 * 60 * 24); // umrechnung in Tage
		int length = (int) result;
		GregorianCalendar tempCal = new GregorianCalendar(2014,
				GregorianCalendar.APRIL, 1);
		Date tempDate = tempCal.getTime();
		for (int i = 0; i < length; i++) {
			if (tempDate.equals(datumVon)) {
				row = i;
			}
			tempCal.add(tempCal.DAY_OF_MONTH, 1);
			tempDate = tempCal.getTime();
		}

		for (int j = 0; j < dauer; j++) {
			MainFrame.dtm.setValueAt(name, row, col);
			row++;
		}

	}

	public void resetStellplaetze() { // Lara findet überflüssig
		this.stellplaetze = null;
	}

	public void newStellplaetze(int anzahlStellplaetze) {
		this.stellplaetze = null;
		for (int i = 0; i < anzahlStellplaetze; i++) {
			stellplaetze.add(new Stellplatz());
		}
	}

	public void removeFromOberflaeche(String text, int platz, String myDate,
			int dauer) {

		GregorianCalendar first = new GregorianCalendar(2014,
				GregorianCalendar.APRIL, 1);
		Date currentDate = null;
		currentDate = DateUtil.getInstance().formatString(myDate);
		long result = currentDate.getTime() - first.getTimeInMillis();
		result = result / (1000 * 60 * 60 * 24); // umrechnung in Tage

		int col = platz;
		int row = (int) result;

		for (int i = 0; i < dauer + 1; i++) {
			MainFrame.dtm.setValueAt("", row++, col);
		}

	}
	
	public void aendereAnzahlStellplaetze(int anzahlStellplaetze){
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
