package de.htwg.alo.campingplatz.model;

import java.util.Date;

public class Anfrage {

	private String person;
	private Date datumAn;
	private Date datumAb;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Date getDatumAn() {
		return datumAn;
	}

	public void setDatumAn(Date datumAn) {
		this.datumAn = datumAn;
	}

	public Date getDatumAb() {
		return datumAb;
	}

	public void setDatumAb(Date datumAb) {
		this.datumAb = datumAb;
	}

}
