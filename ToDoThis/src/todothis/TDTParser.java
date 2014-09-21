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
		
		/*
		 * 
		 * 
		 * INSERT CODE HERE
		 * 
		 * 
		 * 
		 */
		
		return new Command(commandType, labelName, taskID, commandDetails, dueDate, dueTime,
				isHighPriority);
	}

}
