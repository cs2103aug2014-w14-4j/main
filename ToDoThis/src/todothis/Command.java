package todothis;

import todothis.ITDTParser.COMMANDTYPE;

public class Command {
	private int taskID;
	private COMMANDTYPE commandType;
	private String labelName;
	private String commandDetails;
	private TDTDateAndTime dateAndTime;
	private boolean isHighPriority;
	
	//---------------------CONSTRUCTOR-------------------------------------
	public Command(COMMANDTYPE commandType, String labelName, int taskID,
			String commandDetails, TDTDateAndTime dateAndTime, 
			boolean isHighPriority) {
		this.setTaskID(taskID);
		this.setCommandType(commandType);
		this.setHighPriority(isHighPriority);
		this.setLabelName(labelName);
		this.setCommandDetails(commandDetails);
		this.setDateAndTime(dateAndTime);
	}
	//---------------------------------------------------------------------
	
	
	//-----------------------GETTER & SETTER-------------------------------
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
	public String getCommandDetails() {
		return commandDetails;
	}
	public void setCommandDetails(String commandDetails) {
		this.commandDetails = commandDetails;
	}

	public boolean isHighPriority() {
		return isHighPriority;
	}
	public void setHighPriority(boolean isHighPriority) {
		this.isHighPriority = isHighPriority;
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
}
