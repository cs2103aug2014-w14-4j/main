package todothis.commons;

import java.util.ArrayList;
import java.util.Collections;

public class TDTCommons {
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
	
	public static int renumberTaskID(ArrayList<Task> array, Task t) {
		int newNum = 0;
		for(int i = 0; i < array.size(); i++) {
			Task task = array.get(i);
			task.setTaskID(i + 1);
			if(task == t) {
				newNum = i + 1;
			}
		}
		return newNum;
	}
	
	public static int sort(ArrayList<Task> array, Task task) {
		Collections.sort(array);
		return renumberTaskID(array, task);
	}
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