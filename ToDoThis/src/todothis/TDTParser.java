package todothis;

import java.util.ArrayList;
// JUSTIN

public class TDTParser implements ITDTParser {
	private String[] parts;
	private boolean located;
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
		prepositionWords.add("later");

		commandType = determineCommandType(getFirstWord(userCommand));
		String remainingWords = removeFirstWord(userCommand);
		switch(commandType) {
			case ADD :
				commandDetails = userCommand;
				if (commandDetails.contains("!")) {
					isHighPriority = true;
				}
				parts = commandDetails.split(" ");
				for (int i = 0; i < parts.length; i++) {
					if (prepositionWords.contains(parts[i])) {
						// check date
						if ((parts[i+1].split("/").length == 3) || (parts[i+1].split("/").length == 2)) {
							// pass parts[i+1] to dateandtime class
						} else if ((parts[i+1].split("-").length == 3) || (parts[i+1].split("-").length == 2)) {
							// pass parts[i+1] to dateandtime class
						} else if ((parts[i+1].split(".").length == 3) || (parts[i+1].split(".").length == 2)) {
							// pass parts[i+1] to dateandtime class
						} else if ((parts[i+1].split(" ").length == 3) || (parts[i+1].split(" ").length == 2)) {
							// pass parts[i+1] to dateandtime class
						} else if ((parts[i+1].length() == 6) || (parts[i+1].length() == 8)) {
							if (parts[i+1].matches("\\d+")) {
								// pass to dateandtime class
							}
						}
						// check time
						// 2am 2:00am 2:00(pm) 2359 2359pm 12:15am 12:15pm 12:15(pm) 12.15 0200am 
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
				located = false;
				if (remainingWords.contains("!")) {
					isHighPriority = true;
					remainingWords = remainingWords.replace("!", "");
				}
				parts = remainingWords.split(" ");
				for (int i = 0; i < parts.length; i++) {
					if (parts[i].contains("/")) {
						dueDate = parts[i];
						remainingWords = remainingWords.replace(parts[i], "");
					}
					if (parts[i].contains(".")) {
						dueTime = parts[i];
						remainingWords = remainingWords.replace(parts[i], "");
					}
					if (parts[i].matches("\\d+") && located == false) {
						taskID = Integer.parseInt(parts[i]);
						for (int j = 0; j < i; j++) {
							labelName += parts[j];
						}
						located = true;
						remainingWords = remainingWords.replace(parts[i], "");
						remainingWords = remainingWords.replace(labelName, "");
						break;
					}
				}
				commandDetails = remainingWords.trim();
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
