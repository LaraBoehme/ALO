package de.htwg.alo.campingplatz.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.htwg.alo.campingplatz.util.DateUtil;

public class Stellplatz {

	private DateItem firstItem;
	private DateItem lastItem;

	public String[] getBelegungenAsString() {
		DateItem currentItem = firstItem;
		String[] myBelegungen = new String[1000];
		int i = 0;
		while (currentItem != null) {
			myBelegungen[i] = currentItem.getName() + "_"
					+ DateUtil.getInstance().formatDate(currentItem.getDate());
			i++;
			currentItem = currentItem.next;
		}
		return myBelegungen;
	}
	
	public Set<DateItem> getBelegungsDates() {
		DateItem currentItem = firstItem;
		Set<DateItem> myBelegungen = new HashSet<>();
		while (currentItem != null) {
			myBelegungen.add(currentItem);
			currentItem = currentItem.next;
		}
		return myBelegungen;
	}

	public DateItem getBelegungen() {
		return firstItem;
	}

	public void addDate(Date datum, String name) {
		// erstes Element der Liste
		if (firstItem == null) {
			firstItem = new DateItem(datum, name);
			lastItem = firstItem;

		} else {
			// neues Datum hinter dem bisherigen letzten
			if (lastItem.getDate().before(datum)) {
				lastItem.next = new DateItem(datum, name);
				lastItem = lastItem.next;
				return;

				// neues Datum vor dem ersten
			} else if (datum.before(firstItem.getDate())) {
				DateItem tempItem = firstItem;
				firstItem = new DateItem(datum, name);
				firstItem.next = tempItem;
				System.out.println("---- TEST DATUM VOR ERSTEN -----");

			}
			DateItem currentItem = firstItem;
			while (currentItem.next != null) {
				// neues Datum nach dem Ersten und vor dem n�chsten
				if (currentItem.getDate().before(datum)
						&& currentItem.next.getDate().after(datum)) {
					DateItem newDate = new DateItem(datum, name);
					newDate.next = currentItem.next;
					currentItem.next = newDate;
					System.out.println("test--1");					/*NEU*/
				} else {
					System.out.println("test--2");					/*NEU*/
					System.out.println(currentItem.name);			/*NEU*/
					
				}
				currentItem = currentItem.next;
			}
			
			
		}
	}

	public int removeBelegung(String name, Date datum) {
		DateItem nextItem;
		DateItem tempItem;
		if (firstItem != null) {
			// L�schung des ersten Elementes
			if (firstItem.getDate().compareTo(datum) == 0
					&& firstItem.getName().equalsIgnoreCase(name)) {
				firstItem = firstItem.next;
				return 1;
			} else {
				tempItem = firstItem.next;
				nextItem = firstItem;
				while (tempItem != null) {
					// L�schung des passenden Elementes
					if (tempItem.getDate().compareTo(datum) == 0
							&& tempItem.getName().equalsIgnoreCase(name)) {
						nextItem.next = tempItem.next;
						return 1;
					} else {
						nextItem = tempItem;
						tempItem = tempItem.next;
					}
				}
			}
			return -1;
		}
		return -1;
	}

	public int checkAvailabilitySP(Date datum) {
		if (firstItem == null){
			System.out.println("return  -1‚");
			return -1;
		}
			
		DateItem currentItem = firstItem;
		while (currentItem != null) {
			if (currentItem.getDate().after(datum)) {
						
						
				return (int) ((currentItem.getDate().getTime() - datum
						.getTime()) / (1000 * 60 * 60 * 24)); 
				
			}
			if (currentItem.getDate().equals(datum)){
				
				return 0;
			}
				
			if (currentItem.next == null && currentItem.getDate().before(datum)){
				
				return -1;
			}
				
			if (currentItem.next != null
					&& (currentItem.getDate().before(datum) && currentItem.next
							.getDate().after(datum)))
				return (int) ((currentItem.next.getDate().getTime() - datum
						.getTime()) / (1000 * 60 * 60 * 24));

			currentItem = currentItem.next;
		}
		
		return 0;
	}

	public String[] getBelegungsPlanSP(String monat, int jahr,
			int stellplatzNummer) {
		String[] belegungen = new String[32];
		GregorianCalendar gc = new GregorianCalendar(jahr,
				(Integer.parseInt(monat) - 1), 01);

		int zaehler = 1;
		belegungen[0] = "" + (stellplatzNummer + 1);
		DateItem currentItem = firstItem;
		while (currentItem != null) {
			// If (�bergebener monat = monat der belegung)
			String tempMonat = DateUtil.getInstance()
					.formatDate(currentItem.getDate()).substring(3, 5);
			if (tempMonat.equalsIgnoreCase(monat)) {

				while (currentItem != null
						&& gc.get(GregorianCalendar.MONTH) == Integer
								.parseInt(monat) - 1) {
					// Schreibe Wert, wenn gleiches Datum, ansonsten
					// <placeholder>
					if (DateUtil
							.getInstance()
							.formatDate(gc.getTime())
							.equalsIgnoreCase(
									DateUtil.getInstance().formatDate(
											currentItem.getDate()))) {
						belegungen[zaehler] = currentItem.getName();
						gc.add(Calendar.DATE, 1);
						zaehler++;
						break;
					} else {
						belegungen[zaehler] = "";
					}
					gc.add(Calendar.DATE, 1);
					zaehler++;
				}

			}
			currentItem = currentItem.next;

		}
		return belegungen;
	}

	public class DateItem {
		public DateItem next = null;
		public DateItem previous = null;
		private Date value = null;
		private String name;

		public DateItem(Date value, String name) {
			this.value = value;
			this.name = name;
		}

		public Date getDate() {
			return this.value;
		}

		public String getName() {
			return this.name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DateItem other = (DateItem) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		private Stellplatz getOuterType() {
			return Stellplatz.this;
		}
		
		
	}

}
