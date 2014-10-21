package todothis.command;

import todothis.TDTGUI;
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
		if(!TDTDateAndTime.isValidDateRange(dnt.getStartDate()) || 
				!TDTDateAndTime.isValidDateRange(dnt.getEndDate()) ||
				!TDTDateAndTime.isValidTimeRange(dnt.getStartTime()) ||
				!TDTDateAndTime.isValidTimeRange(dnt.getEndTime())) {
			return "Invalid date/time format.";
		}
		if(!dnt.getStartDate().equals("null") && !dnt.getEndDate().equals("null")){
			if(TDTDateAndTime.compareToDate(dnt.getStartDate(), dnt.getEndDate()) == -1){
				return "Invalid end date! End date should be after start date!";
			}else if(TDTDateAndTime.compareToDate(dnt.getStartDate(), dnt.getEndDate()) == 0){
				if(!dnt.getStartTime().equals("null") && !dnt.getEndTime().equals("null")){
					if(TDTDateAndTime.compareToTime(dnt.getStartTime(), dnt.getEndTime()) == -1){
						return "Invalid end time! End Time should be after start time!";
					}
				}
			}
		}else if(dnt.getStartDate().equals("null") && dnt.getEndDate().equals("null") && 
				!dnt.getStartTime().equals("null") && !dnt.getEndTime().equals("null")){
			if(TDTDateAndTime.compareToTime(dnt.getStartTime(), dnt.getEndTime()) == -1){
				return "Invalid end time! End Time should be after start time!";
			}
		}
		storage.addTask(task, task.getLabelName());
		storage.addTask(task, TDTGUI.DEFAULT_LABEL);
		TDTLogic.sort(storage.getLabelMap());
		storage.write();
		return "Add success";
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
