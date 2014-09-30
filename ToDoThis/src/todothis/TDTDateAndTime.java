package todothis;

import java.util.Calendar;
import java.util.TimeZone;

public class TDTDateAndTime implements Comparable <TDTDateAndTime>{
	//store converted date format dd/mm/yyyy
	private String startDate = "null";
	private String endDate = "null";
	//store converted time format XX:XX 24hrs format
	private String startTime = "null";
	private String endTime = "null";
	
	private String details = "null";
	
	private static Calendar cal = Calendar.getInstance(TimeZone.getDefault());
	//constructor
	public TDTDateAndTime(String dateAndTime_details){
		details = dateAndTime_details;
		decodeDetails(dateAndTime_details);
		
	}
	public TDTDateAndTime(String startDate, String endDate, String startTime, String endTime){
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public TDTDateAndTime(){
		this.startDate = "null";
		this.endDate = "null";
		this.startTime = "null";
		this.endTime = "null";
	}
	
	public static void main(String args[]){
		
		TDTDateAndTime test1 = new TDTDateAndTime("11/11/2014 8pm");
		TDTDateAndTime test2 = new TDTDateAndTime("11/11/2014 9pm");
		System.out.println(test1.compareTo(test2));
		
	}
	
	public void decodeDetails(String details){
		
		String [] parts = details.toLowerCase().split(" ");
		
		boolean endTimeDate = false;
		
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		//int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		//int CurrentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
		int numOfDaysCurrentMonth = getNumOfDaysFromMonth(currentMonth, currentYear);
		
		for(int a = 0; a < parts.length;a++){
			if(parts[a].equals("to") || parts[a].equals("till") || 
					parts[a].equals("by") || parts[a].equals("until") ||
					parts[a].equals("-") ){
				endTimeDate = true;
			}
			if(checkDate(parts[a])){
				String [] dateParts = new String[3];
	 			String [] datePartsTemp = null;
				// 9/12, 9/12/2014, 8-11, 8-11-2015
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
					}else{
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
				}
		
				if(endTimeDate == true){
					endDate = dateParts[0] + "/" + dateParts[1] + "/" + dateParts[2];
				}else{
					startDate = dateParts[0] + "/" + dateParts[1] + "/" + dateParts[2];
				}
			}else if(checkTime(parts[a])){
				String [] timeParts = new String[2];
				if ((parts[a].substring(parts[a].length()-2, parts[a].length()).equals("am")) || 
						(parts[a].substring(parts[a].length()-2, parts[a].length()).equals("pm"))) {
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
				
					int temp;
					temp = Integer.parseInt(timeParts[0]);
					if(parts[a].substring(parts[a].length()-2, parts[a].length()).equals("pm")){
						if(temp != 12){
							temp = temp + 12;  //convert to 24hrs format
						}
						timeParts[0] = Integer.toString(temp);
					}
					else{
						if(temp == 12){
							timeParts[0] = "00";
						}
					}
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
				if(endTimeDate == true){
					endTime = timeParts[0] + ":" + timeParts[1];				
				}else{
					startTime = timeParts[0] + ":" + timeParts[1];	
				}
			}else if(checkDay(parts[a]) != 0){
				int numOfDaysToAdd = 0;
				if(checkDay(parts[a]) <= currentDayOfWeek){
					numOfDaysToAdd = 7 - (currentDayOfWeek - checkDay(parts[a]));
				}else{
					numOfDaysToAdd = checkDay(parts[a]) - currentDayOfWeek;
				}
				
				if((currentDay + numOfDaysToAdd) > numOfDaysCurrentMonth){
					currentMonth++;
					currentDay = (currentDay + numOfDaysToAdd) - numOfDaysCurrentMonth;
				}else{
					currentDay = currentDay + numOfDaysToAdd;
				}
				if(currentMonth > 12){
					currentMonth = 1; //set to Jan
					currentYear++;
				}
				if(endTimeDate == true){
					endDate = Integer.toString(currentDay) + "/" + 
								Integer.toString(currentMonth) + "/" +
								Integer.toString(currentYear);
					
				}else{
					startDate = Integer.toString(currentDay) + "/" + 
							Integer.toString(currentMonth) + "/" +
							Integer.toString(currentYear);
				}
			}
		}
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
	
	//-------------------------------------------DISPLAY SWEE SWEE-------------------------------
	public String displayDateTime(boolean deadline){
		String dateAndTimeContents = "";
		if(deadline == true){
			if(!getEndDate().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  Due Date: " + getEndDate();
			}
			if(!getEndTime().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  Due Time: " + getEndTime();
			}
		}else{
			if(!getStartDate().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  Start Date: " + getStartDate();
			}
			if(!getStartTime().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  Start Time: " + getStartTime();
			}
			if(!getEndDate().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  End Date: " + getEndDate();
			}
			if(!getEndTime().equals("null")){
				dateAndTimeContents = dateAndTimeContents + "  End Time: " + getEndTime();
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
		displayString = displayString + displayDateTime(isDeadline);
		return displayString;
	}
//-------------------------------check if Time valid-------------------------------
	public static boolean isValidTimeRange(String time){
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
	
//-------------------------------check if Date valid-------------------------------
	public static boolean isValidDateRange(String date) {
		String [] dateParts = date.split("/");
		int day = Integer.parseInt(dateParts[0]);
		int month = Integer.parseInt(dateParts[1]);
		int year = Integer.parseInt(dateParts[2]);
		
		 if ((year >= 1900) && (year <= 2099)) {
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
			if (year % 400 == 0)
				isLeapYear = true;
			else if (year % 100 == 0)
				isLeapYear = false;
			else if (year % 4 == 0)
				isLeapYear = true;
			else
				isLeapYear = false;
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
	//----------------------CHECK FUNCTIONS--------------------------------
	public static boolean checkTime(String nextWord) {
		// check time possible cases
		// 2am 11pm --
		// 2:00 12:15 2.00 --
		// 2:00pm 12:15pm 2.00pm 12.15pm --
		// 2359 230
		// 2359pm 230pm -- 
		String temp = "";
		// shortest 2am || longest 12:15pm 
		nextWord = nextWord.toLowerCase();
		if (nextWord.length() > 2 && nextWord.length() <= 7) {
			if ((nextWord.substring(nextWord.length()-2, nextWord.length()).equals("am")) || 
					(nextWord.substring(nextWord.length()-2, nextWord.length()).equals("pm"))) {

				// eg 2:00pm 12:15pm 2.00pm 12.15pm
				if(nextWord.length() >4){
					if ((nextWord.charAt(nextWord.length()-5) == ':') || (nextWord.charAt(nextWord.length()-5) == '.')) {
						temp = nextWord.replace(nextWord.charAt(nextWord.length()-5) + "", "");
						temp = temp.substring(0, temp.length()-2);
						if(temp.matches("\\d+")){
							return true;
						}
					} 
				}
				// eg 2359pm 230pm 2am 11pm 
				// only digits. 2:345pm , 12344pm invalid.
				if (nextWord.substring(0, nextWord.length()-2).matches("\\d+")) {
					return true;
				}

				// eg 2:00 12:15 2.00
			} else if (((nextWord.charAt(nextWord.length()-3)) == ':') ||((nextWord.charAt(nextWord.length()-3)) == '.')) {

				return true;

				// eg 2359 230
			} else if (((nextWord.length() == 3) || (nextWord.length() == 4)) &&
						(nextWord.matches("\\d+"))) {
				return true;
			}
		}
		return false;
	}


	public static boolean checkDate(String nextWord) {
		if ((nextWord.split("/").length == 3) || (nextWord.split("/").length == 2)) {
			return true;
		} else if ((nextWord.split("-").length == 3) || (nextWord.split("-").length == 2)) {
			return true;
		} else if ((nextWord.split(".").length == 3) || (nextWord.split(".").length == 2)) {
			return true;
		} else if ((nextWord.length() == 6) || (nextWord.length() == 8)) {
			if (nextWord.matches("\\d+")) {
				return true;
			}
		}
		return false;
	}

	public static int checkDay(String day) {
		if ((day.equalsIgnoreCase("Monday")) || (day.equalsIgnoreCase("Mon"))) {
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
		} else if ((day.equalsIgnoreCase("Sunday")) || (day.equalsIgnoreCase("Sun"))) {
			return 1;
		} else {
			return 0;
		}

	}
	
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
		
		System.out.println(thisDate);
		System.out.println(comparedDate);
		
		if(thisDate.equals("null") && !comparedDate.equals("null")){ //compareddate<thisdate
			return 1;
		}else if(thisDate.equals("null") && comparedDate.equals("null")){
			return 0;
		}else if(!thisDate.equals("null") && comparedDate.equals("null")){
			return -1;
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
