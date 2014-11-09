package todothis.logic.parser;

import java.util.Calendar;
import java.util.TimeZone;

import todothis.commons.TDTCommons;
import todothis.commons.TDTDateAndTime;
import todothis.commons.TDTDateMethods;
import todothis.commons.TDTTimeMethods;

/**
 * This TDTDateAndTimeParser class parses the string of date and time details to
 * be stored as a TDTDateAndTime object. It also provides a method to parse
 * search by date details and another method to parse set reminder details.
 * 
 * @author
 *
 */
public class TDTDateAndTimeParser {
	// store converted date format dd/mm/yyyy
	private String startDate = "null";
	private String endDate = "null";

	// store converted time format XX:XX 24hrs format
	private String startTime = "null";
	private String endTime = "null";

	private static Calendar cal;

	/**
	 * Constructor
	 * 
	 */
	public TDTDateAndTimeParser() {
		this.startDate = "null";
		this.endDate = "null";
		this.startTime = "null";
		this.endTime = "null";
	}

	/**
	 * This method parse the date and time details.
	 * 
	 * @param details
	 * @return TDTDateAndTime This returns the object TDTDateAndTime with the
	 *         date and time info
	 */
	public TDTDateAndTime decodeDateAndTimeDetails(String details) {
		boolean endTimeDate = false;
		boolean deadlineEndTimeDate = false;
		int thisOrNextOrFollowing = 0; // this = 1 next = 2 following = 3
		String decodedDate = "";
		String decodedTime = "";
		int nextCount = 0;
		int followingCount = 0;

		String[] parts = details.toLowerCase().split(" ");

		// Get current date
		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		// Goes through the string of words in the details
		for (int a = 0; a < parts.length; a++) {
			parts[a] = TDTCommons.replaceEndStringPunctuation(parts[a]);

			if (isPrepositionTo(parts, a)) {
				endTimeDate = true;
				// resets the value for the next date to be encountered
				nextCount = 0;
				followingCount = 0;
			}

			if (isPrepositionDeadline(parts, a)) {
				deadlineEndTimeDate = true;
			}

			if (parts[a].equals("this")) {
				thisOrNextOrFollowing = 1;
			} else if (parts[a].equals("next")) {
				nextCount++;
				thisOrNextOrFollowing = 2;
			} else if (parts[a].equals("following")) {
				followingCount++;
				thisOrNextOrFollowing = 3;
			}

			if (TDTDateMethods.checkDate(parts[a])) {
				decodedDate = decodeDate(parts, a, currentYear, currentMonth);

				storeDecodedDate(endTimeDate, deadlineEndTimeDate, decodedDate);
			} else if (TDTTimeMethods.checkTime(parts[a])) {
				decodedTime = decodeTime(parts, a);

				storeDecodedTime(endTimeDate, deadlineEndTimeDate, decodedTime);
			} else if (TDTDateMethods.checkDay(parts[a]) != 0) {
				int numOfDaysToAdd = determineDaysToBeAdded(
						thisOrNextOrFollowing, parts, a, currentDayOfWeek,
						nextCount, followingCount);

				decodedDate = TDTDateMethods.addDaysToCurrentDate(currentDay,
						currentMonth, currentYear, numOfDaysToAdd);

				decodedDate = adjustmentToDate(endTimeDate, decodedDate, parts,
						a);

				storeDecodedDate(endTimeDate, deadlineEndTimeDate, decodedDate);
			} else if (TDTDateMethods.checkMonth(parts[a].replaceAll("[0-9~]",
					"")) != 0) {
				decodedDate = decodeMonthFormat(parts, a, currentYear,
						currentMonth);

				storeDecodedDate(endTimeDate, deadlineEndTimeDate, decodedDate);
			}
		}
		if (isOnlyEndDateNull()) {
			endDate = startDate;
		}
		return new TDTDateAndTime(startDate, endDate, startTime, endTime);
	}

	// Handles the case when user inputs a single date with start and end time
	private static boolean isPrepositionTo(String[] parts, int a) {
		return parts[a].equals("to") || parts[a].equals("till")
				|| parts[a].equals("until") || parts[a].equals("-");
	}

	private boolean isOnlyEndDateNull() {
		return !startDate.equals("null") && endDate.equals("null")
				&& !startTime.equals("null") && !endTime.equals("null");
	}

	// Checks for occurrence of deadline related connectors
	private boolean isPrepositionDeadline(String[] parts, int a) {
		return parts[a].equals("by") || parts[a].equals("due")
				|| parts[a].equals("before");
	}

	// This method decodes the date and converts it to the DD/MM/YYYY date
	// format for storage
	// Date formats: 9/12, 9/12/2014, 8-11, 8-11-2015 9/12/12, 091214, 09122014
	private static String decodeDate(String[] parts, int a, int currentYear,
			int currentMonth) {
		String[] dateParts = new String[3];
		String[] datePartsTemp = null;
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
				// For date format 091214, it is converted to 09122014
				dateParts[2] = "20" + parts[a].substring(4, 6);
			} else if (parts[a].length() == 8) {
				dateParts[2] = parts[a].substring(4, 8);
			}
		}
		// For date format 9/12, it is converted 9/12/2014
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
				// Checks if the month has past for the current year to
				// determine the year
				// Assumes that user will not type past events of more than a
				// month
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
			// For date format 9/12/14, it is converted 9/12/2014
			if (datePartsTemp.length == 3) {
				if (datePartsTemp[2].length() == 2) {
					dateParts[2] = "20" + datePartsTemp[2];
				}
			}
		}
		return dateParts[0] + "/" + dateParts[1] + "/" + dateParts[2];
	}

	// This method decodes the date and converts it to the DD/MM/YYYY date
	// format storage
	// Date formats: 14 dec 2014 / 14 dec 14 / 14 dec
	private static String decodeMonthFormat(String[] parts, int a,
			int currentYear, int currentMonth) {
		String[] tempParts = parts[a].split("~");

		int day = Integer.parseInt(tempParts[0]);
		int month = TDTDateMethods.checkMonth(tempParts[1]);
		int year = 0;

		if (tempParts.length == 3) {
			if (tempParts[2].length() == 2) {
				tempParts[2] = "20" + tempParts[2];
			}
			year = Integer.parseInt(tempParts[2]);
		} else {
			if (currentMonth > month) {
				year = currentYear + 1;
			} else {
				year = currentYear;
			}
		}
		return day + "/" + month + "/" + year;
	}

	// This method determines the number of days to be added from the current
	// date
	private static int determineDaysToBeAdded(int thisOrNextOrFollowing,
			String[] parts, int a, int currentDayOfWeek, int nextCount,
			int followingCount) {
		int numOfDaysToAdd = 0;
		// If part[a] is a day between monday to sunday
		if (TDTDateMethods.checkDay(parts[a]) <= 7
				&& TDTDateMethods.checkDay(parts[a]) > 0) {
			// Does not have "this", "next" or "following" before the day
			if (thisOrNextOrFollowing == 0) {
				if (TDTDateMethods.checkDay(parts[a]) <= currentDayOfWeek) {
					numOfDaysToAdd = 7 - (currentDayOfWeek - TDTDateMethods
							.checkDay(parts[a]));
				} else {
					numOfDaysToAdd = TDTDateMethods.checkDay(parts[a])
							- currentDayOfWeek;
				}
			} else {// If "this" is before the day
				/*
				 * "this" is taken as the current week If the current day today
				 * is a wednesday, calling this tuesday will refer to the date
				 * which is yesterday.
				 */
				if (TDTDateMethods.checkDay(parts[a]) == 1) {// sunday
					if (currentDayOfWeek != 0) {
						numOfDaysToAdd = 8 - currentDayOfWeek;
					}
				} else {
					numOfDaysToAdd = TDTDateMethods.checkDay(parts[a])
							- currentDayOfWeek;
				}
			}
			// If "next" or "following" is before the day
			if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
				numOfDaysToAdd = numOfDaysToAdd + (14 * followingCount)
						+ (7 * nextCount);
			}
		} else if (TDTDateMethods.checkDay(parts[a]) == 8) { // today
			// value of numOfDaysToAdd is already 0;
		} else if (TDTDateMethods.checkDay(parts[a]) == 9) { // tomorrow
			numOfDaysToAdd++;
		} else if (TDTDateMethods.checkDay(parts[a]) == 10) { // day
			if (thisOrNextOrFollowing == 2) {// next
				numOfDaysToAdd = numOfDaysToAdd + (1 * nextCount);

			} else if (thisOrNextOrFollowing == 3) {// following
				numOfDaysToAdd = numOfDaysToAdd + (2 * followingCount);
			}
		}
		return numOfDaysToAdd;
	}

	/*
	 * This method does adjustment to the endDate if the startDate is not null.
	 * The endDate is < or = to the startDate due to cases when the user types
	 * in from today to this thursday when today is a friday. Hence this method
	 * adjusts the endDate to thursday of next week.
	 */
	private String adjustmentToDate(boolean endTimeDate, String decodedDate,
			String[] parts, int a) {
		String[] toBeAddedDateParts = decodedDate.split("/");
		if (endTimeDate == true) {
			if (!startDate.equals("null")
					&& TDTDateMethods.checkDay(parts[a]) != 8) {
				// endDate != today
				if (TDTDateMethods.compareToDate(startDate, decodedDate) == -1
						|| TDTDateMethods.compareToDate(startDate, decodedDate) == 0) {
					int dayTemp = Integer.parseInt(toBeAddedDateParts[0]);
					int mthTemp = Integer.parseInt(toBeAddedDateParts[1]);
					int yearTemp = Integer.parseInt(toBeAddedDateParts[2]);

					decodedDate = TDTDateMethods.addDaysToCurrentDate(dayTemp,
							mthTemp, yearTemp, 7);
				}
			}
		}
		return decodedDate;
	}

	// This method decodes the time and converts it to HH:mm time format for
	// storage
	// Time formats: 2am 11pm 2:00am 12:15pm 2345h 2345hr 2345hrs 15:35
	private static String decodeTime(String[] parts, int a) {
		String[] timeParts = new String[2];
		int temp;

		if ((parts[a].substring(parts[a].length() - 2, parts[a].length())
				.equals("am"))
				|| (parts[a]
						.substring(parts[a].length() - 2, parts[a].length())
						.equals("pm") || (parts[a].substring(
						parts[a].length() - 2, parts[a].length()).equals("hr")))) {
			if (parts[a].length() > 4) {
				if (parts[a].charAt(parts[a].length() - 5) == ':'
						|| parts[a].charAt(parts[a].length() - 5) == '.') {
					if (parts[a].length() == 6) {
						timeParts[0] = parts[a].substring(0, 1);
						timeParts[1] = parts[a].substring(2, 4);
					} else {
						timeParts[0] = parts[a].substring(0, 2);
						timeParts[1] = parts[a].substring(3, 5);
					}
				}
			}
			if (parts[a].substring(0, parts[a].length() - 2).matches("\\d+")) {
				if (parts[a].length() == 3 || parts[a].length() == 4) {
					timeParts[0] = parts[a].substring(0, parts[a].length() - 2);
					timeParts[1] = "00";
				} else {
					timeParts[0] = parts[a].substring(0, parts[a].length() - 4);
					timeParts[1] = parts[a].substring(parts[a].length() - 4,
							parts[a].length() - 2);
				}
			}

			temp = Integer.parseInt(timeParts[0]);
			if (parts[a].substring(parts[a].length() - 2, parts[a].length())
					.equals("pm")) {
				if (temp < 12) {
					temp = temp + 12; // convert to 24hrs format
				}
				timeParts[0] = Integer.toString(temp);
			} else if (parts[a].substring(parts[a].length() - 2,
					parts[a].length()).equals("am")) {
				if (temp == 12) {
					timeParts[0] = "00";
				}
			}
		} else if (parts[a].substring(parts[a].length() - 1, parts[a].length())
				.equals("h")) {
			timeParts[0] = parts[a].substring(0, parts[a].length() - 3);
			timeParts[1] = parts[a].substring(parts[a].length() - 3,
					parts[a].length() - 1);
		} else if (parts[a].substring(parts[a].length() - 3, parts[a].length())
				.equals("hrs")) {
			timeParts[0] = parts[a].substring(0, parts[a].length() - 5);
			timeParts[1] = parts[a].substring(parts[a].length() - 5,
					parts[a].length() - 3);
		} else {
			if (parts[a].length() > 2) {
				if (parts[a].charAt(parts[a].length() - 3) == ':'
						|| parts[a].charAt(parts[a].length() - 3) == '.') {
					timeParts[0] = parts[a].substring(0, parts[a].length() - 3);
					timeParts[1] = parts[a].substring(parts[a].length() - 2,
							parts[a].length());
				} else {
					timeParts[0] = parts[a].substring(0, parts[a].length() - 2);
					timeParts[1] = parts[a].substring(parts[a].length() - 2,
							parts[a].length());
				}
			}
		}
		return timeParts[0] + ":" + timeParts[1];
	}

	// This method stores the decoded time to either startTime or endTime
	// depending on the boolean value endTimeDate or deadlineEndTimeDate
	private void storeDecodedTime(boolean endTimeDate,
			boolean deadlineEndTimeDate, String decodedTime) {
		if (deadlineEndTimeDate == true) {
			endTime = decodedTime;
		} else if (endTimeDate == true) {
			if (startTime.equals("null")) {
				startTime = decodedTime;
			} else {
				endTime = decodedTime;
			}
		} else {
			startTime = decodedTime;
		}
	}

	// This method stores the decoded date to either startDate or endDate
	// depending on the boolean value endTimeDate or deadlineEndTimeDate
	private void storeDecodedDate(boolean endTimeDate,
			boolean deadlineEndTimeDate, String decodedDate) {
		// Change the date format to ensure that the date stored follows an
		// uniform date format used by other components.
		decodedDate = TDTDateMethods.changeDateFormat(decodedDate);

		if (deadlineEndTimeDate == true) {
			endDate = decodedDate;
		} else if (endTimeDate == true) {
			if (startDate.equals("null")) {
				startDate = decodedDate;
			} else {
				endDate = decodedDate;
			}
		} else {
			startDate = decodedDate;
		}
	}

	/**
	 * This method parses the search-by-date details.
	 * 
	 * @param searchString
	 * @return String This returns a list of dates in a string to be searched
	 *         from a list of tasks
	 */
	public static String decodeSearchDetails(String searchString) {
		String[] searchParts = searchString.toLowerCase().split(" ");
		int thisOrNextOrFollowing = 0; // this = 1 next = 2 following = 3
		String decodedSearchString = "";
		String decodedDate = "";
		int nextCount = 0;
		int followingCount = 0;
		String startSearchDate = "";
		String endSearchDate = "";
		boolean isSearchDateRange = false;
		// Get current date
		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		for (int i = 0; i < searchParts.length; i++) {
			searchParts[i] = TDTCommons
					.replaceEndStringPunctuation(searchParts[i]);

			if (isPrepositionTo(searchParts, i) && !startSearchDate.equals("")) {
				isSearchDateRange = true;
			}

			if (searchParts[i].equals("this")) {
				thisOrNextOrFollowing = 1;
			} else if (searchParts[i].equals("next")) {
				nextCount++;
				thisOrNextOrFollowing = 2;
			} else if (searchParts[i].equals("following")) {
				followingCount++;
				thisOrNextOrFollowing = 3;
			}

			if (TDTDateMethods.checkDate(searchParts[i])) {
				decodedDate = decodeDate(searchParts, i, currentYear,
						currentMonth);
				if (TDTDateMethods.isValidDateRange(decodedDate)) {
					decodedDate = TDTDateMethods.changeDateFormat(decodedDate);
				}
				if (isSearchDateRange) {
					endSearchDate = decodedDate;
				} else {
					startSearchDate = decodedDate;
				}
				decodedSearchString = decodedSearchString + decodedDate + " ";
			} else if (TDTDateMethods.checkDay(searchParts[i]) != 0) {
				int numOfDaysToAdd = determineDaysToBeAdded(
						thisOrNextOrFollowing, searchParts, i,
						currentDayOfWeek, nextCount, followingCount);
				decodedDate = TDTDateMethods.addDaysToCurrentDate(currentDay,
						currentMonth, currentYear, numOfDaysToAdd);
				if (TDTDateMethods.isValidDateRange(decodedDate)) {
					decodedDate = TDTDateMethods.changeDateFormat(decodedDate);
				}
				if (isSearchDateRange) {
					endSearchDate = decodedDate;
				} else {
					startSearchDate = decodedDate;
				}
				decodedSearchString = decodedSearchString + decodedDate + " ";
			} else if (TDTDateMethods.checkMonth(searchParts[i]) != 0) {
				// Date format: 14 aug 2014, 14 aug 14
				decodedDate = decodeSearchMonthFormat(searchParts, decodedDate,
						i);
				if (isSearchDateRange) {
					endSearchDate = decodedDate;
				} else {
					startSearchDate = decodedDate;
				}
				decodedSearchString = decodedSearchString + decodedDate + " ";
			} else if (TDTDateMethods.checkWeekMonthYear(searchParts[i]) != 0) {
				if (TDTDateMethods.checkWeekMonthYear(searchParts[i]) == 1) {
					// this week next week following week
					decodedSearchString = searchWeek(thisOrNextOrFollowing,
							decodedSearchString, currentDay, currentMonth,
							currentYear, currentDayOfWeek, nextCount,
							followingCount);
				} else if (TDTDateMethods.checkWeekMonthYear(searchParts[i]) == 2) {
					// this month next month following month
					decodedSearchString = searchMonth(thisOrNextOrFollowing,
							decodedSearchString, currentDay, currentMonth,
							currentYear, currentDayOfMonth, nextCount,
							followingCount);
				} else if (TDTDateMethods.checkWeekMonthYear(searchParts[i]) == 3) {
					// this year next year following year
					decodedSearchString = searchYear(thisOrNextOrFollowing,
							decodedSearchString, currentYear, nextCount,
							followingCount);
				}
			}
			// search by a date range eg. 22/12/14 to 29/12/14
			if (isSearchDateRange && !endSearchDate.equals("")) {
				if (TDTDateMethods.isValidDateRange(startSearchDate)
						&& TDTDateMethods.isValidDateRange(endSearchDate)) {
					if (TDTDateMethods.compareToDate(startSearchDate,
							endSearchDate) == 1) {
						decodedSearchString = searchDateRange(
								decodedSearchString, startSearchDate,
								endSearchDate);
					}
				}
				// resets the values so as to take in another range of dates
				isSearchDateRange = false;
				startSearchDate = "";
				endSearchDate = "";
			}
		}
		return decodedSearchString.trim();
	}

	// This method detects a valid month and checks the word before and after.
	// If it detects a valid date format that follow dd MMM YYYY or dd MMM YY,
	// it converts it to date format DD/MM/YYYY
	private static String decodeSearchMonthFormat(String[] searchParts,
			String decodedDate, int i) {
		boolean isValidDayYear = true;
		// Prevents null pointer exception
		if (i != 0 && i != searchParts.length - 1) {
			String before = searchParts[i - 1];
			String after = searchParts[i + 1];
			try {
				Integer.parseInt(before);
				Integer.parseInt(after);
			} catch (NumberFormatException e) {
				isValidDayYear = false;
			}
			if (isValidDayYear) {
				int day = Integer.parseInt(before);
				int month = TDTDateMethods.checkMonth(searchParts[i]);
				int year = 0;

				if (after.length() == 2) {
					after = "20" + after;
				}
				year = Integer.parseInt(after);

				decodedDate = day + "/" + month + "/" + year;
				if (TDTDateMethods.isValidDateRange(decodedDate)) {
					decodedDate = TDTDateMethods.changeDateFormat(decodedDate);
				}
			}
		}
		return decodedDate;
	}

	// This method creates a list of dates concat in a string to be searched in
	// between the range of dates.
	private static String searchDateRange(String decodedSearchString,
			String startSearchDate, String endSearchDate) {
		String dateTemp = "";
		dateTemp = startSearchDate;
		while (!dateTemp.equals(endSearchDate)) {
			String[] dateParts = dateTemp.split("/");
			int dayTemp = Integer.parseInt(dateParts[0]);
			int monthTemp = Integer.parseInt(dateParts[1]);
			int yearTemp = Integer.parseInt(dateParts[2]);
			dateTemp = TDTDateMethods.addDaysToCurrentDate(dayTemp, monthTemp,
					yearTemp, 1);
			decodedSearchString = decodedSearchString + dateTemp + " ";
		}
		return decodedSearchString;
	}

	// This method creates a list of dates concat in a string to be searched in
	// a specific week
	private static String searchWeek(int thisOrNextOrFollowing,
			String decodedSearchString, int currentDay, int currentMonth,
			int currentYear, int currentDayOfWeek, int nextCount,
			int followingCount) {
		int dayOfWeek = currentDayOfWeek;
		String startDayOfWeek = TDTDateMethods.addDaysToCurrentDate(currentDay,
				currentMonth, currentYear,
				Integer.parseInt("-" + (dayOfWeek - 1)));
		String[] dateParts;
		String dateTemp;
		dateParts = startDayOfWeek.split("/");
		int dayTemp = Integer.parseInt(dateParts[0]);
		int monthTemp = Integer.parseInt(dateParts[1]);
		int yearTemp = Integer.parseInt(dateParts[2]);

		// Determines the week to be searched
		if (thisOrNextOrFollowing == 0) {
			return decodedSearchString;
		} else if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
			startDayOfWeek = TDTDateMethods.addDaysToCurrentDate(dayTemp,
					monthTemp, yearTemp, (7 * nextCount)
							+ (14 * followingCount));
		}
		decodedSearchString = decodedSearchString + startDayOfWeek + " ";
		dateTemp = startDayOfWeek;
		// Creates the list of dates after the week is determined
		// The list of dates is created starting from the first day of the week,
		// sunday
		for (int z = 0; z < 6; z++) {
			dateParts = dateTemp.split("/");
			dayTemp = Integer.parseInt(dateParts[0]);
			monthTemp = Integer.parseInt(dateParts[1]);
			yearTemp = Integer.parseInt(dateParts[2]);

			dateTemp = TDTDateMethods.addDaysToCurrentDate(dayTemp, monthTemp,
					yearTemp, 1);
			decodedSearchString = decodedSearchString + dateTemp + " ";
		}
		return decodedSearchString;
	}

	// This method creates a list of dates concat in a string to be searched in
	// a specific month
	private static String searchMonth(int thisOrNextOrFollowing,
			String decodedSearchString, int currentDay, int currentMonth,
			int currentYear, int currentDayOfMonth, int nextCount,
			int followingCount) {
		int dayOfMonth = currentDayOfMonth - 1;
		String startDayOfMonth = TDTDateMethods.addDaysToCurrentDate(
				currentDay, currentMonth, currentYear,
				Integer.parseInt("-" + dayOfMonth));
		String[] dateParts;
		String dateTemp = "";
		dateParts = startDayOfMonth.split("/");
		int dayTemp = Integer.parseInt(dateParts[0]);
		int monthTemp = Integer.parseInt(dateParts[1]);
		int yearTemp = Integer.parseInt(dateParts[2]);

		// Determines the month to be searched
		if (thisOrNextOrFollowing == 0) {
			return decodedSearchString;
		} else if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
			int numOfMthToAdd = nextCount + followingCount * 2;
			for (int i = 0; i < numOfMthToAdd; i++) {
				if (monthTemp == 12) {
					monthTemp = 1;
					yearTemp = yearTemp + 1;
				} else {
					monthTemp++;
				}
			}
			startDayOfMonth = dayTemp + "/" + monthTemp + "/" + yearTemp;
		}

		decodedSearchString = decodedSearchString + startDayOfMonth + " ";
		dateTemp = startDayOfMonth;
		dateParts = dateTemp.split("/");

		// Creates the list of dates after the month is determined
		// The list of dates is created starting from the first day of the month
		if (dateParts.length == 3) { // ensure dateTemp not = ""
			int numDayOfMonth = TDTDateMethods.getNumOfDaysFromMonth(
					Integer.parseInt(dateParts[1]),
					Integer.parseInt(dateParts[2]));
			for (int z = 0; z < numDayOfMonth - 1; z++) {
				dateParts = dateTemp.split("/");
				dayTemp = Integer.parseInt(dateParts[0]);
				monthTemp = Integer.parseInt(dateParts[1]);
				yearTemp = Integer.parseInt(dateParts[2]);

				dateTemp = TDTDateMethods.addDaysToCurrentDate(dayTemp,
						monthTemp, yearTemp, 1);
				decodedSearchString = decodedSearchString + dateTemp + " ";
			}
		}
		return decodedSearchString;
	}

	// This method creates a list of dates concat in a string to be searched in
	// a specific year
	private static String searchYear(int thisOrNextOrFollowing,
			String decodedSearchString, int currentYear, int nextCount,
			int followingCount) {
		int yearTemp = currentYear;
		// Determines the year to be searched
		if (thisOrNextOrFollowing == 0) {
			return decodedSearchString;
		} else if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
			yearTemp = yearTemp + (nextCount + followingCount * 2);
		}
		// Creates the list of dates after the year is determined
		// The list of dates is created starting from the first day of the year
		for (int a = 1; a <= 12; a++) {
			int numDayOfMonth = TDTDateMethods.getNumOfDaysFromMonth(a,
					yearTemp);
			for (int b = 1; b <= numDayOfMonth; b++) {
				String date = b + "/" + a + "/" + yearTemp;
				decodedSearchString = decodedSearchString + date + " ";
			}
		}
		return decodedSearchString;
	}

	/**
	 * This method parses the set-reminder details to get the reminder date and
	 * time with the correct formats used in storage.
	 * 
	 * @param reminderString
	 * @return String This returns a string of date and time concat together.
	 */
	public static String decodeReminderDetails(String reminderString) {
		String[] reminderParts = reminderString.toLowerCase().split(" ");
		int thisOrNextOrFollowing = 0; // this = 1 next = 2 following = 3
		String decodedReminderDate = "null";
		String decodedReminderTime = "null";
		int nextCount = 0;
		int followingCount = 0;
		// Get current date and time
		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int currentMinute = cal.get(Calendar.MINUTE);

		String currentTime = currentHour + ":" + currentMinute;
		String currentDate = currentDay + "/" + currentMonth + "/"
				+ currentYear;

		for (int i = 0; i < reminderParts.length; i++) {
			reminderParts[i] = TDTCommons
					.replaceEndStringPunctuation(reminderParts[i]);

			if (reminderParts[i].equals("this")) {
				thisOrNextOrFollowing = 1;
			} else if (reminderParts[i].equals("next")) {
				nextCount++;
				thisOrNextOrFollowing = 2;
			} else if (reminderParts[i].equals("following")) {
				followingCount++;
				thisOrNextOrFollowing = 3;
			}

			if (TDTDateMethods.checkDate(reminderParts[i])) {
				decodedReminderDate = decodeDate(reminderParts, i, currentYear,
						currentMonth);
			} else if (TDTDateMethods.checkDay(reminderParts[i]) != 0) {
				int numOfDaysToAdd = determineDaysToBeAdded(
						thisOrNextOrFollowing, reminderParts, i,
						currentDayOfWeek, nextCount, followingCount);
				decodedReminderDate = TDTDateMethods.addDaysToCurrentDate(
						currentDay, currentMonth, currentYear, numOfDaysToAdd);
			} else if (TDTDateMethods.checkMonth(reminderParts[i]) != 0) {
				decodedReminderDate = decodeRemindMonthFormat(reminderParts,
						decodedReminderDate, i);
			} else if (TDTTimeMethods.checkTime(reminderParts[i])) {
				decodedReminderTime = decodeTime(reminderParts, i);
			}
		}

		if (decodedReminderTime.equals("null")) {
			return "null";
		} else if (decodedReminderDate.equals("null")) {
			// date is taken as the current date
			if (TDTTimeMethods.compareToTime(currentTime, decodedReminderTime) == 1) {
				//ReminderTime has to be after currentTime if date is current date
				return currentDate + " " + decodedReminderTime;
			}
		} else {
			if (TDTDateMethods.isValidDateRange(decodedReminderDate)) {
				if (TDTDateMethods.compareToDate(currentDate,
						decodedReminderDate) == 0) {
					if (TDTTimeMethods.compareToTime(currentTime,
							decodedReminderTime) == 1) {
						return decodedReminderDate + " " + decodedReminderTime;
					}
				} else if (TDTDateMethods.compareToDate(currentDate,
						decodedReminderDate) == 1) {
					return decodedReminderDate + " " + decodedReminderTime;
				}
			}
		}
		return "null";
	}

	// This method detects a valid month and checks the word before and after.
	// If it detects a valid date format that follow dd MMM YYYY or dd MMM YY,
	// it converts it to date format DD/MM/YYYY
	private static String decodeRemindMonthFormat(String[] reminderParts,
			String decodedReminderDate, int i) {
		boolean isValidDayYear = true;
		if (i != 0 && i != reminderParts.length - 1) {
			String before = reminderParts[i - 1];
			int month = TDTDateMethods.checkMonth(reminderParts[i]);
			String after = reminderParts[i + 1];
			try {
				Integer.parseInt(before);
				Integer.parseInt(after);
			} catch (NumberFormatException e) {
				isValidDayYear = false;
			}
			if (isValidDayYear) {
				int day = Integer.parseInt(before);
				int year = 0;

				if (after.length() == 2) {
					after = "20" + after;
				}
				year = Integer.parseInt(after);

				decodedReminderDate = day + "/" + month + "/" + year;
			} else {
				decodedReminderDate = before + "/" + month + "/" + after;
			}
		}
		return decodedReminderDate;
	}
}
