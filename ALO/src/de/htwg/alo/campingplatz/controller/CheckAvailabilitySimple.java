package de.htwg.alo.campingplatz.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import de.htwg.alo.campingplatz.model.Stellplatz;
import de.htwg.alo.campingplatz.model.Stellplatz.DateItem;
import de.htwg.alo.campingplatz.util.DateUtil;

public class CheckAvailabilitySimple implements ICheckAvailability {

	private int anzahlAngenommen = 0;
	private int anzahlAbgelehnt = 0;
	int freieTage;

	@Override
	public boolean checkAvailability(Stellplatz[] stellplaetze, Date datum,
			int dauer, int limit, String name) {

		int besterStellplatz = 0;
		int besteDauer = 0;
		for (int i = 0; i < stellplaetze.length; i++) {
			int availability = stellplaetze[i].checkAvailabilitySP(datum);

			if (availability >= dauer || availability == -1) {

				belegeStellplatz(stellplaetze, i, datum, dauer, name);
				anzahlAngenommen++;
				return true;
			} else if (availability > 0) {
				if (availability > besteDauer) {
					besteDauer = availability;
					besterStellplatz = i;
				}
			}
			freieTage = availability;
		}
		if (besteDauer > 0) {
			int neueDauer = dauer - besteDauer;
			Date neuesDatum = new Date(datum.getTime() + (24 * 60 * 60 * 1000)
					* (besteDauer));
			belegeStellplatz(stellplaetze, besterStellplatz, datum, besteDauer,
					name);
			return checkAvailability(stellplaetze, neuesDatum, neueDauer,
					limit, name);
		}

		anzahlAbgelehnt++;
		return false;
	}

	@Override
	/* NEU */public boolean checkAvailability(Stellplatz stellplaetze,
			Date datum, int dauer, int limit, String name, int stellplatzNummer) {

		int availability = stellplaetze.checkAvailabilitySP(datum);

		if (availability >= dauer || availability == -1) {
			belegeStellplatz(stellplaetze, datum, dauer, name);
			anzahlAngenommen++;
			return true;
		} else if (availability == 0) {
			belegeStellplatz(stellplaetze, datum, dauer, name);
			anzahlAngenommen++;
			return false;

			// System.out.println("checkAvaiability: 0 -> bereits belegt");
		} else {
			belegeStellplatz(stellplaetze, datum, dauer, name);
			anzahlAngenommen++;
			System.out
					.println("checkAvailability: false ----- DAUER > VERFÜGBARKEIT");
		}

		freieTage = availability;

		anzahlAbgelehnt++;
		return false;
	}

	public int checkAvailabilityTest(Stellplatz[] stellplaetze, Date datum,
			int dauer, int limit, String name) {

		int besterStellplatz = 0;
		int besteDauer = 0;
		for (int i = 0; i < stellplaetze.length; i++) {
			int availability = stellplaetze[i].checkAvailabilitySP(datum);

			// if (availability == 0) {
			// System.out.println(availability + " STELLPLATZ BELEGT");
			// return -2;//eingefügt, war nix
			// }

			if (availability >= dauer || availability == -1) {
				// System.out.println("Klasse availabilitySimple: availability = "
				// + availability + " / STELLPLATZ FREI");
				return 0;

			} else if (availability > 0) {
				if (availability > besteDauer) {
					besteDauer = availability;
					besterStellplatz = i;
					return 1;
				}
			}
			// freieTage = availability;
		}
		if (besteDauer > 0) {
			int neueDauer = dauer - besteDauer;
			Date neuesDatum = new Date(datum.getTime() + (24 * 60 * 60 * 1000)
					* (besteDauer));
			return checkAvailabilityTest(stellplaetze, neuesDatum, neueDauer,
					limit, name);
		}

		return dauer;
	}

	@Override
	public void belegeStellplatz(Stellplatz[] stellplaetze,
			int stellplatzNummer, Date datum, int dauer, String name) {

		for (int j = 0; j < dauer; j++) {
			belegeStellplatz(stellplaetze[j], datum, dauer, name); /* WiSe14/15 */

		}
	}

	@Override
	public void belegeStellplatz(Stellplatz stellplaetze, Date datum,
			int dauer, String name) { /* WiSe14/15 */

		for (int j = 0; j < dauer; j++) {
			stellplaetze.addDate(datum, name);
			datum = new Date(datum.getTime() + 24 * 60 * 60 * 1000);
		}
	} /* WiSe14/15 */

	public String[] getBelegungsPlan(Stellplatz[] stellplaetze,
			int stellplatzNummer, String monat, int jahr) {
		String[] tempBelegung = stellplaetze[stellplatzNummer]
				.getBelegungsPlanSP(monat, jahr, stellplatzNummer);
		return tempBelegung;

	}

	@Override
	public Set<String> getAllBelegungenForMonth(Stellplatz[] stellplaetze,				/* WiSe14/15 */
			String month) {
		Set<String> belegungen = new HashSet<>();
		for (Stellplatz stellplatz : stellplaetze) {
			
			if (stellplatz != null) {
				for (String belegung : stellplatz.getBelegungenAsString()) {
					Set<DateItem> stellplatzBelegung = stellplatz
							.getBelegungsDates();
					// because of String[1000]
					if (belegung != null && belegung.length() > 2) {
						System.out.println("belegung: "+ belegung);

						for (DateItem dateItem : stellplatzBelegung) {
							Date date = dateItem.getDate();
							DateUtil dateUtil = DateUtil.getInstance();
							System.out.println("datum: " + dateUtil.getMonth(date) + "\nexpected: "+ month);
							if (dateUtil.getMonth(date).equals(month)) {
								System.out.println("added date!");
								belegungen.add(belegung);
							}
						}
					}
				}
			}
		}
		return belegungen;
	}																						/* WiSe14/15 */

	public int removeBelegung(Stellplatz[] stellplaetze, int stellplatzNummer,
			String name, Date datum) {
		return stellplaetze[stellplatzNummer - 1].removeBelegung(name, datum);
	}

	@Override
	public int getAnfragenAngenommen() {
		return anzahlAngenommen;
	}

	@Override
	public int getAnfragenAbgelehnt() {
		return anzahlAbgelehnt;
	}

}
