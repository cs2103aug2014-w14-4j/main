package todothis;
// JUSTIN

public class TDTParser implements ITDTParser {
	private static final int longestString = 100;
	@Override
	public Command parse(String userCommand) {
		COMMANDTYPE commandType = COMMANDTYPE.INVALID;
		String labelName = "";
		String dueDate = "";
		String dueTime = "";
		boolean isHighPriority = false;
		String commandDetails = "";
		int taskID = -1;
		
		commandType = determineCommandType(getFirstWord(userCommand));
		String remainingWords = removeFirstWord(userCommand);
		switch(commandType) {
			case ADD :
				commandDetails = userCommand;
				if (commandDetails.contains("!")) {
					isHighPriority = true;
				}
				String[] words = new String[longestString];
				words = commandDetails.split(" ");
				for (int i = 0; i < words.length; i++) {
					if (words[i].contains("/")) {
						dueDate = words[i];
						dueTime = words[i+1];
						break;
					}
				}
				break;
			case DELETE :
				break;
			case EDIT :
				break;
			case LABEL :
				break;
			case SORT :
				break;
			case UNDO :
				break;
			case SEARCH :
				commandType = COMMANDTYPE.SEARCH;
				commandDetails = remainingWords;
				break;
			case DISPLAY :
				String checkDisplay[] = remainingWords.split(" ");
				if (checkDisplay.length > 1) {
					commandType = COMMANDTYPE.INVALID;
					break;
				}
				commandType = COMMANDTYPE.DISPLAY;
				labelName = remainingWords;
				break;
			case HIDE :
				String checkHide[] = remainingWords.split(" ");
				if (checkHide.length > 1) {
					commandType = COMMANDTYPE.INVALID;
					break;
				}
				commandType = COMMANDTYPE.HIDE;
				labelName = remainingWords;
				break;
			case DONE :
				commandType = COMMANDTYPE.DONE;
				String[] tempWords = remainingWords.split(" ");
				String word1 = tempWords[0];
				String word2 = tempWords[1];
				// [label][taskID]
				if (Character.isDigit(word1.charAt(0))) {
					taskID = Integer.parseInt(word1);
					labelName = tempWords[1];
				// [taskID][label]
				} else if (Character.isDigit(word2.charAt(0))) {
					taskID = Integer.parseInt(word2);
					labelName = tempWords[0];
				} else {
					commandType = COMMANDTYPE.INVALID;
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
