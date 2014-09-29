package todothis;

import java.util.ArrayList;

public class TDTParser implements ITDTParser {
	private String[] parts;
	private boolean valid;
	
	@Override
	public Command parse(String userCommand) {
		COMMANDTYPE commandType = COMMANDTYPE.INVALID;
		String labelName = "";
		boolean isHighPriority = false;
		String commandDetails = "";
		int taskID = -1;
		TDTDateAndTime dateAndTime = new TDTDateAndTime();
		String commandDetailsTemp = "";
		String remainingWordsTemp = "";
		
		ArrayList<String> prepositionWords = new ArrayList<String>();
		prepositionWords.add("on");
		prepositionWords.add("at");
		prepositionWords.add("by");
		prepositionWords.add("from");
		prepositionWords.add("about");
		prepositionWords.add("to");

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
					String checkWord = parts[i];
					int end = i;
					if (TDTDateAndTime.checkDate(checkWord)) {
						if (prepositionWords.contains(parts[i-1])) {
							end = i-1;
						}
						for (int a = 0; a < end; a++) {
							commandDetailsTemp += (parts[a] + " ");
						}
						for (int b = end; b < parts.length; b++) {
							remainingWordsTemp += (parts[b] + " ");
						}
						remainingWords = remainingWordsTemp.trim();
						commandDetails = commandDetailsTemp.trim();
						dateAndTime = new TDTDateAndTime(remainingWords);
						break;
					} else if (TDTDateAndTime.checkTime(checkWord)) {
						if (prepositionWords.contains(parts[i-1])) {
							end = i-1;
						}
						for (int a = 0; a < end; a++) {
							commandDetailsTemp += (parts[a] + " ");
						}
						for (int b = end; b < parts.length; b++) {
							remainingWordsTemp += (parts[b] + " ");
						}
						remainingWords = remainingWordsTemp.trim();
						commandDetails = commandDetailsTemp.trim();
						dateAndTime = new TDTDateAndTime(remainingWords);
						break;
					} else if (TDTDateAndTime.checkDay(checkWord) != 0) {
						if (prepositionWords.contains(parts[i-1])) {
							end = i-1;
						}
						for (int a = 0; a < end; a++) {
							commandDetailsTemp += (parts[a] + " ");
						}
						for (int b = end; b < parts.length; b++) {
							remainingWordsTemp += (parts[b] + " ");
						}
						remainingWords = remainingWordsTemp.trim();
						commandDetails = commandDetailsTemp.trim();
						dateAndTime = new TDTDateAndTime(remainingWords);
						break;
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
					// same as ADD
					commandDetails = remainingWords.trim();
					if (commandDetails.contains("!")) {
						isHighPriority = true;
					}
					parts = commandDetails.split(" ");
					for (int i = 0; i < parts.length-1; i++) {
						String checkWord = parts[i];
						int end = i;
						if (TDTDateAndTime.checkDate(checkWord)) {
							if (prepositionWords.contains(parts[i-1])) {
								end = i-1;
							}
							for (int a = 0; a < end; a++) {
								commandDetailsTemp += (parts[a] + " ");
							}
							for (int b = end; b < parts.length; b++) {
								remainingWordsTemp += (parts[b] + " ");
							}
							remainingWords = remainingWordsTemp.trim();
							commandDetails = commandDetailsTemp.trim();
							dateAndTime = new TDTDateAndTime(remainingWords);
							
						} else if (TDTDateAndTime.checkTime(checkWord)) {
							if (prepositionWords.contains(parts[i-1])) {
								end = i-1;
							}
							for (int a = 0; a < end; a++) {
								commandDetailsTemp += (parts[a] + " ");
							}
							for (int b = end; b < parts.length; b++) {
								remainingWordsTemp += (parts[b] + " ");
							}
							remainingWords = remainingWordsTemp.trim();
							commandDetails = commandDetailsTemp.trim();
							dateAndTime = new TDTDateAndTime(remainingWords);
						} else if (TDTDateAndTime.checkDay(checkWord) != 0) {
							if (prepositionWords.contains(parts[i-1])) {
								end = i-1;
							}
							for (int a = 0; a < end; a++) {
								commandDetailsTemp += (parts[a] + " ");
							}
							for (int b = end; b < parts.length; b++) {
								remainingWordsTemp += (parts[b] + " ");
							}
							remainingWords = remainingWordsTemp.trim();
							commandDetails = commandDetailsTemp.trim();
							dateAndTime = new TDTDateAndTime(remainingWords);
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
		return new Command(commandType, labelName, taskID, commandDetails, dateAndTime,
				isHighPriority);
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
