package de.htwg.alo.campingplatz.persistence;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Campingplatz")
public class StellplatzDTO {
	int stellpatz;
	String datumVon;
	int dauer;

	@XmlElement(name = "stellplatz")
	public int getStellpatz() {
		return stellpatz;
	}

	public void setStellpatz(int stellpatz) {
		this.stellpatz = stellpatz;
	}

	@XmlElement(name = "datumVon")
	public String getDatumVon() {
		return datumVon;
	}

	public void setDatumVon(String datumVon) {
		this.datumVon = datumVon;
	}

	@XmlElement(name = "dauer")
	public int getDauer() {
		return dauer;
	}

	public void setDauer(int dauer) {
		this.dauer = dauer;
	}

}
