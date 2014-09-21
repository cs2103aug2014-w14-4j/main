package todothis;


public class TDTParser implements ITDTParser {

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
				break;
			case DISPLAY :
				break;
			case HIDE :
				break;
			case DONE :
				break;
			case INVALID :
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
