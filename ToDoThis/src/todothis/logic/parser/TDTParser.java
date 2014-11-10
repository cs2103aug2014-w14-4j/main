//@author A0115933H
package todothis.logic.parser;

import java.util.ArrayList;
import todothis.commons.TDTDateAndTime;
import todothis.commons.TDTDateMethods;
import todothis.commons.TDTTimeMethods;
import todothis.logic.command.AddCommand;
import todothis.logic.command.Command;
import todothis.logic.command.DeleteCommand;
import todothis.logic.command.DoneCommand;
import todothis.logic.command.EditCommand;
import todothis.logic.command.ExitCommand;
import todothis.logic.command.HelpCommand;
import todothis.logic.command.HideCommand;
import todothis.logic.command.LabelCommand;
import todothis.logic.command.RedoCommand;
import todothis.logic.command.RemindCommand;
import todothis.logic.command.SearchCommand;
import todothis.logic.command.ShowCommand;
import todothis.logic.command.UndoCommand;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TDTParser implements ITDTParser {

	private COMMANDTYPE commandType = COMMANDTYPE.INVALID;
	private String labelName;
	private boolean isHighPriority;
	private String commandDetails;
	private int taskID;
	private TDTDateAndTime dateAndTime;
	private String dateAndTimeParts;
	private boolean isSkipNextWord;
	private boolean[] isCommandDetails;
	private String remainingWords;
	private String[] parts;
	private ArrayList<String> connectorWordsArr;
	private int quotationMarks;
	private int quotationCounter;
	private Logger logger = Logger.getLogger("TDTParser");

	/**
	 * This function parses the user's command according to the type of command it is. 
	 */
	public Command parse(String userCommand) {
		assert(userCommand!= null);
		logger.log(Level.INFO, "start parsing");
		setInitalConditions();
		int length = userCommand.split(" ").length;
		String firstWord = getFirstWord(userCommand.trim());
		setCommandType(determineCommandType(firstWord, length));
		setRemainingWords(removeFirstWord(userCommand));
		
		switch(getCommandType()) {
		case ADD :
			add(userCommand);
			return new AddCommand(getCommandDetails(), getDateAndTime(), getIsHighPriority());
		case DELETE :
			delete();
			return new DeleteCommand(getLabelName(), getTaskID());
		case EDIT :
			edit(userCommand);	
			return new EditCommand(getLabelName(), getTaskID(),getCommandDetails(), 
					getDateAndTime(), getIsHighPriority());
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
		case HELP :
			help();
			return new HelpCommand(getCommandDetails());
		case EXIT :
			return new ExitCommand();
		case INVALID :
			break;
		default:
			break;
		}
		logger.log(Level.INFO, "end of parsing");
		return null;
	}
	
	/**
	 * This function determines the command type of the command entered by the user.
	 * Default command is ADD
	 */
	private COMMANDTYPE determineCommandType(String commandTypeString, int length) {
		if (commandTypeString == null) {
			return COMMANDTYPE.INVALID;
		} else if (commandTypeString.equalsIgnoreCase("hide")) {
			return COMMANDTYPE.HIDE;
		} else if (commandTypeString.equalsIgnoreCase("show")) {
			return COMMANDTYPE.SHOW;
		} else if ((commandTypeString.equalsIgnoreCase("delete") 
				|| commandTypeString.equalsIgnoreCase("de")) && length < 4) {
			return COMMANDTYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("label") 
				|| commandTypeString.equalsIgnoreCase("la") && length == 2) {
			return COMMANDTYPE.LABEL;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return COMMANDTYPE.EDIT;
		} else if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMANDTYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("search") || commandTypeString.equalsIgnoreCase("se")) {
			return COMMANDTYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("undo") && length == 1) {
			return COMMANDTYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("done") && length < 4) {
			return COMMANDTYPE.DONE;
		} else if (commandTypeString.equalsIgnoreCase("redo") && length == 1) {
			return COMMANDTYPE.REDO;
		} else if (commandTypeString.equalsIgnoreCase("remind") || commandTypeString.equalsIgnoreCase("rem")) {
			return COMMANDTYPE.REMIND;
		} else if (commandTypeString.equalsIgnoreCase("exit") && length == 1) {
			return COMMANDTYPE.EXIT;
		} else if (commandTypeString.equalsIgnoreCase("help") && length < 3) {
			return COMMANDTYPE.HELP;
		} else {
			return COMMANDTYPE.ADD;
		}
	}

	//---------------------------------- Main Command Methods -------------------------------------
	private void add(String userCommand) {
		setQuotationMarks(0);
		setQuotationCounter(0);
		checkFirstWord(userCommand);
		parts = getRemainingWords().split(" ");
		isCommandDetails = new boolean[parts.length];

		for (int i=0; i<parts.length; i++) {
			isCommandDetails[i] = true;
			String checkWord = parts[i];
			if (hasQuotationMarks(checkWord, i)) {
				quotationAdd(checkWord , i);
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
			if (parts.length == 1) { 		// Only taskID OR labelName 
				completeTaskIdOrLabelName();
			} else if (parts.length == 2) { // Both taskID and labelName 
				completeTaskIdAndLabelName();
			} 
		}
	}

	private void remind() {
		String details = "";
		parts = getRemainingWords().split(" ");
		if (isValidPartsLength()) {
			if (parts.length == 1) { 			// Only <taskID>
				if (parts[0].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[0]));
				}
			} else if (parts.length > 1) {
				if (parts[0].matches("\\d+")) { // <taskID> <date and time>
					setTaskID(Integer.parseInt(parts[0]));
					if (parts.length > 1) {
						for (int i=1; i<parts.length; i++) {
							details += parts[i] + " ";
						}
					}
				} else if (parts[1].matches("\\d+")) { // <labelName> <taskID> <date and time>
					setLabelName(parts[0]);
					setTaskID(Integer.parseInt(parts[1]));
					if (parts.length > 2) {
						for (int i=2; i<parts.length; i++) {
							details += parts[i] + " ";
						}
					}
				}
				setCommandDetails(details.trim());
			}
		}
	}

	private void edit(String userCommand) {
		boolean isValidEdit = false;
		parts = getRemainingWords().split(" ");
		if (isValidPartsLength()) { 
			if (parts[0].matches("\\d+")) { // <taskID> <task details>
				setTaskID(Integer.parseInt(parts[0]));
				if (parts.length > 1) {
					setRemainingWords(getRemainingWords().substring(parts[0].length()).trim());
					isValidEdit = true;
				}
			} else if (parts.length > 1) { // <labelName> <taskID> <task details>
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
			if (parts.length == 1) { 		// Only taskID or labelName
				completeTaskIdOrLabelName();
			} else if (parts.length == 2) { // Both taskID and labelName 
				completeTaskIdAndLabelName();
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
		setCommandDetails(getRemainingWords());
	}

	//------------------------------ Other Methods -----------------------------------------------
	/**
	 * This function set the variables to the respective initial conditions they should be 
	 * before a command is parsed.
	 */
	private void setInitalConditions() {
		setCommandType(COMMANDTYPE.INVALID);
		setLabelName("");
		setIsHighPriority(false);
		setSkipNextWord(false);
		setCommandDetails("");
		setTaskID(-1);
		setConnectorWords();
		setQuotationMarks(0);
		setDateAndTimeParts("");
	}

	/**
	 * This function removes the first word of the userCommand.
	 * @param userCommand
	 * @return String without the first word
	 */
	private static String removeFirstWord(String userCommand) {
		return userCommand.replaceFirst(getFirstWord(userCommand), "").trim();
	}

	/**
	 * This function gets the first word of the userCommand
	 * @param userCommand
	 * @return String of the first word.
	 */
	private static String getFirstWord(String userCommand) {
		if(userCommand.indexOf(" ") == -1) {
			userCommand = userCommand.replaceAll(("\\W"), "");
			return userCommand;
		} else {
			String userCommandTemp = userCommand.substring(0, userCommand.indexOf(" "));
			if (!userCommandTemp.contains("\"")) { 
				userCommandTemp = userCommandTemp.replaceAll(("\\W"), "");
			}
			return userCommandTemp;
		}
	}

	/**
	 * This function checks if the word contains a quotation mark.
	 * @param checkWord
	 * @param i
	 * @return boolean. True if present, False is not. 
	 */
	private boolean hasQuotationMarks(String checkWord, int i) {
		if (checkWord.contains("\"") && (getQuotationMarks() == 0)) {
			setQuotationMarks(1);
		}
		if (getQuotationMarks()!=0) {
			setQuotationCounter(getQuotationCounter()+1);
			return true;
		}
		return false;
	}

	/**
	 * This function checks if the first word of the user command is ADD and 
	 * removes the word if it is present.
	 * @param userCommand
	 */
	private void checkFirstWord(String userCommand) {
		if (getFirstWord(userCommand).equalsIgnoreCase("add")) {
			setRemainingWords(removeFirstWord(userCommand));
		} else {
			this.setRemainingWords(userCommand);
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
					setDateAndTimeParts( getDateAndTimeParts() + parts[i] );
				} else {
					setDateAndTimeParts( getDateAndTimeParts() +" " + parts[i] );
				}
			}
		}
		setCommandDetails(commandDetails.trim());
		TDTDateAndTimeParser dateAndTimeParser = new TDTDateAndTimeParser();
		setDateAndTime(dateAndTimeParser.decodeDateAndTimeDetails(dateAndTimeParts));
	}

	/**
	 * This function does the ADD COMMAND for words contained inside the " ".
	 * All the words will be included in commandDetails.
	 * @param checkWord
	 * @param i
	 */
	private void quotationAdd(String checkWord, int i) {
		if (checkWord.contains("\"")) {
			String check = checkWord.replaceAll("[^\"]", "");
			// words after the first "
			if (getQuotationCounter() > 1 && getQuotationMarks() > 0) {
				// second "
				if (check.length()== 1 && getQuotationMarks()== 1) {
					setQuotationMarks(0);
					setQuotationCounter(0);
					// more than 1 " in a word
				} else if (check.length() > 1) {
					int num = check.length()%2;
					if (num==0) {
						setQuotationMarks(1);
					} else if (num==1) {
						setQuotationMarks(0);
						setQuotationCounter(0);
					}
				}
				// first word has 2 "
			} else if (getQuotationCounter() == 1 && (check.length()==2)) {
				setQuotationMarks(0);
				setQuotationCounter(0);
			}
			parts[i] = checkWord.replace("\"", "");
		}
		if (getQuotationMarks() == 2) {
			setQuotationMarks(0);
		}
	}

	/**
	 * This function does the ADD COMMAND normally for words no within quotation marks. 
	 * It checks if the word is a date/time/day/month and is then processed accordingly.
	 * @param i
	 * @param checkWord
	 */
	private void usualAdd(int i, String checkWord) {
		isPriority(checkWord , i);
		if (TDTDateMethods.checkDate(checkWord) || TDTTimeMethods.checkTime(checkWord)) {
			parseDateTimeDetails(i);
		} else if (TDTDateMethods.checkDay(checkWord)!=0) {
			if (TDTDateMethods.checkDay(checkWord) == 10) {
				parseDayWordDetails(i, checkWord);
			} else {
				parseDayDetails(i);
			}
		} else if (TDTDateMethods.checkMonth(checkWord)!=0) {
			setSkipNextWord(false);
			parseMonthDetails(i);
		} 
	}

	/**
	 * This function completes the commandDetails for an input that contains a date format
	 * with the month spelled out. Eg. 4 August 2014 or 4 Aug. 
	 */
	private void parseMonthDetails(int i) {
		// checks it the word before the month is a number (date)
		if ((i>0) && parts[i-1].matches("\\d+")) {
			if ((i>1) && getConnectorWords().contains(parts[i-2])) {
				isCommandDetails[i-2] = false;
			}
			isCommandDetails[i] = false;
			isCommandDetails[i-1] = false;
			parts[i] = "~" + parts[i];
			// checks if the word after the month is a number (year)
			if ((i+1 < parts.length) && (parts[i+1].matches("\\d+"))) {
				isCommandDetails[i+1] = false;
				parts[i+1] = "~" + parts[i+1];
				setSkipNextWord(true);
			}
		} 
	}

	/**
	 * This function completes the commandDetails for words that is
	 * of a date (date format consisting of all digits ) or time. 
	 * @param i
	 */
	private void parseDateTimeDetails(int i) {
		if (i>0) {	
			if (getConnectorWords().contains(parts[i-1])) {
				isCommandDetails[i-1] = false;
			}
		} 
		isCommandDetails[i] = false;
	}

	/**
	 * This function completes the commandDetails for words that are days such as Monday Tuesday etc
	 * @param i
	 */
	private void parseDayDetails(int i) {
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
				&& getConnectorWords().contains(parts[firstOccurance-1])) {
			isCommandDetails[firstOccurance-1] = false;
			if ((firstOccurance>1) 
					&& getConnectorWords().contains(parts[firstOccurance-2])) {
				isCommandDetails[firstOccurance-2] = false;
			}
			if ((firstOccurance>2) 
					&& getConnectorWords().contains(parts[firstOccurance-3])) {
				isCommandDetails[firstOccurance-3] = false;
			}
		}
		isCommandDetails[i] = false;
	}

	/**
	 * This function checks for the words 'next' and 'following' before the word 'day'
	 * @param i
	 * @param checkWord
	 */
	private void parseDayWordDetails(int i, String checkWord) {
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
					&& getConnectorWords().contains(parts[firstOccurance-1])) {
				isCommandDetails[firstOccurance-1] = false;
				if ((firstOccurance > 1) 
						&& getConnectorWords().contains(parts[firstOccurance-2])) {
					isCommandDetails[firstOccurance-2] = false;
				}
			} 
		} 
	}

	/**
	 * This function checks if the array parts is of valid length (!=0)
	 * @return boolean. True if not empty (>0), False if otherwise. 
	 */
	private boolean isValidPartsLength() {
		if (parts.length == 0) {
			return false;
		}
		return true;
	}

	/**
	 * This function checks if the command input is of priority - presence of '!'
	 * '!' will not appear in commandDetails. 
	 * @param word
	 * @param i
	 */
	private void isPriority(String word, int i) {
		if (word.contains("!")) {
			parts[i] = word.replace("!", "");
			setIsHighPriority(true);
		}
	}
	
	/**
	 * This function completes both the taskID and labelName accordingly
	 * for the Delete and Done commands. 
	 */
	private void completeTaskIdAndLabelName() {
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
	}

	/**
	 * This function completes either the taskID or labelName according to whether
	 * the input is a digit. 
	 * for the Delete and Done commands.
	 */
	private void completeTaskIdOrLabelName() {
		if (parts[0].matches("\\d+")) {
			setTaskID(Integer.parseInt(parts[0]));
		} else {
			setLabelName(parts[0]);
		}
	}

	//--------------------------------- Getters and Setters -----------------------------------------
	private ArrayList<String> getConnectorWords() {
		return connectorWordsArr;
	}

	/**
	 * This function sets the list of "connector" words used in the checking of
	 * day, date, time.
	 */
	private void setConnectorWords() {
		ArrayList<String> connectorWords = new ArrayList<String>();
		connectorWords.add("on");
		connectorWords.add("at");
		connectorWords.add("by");
		connectorWords.add("from");
		connectorWords.add("about");
		connectorWords.add("to");
		connectorWords.add("-");
		connectorWords.add("until");
		connectorWords.add("till");
		connectorWords.add("next");
		connectorWords.add("following");
		connectorWords.add("this");
		connectorWords.add("the");
		connectorWords.add("due");
		connectorWords.add("before");
		this.connectorWordsArr = connectorWords;
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

	private int getQuotationMarks() {
		return quotationMarks;
	}

	private void setQuotationMarks(int quotationMarks) {
		this.quotationMarks = quotationMarks;
	}

	private int getQuotationCounter() {
		return quotationCounter;
	}

	private void setQuotationCounter(int counter) {
		this.quotationCounter = counter;
	}

	public String getDateAndTimeParts() {
		return dateAndTimeParts;
	}

	private void setDateAndTimeParts(String dateAndTimeParts) {
		this.dateAndTimeParts = dateAndTimeParts;
	}
}