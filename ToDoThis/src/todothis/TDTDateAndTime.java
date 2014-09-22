package todothis;

import java.util.ArrayList;

public class TDTDateAndTime {
	//store converted date format dd/mm/yyyy
	private String startDate = "";
	private String endDate = "";
	//store converted time format XX:XX 24hrs format
	private String startTime = "";
	private String endTime = "";
	
	private String details = "";
	//constructor
	public TDTDateAndTime(String dateAndTime_details){
		details = dateAndTime_details;
		
	}
	
	public void decodeDetails(String details){
		String [] parts = details.toLowerCase().split(" ");
		
		boolean endTimeDate = false;
		
		for(int a = 0; a < parts.length;a++){
			if(parts[a].equals("to")){
				endTimeDate = true;
			}
			if(checkDate(parts[a])){
				String [] dateParts = new String[3];
				if ((parts[a].split("/").length == 3) || (parts[a].split("/").length == 2)) {
					dateParts = parts[a].split("/");
				} else if ((parts[a].split("-").length == 3) || (parts[a].split("-").length == 2)) {
					dateParts = parts[a].split("-");
				} else if ((parts[a].split(".").length == 3) || (parts[a].split(".").length == 2)) {
					dateParts = parts[a].split("-");
				}else{
					dateParts[0] = parts[a].substring(0, 2);
					dateParts[1] = parts[a].substring(2, 4);
					if(parts[a].length() == 6){
						dateParts[2] = parts[a].substring(4, 6);
					}else{
						dateParts[2] = parts[a].substring(4, 8);
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
					if(parts[a].charAt(parts[a].length()-5) == ':' || 
							parts[a].charAt(parts[a].length()-5) == '.'){
						if(parts[a].length() == 6){
							timeParts[0] = parts[a].substring(0, 1);
							timeParts[1] = parts[a].substring(2, 4);
						}else{
							timeParts[0] = parts[a].substring(0, 2);
							timeParts[1] = parts[a].substring(3, 5);
						}
					}else{
						if(parts[a].substring(0, parts[a].length()-2).matches("\\d+")){
							if(parts[a].length() == 3 || parts[a].length() == 4){
								timeParts[0] = parts[a].substring(0, parts[a].length()-2);
								timeParts[1] = "00";
							}else{
								timeParts[0] = parts[a].substring(0,parts[a].length()-4);
								timeParts[1] = parts[a].substring(parts[a].length()-4, parts[a].length()-2);
							}
						}
					}
					
					if(parts[a].substring(parts[a].length()-2, parts[a].length()).equals("pm")){
						int temp;
						temp = Integer.parseInt(timeParts[0]);
						if(temp != 12){
							temp = temp + 12;  //convert to 24hrs format
						}
						timeParts[0] = Integer.toString(temp);
					}
				}else{
					if(parts[a].charAt(parts[a].length()-3) == ':' || 
							parts[a].charAt(parts[a].length()-3) == '.'){
						timeParts[0] = parts[a].substring(0,parts[a].length()-3);
						timeParts[1] = parts[a].substring(parts[a].length()-2, parts[a].length());
					}else{
						timeParts[0] = parts[a].substring(0,parts[a].length()-2);
						timeParts[1] = parts[a].substring(parts[a].length()-2, parts[a].length());
					}
				}
				if(endTimeDate == true){
					endTime = timeParts[0] + ":" + timeParts[1];				
				}else{
					startTime = timeParts[0] + ":" + timeParts[1];	
				}
			}else if(checkDay(parts[a]) != 0){
				
			}
		}
	}
	
	private boolean checkTime(String nextWord) {
		// check time possible cases
		// 2am 11pm --
		// 2:00 12:15 2.00 --
		// 2:00pm 12:15pm 2.00pm 12.15pm --
		// 2359 230
		// 2359pm 230pm -- 
		
		// shortest 2am || longest 12:15pm 
		if (nextWord.length() > 2 || nextWord.length() <= 7) {
			if ((nextWord.substring(nextWord.length()-2, nextWord.length()).equals("am")) || 
					(nextWord.substring(nextWord.length()-2, nextWord.length()).equals("pm"))) {

				// eg 2:00pm 12:15pm 2.00pm 12.15pm
				if ((nextWord.charAt(nextWord.length()-6) == ':') || (nextWord.charAt(nextWord.length()-6) == '.')) {
					return true;

					// eg 2359pm 230pm 2am 11pm 
					// only digits. 2:345pm , 12344pm invalid.
				} else if (nextWord.matches("\\d+")) {
					if ((nextWord.length() > 2) || (nextWord.length() < 7)){
						return true;
					}
				}

				// eg 2:00 12:15 2.00
			} else if (((nextWord.charAt(nextWord.length()-4)) == ':') ||((nextWord.charAt(nextWord.length()-4)) == '.')) {
				return true;

				// eg 2359 230
			} else if ( (nextWord.length() == 3) || (nextWord.length() == 4)) {
				return true;
			}
		}
		return false;
	}


	private boolean checkDate(String nextWord) {
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


	private int checkDay(String day) {
		if ((day.equalsIgnoreCase("Monday")) || (day.equalsIgnoreCase("Mon"))) {
			return 1;
		} else if ((day.equalsIgnoreCase("Tuesday")) || (day.equalsIgnoreCase("Tue")) 
				|| (day.equalsIgnoreCase("Tues"))) {
			return 2;	
		} else if ((day.equalsIgnoreCase("Wednesday")) || (day.equalsIgnoreCase("Wed"))) {
			return 3;
		} else if ((day.equalsIgnoreCase("Thursday")) || (day.equalsIgnoreCase("Thur"))
				|| (day.equalsIgnoreCase("Thurs"))) {
			return 4;
		} else if ((day.equalsIgnoreCase("Friday")) || (day.equalsIgnoreCase("Fri"))) {
			return 5;
		} else if ((day.equalsIgnoreCase("Saturday")) || (day.equalsIgnoreCase("Sat"))) {
			return 6;
		} else if ((day.equalsIgnoreCase("Sunday")) || (day.equalsIgnoreCase("Sun"))) {
			return 7;
		} else {
			return 0;
		}

	}
}
