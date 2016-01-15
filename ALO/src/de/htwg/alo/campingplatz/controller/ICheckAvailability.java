package de.htwg.alo.campingplatz.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import de.htwg.alo.campingplatz.model.Stellplatz;

public interface ICheckAvailability {

	boolean checkAvailability(ArrayList<Stellplatz> stellplaetze, Date datum, int dauer,
			int limit, String name);

    boolean checkAvailability(Stellplatz stellplaetze, Date datum, int dauer,			/* WiSe14/15 */
    								int limit, String name, int stellplatzNummber);

	int checkAvailabilityTest(ArrayList<Stellplatz> stellplaetze, Date datum, int dauer,
			int limit, String name);


	void belegeStellplatz(ArrayList<Stellplatz> stellplaetze, int stellplatzNummer,
			Date datum, int dauer, String name);

	 void belegeStellplatz(Stellplatz stellplaetze,										/* WiSe14/15 */
                          Date datum, int dauer, String name);

	String[] getBelegungsPlan(ArrayList<Stellplatz> stellplaetze, int stellplatzNummer,
			String monat, int jahr);
	
	public Set<String> getAllBelegungenForMonth(ArrayList<Stellplatz> stellplaetze, String month);	/* WiSe14/15 */

	int removeBelegung(ArrayList<Stellplatz> stellplaetze, int stellplatzNummer,
			String name, Date datum);

	int getAnfragenAngenommen();

	int getAnfragenAbgelehnt();

}
