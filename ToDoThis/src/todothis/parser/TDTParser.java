package todothis.parser;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import todothis.command.AddCommand;
import todothis.command.Command;
import todothis.command.DeleteCommand;
import todothis.command.DisplayCommand;
import todothis.command.DoneCommand;
import todothis.command.EditCommand;
import todothis.command.HideCommand;
import todothis.command.LabelCommand;
import todothis.command.RedoCommand;
import todothis.command.SearchCommand;
import todothis.command.UndoCommand;
import todothis.logic.TDTDateAndTime;

// somerset 313 how. 313 is time. 

public class TDTParser implements ITDTParser {
	
	String[] parts;
	String dateAndTimeParts = "";
	COMMANDTYPE commandType = COMMANDTYPE.INVALID;
	String labelName;
	boolean isHighPriority;
	boolean isValidEdit;
	boolean isSkipNextWord;
	String commandDetails;
	String remainingWords;
	int taskID;
	TDTDateAndTime dateAndTime;
	ArrayList<String> prepositionWordsArr;
	private Logger logger = Logger.getLogger("TDTParser");

	public Command parse(String userCommand) {
		logger.log(Level.INFO, "start parsing");
		this.setCommandType(COMMANDTYPE.INVALID);
		this.setLabelName("");
		this.setIsHighPriority(false);
		this.setCommandDetails("");
		this.setTaskID(-1);
		this.setPrepositionWords();
		dateAndTimeParts = "";
		setValidEdit(false);

		this.setCommandType(determineCommandType(getFirstWord(userCommand)));
		this.setRemainingWords(removeFirstWord(userCommand));
		switch(getCommandType()) {
			case ADD :
				add(userCommand);
				return new AddCommand(getLabelName(), getTaskID(),getCommandDetails(), getDateAndTime(), 
						getIsHighPriority());
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
			case INVALID :
				break;
			default:
				break;
		}
		logger.log(Level.INFO, "end of parsing");
		return null;
	}
	
	/**
	 * Default command is assumed to be ADD
	 */
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
		} else {
			return COMMANDTYPE.ADD;
		}
	}
	
//---------------------------------- main command methods -------------------------------------
	public void add(String userCommand) {
		this.setRemainingWords(userCommand);
		isPriority(getRemainingWords());
		parts = getRemainingWords().split(" ");
		for (int i = 0; i < parts.length; i++) {
			String checkWord = parts[i];
			if (TDTDateAndTime.checkDate(checkWord) || TDTDateAndTime.checkTime(checkWord) || 
					TDTDateAndTime.checkDay(checkWord)!=0) {
				completeTimeDateDayDetails(i);
			} else if (TDTDateAndTime.checkMonth(checkWord)!=0) {
				setSkipNextWord(false);
				completeMonthDetails(i);
				if (isSkipNextWord()) {
					i++;
				}
			} else {
				setCommandDetails(getCommandDetails() + " " +checkWord);
			}
		}
		setDateAndTime(new TDTDateAndTime(dateAndTimeParts));
	}
	
	public void done() {
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

	public void edit(String userCommand) {
		parts = getRemainingWords().split(" ");
		if (isValidPartsLength()) {
			// [taskID][commandDetails]
			if (parts[0].matches("\\d+")) {
				setTaskID(Integer.parseInt(parts[0]));
				if (parts.length > 1) {
					setRemainingWords(getRemainingWords().substring(parts[0].length()).trim());
					setValidEdit(true);
				}
			// [labelname][taskID][commandDetails]
			} else if (parts.length > 1) {
				if (parts[1].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[1]));
					setLabelName(parts[0]);
					setRemainingWords(getRemainingWords().substring(parts[0].length()).trim());
					setRemainingWords(getRemainingWords().substring(parts[1].length()).trim());
					setValidEdit(true);
				}
			}
		}	
		if (isValidEdit()) {
			add(getRemainingWords());
		}
	}

	public void delete() {
		parts = getRemainingWords().split(" ");
		if (isValidPartsLength()) {
			// delete [label] / delete [taskID]
			if (parts.length == 1) {
				if (parts[0].matches("\\d+")) {
					setTaskID(Integer.parseInt(parts[0]));
				} else {
					setLabelName(parts[0]);
				}
			}
			// delete [label][taskID] or delete [taskID][label]
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
	
	public void display() {
		setCommandDetails(getRemainingWords());
	}
	
	public void hide() {
		setCommandDetails(getRemainingWords());
	}

	public void search() {
		setCommandDetails(getRemainingWords());
	}

	public void label() {
		setLabelName(getRemainingWords());
	}

//------------------------------ other methods -----------------------------------------------
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
	
	/**
	 * This function completes the commandDetails for an input that contains a date format by
	 * which the month is spelled out.
	 */
	private void completeMonthDetails(int i) {
		if ((i>0) && parts[i-1].matches("\\d+")) {
			if ((i>1) && getPrepositionWords().contains(parts[i-2])) {
				removeDetails(getCommandDetails(), i-2);
			}
			dateAndTimeParts += " "+parts[i-1] + "~"+ parts[i];
			if ((i+1 < parts.length) && (parts[i+1].matches("\\d+"))) {
				dateAndTimeParts += "~"+ parts[i+1];
				setSkipNextWord(true);
			}
			removeDetails(getCommandDetails(), i-1);
		}
	}
	
	/**
	 * This function completes the commandDetails for an input that contains a
	 * date or day or time. 
	 */
	private void completeTimeDateDayDetails(int i) {
		if (i>0) {
			if (getPrepositionWords().contains(parts[i-1])) {
				dateAndTimeParts += (" " + parts[i-1]);
				removeDetails(getCommandDetails(), i-1);
			} 	
		} 
		dateAndTimeParts += (" " + parts[i]);
	}
	
	/**
	 * This function removes the preposition word from the commandDetails.
	 */
	public void removeDetails(String details, int i) {
		int last = details.length()-(parts[i].length());
		details = details.substring(0, last);
		setCommandDetails(details.trim());
	}
	
	private boolean isValidPartsLength() {
		if (parts.length == 0) {
			return false;
		}
		return true;
	}
	
	private void isPriority(String remainingWordsTemp) {
		if (remainingWordsTemp.contains("!")) {
			remainingWordsTemp = remainingWordsTemp.replace("!", "");
			setIsHighPriority(true);
		}
	}
	
//--------------------------------- getter and setter -----------------------------------------
	public ArrayList<String> getPrepositionWords() {
		return prepositionWordsArr;
	}

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
		this.prepositionWordsArr = prepositionWords;
	}

	public String getRemainingWords() {
		return remainingWords;
	}

	public void setRemainingWords(String remainingWords) {
		this.remainingWords = remainingWords;
	}

	public COMMANDTYPE getCommandType() {
		return commandType;
	}

	public void setCommandType(COMMANDTYPE commandType) {
		this.commandType = commandType;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public boolean getIsHighPriority() {
		return isHighPriority;
	}

	public void setIsHighPriority(boolean isHighPriority) {
		this.isHighPriority = isHighPriority;
	}

	public boolean isValidEdit() {
		return isValidEdit;
	}

	public void setValidEdit(boolean isValidEdit) {
		this.isValidEdit = isValidEdit;
	}

	public String getCommandDetails() {
		return commandDetails;
	}

	public void setCommandDetails(String commandDetails) {
		this.commandDetails = commandDetails;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public TDTDateAndTime getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(TDTDateAndTime dateAndTime) {
		this.dateAndTime = dateAndTime;
	}
	
	public boolean isSkipNextWord() {
		return isSkipNextWord;
	}

	public void setSkipNextWord(boolean isSkipNextWord) {
		this.isSkipNextWord = isSkipNextWord;
	}

}