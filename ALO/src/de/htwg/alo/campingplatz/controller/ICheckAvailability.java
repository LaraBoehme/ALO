package de.htwg.alo.campingplatz.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import de.htwg.alo.campingplatz.model.Stellplatz;

public interface ICheckAvailability {

	boolean checkAvailability(ArrayList<Stellplatz> stellplaetze, Date datum, int dauer,
			int limit, String name, String zusatzInfos);

    boolean checkAvailability(Stellplatz stellplatz, Date datum, int dauer,			/* WiSe14/15 */
    								int limit, String name, String zusatzInfos);

	int checkAvailabilityTest(ArrayList<Stellplatz> stellplaetze, Date datum, int dauer,
			int limit, String name, String zusatzInfos);


	void belegeStellplatz(ArrayList<Stellplatz> stellplaetze, int stellplatzIndex,
			Date datum, int dauer, String name, String zusatzInfos);

	 void belegeStellplatz(Stellplatz stellplatz,										/* WiSe14/15 */
                          Date datum, int dauer, String name, String zusatzInfos);
	
	public Set<String> getAllBelegungenForMonth(ArrayList<Stellplatz> stellplaetze, String month);	/* WiSe14/15 */

	int removeBelegung(ArrayList<Stellplatz> stellplaetze, int stellplatzNummer,
			String name, Date datum);

	int getAnfragenAngenommen();

	int getAnfragenAbgelehnt();

}
