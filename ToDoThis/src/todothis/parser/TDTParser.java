package todothis.parser;

import java.util.ArrayList;

import todothis.command.AddCommand;
import todothis.command.Command;
import todothis.command.DeleteCommand;
import todothis.command.DisplayCommand;
import todothis.command.DoneCommand;
import todothis.command.EditCommand;
import todothis.command.HideCommand;
import todothis.command.LabelCommand;
import todothis.command.SearchCommand;
import todothis.command.UndoCommand;
import todothis.logic.TDTDateAndTime;

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
		String dateAndTimeParts = "";
		
		ArrayList<String> prepositionWords = new ArrayList<String>();
		prepositionWords.add("on");
		prepositionWords.add("at");
		prepositionWords.add("by");
		prepositionWords.add("from");
		prepositionWords.add("about");
		prepositionWords.add("to");
		prepositionWords.add("-");
		prepositionWords.add("until");
		prepositionWords.add("till");
		prepositionWords.add("next");
		prepositionWords.add("following");

		commandType = determineCommandType(getFirstWord(userCommand));
		String remainingWords = removeFirstWord(userCommand);
		
		switch(commandType) {
			case ADD :
				remainingWords = userCommand;
				isHighPriority = isPriority(remainingWords);
				if (isHighPriority) {
					remainingWords = remainingWords.replace("!", "");
				}
				parts = remainingWords.split(" ");
				for (int i = 0; i < parts.length; i++) {
					String checkWord = parts[i];
					if (TDTDateAndTime.checkDate(checkWord)) {
						completeDetails = true;
					} else if (TDTDateAndTime.checkTime(checkWord)) {
						completeDetails = true;
					} else if (TDTDateAndTime.checkDay(checkWord)!=0) {
						completeDetails = true;
					} else if (TDTDateAndTime.checkMonth(checkWord)!=0) {
						if ((i>0) && parts[i-1].contains("\\d+")) {
							if ( (i+1 < parts.length) && (parts[i+1].contains("\\d+"))) {
								dateAndTimeParts = addDetails(dateAndTimeParts, parts[i-1]);
								dateAndTimeParts = addDetails(dateAndTimeParts, parts[i]);
								dateAndTimeParts = addDetails(dateAndTimeParts, parts[i+1]);
								commandDetails = removeDetails(commandDetails, i);
								checkWord = "";
								i++;
							}
						}
					}

					if (completeDetails) {
						if (i>0) {
							if (prepositionWords.contains(parts[i-1])) {
								dateAndTimeParts = addDetails(dateAndTimeParts, parts[i-1]);
								commandDetails = removeDetails(commandDetails, i);
							} 
								
						} 
						dateAndTimeParts = addDetails(dateAndTimeParts, checkWord);
						completeDetails = false;
					} else {
						commandDetails = addDetails(commandDetails, checkWord);
					}
				}
				dateAndTime = new TDTDateAndTime(dateAndTimeParts);
				return new AddCommand(labelName,taskID,commandDetails,dateAndTime, 
						isHighPriority);
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
				return new DeleteCommand(labelName, taskID);
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
					remainingWords = remainingWords.trim();
					isHighPriority = isPriority(remainingWords);
					if (isHighPriority) {
						remainingWords.replace("!", "");
					}
					parts = remainingWords.split(" ");
					for (int i = 0; i < parts.length; i++) {
						String checkWord = parts[i];
						if (TDTDateAndTime.checkDate(checkWord)) {
							completeDetails = true;
						} else if (TDTDateAndTime.checkTime(checkWord)) {
							completeDetails = true;
						} else if (TDTDateAndTime.checkDay(checkWord)!=0) {
							completeDetails = true;
						} else if (TDTDateAndTime.checkMonth(checkWord)!=0) {
							if ((i>0) && parts[i-1].contains("\\d+")) {
								if ( (i+1 < parts.length) && (parts[i+1].contains("\\d+"))) {
									dateAndTimeParts = addDetails(dateAndTimeParts, parts[i-1]);
									dateAndTimeParts = addDetails(dateAndTimeParts, parts[i]);
									dateAndTimeParts = addDetails(dateAndTimeParts, parts[i+1]);
									commandDetails = removeDetails(commandDetails, i);
									checkWord = "";
									i++;
								}
							}
						}

						if (completeDetails) {
							if (i>0) {
								if (prepositionWords.contains(parts[i-1])) {
									dateAndTimeParts = addDetails(dateAndTimeParts, parts[i-1]);
									commandDetails = removeDetails(commandDetails, i);
								} 
									
							} 
							dateAndTimeParts = addDetails(dateAndTimeParts, checkWord);
							completeDetails = false;
						} else {
							commandDetails = addDetails(commandDetails, checkWord);
						}
					}
					dateAndTime = new TDTDateAndTime(dateAndTimeParts);
				}
				return new EditCommand(labelName,taskID,commandDetails,dateAndTime, 
						isHighPriority);
			case LABEL :
				commandType = COMMANDTYPE.LABEL;
				labelName = remainingWords;
				return new LabelCommand(labelName);
			case SORT :
				commandType = COMMANDTYPE.SORT;
				break;
			case UNDO :
				commandType = COMMANDTYPE.UNDO;
				return new UndoCommand();
			case SEARCH :
				commandType = COMMANDTYPE.SEARCH;
				commandDetails = remainingWords;
				return new SearchCommand(commandDetails);
			case DISPLAY :
				String checkDisplay[] = remainingWords.split(" ");
				commandType = COMMANDTYPE.DISPLAY;
				labelName = checkDisplay[0];
				return new DisplayCommand(labelName);
			case HIDE :
				String checkHide[] = remainingWords.split(" ");
				commandType = COMMANDTYPE.HIDE;
				labelName = checkHide[0];
				return new HideCommand(labelName);
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
				return new DoneCommand(labelName, taskID);
			case INVALID :
				commandType = COMMANDTYPE.INVALID;
				break;
			default:
				//Will not reach here
				break;
		}
		return null;
	}

	public String removeDetails(String commandDetails, int i) {
		int last = commandDetails.length()-(parts[i-1].length());
		commandDetails = commandDetails.substring(0, last);
		return commandDetails.trim();
	}

	public String addDetails(String commandDetails, String checkWord) {
		commandDetails += " " + checkWord;
		return commandDetails.trim();
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
