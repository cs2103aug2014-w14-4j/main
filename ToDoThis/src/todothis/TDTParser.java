package todothis;

import java.util.ArrayList;

public class TDTParser implements ITDTParser {
	private String[] parts;
	private boolean valid;
	
	@Override
	public Command parse(String userCommand) {
		COMMANDTYPE commandType = COMMANDTYPE.INVALID;
		String labelName = "";
		String dueDate = "";
		String dueTime = "";
		boolean isHighPriority = false;
		String commandDetails = "";
		int taskID = -1;
		ArrayList<String> prepositionWords = new ArrayList<String>();
		prepositionWords.add("on");
		prepositionWords.add("at");
		prepositionWords.add("by");
		prepositionWords.add("from");
		prepositionWords.add("about");
		prepositionWords.add("to");
		/*
		 * should include this in the checkDay? 
		prepositionWords.add("later");
		prepositionWords.add("tmr");
		prepositionWords.add("tomorrow");
		prepositionWords.add("next week...?");
		
		*/
		commandType = determineCommandType(getFirstWord(userCommand));
		String remainingWords = removeFirstWord(userCommand);
		switch(commandType) {
			case ADD :
				commandDetails = userCommand;
				if (commandDetails.contains("!")) {
					isHighPriority = true;
				}
				parts = commandDetails.split(" ");
				for (int i = 0; i < parts.length-1; i++) {
					if (prepositionWords.contains(parts[i])) {
						String nextWord = parts[i+1];
						if (checkDate(nextWord)) {
							new TDTDateAndTime(nextWord);
							commandDetails = commandDetails.replaceAll(nextWord, "");
							break;
						} else if (checkTime(nextWord)) {
							new TDTDateAndTime(nextWord);
							commandDetails = commandDetails.replaceAll(nextWord, "");
							break;
						} else if (checkDay(nextWord) != 0) {
							new TDTDateAndTime(nextWord);
							commandDetails = commandDetails.replaceAll(nextWord, "");
							break;
						}
					}
				}
				break;
			case DELETE :
				parts = remainingWords.split(" ");
				String lastWord = parts[parts.length - 1];
				if (lastWord.matches("\\d+")) {
					taskID = Integer.parseInt(lastWord);
					labelName = remainingWords.substring(0, remainingWords.lastIndexOf(" "));
				} else {
					labelName = remainingWords;
				}
				break;
			case EDIT :
				valid = false;
				if (remainingWords.contains("!")) {
					isHighPriority = true;
					remainingWords = remainingWords.replace("!", "");
				}
				parts = remainingWords.split(" ");

				// gets [labelName][taskID]  
				for (int i = 0; i < parts.length; i++) {
					if (parts[i].matches("\\d+")) {
						taskID = Integer.parseInt(parts[i]);
						for (int j = 0; j < i; j++) {
							labelName += parts[j];
						}
						valid = true;
						remainingWords = remainingWords.replace(parts[i], "");
						remainingWords = remainingWords.replace(labelName, "");
						break;
					}
				}
				if (valid) {
					commandDetails = remainingWords.trim();
					// gets [remainingWords]
					for (int k = 0; k < parts.length-1; k++) {
						if (prepositionWords.contains(parts[k])) {
							String nextWord = parts[k+1];
							if (checkDate(nextWord)) {
								new TDTDateAndTime(nextWord);
								commandDetails = commandDetails.replaceAll(nextWord, "");
								break;
							} else if (checkTime(nextWord)) {
								new TDTDateAndTime(nextWord);
								commandDetails = commandDetails.replaceAll(nextWord, "");
								break;
							} else if (checkDay(nextWord) != 0) {
								new TDTDateAndTime(nextWord);
								commandDetails = commandDetails.replaceAll(nextWord, "");
								break;
							}
						}
					}
					commandDetails = remainingWords.trim();
				}
				break;
			case LABEL :
				commandType = COMMANDTYPE.LABEL;
				labelName = remainingWords;
				break;
			case SORT :
				commandType = COMMANDTYPE.SORT;
				break;
			case UNDO :
				commandType = COMMANDTYPE.UNDO;
				break;
			case SEARCH :
				commandType = COMMANDTYPE.SEARCH;
				commandDetails = remainingWords;
				break;
			case DISPLAY :
				String checkDisplay[] = remainingWords.split(" ");
				commandType = COMMANDTYPE.DISPLAY;
				labelName = checkDisplay[0];
				break;
			case HIDE :
				String checkHide[] = remainingWords.split(" ");
				commandType = COMMANDTYPE.HIDE;
				labelName = checkHide[0];
				break;
			case DONE :
				commandType = COMMANDTYPE.DONE;
				String[] tempWords = remainingWords.split(" ");
				String lastWord1 = tempWords[tempWords.length -1];
				if (lastWord1.matches("\\d+")) {
					taskID = Integer.parseInt(lastWord1);
					labelName = remainingWords.substring(0, remainingWords.lastIndexOf(" "));
				} else {
					labelName = remainingWords;
				}
				break;
			case INVALID :
				commandType = COMMANDTYPE.INVALID;
				break;
			default:
				//Will not reach here
				break;
		}

		return new Command(commandType, labelName, taskID, commandDetails, dueDate, dueTime,
				isHighPriority);
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
			if ((nextWord.substring(nextWord.length()-2, nextWord.length()-1).equals("am")) || 
					(nextWord.substring(nextWord.length()-2, nextWord.length()-1).equals("pm"))) {

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


	//By default command is assume to be ADD.
	private static COMMANDTYPE determineCommandType(String commandTypeString) {
		if (commandTypeString == null) {
			return COMMANDTYPE.INVALID;
		} else if (commandTypeString.equalsIgnoreCase("hide")) {
			return COMMANDTYPE.HIDE;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMANDTYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMANDTYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("label")) {
			return COMMANDTYPE.LABEL;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return COMMANDTYPE.EDIT;
		} else if (commandTypeString.equalsIgnoreCase("sort")) {
			return COMMANDTYPE.SORT;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return COMMANDTYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return COMMANDTYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("done")) {
			return COMMANDTYPE.DONE;
		} else {
			return COMMANDTYPE.ADD;
		}
	}

	private static String removeFirstWord(String userCommand) {
		return userCommand.replaceFirst(getFirstWord(userCommand), "").trim();
	}

	private static String getFirstWord(String userCommand) {
		String userWords = userCommand.trim().split("\\s+")[0];
		return userWords;
	}

}
