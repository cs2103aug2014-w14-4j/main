package todothis;

public class TDTParser implements ITDTParser {

	@Override
	public Command parse(String userCommand) {
		COMMANDTYPE commandType = COMMANDTYPE.INVALID;
		String labelName = "default";
		String dueDate = "";
		String dueTime = "";
		boolean isHighPriority = false;
		String commandDetails = "";
		
		/*
		 * 
		 * 
		 * INSERT CODE HERE
		 * 
		 * 
		 * 
		 */
		
		return new Command(commandType, labelName, commandDetails, dueDate, dueTime,
				isHighPriority);
	}

}
