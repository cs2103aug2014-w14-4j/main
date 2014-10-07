package todothis;

import java.util.ArrayList;

public class TDTParser implements ITDTParser {
	private String[] parts;
	
	// anything with !!!  added doesnt appear until reopened.
	
	@Override
	public Command parse(String userCommand) {
		COMMANDTYPE commandType = COMMANDTYPE.INVALID;
		String labelName = "";
		boolean isHighPriority = false;
		boolean completeDetails = false;
		boolean isValidEdit = false;
		String commandDetails = "";
		int taskID = -1;
		TDTDateAndTime dateAndTime = new TDTDateAndTime();
		
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
				isHighPriority = isPriority(commandDetails);
				parts = commandDetails.split(" ");
				for (int i = 0; i < parts.length; i++) {
					String checkWord = parts[i];
					int end = i;
					if (TDTDateAndTime.checkDate(checkWord)) {
						completeDetails = true;
					} else if (TDTDateAndTime.checkTime(checkWord)) {
						completeDetails = true;
					} else if (TDTDateAndTime.checkDay(checkWord)!= 0) {
						completeDetails = true;
					}
					
					if (completeDetails) {
						if (i!=0) {
							if (prepositionWords.contains(parts[i-1])) {
								end = i-1;
							}
							commandDetails = getCommandDetails(end);
							remainingWords = getRemainingWords(end);
							dateAndTime = new TDTDateAndTime(remainingWords);
							break;
						}
					}
				}
				break;
			case DELETE :
				parts = remainingWords.split(" ");
				if (!isValidPartsLength()) {
					break;
				}
				// delete [label] / delete [taskID]
				if (parts.length == 1) {
					if (parts[0].matches("\\d+")) {
						taskID = getTaskID(0);
					} else {
						labelName = getLabelName(0);
					}
				}
				// delete [label][taskID] or delete [taskID][label] (assumes label to be one word)
				if (parts.length == 2) {
					if (parts[1].matches("\\d+")) {
						taskID = getTaskID(1);
						labelName = getLabelName(0);
					} else {
						taskID = getTaskID(0);
						labelName = getLabelName(1);
					}
				}
				break;
			case EDIT :
				parts = remainingWords.split(" ");
				if (!isValidPartsLength()) {
					break;
				}
				// [taskID][commandDetails]
				if (parts[0].matches("\\d+")) {
					taskID = getTaskID(0);
					if (parts.length > 1) {
						remainingWords = remainingWords.substring(parts[0].length()).trim();
						isValidEdit = true;
					}
				// [labelname][taskID][commandDetails]
				} else if (parts.length > 1) {
					if (parts[1].matches("\\d+")) {
						taskID = getTaskID(1);
						labelName = getLabelName(0);
						remainingWords = remainingWords.substring(parts[0].length()).trim();
						remainingWords = remainingWords.substring(parts[1].length()).trim();
						isValidEdit = true;
					}
				}
				
				if (isValidEdit) {
					// same as ADD
					commandDetails = remainingWords.trim();
					isHighPriority = isPriority(commandDetails);
					parts = commandDetails.split(" ");
					for (int i = 0; i < parts.length; i++) {
						String checkWord = parts[i];
						int end = i;
						if (TDTDateAndTime.checkDate(checkWord)) {
							completeDetails = true;
						} else if (TDTDateAndTime.checkTime(checkWord)) {
							completeDetails = true;
						} else if (TDTDateAndTime.checkDay(checkWord) != 0) {
							completeDetails = true;
						}
						
						if (completeDetails) {
							if (i!=0) {
								if (prepositionWords.contains(parts[i-1])) {
									end = i-1;
								}
								commandDetails = getCommandDetails(end);
								remainingWords = getRemainingWords(end);
								dateAndTime = new TDTDateAndTime(remainingWords);
								break;
							}
						}
					}
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
				parts = remainingWords.split(" ");
				if (!isValidPartsLength()) {
					break;
				}
				if (parts.length == 1) {
					if (parts[0].matches("\\d+")) {
						taskID = getTaskID(0);
					} else {
						labelName = getLabelName(0);
					}
				}
				if (parts.length == 2) {
					if (parts[1].matches("\\d+")) {
						taskID = getTaskID(1);
						labelName = getLabelName(0);
					} else {
						taskID = getTaskID(0);
						labelName = getLabelName(1);	
					}
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

	public String getLabelName(int i) {
		return parts[i];
	}

	public int getTaskID(int i) {
		int taskID;
		taskID = Integer.parseInt(parts[i]);
		return taskID;
	}

	private boolean isValidPartsLength() {
		if(parts.length == 0) {
			return false;
		}
		return true;
	}

	/**
	 * This function checks if the task added is of priority.
	 */
	public boolean isPriority(String commandDetails) {
		if (commandDetails.contains("!")) {
			return true;
		}
		return false;
	}
	
	/**
	 * This function gets the remaining words to be set as the date and time
	 */
	public String getRemainingWords(int end) {
		String remainingWordsTemp = ""; 
		for (int b = end; b < parts.length; b++) {
			remainingWordsTemp += (parts[b] + " ");
		}
		return remainingWordsTemp.trim();
	}

	public String getCommandDetails(int end) {
		String commandDetailsTemp = "";
		for (int a = 0; a < end; a++) {
			commandDetailsTemp += (parts[a] + " ");
		}
		return commandDetailsTemp.trim();
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
		if(userCommand.indexOf(" ") == -1) {
			userCommand = userCommand.replaceAll(("\\W"), "");
			return userCommand;
		} else {
			String userWord = userCommand.substring(0, userCommand.indexOf(" "));
			userWord = userWord.replaceAll(("\\W"), "");
			return userWord;
		}
	}

}
