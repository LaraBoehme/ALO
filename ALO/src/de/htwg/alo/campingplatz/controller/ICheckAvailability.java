package de.htwg.alo.campingplatz.controller;

import java.util.Date;
import java.util.List;
import java.util.Set;

import de.htwg.alo.campingplatz.model.Stellplatz;

public interface ICheckAvailability {

	boolean checkAvailability(Stellplatz[] stellplaetze, Date datum, int dauer,
			int limit, String name);

    boolean checkAvailability(Stellplatz stellplaetze, Date datum, int dauer,			/* WiSe14/15 */
    								int limit, String name, int stellplatzNummber);

	int checkAvailabilityTest(Stellplatz[] stellplaetze, Date datum, int dauer,
			int limit, String name);


	void belegeStellplatz(Stellplatz[] stellplaetze, int stellplatzNummer,
			Date datum, int dauer, String name);

	 void belegeStellplatz(Stellplatz stellplaetze,										/* WiSe14/15 */
                          Date datum, int dauer, String name);

	String[] getBelegungsPlan(Stellplatz[] stellplaetze, int stellplatzNummer,
			String monat, int jahr);
	
	public Set<String> getAllBelegungenForMonth(Stellplatz[] stellplaetze, String month);	/* WiSe14/15 */

	int removeBelegung(Stellplatz[] stellplaetze, int stellplatzNummer,
			String name, Date datum);

	int getAnfragenAngenommen();

	int getAnfragenAbgelehnt();

}
