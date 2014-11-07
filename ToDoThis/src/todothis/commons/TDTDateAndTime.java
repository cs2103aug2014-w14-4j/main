package todothis.commons;

import java.util.Calendar;
import java.util.TimeZone;

public class TDTDateAndTime implements Comparable<TDTDateAndTime> {
	// store converted date format dd/mm/yyyy
	private String startDate = "null";
	private String endDate = "null";
	// store converted time format XX:XX 24hrs format
	private String startTime = "null";
	private String endTime = "null";

	private boolean isTimedTask = false;
	private boolean isDeadlineTask = false;

	private static Calendar cal;

	// constructor
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

	public static void main(String args[]) {

		// TDTDateAndTime test1 = new TDTDateAndTime("11/11/2014");
		// TDTDateAndTime test2 = new TDTDateAndTime("12/12");
		// System.out.println(test2.display());
		// System.out.println(TDTDateAndTime.decodeSearchDetails(""));
		// System.out.println(TDTDateAndTime.changeTimeFormat("2:59"));
		// System.out.println(calculateRemainingTime(TDTDateAndTime.decodeReminderDetails("1/1 3.38am")));

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

	public boolean isTimedTask() {
		return isTimedTask;
	}

	public boolean isDeadlineTask() {
		return isDeadlineTask;
	}

	// --------------------------DISPLAY----------------------
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

	// -----------------------------------CHECK IF OVERDUE---------------
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
