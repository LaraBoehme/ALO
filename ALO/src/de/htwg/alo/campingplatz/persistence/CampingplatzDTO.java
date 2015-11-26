package de.htwg.alo.campingplatz.persistence;

import de.htwg.alo.campingplatz.model.Stellplatz;

public class CampingplatzDTO {

	private Stellplatz[] stellplaetze = null;

	public Stellplatz[] getStellplaetze() {
		return stellplaetze;
	}

	public void setStellplaetze(Stellplatz[] stellplaetze) {
		this.stellplaetze = stellplaetze;
	}

}
