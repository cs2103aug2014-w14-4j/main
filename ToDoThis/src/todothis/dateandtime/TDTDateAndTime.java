package todothis.dateandtime;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimeZone;

public class TDTDateAndTime implements Comparable<TDTDateAndTime> {
	// store converted date format dd/mm/yyyy
	private String startDate = "null";
	private String endDate = "null";
	// store converted time format XX:XX 24hrs format
	private String startTime = "null";
	private String endTime = "null";

	private String details = "null";

	private boolean isTimedTask = false;
	private boolean isDeadlineTask = false;

	private static Calendar cal;

	// constructor
	public TDTDateAndTime(String dateAndTime_details) {
		details = dateAndTime_details;
		decodeDetails(dateAndTime_details);
		if (!getStartDate().equals("null") || !getStartTime().equals("null")) {
			isTimedTask = true;
		} else if (!getEndDate().equals("null") || !getEndTime().equals("null")) {
			isDeadlineTask = true;
		}

	}

	public TDTDateAndTime(String startDate, String endDate, String startTime,
			String endTime) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		if (!getStartDate().equals("null") || !getStartTime().equals("null")) {
			isTimedTask = true;
		} else if (!getEndDate().equals("null") || !getEndTime().equals("null")) {
			isDeadlineTask = true;
		}
	}

	public TDTDateAndTime() {
		this.startDate = "null";
		this.endDate = "null";
		this.startTime = "null";
		this.endTime = "null";
	}

	public static void main(String args[]) throws ParseException {

		// TDTDateAndTime test1 = new TDTDateAndTime("11/11/2014");
		// TDTDateAndTime test2 = new TDTDateAndTime("12/12");
		// System.out.println(test2.display());
		// System.out.println(TDTDateAndTime.decodeSearchDetails(""));
		// System.out.println(TDTDateAndTime.changeTimeFormat("2:59"));
		// System.out.println(calculateRemainingTime(TDTDateAndTime.decodeReminderDetails("1/1 3.38am")));

	}

	private void decodeDetails(String details) {
		System.out.println(details);

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
		// int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		// int CurrentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		// int numOfDaysCurrentMonth = getNumOfDaysFromMonth(currentMonth,
		// currentYear);

		for (int a = 0; a < parts.length; a++) {
			parts[a] = replaceEndStringPunctuation(parts[a]);

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
				decodedDate = TDTDateMethods.decodeDate(parts, a, currentYear,
						currentMonth);

				storeDecodedDate(endTimeDate, deadlineEndTimeDate, decodedDate);
			} else if (TDTTimeMethods.checkTime(parts[a])) {
				decodedTime = TDTTimeMethods.decodeTime(parts, a);

				storeDecodedTime(endTimeDate, deadlineEndTimeDate, decodedTime);
			} else if (TDTDateMethods.checkDay(parts[a]) != 0) {
				int numOfDaysToAdd = TDTDateMethods.determineDaysToBeAdded(
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

	/*
	 * private static String addDaysToCurrentDate(int currentDay, int
	 * currentMonth, int currentYear, int numOfDaysCurrentMonth, int
	 * numOfDaysToAdd) { int dayTemp = currentDay; int monthTemp = currentMonth;
	 * int yearTemp = currentYear;
	 * 
	 * if ((dayTemp + numOfDaysToAdd) > numOfDaysCurrentMonth) { monthTemp++; if
	 * (monthTemp > 12) { monthTemp = 1; // set to Jan yearTemp++; } dayTemp =
	 * (dayTemp + numOfDaysToAdd) - numOfDaysCurrentMonth; } else if ((dayTemp +
	 * numOfDaysToAdd) <= 0) { monthTemp--; if (monthTemp <= 0) { monthTemp =
	 * 12; // set to Dec yearTemp--; } dayTemp =
	 * getNumOfDaysFromMonth(monthTemp, yearTemp) + (dayTemp + numOfDaysToAdd);
	 * } else { dayTemp = dayTemp + numOfDaysToAdd; } return dayTemp + "/" +
	 * monthTemp + "/" + yearTemp; }
	 */

	public static boolean isPrepositionTo(String[] parts, int a) {
		return parts[a].equals("to") || parts[a].equals("till")
				|| parts[a].equals("until") || parts[a].equals("-");
	}

	public static String replaceEndStringPunctuation(String word) {
		int length = word.length();
		String replacedWord = word;
		for (int i = length - 1; i >= 0; i--) {
			if (word.charAt(i) == '.' || word.charAt(i) == '!'
					|| word.charAt(i) == ',') {
				replacedWord = word.substring(0, i);
			} else {
				return replacedWord;
			}
		}
		return replacedWord;
	}

	// -----------------------GETTER-------------------------------
	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getDetails() {
		return details;
	}

	public boolean isTimedTask() {
		return isTimedTask;
	}

	public boolean isDeadlineTask() {
		return isDeadlineTask;
	}

	// -------------------------------------------DISPLAY------------------------------------------------
	public String display() {
		String dateAndTimeContents = "";
		if (isDeadlineTask) {
			dateAndTimeContents = dateAndTimeContents + "Due: ";
			if (!getEndDate().equals("null")) {
				dateAndTimeContents = dateAndTimeContents
						+ TDTDateMethods.changeToDayOfWeek(getEndDate()) + "\t"
						+ TDTDateMethods.changeDateFormatDisplay(getEndDate())
						+ "\t";
			}
			if (!getEndTime().equals("null")) {
				dateAndTimeContents = dateAndTimeContents
						+ TDTTimeMethods.changeTimeFormatDisplay(getEndTime());
			}
			dateAndTimeContents = dateAndTimeContents + "<br>";
		} else if (isTimedTask) {
			dateAndTimeContents = dateAndTimeContents + "Start: ";
			if (!getStartDate().equals("null")) {
				dateAndTimeContents = dateAndTimeContents
						+ TDTDateMethods.changeToDayOfWeek(getStartDate())
						+ "\t"
						+ TDTDateMethods
								.changeDateFormatDisplay(getStartDate()) + "\t";
			}
			if (!getStartTime().equals("null")) {
				dateAndTimeContents = dateAndTimeContents
						+ TDTTimeMethods
								.changeTimeFormatDisplay(getStartTime());
			}
			dateAndTimeContents = dateAndTimeContents + "<br>";
			if (!getEndDate().equals("null") || !getEndTime().equals("null")) {
				dateAndTimeContents = dateAndTimeContents + "End: ";
				if (!getEndDate().equals("null")) {
					dateAndTimeContents = dateAndTimeContents
							+ TDTDateMethods.changeToDayOfWeek(getEndDate())
							+ "\t"
							+ TDTDateMethods
									.changeDateFormatDisplay(getEndDate())
							+ "\t";
				}
				if (!getEndTime().equals("null")) {
					dateAndTimeContents = dateAndTimeContents
							+ TDTTimeMethods
									.changeTimeFormatDisplay(getEndTime());
				}
				dateAndTimeContents = dateAndTimeContents + "<br>";
			}
		}
		return dateAndTimeContents;
	}

	/*
	 * previous one. // check time possible cases // 2am 11pm -- // 2:00 12:15
	 * 2.00 -- // 2:00pm 12:15pm 2.00pm 12.15pm -- // 2359 230 // 2359pm 230pm
	 * -- // shortest 2am || longest 12:15pm nextWord =
	 * nextWord.replaceAll("[.!,]", ""); nextWord = nextWord.toLowerCase(); if
	 * (isValidTimeLengthRange(nextWord)) { if (isAMorPM(nextWord)) { // eg
	 * 2:00pm 12:15pm 2.00pm 12.15pm if(nextWord.length()>4) { if
	 * (isValidTimeTypeAMPM(nextWord)) { return true; } } // eg 2359pm 230pm 2am
	 * 11pm - only digits. 2:345pm , 12344pm are invalid. if
	 * (isDigits(nextWord.substring(0, nextWord.length()-2))) { return true; }
	 * // eg 2:00 12:15 2.00 } else if (isValidTimeType(nextWord)) { return
	 * true; // eg 2359 230 } else if (((nextWord.length() == 3) ||
	 * (nextWord.length() == 4)) && (isDigits(nextWord))) { return true; } }
	 * return false;
	 * 
	 * 
	 * }
	 * 
	 * public static boolean isValidTimeLengthRange(String nextWord) {
	 * if(nextWord.length() > 2 && nextWord.length() <= 7) { return true; }
	 * return false; }
	 * 
	 * public static boolean isAMorPM(String nextWord) { if
	 * ((nextWord.substring(nextWord.length()-2,
	 * nextWord.length()).equals("am")) ||
	 * (nextWord.substring(nextWord.length()-2,
	 * nextWord.length()).equals("pm"))) { return true; } return false; }
	 * 
	 * public static boolean isValidTimeTypeAMPM(String nextWord) { if
	 * ((nextWord.charAt(nextWord.length()-5) == ':') ||
	 * (nextWord.charAt(nextWord.length()-5) == '.')) { String temp =
	 * nextWord.replace(nextWord.charAt(nextWord.length()-5) + "", ""); temp =
	 * temp.substring(0, temp.length()-2); if (isDigits(temp)) { return true; }
	 * } return false; }
	 * 
	 * public static boolean isValidTimeType(String nextWord) { if
	 * (((nextWord.charAt(nextWord.length()-3)) == ':')
	 * ||((nextWord.charAt(nextWord.length()-3)) == '.')) { return true; }
	 * return false; }
	 * 
	 * public static boolean isDigits(String temp) { if(temp.matches("\\d+")) {
	 * return true; } return false; }
	 */

	// -----------------------------------CHECK FOR OVERDUE TASK---------------
	public boolean isOverdue() {
		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);

		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int currentMinute = cal.get(Calendar.MINUTE);

		String currentDate = currentDay + "/" + currentMonth + "/"
				+ currentYear;
		String currentTime = currentHour + ":" + currentMinute;

		String checkDate = "null";
		String checkTime = "null";
		if (isDeadlineTask) {
			checkDate = getEndDate();
			checkTime = getEndTime();
		} else if (isTimedTask) {
			if (getEndDate().equals("null") && !getEndTime().equals("null")) {
				checkDate = getStartDate();
				checkTime = getEndTime();
			} else if (!getEndDate().equals("null")) {
				checkDate = getEndDate();
				checkTime = getEndTime();
			} else {
				checkDate = getStartDate();
				checkTime = getStartTime();
			}
		}

		if (!checkDate.equals("null")) {
			if (TDTDateMethods.compareToDate(currentDate, checkDate) == -1) {
				return true;
			} else if (TDTDateMethods.compareToDate(currentDate, checkDate) == 0) {
				if (!checkTime.equals("null")) {
					if (TDTTimeMethods.compareToTime(currentTime, checkTime) == -1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// -----------------------------CHECK FOR CLASHES---------------------
	public boolean isClash(TDTDateAndTime arg0) {
		if (arg0.isTimedTask() == true) {
			if (!this.getStartDate().equals("null")
					&& !this.getStartTime().equals("null")) {
				if (this.getEndDate().equals("null")
						&& this.getEndTime().equals("null")) {
					if (arg0.getStartDate().equals(this.getStartDate())
							&& arg0.getStartTime().equals(this.getStartTime())) {
						return true;
					} else {
						if (!arg0.getEndDate().equals("null")) {
							if (TDTDateMethods.compareToDate(
									this.getStartDate(), arg0.getStartDate()) == -1
									&& TDTDateMethods.compareToDate(
											this.getStartDate(),
											arg0.getEndDate()) == 1) {
								return true;
							} else if (TDTDateMethods.compareToDate(
									this.getStartDate(), arg0.getStartDate()) == 0
									&& TDTDateMethods.compareToDate(
											this.getStartDate(),
											arg0.getEndDate()) == 0) {
								if (TDTTimeMethods.compareToTime(
										this.getStartTime(),
										arg0.getStartTime()) == -1
										&& TDTTimeMethods.compareToTime(
												this.getStartTime(),
												arg0.getEndTime()) == 1) {
									return true;
								}
							} else {
								if (TDTDateMethods.compareToDate(
										this.getStartDate(),
										arg0.getStartDate()) == 0) {
									if (TDTTimeMethods.compareToTime(
											this.getStartTime(),
											arg0.getStartTime()) == -1) {
										return true;
									}
								}
								if (TDTDateMethods.compareToDate(
										this.getStartDate(), arg0.getEndDate()) == 0) {
									if (TDTTimeMethods.compareToTime(
											this.getStartTime(),
											arg0.getEndTime()) == 1) {
										return true;
									}
								}
							}
						}
					}
				}
				if ((!this.getEndDate().equals("null") && !this.getEndTime()
						.equals("null")) && !arg0.getStartDate().equals("null")) {
					// condition where this->has SD ED ST ET and arg0->has SD
					// may have ED may have ST ET
					if (TDTDateMethods.compareToDate(this.getStartDate(),
							arg0.getStartDate()) == 1
							&& TDTDateMethods.compareToDate(this.getEndDate(),
									arg0.getStartDate()) == -1) {
						// SDate = 8/10/2014 EDate = 11/10/2014 startArgdate =
						// 10/10/2014
						return true;
					}
					if (TDTDateMethods.compareToDate(this.getStartDate(),
							arg0.getStartDate()) == 0
							&& TDTDateMethods.compareToDate(this.getEndDate(),
									arg0.getStartDate()) == -1
							&& !arg0.getStartTime().equals("null")) {
						// SDate = 8/10/2014 EDate = 11/10/2014 Argdate =
						// 8/10/2014 NEED CHECK TIMING
						if (TDTTimeMethods.compareToTime(this.getStartTime(),
								arg0.getStartTime()) == 1
								|| TDTTimeMethods.compareToTime(
										this.getStartTime(),
										arg0.getStartTime()) == 0) {
							// Timing SDate 8/10/2014 0800 argdate 8/10/2014
							// 0900/0800
							return true;
						}
					}
					if (TDTDateMethods.compareToDate(this.getStartDate(),
							arg0.getStartDate()) == 1
							&& TDTDateMethods.compareToDate(this.getEndDate(),
									arg0.getStartDate()) == 0
							&& !arg0.getStartTime().equals("null")) {
						// SDate = 8/10/2014 EDate = 11/10/2014 Argdate =
						// 11/10/2014 NEED CHECK TIMING
						if (TDTTimeMethods.compareToTime(this.getEndTime(),
								arg0.getStartTime()) == -1) {
							// Timing EDate 11/10/2014 0800 argdate 11/10/2014
							// 0700
							return true;
						}
					}
					if (TDTDateMethods.compareToDate(this.getStartDate(),
							arg0.getStartDate()) == 0
							&& TDTDateMethods.compareToDate(this.getEndDate(),
									arg0.getStartDate()) == 0
							&& !arg0.getStartTime().equals("null")) {
						// SDate = 8/10/2014 EDate = 8/10/2014 Argdate =
						// 8/10/2014 NEED CHECK TIMING
						if ((TDTTimeMethods.compareToTime(this.getStartTime(),
								arg0.getStartTime()) == 1 || TDTTimeMethods
								.compareToTime(this.getStartTime(),
										arg0.getStartTime()) == 0)
								&& TDTTimeMethods.compareToTime(
										this.getEndTime(), arg0.getStartTime()) == -1) {
							// Timing date same sTime = 0900 eTime = 1200
							// argtime = 1000/0900
							return true;
						}
						if (!arg0.getEndTime().equals("null")) {
							if (TDTTimeMethods.compareToTime(
									this.getStartTime(), arg0.getStartTime()) == -1
									&& TDTTimeMethods.compareToTime(
											this.getEndTime(),
											arg0.getEndTime()) == 1) {
								return true;
							}
						}
					}
					if (!arg0.getEndDate().equals("null")) {
						if (TDTDateMethods.compareToDate(this.getStartDate(),
								arg0.getEndDate()) == 1
								&& TDTDateMethods.compareToDate(
										this.getEndDate(), arg0.getEndDate()) == -1) {
							// SDate = 8/10/2014 EDate = 11/10/2014 endArgdate =
							// 10/10/2014
							return true;
						}
						if (TDTDateMethods.compareToDate(this.getStartDate(),
								arg0.getEndDate()) == 0
								&& TDTDateMethods.compareToDate(
										this.getEndDate(), arg0.getEndDate()) == -1
								&& !arg0.getEndTime().equals("null")) {
							// SDate = 8/10/2014 EDate = 11/10/2014 endArgdate =
							// 8/10/2014
							if (TDTTimeMethods.compareToTime(
									this.getStartTime(), arg0.getEndTime()) == 1) {
								// Timing SDate 8/10/2014 0800 argdate 8/10/2014
								// 0900
								return true;
							}
						}
						if (TDTDateMethods.compareToDate(this.getStartDate(),
								arg0.getEndDate()) == 1
								&& TDTDateMethods.compareToDate(
										this.getEndDate(), arg0.getEndDate()) == 0
								&& !arg0.getEndTime().equals("null")) {
							// SDate = 8/10/2014 EDate = 11/10/2014 endArgdate =
							// 11/10/2014
							if (TDTTimeMethods.compareToTime(this.getEndTime(),
									arg0.getEndTime()) == -1
									|| TDTTimeMethods.compareToTime(
											this.getEndTime(),
											arg0.getEndTime()) == 0) {
								// Timing EDate 11/10/2014 0800 endargdate
								// 11/10/2014 0700/0800
								return true;
							}
						}
						if (TDTDateMethods.compareToDate(this.getStartDate(),
								arg0.getEndDate()) == 0
								&& TDTDateMethods.compareToDate(
										this.getEndDate(), arg0.getEndDate()) == 0
								&& !arg0.getEndTime().equals("null")) {
							// SDate = 8/10/2014 EDate = 8/10/2014 endArgdate =
							// 8/10/2014
							if (TDTTimeMethods.compareToTime(
									this.getStartTime(), arg0.getEndTime()) == 1
									&& (TDTTimeMethods.compareToTime(
											this.getEndTime(),
											arg0.getEndTime()) == -1 || TDTTimeMethods
											.compareToTime(this.getEndTime(),
													arg0.getEndTime()) == 0)) {
								return true;
							}
							if (!arg0.getStartTime().equals("null")) {
								if (TDTTimeMethods.compareToTime(
										this.getStartTime(),
										arg0.getStartTime()) == -1
										&& TDTTimeMethods.compareToTime(
												this.getEndTime(),
												arg0.getEndTime()) == 1) {
									return true;
								}
							}
						}
						if (TDTDateMethods.compareToDate(this.getStartDate(),
								arg0.getStartDate()) == -1
								&& TDTDateMethods.compareToDate(
										this.getEndDate(), arg0.getEndDate()) == 1) {
							return true;
						}
						// when startdate = arg0startdate & enddate<arg0enddate
						if (TDTDateMethods.compareToDate(this.getStartDate(),
								arg0.getStartDate()) == 0
								&& TDTDateMethods.compareToDate(
										this.getEndDate(), arg0.getEndDate()) == 1) {
							if (!arg0.getStartTime().equals("null")) {
								if (TDTTimeMethods.compareToTime(
										this.getStartTime(),
										arg0.getStartTime()) == -1) {
									return true;
								}
							}
						}

						// when startdate > arg0startdate & enddate =
						// arg0enddate
						if (TDTDateMethods.compareToDate(this.getStartDate(),
								arg0.getStartDate()) == -1
								&& TDTDateMethods.compareToDate(
										this.getEndDate(), arg0.getEndDate()) == 0) {
							if (!arg0.getEndTime().equals("null")) {
								if (TDTTimeMethods.compareToTime(
										this.getEndTime(), arg0.getEndTime()) == 1) {
									return true;
								}
							}
						}

						// when startdate = arg0startdate & enddate =
						// arg0enddate
						// arg0 start ----- end arg0
						if (TDTDateMethods.compareToDate(this.getStartDate(),
								arg0.getStartDate()) == 0
								&& TDTDateMethods.compareToDate(
										this.getEndDate(), arg0.getEndDate()) == 0) {
							if (!arg0.getStartTime().equals("null")
									&& !arg0.getEndTime().equals("null")) {
								if (TDTTimeMethods.compareToTime(
										this.getStartTime(),
										arg0.getStartTime()) == -1
										&& TDTTimeMethods.compareToTime(
												this.getEndTime(),
												arg0.getEndTime()) == 1) {
									return true;
								}
							}
						}

					}
				}
			}
		}
		return false;
	}

	// ------------------------------------COMPARABLE----------------------------------
	@Override
	public int compareTo(TDTDateAndTime arg0) {
		String thisDate = "null";
		String thisTime = "null";
		String comparedDate = "null";
		String comparedTime = "null";

		if (this.getStartDate().equals("null")) {
			thisDate = this.getEndDate();
		} else {
			thisDate = this.getStartDate();
		}

		if (this.getStartTime().equals("null")) {
			thisTime = this.getEndTime();
		} else {
			thisTime = this.getStartTime();
		}

		if (arg0.getStartDate().equals("null")) {
			comparedDate = arg0.getEndDate();
		} else {
			comparedDate = arg0.getStartDate();
		}

		if (arg0.getStartTime().equals("null")) {
			comparedTime = arg0.getEndTime();
		} else {
			comparedTime = arg0.getStartTime();
		}

		if (TDTDateMethods.compareToDate(thisDate, comparedDate) == 1) { // thisdate<compareddate
			return -1;
		} else if (TDTDateMethods.compareToDate(thisDate, comparedDate) == 0) {
			if (thisTime.equals("null") && !comparedTime.equals("null")) {
				return 1;
			} else if (thisTime.equals("null") && comparedTime.equals("null")) {
				return 0;
			} else if (!thisTime.equals("null") && comparedTime.equals("null")) {
				return -1;
			}

			if (TDTTimeMethods.compareToTime(thisTime, comparedTime) == 1) { // thistime<comparedtime
				return -1;
			} else if (TDTTimeMethods.compareToTime(thisTime, comparedTime) == 0) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}

}
