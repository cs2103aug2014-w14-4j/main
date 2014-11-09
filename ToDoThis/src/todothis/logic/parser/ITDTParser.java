//@author A0110398H
package todothis.logic.parser;

import todothis.logic.command.Command;

//Interface for parser
public interface ITDTParser {
	
	public enum COMMANDTYPE {
		REMIND, ADD, DELETE, HIDE, SEARCH, REDO, EDIT, DONE, 
		UNDO, LABEL, SHOW, HELP, INVALID, EXIT;
	}
	
	/**
	 * Takes in userCommand and return a commmand object using the below constructor
	 * Validates userCommand and make sure that command is valid.
	 * 
	 * public Command(COMMANDTYPE commandType, String labelName, int taskID, 
	 * String commandDetails, String dueDate, String dueTime, boolean isHighPriority)
			
	 * @param userCommand
	 * @return Command
	 */
	public Command parse(String userCommand);
}
