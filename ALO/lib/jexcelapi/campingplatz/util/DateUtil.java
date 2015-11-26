package de.htwg.alo.campingplatz.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	private static DateUtil INSTANCE = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

	private DateUtil() {

	}

	public static DateUtil getInstance() {
		if (INSTANCE == null)
			INSTANCE = new DateUtil();
		return INSTANCE;
	}

	public String formatDate(Date date) {
		return sdf.format(date);
	}

	public Date formatString(String date) {
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getMonth(Date date) {
		SimpleDateFormat tSdf = new SimpleDateFormat("MMMM");
		return tSdf.format(date);
	}
}
