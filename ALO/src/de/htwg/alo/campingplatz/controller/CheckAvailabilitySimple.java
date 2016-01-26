package de.htwg.alo.campingplatz.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.htwg.alo.campingplatz.model.Stellplatz;
import de.htwg.alo.campingplatz.model.Stellplatz.DateItem;
import de.htwg.alo.campingplatz.util.DateUtil;

public class CheckAvailabilitySimple implements ICheckAvailability {

	private int anzahlAngenommen = 0;
	private int anzahlAbgelehnt = 0;
	int freieTage;

	public CheckAvailabilitySimple(){
		
	}
	
	
	@Override
	public boolean checkAvailability(ArrayList<Stellplatz> stellplaetze, Date datum,    
			int dauer, int limit, String name, String zusatzInfos) {

		int besterStellplatz = 0;
		int besteDauer = 0;
		for (int i = 0; i < stellplaetze.size(); i++) {
			int availability = stellplaetze.get(i).checkAvailabilitySP(datum);

			if (availability >= dauer || availability == -1) {

				belegeStellplatz(stellplaetze, i, datum, dauer, name, zusatzInfos);
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
					name, zusatzInfos);
			return checkAvailability(stellplaetze, neuesDatum, neueDauer,
					limit, name, zusatzInfos);
		}

		anzahlAbgelehnt++;
		return false;
	}

	@Override
	/* NEU */public boolean checkAvailability(Stellplatz stellplatz,
			Date datum, int dauer, int limit, String name, String zusatzInfos) {

		int availability = stellplatz.checkAvailabilitySP(datum);

		if (availability >= dauer || availability == -1) {
			belegeStellplatz(stellplatz, datum, dauer, name, zusatzInfos);
			anzahlAngenommen++;
			return true;
		} else if (availability == 0) {
			belegeStellplatz(stellplatz, datum, dauer, name, zusatzInfos);
			anzahlAngenommen++;
			return false;
		} else {
			belegeStellplatz(stellplatz, datum, dauer, name, zusatzInfos);
			anzahlAngenommen++;
		}

		freieTage = availability;

		anzahlAbgelehnt++;
		return false;
	}

	public int checkAvailabilityTest(ArrayList<Stellplatz> stellplaetze, Date datum,
			int dauer, int limit, String name, String zusatzInfos) {

		int besterStellplatz = 0;
		int besteDauer = 0;
		for (int i = 0; i < stellplaetze.size(); i++) {
			int availability = stellplaetze.get(i).checkAvailabilitySP(datum);

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
					limit, name, zusatzInfos);
		}

		return dauer;
	}

	@Override
	public void belegeStellplatz(ArrayList<Stellplatz> stellplaetze,
			int stellplatzIndex, Date datum, int dauer, String name, String zusatzInfos) {

		for (int j = 0; j < dauer; j++) {
			belegeStellplatz(stellplaetze.get(stellplatzIndex), datum, dauer, name, zusatzInfos); /* WiSe14/15 */  
		}
	}

	@Override
	public void belegeStellplatz(Stellplatz stellplatz, Date datum,
			int dauer, String name, String zusatzInfos) { /* WiSe14/15 */

		for (int j = 0; j < dauer; j++) {
			stellplatz.addDate(datum, name, zusatzInfos);
			datum = new Date(datum.getTime() + 24 * 60 * 60 * 1000);
		}
	} /* WiSe14/15 */


	@Override
	public Set<String> getAllBelegungenForMonth(ArrayList<Stellplatz> stellplaetze,				/* WiSe14/15 */
			String month) {
		Set<String> belegungen = new HashSet<String>();
		for (Stellplatz stellplatz : stellplaetze) {
			
			
			if (stellplatz != null) {
				for (String belegung : stellplatz.getBelegungenAsString()) {
					Set<DateItem> stellplatzBelegung = stellplatz
							.getBelegungsDates();
					// because of String[1000]
					
					if (belegung != null && belegung.length() > 2) {

						for (DateItem dateItem : stellplatzBelegung) {
							Date date = dateItem.getDate();
							DateUtil dateUtil = DateUtil.getInstance();
							if (dateUtil.getMonth(date).equals(month)) {
								belegungen.add(belegung);
							}
						}
					}
				}
			}
		}
		return belegungen;
	}																						/* WiSe14/15 */

	public int removeBelegung(ArrayList<Stellplatz> stellplaetze, int stellplatzNummer,
			String name, Date datum) {
		return stellplaetze.get(stellplatzNummer - 1).removeBelegung(name, datum);
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
