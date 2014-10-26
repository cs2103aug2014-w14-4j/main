package todothis.logic;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
//import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TDTDateAndTime implements Comparable <TDTDateAndTime>{
	//store converted date format dd/mm/yyyy
	private String startDate = "null";
	private String endDate = "null";
	//store converted time format XX:XX 24hrs format
	private String startTime = "null";
	private String endTime = "null";
	
	private String details = "null";
	
	private boolean isTimedTask = false;
	private boolean isDeadlineTask = false;
	
	private static Pattern[] pattern;
	private static Matcher matcher;
	
	// 2am 11pm --
	private static final String TIME_PATTERN_1 = 
			"(0?[1-9]|1[012])([aA][Mm]|[pP][mM])";
	// 2:00 12:15 2.00 23:30 ----------
	private static final String TIME_PATTERN_2 = 
			"(0?[0-9]|1[0-9]|2[0-3])(0?[:.])(0?[0-5][0-9])";
	// 2:00pm 12:15pm 2.00pm 12.15pm --
	private static final String TIME_PATTERN_3 = 
			"(0?[0-9]|1[012])(0?[:.])(0?[0-5][0-9])([aA][Mm]|[pP][mM])";
	// 13:00pm 12:01pm
	private static final String TIME_PATTERN_4 = 
			"(0?1[2-9]|2[0-3])(0?[:.])(0?[0-5][0-9])([pP][mM])";
	// 1200am to 1259pm 
	private static final String TIME_PATTERN_5 = 
			"(0?[1-9]|1[012])(0?[0-5][0-9])([aA][Mm]|[pP][mM]|[hH][rR]|[hH][rR][sS]|[hH])";
	// 1300pm onwards
	private static final String TIME_PATTERN_6 = 
			"(0?1[3-9]|2[0-3])(0?[0-5][0-9])([pP][mM]|[hH][rR]|[hH][rR][sS]|[hH])";
	// 000am 0000am
	private static final String TIME_PATTERN_7 = 
			"(0?0)(0?[0-5][0-9])([aA][mM]|[hH][rR]|[hH][rR][sS]|[hH])";
	
	private static Calendar cal = Calendar.getInstance(TimeZone.getDefault());
	
	//constructor
	public TDTDateAndTime(String dateAndTime_details){
		details = dateAndTime_details;
		decodeDetails(dateAndTime_details);
		if(!getStartDate().equals("null") || !getStartTime().equals("null")){
			isTimedTask = true;
		}else if(!getEndDate().equals("null") || !getEndTime().equals("null")){
			isDeadlineTask = true;
		}
		
	}
	public TDTDateAndTime(String startDate, String endDate, String startTime, String endTime){
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		if(!getStartDate().equals("null") || !getStartTime().equals("null")){
			isTimedTask = true;
		}else if(!getEndDate().equals("null") || !getEndTime().equals("null")){
			isDeadlineTask = true;
		}
	}
	public TDTDateAndTime(){
		this.startDate = "null";
		this.endDate = "null";
		this.startTime = "null";
		this.endTime = "null";
	}
	
	public static void main(String args[]){
		
		//TDTDateAndTime test1 = new TDTDateAndTime("11/11/2014");
		TDTDateAndTime test2 = new TDTDateAndTime("0212hrs");
		System.out.println(test2.display());
		//System.out.println(TDTDateAndTime.decodeSearchDetails("tml today monday"));
	
	}
	public static String replaceEndStringPunctuation(String word){
		int length = word.length();
		String replacedWord = word;
		for(int i = length - 1; i >= 0; i--){
			if(word.charAt(i) == '.' || word.charAt(i) == '!' || word.charAt(i) == ','){
				replacedWord = word.substring(0,i);
			}else {
				return replacedWord;
			}
		}
		return replacedWord;
	}
	
	private void decodeDetails(String details){
		System.out.println(details);
		boolean endTimeDate = false;
		boolean deadlineEndTimeDate = false;
		int thisOrNextOrFollowing = 0; //this = 1 next = 2 following = 3
		String decodedDate = "";
		String decodedTime = "";
		
		String [] parts = details.toLowerCase().split(" ");
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		//int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		//int CurrentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		int numOfDaysCurrentMonth = getNumOfDaysFromMonth(currentMonth, currentYear);
		for(int a = 0; a < parts.length;a++){
			parts[a] = replaceEndStringPunctuation(parts[a]);
			
			if(isPrepositionTo(parts, a) ){
				endTimeDate = true;
			}
			if(parts[a].equals("by")){
				deadlineEndTimeDate = true;
			}
			
			if(parts[a].equals("this")){
				thisOrNextOrFollowing = 1;
			} else if(parts[a].equals("next")){
				thisOrNextOrFollowing = 2;
			} else if(parts[a].equals("following")){
				thisOrNextOrFollowing = 3;
			}
			
			if(checkDate(parts[a])){
				decodedDate = decodeDate(parts, a, currentYear);
				
				storeDecodedDate(endTimeDate, deadlineEndTimeDate, decodedDate);
			} else if(checkTime(parts[a])){
				decodedTime = decodeTime(parts, a);

				storeDecodedTime(endTimeDate, deadlineEndTimeDate, decodedTime);
			} else if(checkDay(parts[a]) != 0){
				int numOfDaysToAdd = determineDaysToBeAdded(
						thisOrNextOrFollowing, parts, a, currentDayOfWeek);
				
				decodedDate = addDaysToCurrentDate(currentDay, currentMonth,
						currentYear, numOfDaysCurrentMonth, numOfDaysToAdd);
				
				decodedDate = adjustmentToDate(endTimeDate, decodedDate, parts,
						a);
				
				storeDecodedDate(endTimeDate, deadlineEndTimeDate, decodedDate);
			} else if(checkMonth(parts[a].replaceAll("[0-9~]", "")) != 0){
				decodedDate = decodeMonthFormat(parts, a, currentYear);
				
				storeDecodedDate(endTimeDate, deadlineEndTimeDate, decodedDate);
			}
		}
	}
	private String adjustmentToDate(boolean endTimeDate, String decodedDate,
			String[] parts, int a) {
		String [] toBeAddedDateParts = decodedDate.split("/");
		if(endTimeDate == true){
			if(!startDate.equals("null") && checkDay(parts[a]) != 8){ // enddate != today
				if(compareToDate(startDate,decodedDate) == -1 || 
						compareToDate(startDate,decodedDate) == 0 ){
					int dayTemp = Integer.parseInt(toBeAddedDateParts[0]);
					int mthTemp = Integer.parseInt(toBeAddedDateParts[1]);
					int yearTemp = Integer.parseInt(toBeAddedDateParts[2]);

					decodedDate = addDaysToCurrentDate(dayTemp, mthTemp, yearTemp,
							getNumOfDaysFromMonth(mthTemp, yearTemp), 7);	
				}
			}
		}
		return decodedDate;
	}
	private void storeDecodedTime(boolean endTimeDate,
			boolean deadlineEndTimeDate, String decodedTime) {
		if(deadlineEndTimeDate == true){
			endTime = decodedTime;
		} else if (endTimeDate == true){
			if(startTime.equals("null")){
				startTime = decodedTime;
			}else{
				endTime = decodedTime;
			}
		} else {
			startTime = decodedTime;
		}
	}
	private void storeDecodedDate(boolean endTimeDate,
			boolean deadlineEndTimeDate, String decodedDate) {
		if(deadlineEndTimeDate == true){
			endDate = decodedDate;
		} else if (endTimeDate == true){
			if(startDate.equals("null")){
				startDate = decodedDate;
			}else{
				endDate = decodedDate;
			}
		} else{
			startDate = decodedDate;
		}
	}
	private static String decodeMonthFormat(String [] parts, int a, int currentYear){
		String[] tempParts = parts[a].split("~");
		
		int day = Integer.parseInt(tempParts[0]);
		int month = checkMonth(tempParts[1]);
		int year = 0;
		
		if(tempParts.length == 3){
			if(tempParts[2].length() == 2){
				tempParts[2] = "20" + tempParts[2];
			}
			year = Integer.parseInt(tempParts[2]);
		}else{
			year = currentYear;
		}
		return day + "/" + month + "/" + year;
	}
	private static String addDaysToCurrentDate(int currentDay, int currentMonth,
			int currentYear, int numOfDaysCurrentMonth, int numOfDaysToAdd){
		int dayTemp = currentDay;
		int monthTemp = currentMonth;
		int yearTemp = currentYear;
		
		if((dayTemp + numOfDaysToAdd) > numOfDaysCurrentMonth){
			monthTemp++;
			if(monthTemp > 12){
				monthTemp = 1; //set to Jan
				yearTemp++;
			}
			dayTemp = (dayTemp + numOfDaysToAdd) - numOfDaysCurrentMonth;
		}else if((dayTemp + numOfDaysToAdd) <= 0){
			monthTemp--;
			if(monthTemp <= 0){
				monthTemp = 12; //set to Dec
				yearTemp--;
			}
			dayTemp = getNumOfDaysFromMonth(monthTemp, yearTemp) 
					+ (dayTemp + numOfDaysToAdd);
		}else{
			dayTemp = dayTemp + numOfDaysToAdd;
		}
		return dayTemp + "/" + monthTemp + "/" + yearTemp;
	}
	private static int determineDaysToBeAdded(int thisOrNextOrFollowing,
			String[] parts, int a, int currentDayOfWeek) {
		int numOfDaysToAdd = 0;
		if(checkDay(parts[a]) <= 7 && checkDay(parts[a]) > 0){
			if(thisOrNextOrFollowing == 0){ //None of the above
				if(checkDay(parts[a]) <= currentDayOfWeek){
					numOfDaysToAdd = 7 - (currentDayOfWeek - checkDay(parts[a]));
				}else{
					numOfDaysToAdd = checkDay(parts[a]) - currentDayOfWeek;
				}
			}else{//this
				if(checkDay(parts[a]) == 1){//sunday
					if(currentDayOfWeek != 0){
						numOfDaysToAdd = 8 - currentDayOfWeek; 
					}
				}else{
					numOfDaysToAdd = checkDay(parts[a]) - currentDayOfWeek;
				}
			}
			if(thisOrNextOrFollowing == 2){//next
				numOfDaysToAdd = numOfDaysToAdd + 7;
				
			}else if(thisOrNextOrFollowing == 3){//following
				numOfDaysToAdd = numOfDaysToAdd + 14;
			}
		}else if (checkDay(parts[a]) == 8){
			//numofdaystoadd already 0;
		}else if (checkDay(parts[a]) == 9){
			numOfDaysToAdd++;
		}else if (checkDay(parts[a]) == 10){
			if(thisOrNextOrFollowing == 2){//next
				numOfDaysToAdd = numOfDaysToAdd + 1;
				
			}else if(thisOrNextOrFollowing == 3){//following
				numOfDaysToAdd = numOfDaysToAdd + 2;
			}
		}
		return numOfDaysToAdd;
	}
	private String decodeTime(String[] parts, int a) {
		String [] timeParts = new String[2];
		int temp;
		if ((parts[a].substring(parts[a].length()-2, parts[a].length()).equals("am")) || 
				(parts[a].substring(parts[a].length()-2, parts[a].length()).equals("pm") ||
						(parts[a].substring(parts[a].length()-2, parts[a].length()).equals("hr")))){
			if(parts[a].length() > 4){
				if(parts[a].charAt(parts[a].length()-5) == ':' || 
						parts[a].charAt(parts[a].length()-5) == '.'){
					if(parts[a].length() == 6){
						timeParts[0] = parts[a].substring(0, 1);
						timeParts[1] = parts[a].substring(2, 4);
					}else{
						timeParts[0] = parts[a].substring(0, 2);
						timeParts[1] = parts[a].substring(3, 5);
					}
				}
			}
			if(parts[a].substring(0, parts[a].length()-2).matches("\\d+")){
				if(parts[a].length() == 3 || parts[a].length() == 4){
					timeParts[0] = parts[a].substring(0, parts[a].length()-2);
					timeParts[1] = "00";
				}else{
					timeParts[0] = parts[a].substring(0,parts[a].length()-4);
					timeParts[1] = parts[a].substring(parts[a].length()-4, parts[a].length()-2);
				}
			}
		
			temp = Integer.parseInt(timeParts[0]);
			if(parts[a].substring(parts[a].length()-2, parts[a].length()).equals("pm")){
				if(temp < 12){
					temp = temp + 12;  //convert to 24hrs format
				}
				timeParts[0] = Integer.toString(temp);
			}
			else if(parts[a].substring(parts[a].length()-2, parts[a].length()).equals("am")){
				if(temp == 12){
					timeParts[0] = "00";
				}
			}
		}else if(parts[a].substring(parts[a].length()-1, parts[a].length()).equals("h")){
			timeParts[0] = parts[a].substring(0,parts[a].length()-3);
			timeParts[1] = parts[a].substring(parts[a].length()-3, parts[a].length()-1);
		}else if(parts[a].substring(parts[a].length()-3, parts[a].length()).equals("hrs")){
			timeParts[0] = parts[a].substring(0,parts[a].length()-5);
			timeParts[1] = parts[a].substring(parts[a].length()-5, parts[a].length()-3);
		}else{
			if(parts[a].length() > 2){
				if(parts[a].charAt(parts[a].length()-3) == ':' || 
						parts[a].charAt(parts[a].length()-3) == '.'){
					timeParts[0] = parts[a].substring(0,parts[a].length()-3);
					timeParts[1] = parts[a].substring(parts[a].length()-2, parts[a].length());
				}else{
					timeParts[0] = parts[a].substring(0,parts[a].length()-2);
					timeParts[1] = parts[a].substring(parts[a].length()-2, parts[a].length());
				}
			}
		}
		return timeParts[0] + ":" + timeParts[1];
	}
	private static String decodeDate(String[] parts, int a, int currentYear) {
		String [] dateParts = new String[3];
		String [] datePartsTemp = null;
		// 9/12, 9/12/2014, 8-11, 8-11-2015 9/12/12
		if ((parts[a].split("/").length == 3) || (parts[a].split("/").length == 2)) {
			datePartsTemp = parts[a].split("/");
		} else if ((parts[a].split("-").length == 3) || (parts[a].split("-").length == 2)) {
			datePartsTemp = parts[a].split("-");
		} 
		/*else if ((parts[a].split(".").length == 3) || (parts[a].split(".").length == 2)) {
			dateParts = parts[a].split(".");
		}*/
		else{
			dateParts[0] = parts[a].substring(0, 2);
			dateParts[1] = parts[a].substring(2, 4);
			if(parts[a].length() == 6){
				dateParts[2] = "20" + parts[a].substring(4, 6); //valid year 2014-2099
			}else if(parts[a].length() == 8){
				dateParts[2] = parts[a].substring(4, 8);
			}
		}
		//if 9/12 entered, add on to 9/12/2014
		if(datePartsTemp != null){
			if(datePartsTemp.length == 2){
				dateParts[0] = datePartsTemp[0];
				dateParts[1] = datePartsTemp[1];
				dateParts[2] = Integer.toString(currentYear);
			}else{
				dateParts = datePartsTemp;
			}
			if(datePartsTemp.length == 3){
				if(datePartsTemp[2].length() == 2){
					dateParts[2] = "20" + datePartsTemp[2];
				}
			}
		}
		return dateParts[0] + "/" + dateParts[1] + "/" + dateParts[2];
	}
	private boolean isPrepositionTo(String[] parts, int a) {
		return parts[a].equals("to") || parts[a].equals("till") || 
				parts[a].equals("until") || parts[a].equals("-");
	}
	//-----------------------GETTER-------------------------------
	public String getStartDate(){
		return startDate;
	}
	public String getEndDate(){
		return endDate;
	}
	public String getStartTime(){
		return startTime;
	}
	public String getEndTime(){
		return endTime;
	}
	public String getDetails(){
		return details;
	}
	public boolean isTimedTask(){
		return isTimedTask;
	}
	public boolean isDeadlineTask(){
		return isDeadlineTask;
	}
	
	//-------------------------------------------DISPLAY------------------------------------------------
	public String displayDateTime(boolean deadline){
		String dateAndTimeContents = "";
		if(deadline == true){
			if(!getEndDate().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  Due Date: " + getEndDate()+"<br>";
			}
			if(!getEndTime().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  Due Time: " + getEndTime()+"<br>";
			}
		}else{
			if(!getStartDate().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  Start Date: " + getStartDate()+"<br>";
			}
			if(!getStartTime().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  Start Time: " + getStartTime()+"<br>";
			}
			if(!getEndDate().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  End Date: " + getEndDate()+"<br>";
			}
			if(!getEndTime().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  End Time: " + getEndTime()+"<br>";
			}
		}
		return dateAndTimeContents;
	}
	public String display(){
		boolean isDeadline = false;
		String displayString = "";
		if(!getStartDate().equals("null") || !getStartTime().equals("null")){
			displayString = "(TIMED TASK)";
		}else if(!getEndDate().equals("null") || !getEndTime().equals("null")){
			displayString = "(DEADLINE TASK)";
			isDeadline = true;
		}else{
			displayString = "(FLOATING TASK)";
		}
		displayString = displayString +"<br>"+ displayDateTime(isDeadline);
		return displayString;
	}
//-------------------------------Time Related Methods------------------------------------------
	public static boolean isValidTimeRange(String time){
		if(time.equals("null")){
			return true;
		}
		String [] timeParts = time.split(":");
		int hours = Integer.parseInt(timeParts[0]);
		int minutes = Integer.parseInt(timeParts[1]);
		if(hours < 24 && hours >= 0 && minutes < 60 && minutes >= 0){
			return true;
		}
		return false;
	}
	//use for "from Date1 to Date2" date1 and date2 same.
	public static boolean isValidTimeCompare(String startTime, String endTime){
		String [] startTimeParts = startTime.split(":");
		String [] endTimeParts = endTime.split(":");
		
		int startHours = Integer.parseInt(startTimeParts[0]);
		int startMinutes = Integer.parseInt(startTimeParts[1]);
		int endHours = Integer.parseInt(endTimeParts[0]);
		int endMinutes = Integer.parseInt(endTimeParts[1]);
		
		if(endHours > startHours){
			return true;
		}else if(endHours == startHours){
			if(endMinutes > startMinutes){
				return true;
			}
		}
		return false;
	}
	
	public static int compareToTime(String time1, String time2){
		String [] time1Parts = time1.split(":");
		String [] time2Parts = time2.split(":");
		
		int time1Hours = Integer.parseInt(time1Parts[0]);
		int time1Minutes = Integer.parseInt(time1Parts[1]);
		int time2Hours = Integer.parseInt(time2Parts[0]);
		int time2Minutes = Integer.parseInt(time2Parts[1]);
		
		if(time2Hours > time1Hours){
			return 1;
		}else if(time2Hours == time1Hours){
			if(time2Minutes > time1Minutes){
				return 1;
			}else if(time2Minutes == time1Minutes){
				return 0;
			}else{
				return -1;
			}
		}
		return -1;
	}
	
//-------------------------------Date related methods-------------------------------------
	public static boolean isValidDateRange(String date) {
		String [] dateParts = date.split("/");
		int day, month, year;
		if(date.equals("null")){
			return true;
		}
		
		try{
			day = Integer.parseInt(dateParts[0]);
			month = Integer.parseInt(dateParts[1]);
			year = Integer.parseInt(dateParts[2]);
		}catch(Exception e){
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

	public static boolean isValidDateCompare(String startDate, String endDate){
		String [] startDateParts = startDate.split("/");
		String [] endDateParts = endDate.split("/");
		
		int startDay = Integer.parseInt(startDateParts[0]);
		int startMonth = Integer.parseInt(startDateParts[1]);
		int startYear = Integer.parseInt(startDateParts[2]);
		int endDay = Integer.parseInt(endDateParts[0]);
		int endMonth = Integer.parseInt(endDateParts[1]);
		int endYear = Integer.parseInt(endDateParts[2]);
		
		if(endYear > startYear){
			return true;
		}else if(endYear == startYear){
			if(endMonth > startMonth){
				return true;
			}else if(endMonth == startMonth){
				if(endDay >= startDay){
					return true;
				}
			}
		}
		return false;
	}
	
	public static int compareToDate(String date1, String date2){
		
		if(date1.equals("null") && !date2.equals("null")){ //compareddate<thisdate
			return -1;
		}else if(date1.equals("null") && date2.equals("null")){
			return 0;
		}else if(!date1.equals("null") && date2.equals("null")){
			return 1;
		}
		
		String [] date1Parts = date1.split("/");
		String [] date2Parts = date2.split("/");
		
		int date1Day = Integer.parseInt(date1Parts[0]);
		int date1Month = Integer.parseInt(date1Parts[1]);
		int date1Year = Integer.parseInt(date1Parts[2]);
		int date2Day = Integer.parseInt(date2Parts[0]);
		int date2Month = Integer.parseInt(date2Parts[1]);
		int date2Year = Integer.parseInt(date2Parts[2]);
		
		if(date2Year > date1Year){
			return 1;
		}else if(date2Year == date1Year){
			if(date2Month > date1Month){
				return 1;
			}else if(date2Month == date1Month){
				if(date2Day > date1Day){
					return 1;
				}else if(date2Day == date1Day){
					return 0;
				}else{
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
			if (year % 400 == 0){
				isLeapYear = true;
			} else if (year % 100 == 0){
				isLeapYear = false;
			} else if (year % 4 == 0){
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
	//----------------------CHECK TIME--------------------------------
	
	public static boolean checkTime(String time) {

		time = replaceEndStringPunctuation(time);
		
		pattern = new Pattern[8]; 
		pattern[1] = Pattern.compile(TIME_PATTERN_1);
		pattern[2] = Pattern.compile(TIME_PATTERN_2);
		pattern[3] = Pattern.compile(TIME_PATTERN_3);
		pattern[4] = Pattern.compile(TIME_PATTERN_4);
		pattern[5] = Pattern.compile(TIME_PATTERN_5);
		pattern[6] = Pattern.compile(TIME_PATTERN_6);
		pattern[7] = Pattern.compile(TIME_PATTERN_7);
		 for (int i=1; i<=7; i++) {
			 matcher = pattern[i].matcher(time);
			 if(matcher.matches()){
				 return true;
			 }
		 }
		 return false;
	}

/* previous one.
		// check time possible cases
		// 2am 11pm --
		// 2:00 12:15 2.00 --
		// 2:00pm 12:15pm 2.00pm 12.15pm --
		// 2359 230
		// 2359pm 230pm -- 
		// shortest 2am || longest 12:15pm 
		nextWord = nextWord.replaceAll("[.!,]", "");
		nextWord = nextWord.toLowerCase();
		if (isValidTimeLengthRange(nextWord)) {
			if (isAMorPM(nextWord)) {
				// eg 2:00pm 12:15pm 2.00pm 12.15pm
				if(nextWord.length()>4) {
					if (isValidTimeTypeAMPM(nextWord)) {
							return true;
					} 
				}
				// eg 2359pm 230pm 2am 11pm - only digits. 2:345pm , 12344pm are invalid.
				if (isDigits(nextWord.substring(0, nextWord.length()-2))) {
					return true;
				}
				// eg 2:00 12:15 2.00
			} else if (isValidTimeType(nextWord)) {
				return true;
				// eg 2359 230
			} else if (((nextWord.length() == 3) || (nextWord.length() == 4)) &&
						(isDigits(nextWord))) {
				return true;
			}
		}
		return false;
		
		
	}
	
	public static boolean isValidTimeLengthRange(String nextWord) {
		if(nextWord.length() > 2 && nextWord.length() <= 7) {
			return true;
		}
		return false;
	}
	
	public static boolean isAMorPM(String nextWord) {
		if ((nextWord.substring(nextWord.length()-2, nextWord.length()).equals("am")) || 
				(nextWord.substring(nextWord.length()-2, nextWord.length()).equals("pm"))) {
			return true;
		}
		return false;
	}

	public static boolean isValidTimeTypeAMPM(String nextWord) {
		if ((nextWord.charAt(nextWord.length()-5) == ':') || (nextWord.charAt(nextWord.length()-5) == '.')) {
			String temp = nextWord.replace(nextWord.charAt(nextWord.length()-5) + "", "");
			temp = temp.substring(0, temp.length()-2);
			if (isDigits(temp)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidTimeType(String nextWord) {
		if (((nextWord.charAt(nextWord.length()-3)) == ':') ||((nextWord.charAt(nextWord.length()-3)) == '.')) {
			return true;
		}
		return false;
	}

	public static boolean isDigits(String temp) {
		if(temp.matches("\\d+")) {
			return true;
		}
		return false;
	}
*/

	//---------------------------CHECK DATE------------------------------------------
	public static boolean checkDate(String nextWord) {
		
		nextWord = replaceEndStringPunctuation(nextWord);
		
		if ((nextWord.split("/").length == 3) || (nextWord.split("/").length == 2)) {
			return true;
		} else if ((nextWord.split("-").length == 3) || (nextWord.split("-").length == 2)) {
			return true;
		} else if ((nextWord.length() == 6) || (nextWord.length() == 8)) {
			if (nextWord.matches("\\d+")) {
				return true;
			}
		}
		return false;
	}

	public static int checkDay(String day) {
		day = replaceEndStringPunctuation(day);
		
		if ((day.equalsIgnoreCase("Sunday")) || (day.equalsIgnoreCase("Sun"))) {
			return 1;
		}else if ((day.equalsIgnoreCase("Monday")) || (day.equalsIgnoreCase("Mon"))) {
			return 2;
		} else if ((day.equalsIgnoreCase("Tuesday")) || (day.equalsIgnoreCase("Tue")) 
				|| (day.equalsIgnoreCase("Tues"))) {
			return 3;	
		} else if ((day.equalsIgnoreCase("Wednesday")) || (day.equalsIgnoreCase("Wed"))) {
			return 4;
		} else if ((day.equalsIgnoreCase("Thursday")) || (day.equalsIgnoreCase("Thur"))
				|| (day.equalsIgnoreCase("Thurs"))) {
			return 5;
		} else if ((day.equalsIgnoreCase("Friday")) || (day.equalsIgnoreCase("Fri"))) {
			return 6;
		} else if ((day.equalsIgnoreCase("Saturday")) || (day.equalsIgnoreCase("Sat"))) {
			return 7;
		} else if ((day.equalsIgnoreCase("Today")) || (day.equalsIgnoreCase("Tdy"))) {
			return 8;
		} else if ((day.equalsIgnoreCase("Tomorrow")) || (day.equalsIgnoreCase("Tml"))
				|| (day.equalsIgnoreCase("Tmw")) || (day.equalsIgnoreCase("Tmr")) 
				|| (day.equalsIgnoreCase("2moro"))) {
			return 9;
		} else if (day.equalsIgnoreCase("Day")) {
			return 10;
		} else {
			return 0;
		}

	}
	
	public static int checkMonth(String month) {
		if ((month.equalsIgnoreCase("January")) || (month.equalsIgnoreCase("Jan"))) {
			return 1;
		} else if ((month.equalsIgnoreCase("February")) || (month.equalsIgnoreCase("Feb"))) {
			return 2;	
		} else if ((month.equalsIgnoreCase("March")) || (month.equalsIgnoreCase("Mar"))) {
			return 3;
		} else if ((month.equalsIgnoreCase("April")) || (month.equalsIgnoreCase("Apr"))) {
			return 4;
		} else if ((month.equalsIgnoreCase("May"))){
			return 5;
		} else if ((month.equalsIgnoreCase("June")) || (month.equalsIgnoreCase("Jun"))) {
			return 6;
		} else if ((month.equalsIgnoreCase("July")) || (month.equalsIgnoreCase("Jul"))) {
			return 7;
		} else if ((month.equalsIgnoreCase("August")) || (month.equalsIgnoreCase("Aug"))) {
			return 8;
		} else if ((month.equalsIgnoreCase("September")) || (month.equalsIgnoreCase("Sep")) 
				|| (month.equalsIgnoreCase("Sept"))) {
			return 9;
		} else if ((month.equalsIgnoreCase("October")) || (month.equalsIgnoreCase("Oct"))) {
			return 10;
		} else if ((month.equalsIgnoreCase("November")) || (month.equalsIgnoreCase("Nov"))) {
			return 11;
		} else if ((month.equalsIgnoreCase("December")) || (month.equalsIgnoreCase("Dec"))) {
			return 12;
		} else {
			return 0;
		}
	}
	//-----------------------------------Decode Search Details-----------------------------
	public static String decodeSearchDetails(String searchString){
		String[] searchParts = searchString.toLowerCase().split(" ");
		int thisOrNextOrFollowing = 0; //this = 1 next = 2 following = 3
		String decodedSearchString = "";
		String decodedDate = "";
		
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		//int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		//int CurrentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		int numOfDaysCurrentMonth = getNumOfDaysFromMonth(currentMonth, currentYear);
		
		for(int i = 0; i < searchParts.length; i++){
			if(searchParts[i].equals("this")){
				thisOrNextOrFollowing = 1;
			}else if(searchParts[i].equals("next")){
				thisOrNextOrFollowing = 2;
			}else if(searchParts[i].equals("following")){
				thisOrNextOrFollowing = 3;
			}
			
			if(checkDate(searchParts[i])){
				decodedDate = decodeDate(searchParts, i, currentYear);
				decodedSearchString = decodedSearchString + decodedDate + " ";
			}else if(checkDay(searchParts[i]) != 0){
				int numOfDaysToAdd = determineDaysToBeAdded(
						thisOrNextOrFollowing, searchParts, i, currentDayOfWeek);
				decodedDate = addDaysToCurrentDate(currentDay, currentMonth,
						currentYear, numOfDaysCurrentMonth, numOfDaysToAdd);
				decodedSearchString = decodedSearchString + decodedDate + " ";
			}else if(checkMonth(searchParts[i]) != 0){
				boolean isValidDayYear = true;
				if(i != 0 && i != searchParts.length - 1){
					String before = searchParts[i - 1];
					String after = searchParts[i + 1];
					try{
						Integer.parseInt(before);
						Integer.parseInt(after);
					}catch(NumberFormatException e) { 
						isValidDayYear = false;
					}
					if(isValidDayYear){
						int day = Integer.parseInt(before);
						int month = checkMonth(searchParts[i]);
						int year = 0;
						
						if(after.length() == 2){
							after = "20" + after;
						}
						year = Integer.parseInt(after);
	
						decodedDate = day + "/" + month + "/" + year;
						decodedSearchString = decodedSearchString + decodedDate + " ";
					}
				}
			}
		}
		return decodedSearchString.trim();
	}
	//-----------------------------------CHECK FOR OVERDUE TASK----------------------------
	//NEED TESTING
	@SuppressWarnings("deprecation")
	public boolean isOverdue(){
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		Date time = cal.getTime();
		int currentHour = time.getHours();
		int currentMinute = time.getMinutes();
	
		String currentDate = currentDay + "/" + currentMonth + "/" + currentYear;
		String currentTime = currentHour + ":" + currentMinute;
		
		String checkDate = "null";
		String checkTime = "null";
		if(isDeadlineTask){
			checkDate = getEndDate();
			checkTime = getEndTime();
		}else if(isTimedTask){
			if(getEndDate().equals("null") && !getEndTime().equals("null")){
				checkDate = getStartDate();
				checkTime = getEndTime();
			}else if(!getEndDate().equals("null")){
				checkDate = getEndDate();
				checkTime = getEndTime();
			}else{
				checkDate = getStartDate();
				checkTime = getStartTime();
			}
		}
		
		if(!checkDate.equals("null")){
			if(compareToDate(currentDate, checkDate) == -1){
				return true;
			}else if(compareToDate(currentDate, checkDate) == 0){
				if(!checkTime.equals("null")){
					if(compareToTime(currentTime, checkTime) == -1){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//------------------------------------CHECK FOR CLASHES-------------------------------
	public boolean isClash(TDTDateAndTime arg0){
		if(arg0.isTimedTask() == true){
			if(!this.getStartDate().equals("null") && !this.getStartTime().equals("null")){
				if(this.getEndDate().equals("null") && this.getEndTime().equals("null")){
					if(arg0.getStartDate().equals(this.getStartDate()) && 
							arg0.getStartTime().equals(this.getStartTime())){
						return true;
					}
				}else if((!this.getEndDate().equals("null") && !this.getEndTime().equals("null")) &&
						!arg0.getStartDate().equals("null")){
					//condition where this->has SD ED ST ET and arg0->has SD may have ED may have ST ET
					if(compareToDate(this.getStartDate(),arg0.getStartDate()) == 1 &&
							compareToDate(this.getEndDate(),arg0.getStartDate()) == -1){ 
						// SDate = 8/10/2014 EDate = 11/10/2014 startArgdate = 10/10/2014
						return true;
					}else if(compareToDate(this.getStartDate(),arg0.getStartDate()) == 0 &&
							compareToDate(this.getEndDate(),arg0.getStartDate()) == -1 &&
							!arg0.getStartTime().equals("null")){
						// SDate = 8/10/2014 EDate = 11/10/2014 Argdate = 8/10/2014 NEED CHECK TIMING
						if(compareToTime(this.getStartTime(),arg0.getStartTime()) == 1 ||
								compareToTime(this.getStartTime(),arg0.getStartTime()) == 0){
							// Timing SDate 8/10/2014 0800 argdate 8/10/2014 0900/0800
							return true;
						}
					}else if(compareToDate(this.getStartDate(),arg0.getStartDate()) == 1 &&
							compareToDate(this.getEndDate(),arg0.getStartDate()) == 0 && 
							!arg0.getStartTime().equals("null")){
						// SDate = 8/10/2014 EDate = 11/10/2014 Argdate = 11/10/2014 NEED CHECK TIMING
						if(compareToTime(this.getEndTime(),arg0.getStartTime()) == -1){
							// Timing EDate 11/10/2014 0800 argdate 11/10/2014 0700
							return true;
						}
					}else if(compareToDate(this.getStartDate(),arg0.getStartDate()) == 0 &&
							compareToDate(this.getEndDate(),arg0.getStartDate()) == 0 &&
							!arg0.getStartTime().equals("null")){
						// SDate = 8/10/2014 EDate = 8/10/2014 Argdate = 8/10/2014 NEED CHECK TIMING
						if((compareToTime(this.getStartTime(),arg0.getStartTime()) == 1 ||
								compareToTime(this.getStartTime(),arg0.getStartTime()) == 0) &&
								compareToTime(this.getEndTime(),arg0.getStartTime()) == -1){
							//Timing date same sTime = 0900 eTime = 1200 argtime = 1000/0900
							return true;
						}
					}else if(!arg0.getEndDate().equals("null")){
						if(compareToDate(this.getStartDate(),arg0.getEndDate()) == 1 &&
							compareToDate(this.getEndDate(),arg0.getEndDate()) == -1){
							// SDate = 8/10/2014 EDate = 11/10/2014 endArgdate = 10/10/2014
							return true;
						}else if(compareToDate(this.getStartDate(),arg0.getEndDate()) == 0 &&
								compareToDate(this.getEndDate(),arg0.getEndDate()) == -1 &&
								!arg0.getEndTime().equals("null")){
							// SDate = 8/10/2014 EDate = 11/10/2014 endArgdate = 8/10/2014
							if(compareToTime(this.getStartTime(),arg0.getEndTime()) == 1){
								//Timing SDate 8/10/2014 0800 argdate 8/10/2014 0900
								return true;
							}
						}else if(compareToDate(this.getStartDate(),arg0.getEndDate()) == 1 &&
								compareToDate(this.getEndDate(),arg0.getEndDate()) == 0 &&
								!arg0.getEndTime().equals("null")){
							// SDate = 8/10/2014 EDate = 11/10/2014 endArgdate = 11/10/2014
							if(compareToTime(this.getEndTime(),arg0.getEndTime()) == -1 ||
									compareToTime(this.getEndTime(),arg0.getEndTime()) == 0){
								//Timing EDate 11/10/2014 0800 endargdate 11/10/2014 0700/0800
								return true;
							}
						}else if(compareToDate(this.getStartDate(),arg0.getEndDate()) == 0 &&
								compareToDate(this.getEndDate(),arg0.getEndDate()) == 0 &&
								!arg0.getEndTime().equals("null")){
							// SDate = 8/10/2014 EDate = 8/10/2014 endArgdate = 8/10/2014
							if(compareToTime(this.getStartTime(),arg0.getEndTime()) == 1 &&
									(compareToTime(this.getEndTime(),arg0.getEndTime()) == -1 ||
									compareToTime(this.getEndTime(),arg0.getEndTime()) == 0)){
								return true;
							}
						}else if(compareToDate(this.getStartDate(),arg0.getStartDate()) == -1 &&
								compareToDate(this.getEndDate(),arg0.getEndDate()) == 1){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	//------------------------------------COMPARABLE----------------------------------------
	@Override
	public int compareTo(TDTDateAndTime arg0) {
		String thisDate = "null";
		String thisTime = "null"; 
		String comparedDate = "null";
		String comparedTime = "null";
		
		if(this.getStartDate().equals("null")){
			thisDate = this.getEndDate();
		}else{
			thisDate = this.getStartDate();
		}
		
		if(this.getStartTime().equals("null")){
			thisTime = this.getEndTime();
		}else{
			thisTime = this.getStartTime();
		}
		
		if(arg0.getStartDate().equals("null")){
			comparedDate = arg0.getEndDate();
		}else{
			comparedDate = arg0.getStartDate();
		}
		
		if(arg0.getStartTime().equals("null")){
			comparedTime = arg0.getEndTime();
		}else{
			comparedTime = arg0.getStartTime();
		}
		
		if(compareToDate(thisDate, comparedDate) == 1){ //thisdate<compareddate
			return -1;
		}else if(compareToDate(thisDate, comparedDate) == 0){
			if(thisTime.equals("null") && !comparedTime.equals("null")){
				return 1;
			}else if(thisTime.equals("null") && comparedTime.equals("null")){
				return 0;
			}else if(!thisTime.equals("null") && comparedTime.equals("null")){
				return -1;
			}
			
			if(compareToTime(thisTime, comparedTime) == 1){ //thistime<comparedtime
				return -1;
			}else if(compareToTime(thisTime, comparedTime) == 0){
				return 0;
			}else{
				return 1;
			}
		}else{
			return 1;
		}
	}
	
}
