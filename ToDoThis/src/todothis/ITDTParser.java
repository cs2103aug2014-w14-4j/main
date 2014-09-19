package todothis;

public interface ITDTParser {
	public enum COMMANDTYPE {
		ADD, DELETE, HIDE, SEARCH, SORT, EDIT, DONE, UNDO, INVALID;
	}
	/**
	 * Takes in userCommand and return a commmand object using the below constructor
	 * Validates userCommand and make sure that command is valid.
	 * 
	 * public Command(COMMANDTYPE commandType, String labelName, String commandDetails, 
			String dueDate, String dueTime, boolean isHighPriority) {
			
	 * @param userCommand
	 * @return Command
	 */
	public Command parse(String userCommand);
}
