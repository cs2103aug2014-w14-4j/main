package todothis;
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
					if (parts[i].contains("/")) {
						dueDate = parts[i];
					}
					if (parts[i].contains(".")) {
						dueTime = parts[i];
					}
				}
				break;
			case DELETE :
				parts = remainingWords.split(" ");
				String lastWord = parts[parts.length - 1];
				if (lastWord.matches("\\d+")) {
					taskID = Integer.parseInt(lastWord);
					labelName = remainingWords.substring(0, remainingWords.lastIndexOf(" "));
				}
				else {
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
				// label name must be one word ( no spaces )
				String checkLabel[] = remainingWords.split(" ");
				commandType = COMMANDTYPE.LABEL;
				labelName = checkLabel[0];
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
				// the word after the command will be the labelName
				String checkDisplay[] = remainingWords.split(" ");
				commandType = COMMANDTYPE.DISPLAY;
				labelName = checkDisplay[0];
				break;
			case HIDE :
				// the word after the command will be the labelName
				String checkHide[] = remainingWords.split(" ");
				commandType = COMMANDTYPE.HIDE;
				labelName = checkHide[0];
				break;
			case DONE :
				commandType = COMMANDTYPE.DONE;
				String[] tempWords = remainingWords.split(" ");
				String word1 = tempWords[0];
				String word2 = tempWords[1];
				// [label][taskID]
				if (word1.matches("\\d+")) {
					taskID = Integer.parseInt(word1);
					labelName = tempWords[1];
					// [taskID][label]
				} else if (word2.matches("\\d+")) {
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
