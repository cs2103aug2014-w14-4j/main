package todothis.logic;

import java.util.Calendar;
import java.util.TimeZone;

import todothis.commons.TDTCommons;
import todothis.commons.TDTDateAndTime;
import todothis.commons.TDTDateMethods;
import todothis.commons.TDTTimeMethods;

public class TDTDateAndTimeParser {
	// store converted date format dd/mm/yyyy
	private String startDate = "null";
	private String endDate = "null";
	// store converted time format XX:XX 24hrs format
	private String startTime = "null";
	private String endTime = "null";

	private static Calendar cal;

	public TDTDateAndTimeParser() {
		this.startDate = "null";
		this.endDate = "null";
		this.startTime = "null";
		this.endTime = "null";
	}

	public TDTDateAndTime decodeDateAndTimeDetails(String details) {
		boolean endTimeDate = false;
		boolean deadlineEndTimeDate = false;
		int thisOrNextOrFollowing = 0; // this = 1 next = 2 following = 3
		String decodedDate = "";
		String decodedTime = "";
		int nextCount = 0;
		int followingCount = 0;

		String[] parts = details.toLowerCase().split(" ");

		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		for (int a = 0; a < parts.length; a++) {
			parts[a] = TDTCommons.replaceEndStringPunctuation(parts[a]);

			if (isPrepositionTo(parts, a)) {
				endTimeDate = true;
				nextCount = 0;
				followingCount = 0;
			}
			if (parts[a].equals("by") || parts[a].equals("due")
					|| parts[a].equals("before")) {
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
		// case when today from 1pm to 10pm
		if (!startDate.equals("null") && endDate.equals("null")
				&& !startTime.equals("null") && !endTime.equals("null")) {
			endDate = startDate;
		}

		return new TDTDateAndTime(startDate, endDate, startTime, endTime);
	}

	private static String decodeDate(String[] parts, int a, int currentYear,
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

	private static int determineDaysToBeAdded(int thisOrNextOrFollowing,
			String[] parts, int a, int currentDayOfWeek, int nextCount,
			int followingCount) {
		int numOfDaysToAdd = 0;
		if (TDTDateMethods.checkDay(parts[a]) <= 7
				&& TDTDateMethods.checkDay(parts[a]) > 0) {
			if (thisOrNextOrFollowing == 0) { // None of the above
				if (TDTDateMethods.checkDay(parts[a]) <= currentDayOfWeek) {
					numOfDaysToAdd = 7 - (currentDayOfWeek - TDTDateMethods
							.checkDay(parts[a]));
				} else {
					numOfDaysToAdd = TDTDateMethods.checkDay(parts[a])
							- currentDayOfWeek;
				}
			} else {// this
				if (TDTDateMethods.checkDay(parts[a]) == 1) {// sunday
					if (currentDayOfWeek != 0) {
						numOfDaysToAdd = 8 - currentDayOfWeek;
					}
				} else {
					numOfDaysToAdd = TDTDateMethods.checkDay(parts[a])
							- currentDayOfWeek;
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

	private String adjustmentToDate(boolean endTimeDate, String decodedDate,
			String[] parts, int a) {
		String[] toBeAddedDateParts = decodedDate.split("/");
		if (endTimeDate == true) {
			if (!startDate.equals("null")
					&& TDTDateMethods.checkDay(parts[a]) != 8) { // enddate
				// !=
				// today
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

	private void storeDecodedDate(boolean endTimeDate,
			boolean deadlineEndTimeDate, String decodedDate) {
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

	private static boolean isPrepositionTo(String[] parts, int a) {
		return parts[a].equals("to") || parts[a].equals("till")
				|| parts[a].equals("until") || parts[a].equals("-");
	}

	// -------------------------Decode Search Details------------
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
				boolean isValidDayYear = true;
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
							decodedDate = TDTDateMethods
									.changeDateFormat(decodedDate);
						}
						if (isSearchDateRange) {
							endSearchDate = decodedDate;
						} else {
							startSearchDate = decodedDate;
						}
						decodedSearchString = decodedSearchString + decodedDate
								+ " ";
					}
				}
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
				isSearchDateRange = false;
				startSearchDate = "";
				endSearchDate = "";
			}
		}
		return decodedSearchString.trim();
	}

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
		if (thisOrNextOrFollowing == 0) {
			return decodedSearchString;
		} else if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
			startDayOfWeek = TDTDateMethods.addDaysToCurrentDate(dayTemp,
					monthTemp, yearTemp, (7 * nextCount)
							+ (14 * followingCount));
		}
		decodedSearchString = decodedSearchString + startDayOfWeek + " ";
		dateTemp = startDayOfWeek;
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

	private static String searchYear(int thisOrNextOrFollowing,
			String decodedSearchString, int currentYear, int nextCount,
			int followingCount) {
		int yearTemp = currentYear;
		if (thisOrNextOrFollowing == 0) {
			return decodedSearchString;
		} else if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
			yearTemp = yearTemp + (nextCount + followingCount * 2);
		}

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

	// ---------------Decode Reminder Details--------------------

	public static String decodeReminderDetails(String reminderString) {
		String[] reminderParts = reminderString.toLowerCase().split(" ");
		int thisOrNextOrFollowing = 0; // this = 1 next = 2 following = 3
		String decodedReminderDate = "null";
		String decodedReminderTime = "null";
		int nextCount = 0;
		int followingCount = 0;

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
						decodedReminderDate = before + "/" + month + "/"
								+ after;
					}
				}
			} else if (TDTTimeMethods.checkTime(reminderParts[i])) {
				decodedReminderTime = decodeTime(reminderParts, i);
			}
		}

		if (decodedReminderTime.equals("null")) {
			return "null";
		} else if (decodedReminderDate.equals("null")) { // today
			if (TDTTimeMethods.compareToTime(currentTime, decodedReminderTime) == 1) {
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
}
