package todothis.parser;

import java.util.ArrayList;

//import java.util.logging.Level;
//import java.util.logging.Logger;
import todothis.command.AddCommand;
import todothis.command.Command;
import todothis.command.DeleteCommand;
import todothis.command.ShowCommand;
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
	private int invertedCommas;
	private int counter;
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
		this.setInvertedCommas(0);
		this.setCounter(0);
		dateAndTimeParts = "";

		this.setCommandType(determineCommandType(getFirstWord(userCommand.trim())));
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
			case SHOW :
				show();
				return new ShowCommand(getCommandDetails());
			case HIDE :
				hide();
				return new HideCommand(getCommandDetails());
			case DONE :
				done();
				return new DoneCommand(getLabelName(), getTaskID());
			case REMIND :
				remind();
				return new RemindCommand(getLabelName(), getTaskID(), getCommandDetails());
			case EXIT :
				exit();
				break;
			case HELP :
				help();
				break;
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
		} else if (commandTypeString.equalsIgnoreCase("show")) {
			return COMMANDTYPE.SHOW;
		} else if (commandTypeString.equalsIgnoreCase("delete") || commandTypeString.equalsIgnoreCase("de")) {
			return COMMANDTYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("label") || commandTypeString.equalsIgnoreCase("la")) {
			return COMMANDTYPE.LABEL;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return COMMANDTYPE.EDIT;
		} else if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMANDTYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("search") || commandTypeString.equalsIgnoreCase("se")) {
			return COMMANDTYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return COMMANDTYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("done")) {
			return COMMANDTYPE.DONE;
		} else if (commandTypeString.equalsIgnoreCase("redo")) {
			return COMMANDTYPE.REDO;
		} else if (commandTypeString.equalsIgnoreCase("remind") || commandTypeString.equalsIgnoreCase("rem")) {
			return COMMANDTYPE.REMIND;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
			return COMMANDTYPE.EXIT;
		} else if (commandTypeString.equalsIgnoreCase("help")) {
			return COMMANDTYPE.HELP;
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
		setInvertedCommas(0);
		setCounter(0);
		for (int i = 0; i < parts.length; i++) {
			isCommandDetails[i] = true;
			String checkWord = parts[i];
			if (checkWord.contains("\"") && (getInvertedCommas() == 0)) {
				setInvertedCommas(1);
			}
			if (invertedCommas!= 0) {
				setCounter(getCounter()+1);
				specialAdd(checkWord , i);
				if (getInvertedCommas() == 2) {
					setInvertedCommas(0);
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
			} else if (parts.length == 2) {
				if (parts[1].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[1]));
					setLabelName(parts[0]);
				} else if (parts[0].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[0]));
					setLabelName(parts[1]);
				} else {
					setTaskID(-1);
					setLabelName(" ");
				}
			} else {
				setTaskID(-1);
				setLabelName(" ");
			}
		}
	}
	
	private void remind() {
		String details = "";
		parts = getRemainingWords().split(" ");
		if (isValidPartsLength()) {
			if (parts[0].matches("\\d+")) {
				setTaskID(Integer.parseInt(parts[0]));
				for (int i=1; i<parts.length; i++) {
					details += parts[i] + " ";
				}
				setCommandDetails(details.trim());
			} else if (parts.length > 1) {
				if (parts[1].matches("\\d+")) {
					setLabelName(parts[0]);
					setTaskID(Integer.parseInt(parts[1]));
					for (int i=2; i<parts.length; i++) {
						details += parts[i] + " ";
					}
					setCommandDetails(details.trim());
				}
			}
		}
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
			else if (parts.length == 2) {
				if (parts[1].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[1]));
					setLabelName(parts[0]);
				} else if (parts[0].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[0]));
					setLabelName(parts[1]);
				} else {
					setTaskID(-1);
					setLabelName(" ");
				}
			} else {
				setTaskID(-1);
				setLabelName(" ");
			}
		}
	}
	
	private void show() {
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

	private void help() {
		// TODO Auto-generated method stub
		
	}

	private void exit() {
		// TODO Auto-generated method stub
		
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
	 */
	private void specialAdd(String checkWord, int i) {
		if (checkWord.contains("\"")) {
			String check = checkWord.replaceAll("[^\"]", "");
			// words after the first "
			if (getCounter() > 1 && getInvertedCommas() > 0) {
				// second "
				if (check.length()== 1 && getInvertedCommas()== 1) {
					setInvertedCommas(0);
					setCounter(0);
				// more than 1 " in a word
				} else if (check.length() > 1) {
					int num = check.length()%2;
					if (num==0) {
						setInvertedCommas(1);
					} else if (num==1) {
						setInvertedCommas(0);
						setCounter(0);
					}
				}
			// first word has 2 "
			} else if ( getCounter() == 1 && (check.length()==2)) {
				setInvertedCommas(0);
				setCounter(0);
			}
			parts[i] = checkWord.replace("\"", "");
		} 
	}

	/**
	 * This function does the ADD normally. It checks if the word is a date/time/day/month
	 * and is then processed accordingly 
	 */
	private int usualAdd(int i, String checkWord) {
		isPriority(checkWord , i);
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
		int firstOccurance = 0;
		for (int j=i-1; j>=0; j--) {
			if (parts[j].equals("next") || parts[j].equals("following") ) {
				isCommandDetails[j] = false;
			} else {
				firstOccurance = j+1;
				break;
			}
		}
		if ((firstOccurance > 0) 
				&& getPrepositionWords().contains(parts[firstOccurance-1])) {
			isCommandDetails[firstOccurance-1] = false;
			if ((firstOccurance>1) 
					&& getPrepositionWords().contains(parts[firstOccurance-2])) {
				isCommandDetails[firstOccurance-2] = false;
			}
			if ((firstOccurance>2) 
					&& getPrepositionWords().contains(parts[firstOccurance-3])) {
				isCommandDetails[firstOccurance-3] = false;
			}
		}
		isCommandDetails[i] = false;
	}

	/**
	 * This function checks for the words 'next' and 'following' before the word 'day'
	 */
	private void completeSpecialDayDetails(int i, String checkWord) {
		int firstOccurance = 0;
		for (int j=i-1; j>=0; j--) {
			if (parts[j].equals("next") || parts[j].equals("following") ) {
				isCommandDetails[j] = false;
			} else {
				firstOccurance = j+1;
				break;
			}
		}
		if (firstOccurance!=i) {
			isCommandDetails[i] = false;
			if ((firstOccurance > 0) 
				&& getPrepositionWords().contains(parts[firstOccurance-1])) {
				isCommandDetails[firstOccurance-1] = false;
					if ((firstOccurance > 1) 
							&& getPrepositionWords().contains(parts[firstOccurance-2])) {
							isCommandDetails[firstOccurance-2] = false;
					}
			} 
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
	 * '!' will not appear in commandDetails. 
	 */
	private void isPriority(String word, int i) {
		if (word.contains("!")) {
			parts[i] = word.replace("!", "");
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

	public int getInvertedCommas() {
		return invertedCommas;
	}

	public void setInvertedCommas(int invertedCommas) {
		this.invertedCommas = invertedCommas;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
}