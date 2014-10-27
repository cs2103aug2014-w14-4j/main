package todothis.parser;

import java.util.ArrayList;

//import java.util.logging.Level;
//import java.util.logging.Logger;
import todothis.command.AddCommand;
import todothis.command.Command;
import todothis.command.DeleteCommand;
import todothis.command.DisplayCommand;
import todothis.command.DoneCommand;
import todothis.command.EditCommand;
import todothis.command.HideCommand;
import todothis.command.LabelCommand;
import todothis.command.RedoCommand;
import todothis.command.RemindCommand;
import todothis.command.SearchCommand;
import todothis.command.UndoCommand;
import todothis.logic.TDTDateAndTime;

public class TDTParser implements ITDTParser {

	private COMMANDTYPE commandType = COMMANDTYPE.INVALID;
	private String labelName;
	private boolean isHighPriority;
	private String commandDetails;
	private int taskID;
	private TDTDateAndTime dateAndTime;
	private boolean isSkipNextWord;
	private boolean[] isCommandDetails;
	private String remainingWords;
	private String[] parts;
	private String dateAndTimeParts = "";
	private ArrayList<String> prepositionWordsArr;
	private ArrayList<String> dayWordsArr;
	//private Logger logger = Logger.getLogger("TDTParser");
	
	public Command parse(String userCommand) {
	//	logger.log(Level.INFO, "start parsing");
		this.setCommandType(COMMANDTYPE.INVALID);
		this.setLabelName("");
		this.setIsHighPriority(false);
		this.setSkipNextWord(false);
		this.setCommandDetails("");
		this.setTaskID(-1);
		this.setPrepositionWords();
		this.setDayWordsArr();
		dateAndTimeParts = "";

		this.setCommandType(determineCommandType(getFirstWord(userCommand)));
		this.setRemainingWords(removeFirstWord(userCommand));
		switch(getCommandType()) {
			case ADD :
				add(userCommand);
				return new AddCommand(getCommandDetails(), getDateAndTime(), getIsHighPriority());
			case DELETE :
				delete();
				return new DeleteCommand(getLabelName(), getTaskID());
			case EDIT :
				edit(userCommand);	
				return new EditCommand(getLabelName(), getTaskID(),getCommandDetails(), getDateAndTime(), 
						getIsHighPriority());
			case LABEL :
				label();
				return new LabelCommand(getLabelName());
			case UNDO :
				return new UndoCommand();
			case REDO :
				return new RedoCommand();
			case SEARCH :
				search();
				return new SearchCommand(getCommandDetails());
			case DISPLAY :
				display();
				return new DisplayCommand(getCommandDetails());
			case HIDE :
				hide();
				return new HideCommand(getCommandDetails());
			case DONE :
				done();
				return new DoneCommand(getLabelName(), getTaskID());
			case REMIND :
				remind();
				return new RemindCommand(getLabelName(), getTaskID(), getCommandDetails());
			case INVALID :
				break;
			default:
				break;
		}
	//logger.log(Level.INFO, "end of parsing");
		return null;
	}
	
	/**
	 * Default command is assumed to be ADD
	 */
	private COMMANDTYPE determineCommandType(String commandTypeString) {
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
		} else if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMANDTYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return COMMANDTYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return COMMANDTYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("done")) {
			return COMMANDTYPE.DONE;
		} else if (commandTypeString.equalsIgnoreCase("redo")) {
			return COMMANDTYPE.REDO;
		} else if (commandTypeString.equalsIgnoreCase("remind")) {
			return COMMANDTYPE.REMIND;
		}else {
			return COMMANDTYPE.ADD;
		}
	}
	
//---------------------------------- Main Command Methods -------------------------------------
	private void add(String userCommand) {
		if (getFirstWord(userCommand).equalsIgnoreCase("add")) {
			setRemainingWords(removeFirstWord(userCommand));
		} else {
			this.setRemainingWords(userCommand);
		}

		parts = getRemainingWords().split(" ");
		isCommandDetails = new boolean[parts.length];
		int invertedCommas = 0;
		for (int i = 0; i < parts.length; i++) {
			isCommandDetails[i] = true;
			String checkWord = parts[i];
			if (checkWord.contains("\"")) {
				invertedCommas++;
			}
			if (invertedCommas!= 0) {
				specialAdd(checkWord , i);
				if (invertedCommas == 2) {
					invertedCommas = 0;
				}
			} else {
				usualAdd(i, checkWord);
				if (isSkipNextWord()) {
					i++;
					setSkipNextWord(false);
				}
			}
		}
		completeAllDetails();
	}

	private void done() {
		parts = getRemainingWords().split(" ");
		if (isValidPartsLength()) {
			if (parts.length == 1) {
				if (parts[0].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[0]));
				} else {
					setLabelName(parts[0]);
				}
			}
			if (parts.length == 2) {
				if (parts[1].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[1]));
					setLabelName(parts[0]);
				} else {
					setTaskID(Integer.parseInt(parts[0]));
					setLabelName(parts[1]);
				}
			}
		}
	}
	
	private void remind() {
		parts = getRemainingWords().split(" ");
		setTaskID(Integer.parseInt(parts[0]));
		String details = "";
		for(int i = 1 ; i < parts.length; i++) {
			details = details + parts[i] + " ";
		}
		setCommandDetails(details);
		
	}

	private void edit(String userCommand) {
		boolean isValidEdit = false;
		parts = getRemainingWords().split(" ");
		if (isValidPartsLength()) {
			if (parts[0].matches("\\d+")) {
				setTaskID(Integer.parseInt(parts[0]));
				if (parts.length > 1) {
					setRemainingWords(getRemainingWords().substring(parts[0].length()).trim());
					isValidEdit = true;
				}
			} else if (parts.length > 1) {
				if (parts[1].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[1]));
					setLabelName(parts[0]);
					setRemainingWords(getRemainingWords().substring(parts[0].length()).trim());
					setRemainingWords(getRemainingWords().substring(parts[1].length()).trim());
					isValidEdit = true;
				}
			}
		}	
		if (isValidEdit) {
			add(getRemainingWords());
		}
	}

	private void delete() {
		parts = getRemainingWords().split(" ");
		if (isValidPartsLength()) {
			if (parts.length == 1) {
				if (parts[0].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[0]));
				} else {
					setLabelName(parts[0]);
				}
			}
			if (parts.length == 2) {
				if (parts[1].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[1]));
					setLabelName(parts[0]);
				} else {
					setTaskID(Integer.parseInt(parts[0]));
					setLabelName(parts[1]);
				}
			}
		}
	}
	
	private void display() {
		setCommandDetails(getRemainingWords());
	}
	
	private void hide() {
		setCommandDetails(getRemainingWords());
	}

	private void search() {
		setCommandDetails(getRemainingWords());
	}

	private void label() {
		setLabelName(getRemainingWords());
	}

//------------------------------ Other Methods -----------------------------------------------
	/**
	 * This function removes the first word of the userCommand. 
	 */
	private static String removeFirstWord(String userCommand) {
		return userCommand.replaceFirst(getFirstWord(userCommand), "").trim();
	}

	/**
	 * This function gets the first word / Command word of the userCommand
	 */
	private static String getFirstWord(String userCommand) {
		if(userCommand.indexOf(" ") == -1) {
			userCommand = userCommand.replaceAll(("\\W"), "");
			return userCommand;
		} else {
			String userWord = userCommand.substring(0, userCommand.indexOf(" "));
			if (!userWord.contains("\"")) { 
				userWord = userWord.replaceAll(("\\W"), "");
			}
			return userWord;
		}
	}
	
	/**
	 * This function fills in all the correct commandDetails and the DateAndTimeParts
	 * after the whole user input has been processed
	 */
	private void completeAllDetails() {
		for (int i = 0; i<isCommandDetails.length; i++) {
			if (isCommandDetails[i] == true) {
				commandDetails += " " + parts[i];
			} else {
				if (parts[i].contains("~")) {
					dateAndTimeParts += parts[i];
				} else {
					dateAndTimeParts += " " + parts[i];
				}
			}
		}
		setCommandDetails(commandDetails);
		setDateAndTime(new TDTDateAndTime(dateAndTimeParts));
	}

	/**
	 * This function does the ADD for words contained inside the " " which will
	 * all be included commandDetails.
	 * @param i 
	 */
	private void specialAdd(String checkWord, int i) {
		if (checkWord.contains("\"")) {
			parts[i] = checkWord.replace("\"", "");
		} 
	}

	/**
	 * This function does the ADD normally. It checks if the word is a date/time/day/month
	 * and is then processed accordingly 
	 */
	private int usualAdd(int i, String checkWord) {
		isPriority(checkWord);
		if (TDTDateAndTime.checkDate(checkWord) || TDTDateAndTime.checkTime(checkWord)) {
			completeDateTimeDetails(i);
		} else if (TDTDateAndTime.checkDay(checkWord)!=0) {
			if (TDTDateAndTime.checkDay(checkWord) == 10) {
				completeSpecialDayDetails(i, checkWord);
			} else {
				completeDayDetails(i);
			}
		} else if (TDTDateAndTime.checkMonth(checkWord)!=0) {
			setSkipNextWord(false);
			completeMonthDetails(i);
		} 
		return i;
	}
	
	/**
	 * This function completes the commandDetails for an input that contains a date format
	 * such as 4 August 2014 and 4 Aug. Date Month Year / Date Month
	 */
	private void completeMonthDetails(int i) {
		if ((i>0) && parts[i-1].matches("\\d+")) {
			if ((i>1) && getPrepositionWords().contains(parts[i-2])) {
				isCommandDetails[i-2] = false;
			}
			isCommandDetails[i] = false;
			isCommandDetails[i-1] = false;
			parts[i] = "~" + parts[i];
			if ((i+1 < parts.length) && (parts[i+1].matches("\\d+"))) {
				isCommandDetails[i+1] = false;
				parts[i+1] = "~" + parts[i+1];
				setSkipNextWord(true);
			}
		} 
	}
	
	/**
	 * This function completes the commandDetails for words that is of a date or time. 
	 */
	private void completeDateTimeDetails(int i) {
		if (i>0) {	
			if (getPrepositionWords().contains(parts[i-1])) {
				isCommandDetails[i-1] = false;
			}
		} 
		isCommandDetails[i] = false;
	}
	
	/**
	 * This function completes the commandDetails for a words of days such as
	 * Monday Tuesday etc
	 */
	private void completeDayDetails(int i) {
		if (i>0) {
			if (getPrepositionWords().contains(parts[i-1])) {
				isCommandDetails[i-1] = false;
				// this / next / following
				if (getDayWordsArr().contains(parts[i-1])) {
					if ((i>1) && getPrepositionWords().contains(parts[i-2])) {
						isCommandDetails[i-2] = false;
					}
					if ((i>2) && getPrepositionWords().contains(parts[i-3])) {
						isCommandDetails[i-3] = false;
					}
				} 
			}
		} 
		isCommandDetails[i] = false;
	}
	
	/**
	 * This function checks for the words 'next' and 'following' before the word 'day'
	 */
	private void completeSpecialDayDetails(int i, String checkWord) {
		if (parts[i-1].equals("next") || parts[i-1].equals("following")) {
			completeDayDetails(i);
		} 
	}
	
	/**
	 * This function checks if the array parts is of valid length (!=0)
	 */
	private boolean isValidPartsLength() {
		if (parts.length == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * This function checks if the command input is of priority
	 * Presence of '!' shows priority. 
	 */
	private void isPriority(String word) {
		if (word.contains("!")) {
			remainingWords = word.replace("!", "");
			setIsHighPriority(true);
		}
	}
	
//--------------------------------- Getters and Setters -----------------------------------------
	public ArrayList<String> getPrepositionWords() {
		return prepositionWordsArr;
	}

	/**
	 * This function sets the list of 'Preposition' words used in
	 * day, date, time checking.
	 */
	public void setPrepositionWords() {
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
		prepositionWords.add("this");
		prepositionWords.add("the");
		this.prepositionWordsArr = prepositionWords;
	}
	
	private ArrayList<String> getDayWordsArr() {
		return dayWordsArr;
	}

	/**
	 * This function sets the list of words used in the checking of days.
	 */
	private void setDayWordsArr() {
		ArrayList<String> dayWordsArr = new ArrayList<String>();
		dayWordsArr.add("this");
		dayWordsArr.add("the");
		dayWordsArr.add("next");
		dayWordsArr.add("following");
		this.dayWordsArr = dayWordsArr;
	}
	
	private String getRemainingWords() {
		return remainingWords;
	}

	private void setRemainingWords(String remainingWords) {
		this.remainingWords = remainingWords;
	}

	public COMMANDTYPE getCommandType() {
		return commandType;
	}

	private void setCommandType(COMMANDTYPE commandType) {
		this.commandType = commandType;
	}

	public String getLabelName() {
		return labelName;
	}

	private void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public boolean getIsHighPriority() {
		return isHighPriority;
	}

	private void setIsHighPriority(boolean isHighPriority) {
		this.isHighPriority = isHighPriority;
	}

	public String getCommandDetails() {
		return commandDetails;
	}

	private void setCommandDetails(String commandDetails) {
		this.commandDetails = commandDetails;
	}

	public int getTaskID() {
		return taskID;
	}

	private void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	private TDTDateAndTime getDateAndTime() {
		return dateAndTime;
	}

	private void setDateAndTime(TDTDateAndTime dateAndTime) {
		this.dateAndTime = dateAndTime;
	}
	
	private boolean isSkipNextWord() {
		return isSkipNextWord;
	}

	private void setSkipNextWord(boolean isSkipNextWord) {
		this.isSkipNextWord = isSkipNextWord;
	}
}