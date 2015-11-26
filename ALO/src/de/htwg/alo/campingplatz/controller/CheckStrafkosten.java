package de.htwg.alo.campingplatz.controller;

//In dieser Klasse werden die überbuchten Tage als Strafkosten für den Anwender dargestellt. Die Strafkosten wachsen nicht-linear an.
//
//		Die folgende Tabelle zeigt die Höhe der Strafkosten an:
//
//		1 Tag			1
//		2 Tage			2
//		3 Tage			3
//		4 Tage			4
//		5 Tage			5
//		6 Tage			6
//		7 Tage			7

public class CheckStrafkosten {

	public int getStrafkosten(int dauer) {
		int strafkosten = dauer;

		switch (dauer) {
		case 1:
			strafkosten = 1;
			break;
		case 2:
			strafkosten = 2;
			break;
		case 3:
			strafkosten = 3;
			break;
		case 4:
			strafkosten = 4;
			break;
		case 5:
			strafkosten = 5;
			break;
		case 6:
			strafkosten = 6;
			break;
		case 7:
			strafkosten = 7;
			break;
		// Bei Überbuchung > 1 Woche - absage!
		default:
			strafkosten = 1337;
		}

		return strafkosten;
	}
}
