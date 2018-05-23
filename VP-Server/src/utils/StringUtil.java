package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {
	
	public static String addSeparatorToDataString(String dateWithoutSeparator, String separator) {
		String year = dateWithoutSeparator.substring(0, 4);
		String month = dateWithoutSeparator.substring(4, 6);
		String day = dateWithoutSeparator.substring(6, 8);
		return year + separator + month + separator + day;
	}
	
	public static String convertDateFormat(Date date) {
		if(date == null)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String format = formatter.format(date);
		return format;
	}
	
	public static Date convertStringToDate(String s) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return formatter.parse(s);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

}
