package todothis.command;

import todothis.logic.TDTDateAndTime;
import todothis.logic.TDTLogic;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;



public class AddCommand extends Command {
	private int taskID;
	private String labelName;
	private String commandDetails;
	private TDTDateAndTime dateAndTime;
	private boolean isHighPriority;

	public AddCommand(String labelName, int taskID,
			String commandDetails, TDTDateAndTime dateAndTime, 
			boolean isHighPriority ) {
		super(COMMANDTYPE.ADD);
		this.setTaskID(taskID);
		this.setHighPriority(isHighPriority);
		this.setLabelName(labelName);
		this.setCommandDetails(commandDetails);
		this.setDateAndTime(dateAndTime);
	}

	@Override
	public String execute(TDTStorage storage) {
		String labelName = storage.getCurrLabel();
		int taskId = storage.getLabelSize(labelName) + 1;
		Task task = new Task(taskId, labelName, getCommandDetails(),
				getDateAndTime(), isHighPriority());
		TDTDateAndTime dnt = getDateAndTime();
		if(TDTDateAndTime.isValidDateRange(dnt.getStartDate()) && 
				TDTDateAndTime.isValidDateRange(dnt.getEndDate()) &&
				TDTDateAndTime.isValidTimeRange(dnt.getStartTime()) &&
				TDTDateAndTime.isValidTimeRange(dnt.getEndTime())) {
			storage.addTask(task);
			TDTLogic.sort(storage.getLabelMap());
			storage.write();
			return "Add success";
		} else {
			return "Invalid date/time format.";
		}
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
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

	public TDTDateAndTime getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(TDTDateAndTime dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public boolean isHighPriority() {
		return isHighPriority;
	}

	public void setHighPriority(boolean isHighPriority) {
		this.isHighPriority = isHighPriority;
	}

}
