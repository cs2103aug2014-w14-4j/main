package todothis.dateandtime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TDTDateMethods {
	public static boolean checkDate(String nextWord) {
		nextWord = TDTDateAndTime.replaceEndStringPunctuation(nextWord);
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
	
	public static String decodeDate(String[] parts, int a, int currentYear,
			int currentMonth) {
		String[] dateParts = new String[3];
		String[] datePartsTemp = null;
		// 9/12, 9/12/2014, 8-11, 8-11-2015 9/12/12
		if ((parts[a].split("/").length == 3)
				|| (parts[a].split("/").length == 2)) {
			datePartsTemp = parts[a].split("/");
		} else if ((parts[a].split("-").length == 3)
				|| (parts[a].split("-").length == 2)) {
			datePartsTemp = parts[a].split("-");
		} else {
			dateParts[0] = parts[a].substring(0, 2);
			dateParts[1] = parts[a].substring(2, 4);
			if (parts[a].length() == 6) {
				dateParts[2] = "20" + parts[a].substring(4, 6); // valid year
																// 2014-2099
			} else if (parts[a].length() == 8) {
				dateParts[2] = parts[a].substring(4, 8);
			}
		}
		// if 9/12 entered, add on to 9/12/2014
		if (datePartsTemp != null) {
			if (datePartsTemp.length == 2) {
				boolean isMonthInt = true;
				dateParts[0] = datePartsTemp[0];
				dateParts[1] = datePartsTemp[1];

				try {
					Integer.parseInt(dateParts[1]);
				} catch (NumberFormatException e) {
					isMonthInt = false;
				}
				if (isMonthInt) {
					if (currentMonth > Integer.parseInt(datePartsTemp[1])) {
						dateParts[2] = Integer.toString(currentYear + 1);
					} else {
						dateParts[2] = Integer.toString(currentYear);
					}
				}
			} else {
				dateParts = datePartsTemp;
			}
			if (datePartsTemp.length == 3) {
				if (datePartsTemp[2].length() == 2) {
					dateParts[2] = "20" + datePartsTemp[2];
				}
			}
		}
		return dateParts[0] + "/" + dateParts[1] + "/" + dateParts[2];
	}
	
	public static int determineDaysToBeAdded(int thisOrNextOrFollowing,
			String[] parts, int a, int currentDayOfWeek, int nextCount,
			int followingCount) {
		int numOfDaysToAdd = 0;
		if (TDTDateMethods.checkDay(parts[a]) <= 7 && TDTDateMethods.checkDay(parts[a]) > 0) {
			if (thisOrNextOrFollowing == 0) { // None of the above
				if (TDTDateMethods.checkDay(parts[a]) <= currentDayOfWeek) {
					numOfDaysToAdd = 7 - (currentDayOfWeek - TDTDateMethods.checkDay(parts[a]));
				} else {
					numOfDaysToAdd = TDTDateMethods.checkDay(parts[a]) - currentDayOfWeek;
				}
			} else {// this
				if (TDTDateMethods.checkDay(parts[a]) == 1) {// sunday
					if (currentDayOfWeek != 0) {
						numOfDaysToAdd = 8 - currentDayOfWeek;
					}
				} else {
					numOfDaysToAdd = TDTDateMethods.checkDay(parts[a]) - currentDayOfWeek;
				}
			}
			if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) { // next
																			// or
																			// following
				numOfDaysToAdd = numOfDaysToAdd + (14 * followingCount)
						+ (7 * nextCount);
			}
		} else if (TDTDateMethods.checkDay(parts[a]) == 8) {
			// numofdaystoadd already 0;
		} else if (TDTDateMethods.checkDay(parts[a]) == 9) {
			numOfDaysToAdd++;
		} else if (TDTDateMethods.checkDay(parts[a]) == 10) {
			if (thisOrNextOrFollowing == 2) {// next
				numOfDaysToAdd = numOfDaysToAdd + (1 * nextCount);

			} else if (thisOrNextOrFollowing == 3) {// following
				numOfDaysToAdd = numOfDaysToAdd + (2 * followingCount);
			}
		}
		return numOfDaysToAdd;
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
		day = TDTDateAndTime.replaceEndStringPunctuation(day);

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
