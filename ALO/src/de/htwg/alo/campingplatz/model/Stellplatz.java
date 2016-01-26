package de.htwg.alo.campingplatz.model;

import java.util.Date;
import java.util.HashSet;
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
		Set<DateItem> myBelegungen = new HashSet<DateItem>();
		while (currentItem != null) {
			myBelegungen.add(currentItem);
			currentItem = currentItem.next;
		}
		return myBelegungen;
	}

	public DateItem getBelegungen() {
		return firstItem;
	}

	public void addDate(Date datum, String name, String zusatzInfos) {
		// erstes Element der Liste
		if (firstItem == null) {
			firstItem = new DateItem(datum, name, zusatzInfos);
			lastItem = firstItem;

		} else {
			
			// neues Datum hinter dem bisherigen letzten 
			if (lastItem.getDate().before(datum)) {	
				lastItem.next = new DateItem(datum, name, zusatzInfos);
				lastItem = lastItem.next;
				return;

				// neues Datum vor dem ersten
			} else if (datum.before(firstItem.getDate())) {
				DateItem tempItem = firstItem;
				firstItem = new DateItem(datum, name, zusatzInfos);
				firstItem.next = tempItem;
			}
		
			DateItem currentItem = firstItem;
			while (currentItem.next != null) {
				// neues Datum nach dem Ersten und vor dem n�chsten
				if (currentItem.getDate().before(datum)
						&& currentItem.next.getDate().after(datum)) {
					DateItem newDate = new DateItem(datum, name, zusatzInfos);
					newDate.next = currentItem.next;
					currentItem.next = newDate;			/*NEU*/
				} else {
							/*NEU*/
					
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
						if(lastItem.getDate().equals(tempItem.getDate())){
							lastItem = nextItem;
						}
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


	public class DateItem {
		public DateItem next = null;
		public DateItem previous = null;
		private Date value = null;
		private String name;
		private String zusatzInfos;

		public DateItem(Date value, String name, String zusatzInfos) {
			this.value = value;
			this.name = name;
			this.zusatzInfos = zusatzInfos;
		}

		public Date getDate() {
			return this.value;
		}
		
		public void setDate(Date value) {
			this.value = value;
		}

		public String getName() {
			return this.name;
		}
		
		public String getZusatzInfos() {
			return zusatzInfos;
		}

		public void setZusatzInfos(String zusatzInfos) {
			this.zusatzInfos = zusatzInfos;
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
