//@author A0115933H
package todothis.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This TDTTimeMethods class stores all the static time related methods which
 * are called by other components of the software.
 */
public class TDTTimeMethods {
	private static Pattern[] pattern;
	private static Matcher matcher;
	private static Calendar cal;

	
	private static final String TIME_PATTERN_1 = "(0?[1-9]|1[012])([aA][Mm]|[pP][mM])";
	
	private static final String TIME_PATTERN_2 = "(0?[0-9]|1[0-9]|2[0-3])(0?[:.])(0?[0-5][0-9])";
	
	private static final String TIME_PATTERN_3 = "(0?[0-9]|1[012])(0?[:.])(0?[0-5][0-9])([aA][Mm]|[pP][mM])";
	
	private static final String TIME_PATTERN_4 = "(0?1[2-9]|2[0-3])(0?[:.])(0?[0-5][0-9])([pP][mM])";

	private static final String TIME_PATTERN_5 = "(0?[1-9]|1[012])(0?[0-5][0-9])([aA][Mm]|[pP][mM]|[hH][rR]|[hH][rR][sS]|[hH])";
	
	private static final String TIME_PATTERN_6 = "(0?1[3-9]|2[0-3])(0?[0-5][0-9])([pP][mM]|[hH][rR]|[hH][rR][sS]|[hH])";
	
	private static final String TIME_PATTERN_7 = "(0?0)(0?[0-5][0-9])([aA][mM]|[hH][rR]|[hH][rR][sS]|[hH])";

	/**
	 * This method check if the string follows a certain type of time format.
	 * @param time
	 * @return boolean This returns true if it follows the time pattern.
	 */
	public static boolean checkTime(String time) {
		time = TDTCommons.replaceEndStringPunctuation(time);

		pattern = new Pattern[8];
		pattern[1] = Pattern.compile(TIME_PATTERN_1);
		pattern[2] = Pattern.compile(TIME_PATTERN_2);
		pattern[3] = Pattern.compile(TIME_PATTERN_3);
		pattern[4] = Pattern.compile(TIME_PATTERN_4);
		pattern[5] = Pattern.compile(TIME_PATTERN_5);
		pattern[6] = Pattern.compile(TIME_PATTERN_6);
		pattern[7] = Pattern.compile(TIME_PATTERN_7);
		// Checks against all the possible time patterns
		for (int i = 1; i <= 7; i++) {
			matcher = pattern[i].matcher(time);
			if (matcher.matches()) {
				return true;
			}
		}
		return false;
	}

	//@author A0110852R
	/**
	 * The method converts the old time format HH:mm to a new format h:mm a
	 * (2:30pm) for display.
	 * 
	 * @param time
	 * @return String This returns the time that follows the new time format.
	 */
	public static String changeTimeFormatDisplay(String time) {
		final String OLD_FORMAT = "HH:mm";
		final String NEW_FORMAT = "h:mm a";

		String oldTimeString = time;
		String newTimeString = "";

		SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
		Date d = new Date();
		try {
			d = sdf.parse(oldTimeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.applyPattern(NEW_FORMAT);
		newTimeString = sdf.format(d);
		return newTimeString;
	}

	/**
	 * This method checks if the time falls in the valid time range.
	 * @param time
	 * @return boolean This returns true if time is valid and false if
	 *         otherwise.
	 */
	public static boolean isValidTimeRange(String time) {
		if (time.equals("null")) {
			return true;
		}

		String[] timeParts = time.split(":");
		int hours;
		int minutes;

		try {
			hours = Integer.parseInt(timeParts[0]);
			minutes = Integer.parseInt(timeParts[1]);
		} catch (NumberFormatException e) {
			return false;
		}

		if (hours < 24 && hours >= 0 && minutes < 60 && minutes >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method compares the two timings and checks if they are the same or
	 * one being later or earlier than another.
	 * 
	 * @param time1
	 * @param time2
	 * @return int This returns a value -1 if time1>time2, value 0 if
	 *         time1=time2 and value 1 if time2>time1.
	 */
	public static int compareToTime(String time1, String time2) {
		String[] time1Parts = time1.split(":");
		String[] time2Parts = time2.split(":");

		assert (TDTTimeMethods.isValidTimeRange(time1));
		assert (TDTTimeMethods.isValidTimeRange(time2));

		int time1Hours = Integer.parseInt(time1Parts[0]);
		int time1Minutes = Integer.parseInt(time1Parts[1]);
		int time2Hours = Integer.parseInt(time2Parts[0]);
		int time2Minutes = Integer.parseInt(time2Parts[1]);

		if (time2Hours > time1Hours) {
			return 1;
		} else if (time2Hours == time1Hours) {
			if (time2Minutes > time1Minutes) {
				return 1;
			} else if (time2Minutes == time1Minutes) {
				return 0;
			} else {
				return -1;
			}
		}
		return -1;
	}

	/**
	 * This method calculates the remaining amount of time left from the current
	 * date and time to the targeted date and time.
	 * 
	 * @param decodedString
	 * @return long This returns the remaining time in seconds.
	 */
	public static long calculateRemainingTime(String decodedString) {
		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);

		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int currentMinute = cal.get(Calendar.MINUTE);
		int currentSeconds = cal.get(Calendar.SECOND);

		// Include seconds portion to decodedString so as to match the date
		// format
		String reminder = decodedString + ":00";

		String currentDateAndTime = currentDay + "/" + currentMonth + "/"
				+ currentYear + " " + currentHour + ":" + currentMinute + ":"
				+ currentSeconds;
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		long remainingTimeInSeconds = 0;

		try {
			d1 = format.parse(currentDateAndTime);
			d2 = format.parse(reminder);

			// In milliseconds
			long diff = d2.getTime() - d1.getTime();

			// Convert to seconds
			remainingTimeInSeconds = diff / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return remainingTimeInSeconds;
	}
}
