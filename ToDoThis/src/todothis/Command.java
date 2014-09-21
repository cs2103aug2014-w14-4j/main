package todothis;

import todothis.ITDTParser.COMMANDTYPE;

public class Command {
	private int taskID;
	private COMMANDTYPE commandType;
	private String labelName;
	private String commandDetails;
	private String dueDate;
	private String dueTime;
	private boolean isHighPriority;
	
	//---------------------CONSTRUCTOR-------------------------------------
	public Command(COMMANDTYPE commandType, String labelName, int taskID,
			String commandDetails, String dueDate, String dueTime, 
			boolean isHighPriority) {
		this.setTaskID(taskID);
		this.setCommandType(commandType);
		this.setDueDate(dueDate);
		this.setDueTime(dueTime);
		this.setHighPriority(isHighPriority);
		this.setLabelName(labelName);
		this.setCommandDetails(commandDetails);
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
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public boolean isHighPriority() {
		return isHighPriority;
	}
	public void setHighPriority(boolean isHighPriority) {
		this.isHighPriority = isHighPriority;
	}
	public String getDueTime() {
		return dueTime;
	}
	public void setDueTime(String dueTime) {
		this.dueTime = dueTime;
	}


	public int getTaskID() {
		return taskID;
	}


	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
}
