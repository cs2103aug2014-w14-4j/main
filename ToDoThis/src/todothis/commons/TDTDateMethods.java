package todothis.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TDTDateMethods {
	public static boolean checkDate(String nextWord) {
		nextWord = TDTCommons.replaceEndStringPunctuation(nextWord);
		String[] parts;
		if ((nextWord.split("/").length == 3)
				|| (nextWord.split("/").length == 2)) {
			parts = nextWord.split("/");
			try {
				for (int i = 0; i < parts.length; i++) {
					Integer.parseInt(parts[i]);
				}
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		} else if ((nextWord.split("-").length == 3)
				|| (nextWord.split("-").length == 2)) {
			parts = nextWord.split("-");
			try {
				for (int i = 0; i < parts.length; i++) {
					Integer.parseInt(parts[i]);
				}
			} catch (NumberFormatException e) {
				return false;
			}
			return true;
		} else if ((nextWord.length() == 6) || (nextWord.length() == 8)) {
			if (nextWord.matches("\\d+")) {
				return true;
			}
		}
		return false;
	}
	
	public static String addDaysToCurrentDate(int currentDay,
			int currentMonth, int currentYear, int numOfDaysToAdd) {
		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
		Date d = new Date();
		try {
			d = sdf.parse(currentDay + "/" + currentMonth + "/" + currentYear);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, numOfDaysToAdd);
		String newDateString = sdf.format(c.getTime());
		return newDateString;
	}
	
	public static String changeToDayOfWeek(String date){
		final String OLD_FORMAT = "dd/MM/yyyy";
		final String NEW_FORMAT = "EEE";
		String oldDateString = date;
		String dayOfWeek = "";
		
		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = new Date();
		try {
			d = sdf.parse(oldDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		dayOfWeek = sdf.format(d);
		return dayOfWeek;
	}
	
	public static String changeDateFormat(String decodedDate) {
		final String OLD_FORMAT = "dd/MM/yyyy";
		final String NEW_FORMAT = "d/M/yyyy";

		String oldDateString = decodedDate;
		String newDateString = "";

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = new Date();
		try {
			d = sdf.parse(oldDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		newDateString = sdf.format(d);
		decodedDate = newDateString;
		return decodedDate;
	}
		
	public static String changeDateFormatDisplay(String date) {
		final String OLD_FORMAT = "dd/MM/yyyy";
		final String NEW_FORMAT = "d MMM yyyy";

		String oldDateString = date;
		String newDateString;

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = new Date();
		try {
			d = sdf.parse(oldDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		newDateString = sdf.format(d);
		return newDateString;
	}
	
	public static boolean isValidDateRange(String date) {
		String[] dateParts = date.split("/");
		int day, month, year;
		if (date.equals("null")) {
			return true;
		}

		try {
			day = Integer.parseInt(dateParts[0]);
			month = Integer.parseInt(dateParts[1]);
			year = Integer.parseInt(dateParts[2]);
		} catch (NumberFormatException e) {
			return false;
		}

		if ((year >= 2014) && (year <= 2099)) {
			if ((month >= 1) && (month <= 12)) {
				if ((day >= 1) && (day <= getNumOfDaysFromMonth(month, year))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isValidDateCompare(String startDate, String endDate) {
		String[] startDateParts = startDate.split("/");
		String[] endDateParts = endDate.split("/");

		int startDay = Integer.parseInt(startDateParts[0]);
		int startMonth = Integer.parseInt(startDateParts[1]);
		int startYear = Integer.parseInt(startDateParts[2]);
		int endDay = Integer.parseInt(endDateParts[0]);
		int endMonth = Integer.parseInt(endDateParts[1]);
		int endYear = Integer.parseInt(endDateParts[2]);

		if (endYear > startYear) {
			return true;
		} else if (endYear == startYear) {
			if (endMonth > startMonth) {
				return true;
			} else if (endMonth == startMonth) {
				if (endDay >= startDay) {
					return true;
				}
			}
		}
		return false;
	}

	public static int compareToDate(String date1, String date2) {

		if (date1.equals("null") && !date2.equals("null")) { // compareddate<thisdate
			return -1;
		} else if (date1.equals("null") && date2.equals("null")) {
			return 0;
		} else if (!date1.equals("null") && date2.equals("null")) {
			return 1;
		}

		String[] date1Parts = date1.split("/");
		String[] date2Parts = date2.split("/");

		int date1Day = Integer.parseInt(date1Parts[0]);
		int date1Month = Integer.parseInt(date1Parts[1]);
		int date1Year = Integer.parseInt(date1Parts[2]);
		int date2Day = Integer.parseInt(date2Parts[0]);
		int date2Month = Integer.parseInt(date2Parts[1]);
		int date2Year = Integer.parseInt(date2Parts[2]);

		if (date2Year > date1Year) {
			return 1;
		} else if (date2Year == date1Year) {
			if (date2Month > date1Month) {
				return 1;
			} else if (date2Month == date1Month) {
				if (date2Day > date1Day) {
					return 1;
				} else if (date2Day == date1Day) {
					return 0;
				} else {
					return -1;
				}
			}
		}
		return -1;
	}

	public static int getNumOfDaysFromMonth(int month, int year) {
		int days = 0;
		boolean isLeapYear = false;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			days = 31;
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			days = 30;
			break;
		case 2:
			if (year % 400 == 0) {
				isLeapYear = true;
			} else if (year % 100 == 0) {
				isLeapYear = false;
			} else if (year % 4 == 0) {
				isLeapYear = true;
			} else {
				isLeapYear = false;
			}
			if (isLeapYear) {
				days = 29;
				break;
			} else {
				days = 28;
				break;
			}
		}
		return days;
	}
	
	public static int checkDay(String day) {
		day = TDTCommons.replaceEndStringPunctuation(day);

		if ((day.equalsIgnoreCase("Sunday")) || (day.equalsIgnoreCase("Sun"))) {
			return 1;
		} else if ((day.equalsIgnoreCase("Monday"))
				|| (day.equalsIgnoreCase("Mon"))) {
			return 2;
		} else if ((day.equalsIgnoreCase("Tuesday"))
				|| (day.equalsIgnoreCase("Tue"))
				|| (day.equalsIgnoreCase("Tues"))) {
			return 3;
		} else if ((day.equalsIgnoreCase("Wednesday"))
				|| (day.equalsIgnoreCase("Wed"))) {
			return 4;
		} else if ((day.equalsIgnoreCase("Thursday"))
				|| (day.equalsIgnoreCase("Thur"))
				|| (day.equalsIgnoreCase("Thurs"))) {
			return 5;
		} else if ((day.equalsIgnoreCase("Friday"))
				|| (day.equalsIgnoreCase("Fri"))) {
			return 6;
		} else if ((day.equalsIgnoreCase("Saturday"))
				|| (day.equalsIgnoreCase("Sat"))) {
			return 7;
		} else if ((day.equalsIgnoreCase("Today"))
				|| (day.equalsIgnoreCase("Tdy"))) {
			return 8;
		} else if ((day.equalsIgnoreCase("Tomorrow"))
				|| (day.equalsIgnoreCase("Tml"))
				|| (day.equalsIgnoreCase("Tmw"))
				|| (day.equalsIgnoreCase("Tmr"))
				|| (day.equalsIgnoreCase("2moro"))) {
			return 9;
		} else if (day.equalsIgnoreCase("Day")) {
			return 10;
		} else {
			return 0;
		}
	}

	public static int checkMonth(String month) {
		if ((month.equalsIgnoreCase("January"))
				|| (month.equalsIgnoreCase("Jan"))) {
			return 1;
		} else if ((month.equalsIgnoreCase("February"))
				|| (month.equalsIgnoreCase("Feb"))) {
			return 2;
		} else if ((month.equalsIgnoreCase("March"))
				|| (month.equalsIgnoreCase("Mar"))) {
			return 3;
		} else if ((month.equalsIgnoreCase("April"))
				|| (month.equalsIgnoreCase("Apr"))) {
			return 4;
		} else if ((month.equalsIgnoreCase("May"))) {
			return 5;
		} else if ((month.equalsIgnoreCase("June"))
				|| (month.equalsIgnoreCase("Jun"))) {
			return 6;
		} else if ((month.equalsIgnoreCase("July"))
				|| (month.equalsIgnoreCase("Jul"))) {
			return 7;
		} else if ((month.equalsIgnoreCase("August"))
				|| (month.equalsIgnoreCase("Aug"))) {
			return 8;
		} else if ((month.equalsIgnoreCase("September"))
				|| (month.equalsIgnoreCase("Sep"))
				|| (month.equalsIgnoreCase("Sept"))) {
			return 9;
		} else if ((month.equalsIgnoreCase("October"))
				|| (month.equalsIgnoreCase("Oct"))) {
			return 10;
		} else if ((month.equalsIgnoreCase("November"))
				|| (month.equalsIgnoreCase("Nov"))) {
			return 11;
		} else if ((month.equalsIgnoreCase("December"))
				|| (month.equalsIgnoreCase("Dec"))) {
			return 12;
		} else {
			return 0;
		}
	}

	public static int checkWeekMonthYear(String string) {
		if ((string.equalsIgnoreCase("Week"))
				|| (string.equalsIgnoreCase("Wk"))) {
			return 1;
		} else if ((string.equalsIgnoreCase("Month"))
				|| (string.equalsIgnoreCase("Mth"))) {
			return 2;
		} else if ((string.equalsIgnoreCase("Year"))
				|| (string.equalsIgnoreCase("Yr"))) {
			return 3;
		} else {
			return 0;
		}
	}
}
