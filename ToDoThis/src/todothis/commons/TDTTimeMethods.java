package todothis.commons;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TDTTimeMethods {
	
	private static Pattern[] pattern;
	private static Matcher matcher;
	private static Calendar cal;

	// 2am 11pm --
	private static final String TIME_PATTERN_1 = "(0?[1-9]|1[012])([aA][Mm]|[pP][mM])";
	// 2:00 12:15 2.00 23:30 ----------
	private static final String TIME_PATTERN_2 = "(0?[0-9]|1[0-9]|2[0-3])(0?[:.])(0?[0-5][0-9])";
	// 2:00pm 12:15pm 2.00pm 12.15pm --
	private static final String TIME_PATTERN_3 = "(0?[0-9]|1[012])(0?[:.])(0?[0-5][0-9])([aA][Mm]|[pP][mM])";
	// 13:00pm 12:01pm
	private static final String TIME_PATTERN_4 = "(0?1[2-9]|2[0-3])(0?[:.])(0?[0-5][0-9])([pP][mM])";
	// 1200am to 1259pm
	private static final String TIME_PATTERN_5 = "(0?[1-9]|1[012])(0?[0-5][0-9])([aA][Mm]|[pP][mM]|[hH][rR]|[hH][rR][sS]|[hH])";
	// 1300pm onwards
	private static final String TIME_PATTERN_6 = "(0?1[3-9]|2[0-3])(0?[0-5][0-9])([pP][mM]|[hH][rR]|[hH][rR][sS]|[hH])";
	// 000am 0000am
	private static final String TIME_PATTERN_7 = "(0?0)(0?[0-5][0-9])([aA][mM]|[hH][rR]|[hH][rR][sS]|[hH])";

	// ----------------------CHECK TIME-------------------------
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
		for (int i = 1; i <= 7; i++) {
			matcher = pattern[i].matcher(time);
			if (matcher.matches()) {
				return true;
			}
		}
		return false;
	}
	
	//-------------------Change Display Format-----------------
	public static String changeTimeFormatDisplay(String time) {
		final String OLD_FORMAT = "HH:mm";
		final String NEW_FORMAT = "h:mm a";

		String oldTimeString = time;
		String newTimeString;

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
	
	// -----------------Time Related Methods-----------------
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

		if (hours < 24 && hours >= 0 
				&& minutes < 60 && minutes >= 0) {
			return true;
		}
		return false;
	}

	// use for "from Date1 to Date2" date1 and date2 same.
	public static boolean isValidTimeCompare(String startTime, String endTime) {
		String[] startTimeParts = startTime.split(":");
		String[] endTimeParts = endTime.split(":");

		int startHours = Integer.parseInt(startTimeParts[0]);
		int startMinutes = Integer.parseInt(startTimeParts[1]);
		int endHours = Integer.parseInt(endTimeParts[0]);
		int endMinutes = Integer.parseInt(endTimeParts[1]);

		if (endHours > startHours) {
			return true;
		} else if (endHours == startHours) {
			if (endMinutes > startMinutes) {
				return true;
			}
		}
		return false;
	}
	
	public static int compareToTime(String time1, String time2) {
		String[] time1Parts = time1.split(":");
		String[] time2Parts = time2.split(":");

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
	
	public static long calculateRemainingTime(String decodedString) {
		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);

		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int currentMinute = cal.get(Calendar.MINUTE);
		int currentSeconds = cal.get(Calendar.SECOND);

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

			// in milliseconds
			long diff = d2.getTime() - d1.getTime();

			remainingTimeInSeconds = diff / 1000;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return remainingTimeInSeconds;
	}
}
