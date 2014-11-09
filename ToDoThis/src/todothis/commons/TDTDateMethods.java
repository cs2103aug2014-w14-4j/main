package todothis.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This TDTDateMethods class stores all the static date related methods which
 * are called by other components of the software.
 * 
 * @author
 *
 */
public class TDTDateMethods {
	/**
	 * This method checks if the string follows a certain type of date format.
	 * 
	 * @param nextWord
	 * @return boolean This returns true if nextWord is a possible date.
	 * 
	 */
	public static boolean checkDate(String nextWord) {
		nextWord = TDTCommons.replaceEndStringPunctuation(nextWord);
		String[] parts;

		if (isDateFormat1(nextWord)) {
			return true;
		}
		if (isDateFormat2(nextWord)) {
			parts = nextWord.split("/");
			if (isNumberFormatException(parts)) {
				return false;
			}
			return true;
		} else if (isDateFormat3(nextWord)) {
			parts = nextWord.split("-");
			if (isNumberFormatException(parts)) {
				return false;
			}
			return true;
		}
		return false;
	}

	// Checks if nextWord follow date format DDMMYY or DDMMYYYY
	private static boolean isDateFormat1(String nextWord) {
		return (nextWord.length() == 6 || nextWord.length() == 8)
				&& (nextWord.matches("\\d+"));
	}

	// Check if nextWord follow date format DD/MM/YYYY or DD/MM
	private static boolean isDateFormat2(String nextWord) {
		return (nextWord.split("/").length == 3)
				|| (nextWord.split("/").length == 2);
	}

	// Check if nextWord follow date format DD-MM-YYYY or DD-MM
	private static boolean isDateFormat3(String nextWord) {
		return (nextWord.split("-").length == 3)
				|| (nextWord.split("-").length == 2);
	}

	// Checks if the date consists of numbers only
	private static boolean isNumberFormatException(String[] parts) {
		try {
			for (int i = 0; i < parts.length; i++) {
				Integer.parseInt(parts[i]);
			}
		} catch (NumberFormatException e) {
			return true;
		}
		return false;
	}

	/**
	 * This method adds a number of days to a date.
	 * 
	 * @param currentDay
	 * @param currentMonth
	 * @param currentYear
	 * @param numOfDaysToAdd
	 * @return String This returns the new date following a date format after
	 *         adding n days.
	 */
	public static String addDaysToCurrentDate(int currentDay, int currentMonth,
			int currentYear, int numOfDaysToAdd) {
		String currentDate = currentDay + "/" + currentMonth + "/"
				+ currentYear;

		assert (TDTDateMethods.isValidDateRange(currentDate));

		SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
		Date d = new Date();
		try {
			d = sdf.parse(currentDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, numOfDaysToAdd);
		String newDateString = sdf.format(c.getTime());
		return newDateString;
	}

	/**
	 * This method converts the old date format dd/MM/yyyy to a new date format
	 * that reflects of the day of the week for display.
	 * 
	 * @param date
	 * @return String This returns the date that follows the new format.
	 */
	public static String changeToDayOfWeek(String date) {
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

	/**
	 * This method converts the old date format dd/MM/yyyy to a new date format
	 * d/M/yyyy.
	 * 
	 * @param decodedDate
	 * @return String This returns the date following the new format.
	 */
	public static String changeDateFormat(String decodedDate) {
		final String OLD_FORMAT = "dd/MM/yyyy";
		final String NEW_FORMAT = "d/M/yyyy";

		if (TDTDateMethods.isValidDateRange(decodedDate)) {
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
		}
		return decodedDate;
	}

	/**
	 * This method converts the old date format dd/MM/yyyy to a new date format
	 * d MMM yyyy (10 nov 2014) for display.
	 * 
	 * @param date
	 * @return String This returns the date following the new format.
	 */
	public static String changeDateFormatDisplay(String date) {
		final String OLD_FORMAT = "dd/MM/yyyy";
		final String NEW_FORMAT = "d MMM yyyy";

		String oldDateString = date;
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
		return newDateString;
	}

	/**
	 * This method checks if the date falls in the a valid date range.
	 * 
	 * @param date
	 * @return boolean This return true if date is valid and false if otherwise.
	 */
	public static boolean isValidDateRange(String date) {
		String[] dateParts = date.split("/");
		int day;
		int month;
		int year;

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

	/**
	 * This method compares the two dates and checks if they are of the same
	 * date or one being later or earlier than another.
	 * 
	 * @param date1
	 * @param date2
	 * @return int This returns a value -1 if date1>date2, a value 0 if
	 *         date1=date2 and a value 1 if date2>date1
	 */
	public static int compareToDate(String date1, String date2) {
		if (date1.equals("null") && !date2.equals("null")) {
			return -1;
		} else if (date1.equals("null") && date2.equals("null")) {
			return 0;
		} else if (!date1.equals("null") && date2.equals("null")) {
			return 1;
		}

		assert (TDTDateMethods.isValidDateRange(date1));
		assert (TDTDateMethods.isValidDateRange(date2));

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

	/**
	 * This method takes into account leap years and gets the number of days of
	 * that particular month of the year.
	 *
	 * @param month
	 * @param year
	 * @return int This returns the number of days of the month in that year.
	 */
	public static int getNumOfDaysFromMonth(int month, int year) {
		assert (month >= 1 && month <= 12);

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

	/**
	 * This method checks if the string depicts the day of the week or today or
	 * tomorrow.
	 * 
	 * @param day
	 * @return int This returns a integer value that correspond to each day of
	 *         the week or today or tomorrow. Example: A value 1 is returned
	 *         when the string is "sunday".
	 */
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

	/**
	 * This method checks if the string depicts one of the month of the year.
	 * 
	 * @param month
	 * @return int This returns a integer value that correspond to the month of
	 *         the year.
	 */
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

	/**
	 * This method checks if the string is either week, month or year.
	 * 
	 * @param string
	 * @return This returns a integer value that correspond to either week,
	 *         month or year.
	 */
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
