package todothis.command;

import java.util.ArrayList;

import todothis.logic.TDTDateAndTime;
import todothis.logic.TDTReminder;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class RemindCommand extends Command {
	private String labelName;
	private int taskID;
	private String commandDetails;
	private Task task;
	
	public RemindCommand(String labelName, int taskId, String commandDetails) {
		super(COMMANDTYPE.REMIND);
		this.setLabelName(labelName);
		this.setTaskID(taskId);
		this.setCommandDetails(commandDetails);
	}

	@Override
	public String execute(TDTStorage storage) {
		if(labelName.equalsIgnoreCase("")) {
			setLabelName(storage.getCurrLabel());
		}
		
		if(getCommandDetails().equals("")) {
			return removeReminder(storage);
		}
		if(storage.getLabelMap().containsKey(getLabelName().toUpperCase())) {
			ArrayList<Task> array = storage.getLabelMap().get(getLabelName());
			if(getTaskID() > 0 && getTaskID() <= array.size()) {
				Task temp = array.get(getTaskID() - 1);
				String remindDateTime = TDTDateAndTime.decodeReminderDetails(getCommandDetails());
				if(!remindDateTime.equals("null")) {
					temp.setRemindDateTime(remindDateTime);
					temp.setReminder(new TDTReminder(TDTDateAndTime
							.calculateRemainingTime(remindDateTime), temp));
					setTask(temp);
					storage.insertToUndoStack(this);
					
					return "Reminder set at " + remindDateTime;
				} else {
					return "Invalid date/time for reminder.";
				}
			} else {
				return "Invalid taskId.";
			}
		} else {
			return "Invalid label Name. Label does not exist.";
		}
	}

	private String removeReminder(TDTStorage storage) {
		if(storage.getLabelMap().containsKey(getLabelName().toUpperCase())) {
			ArrayList<Task> array = storage.getLabelMap().get(getLabelName());
			if(getTaskID() > 0 && getTaskID() <= array.size()) {
				Task temp = array.get(getTaskID() - 1);
				String dateTime = temp.getRemindDateTime();
				temp.getReminder().cancelReminder();
				temp.setReminder(null);
				temp.setRemindDateTime("null");
				return "Reminder at " + dateTime +  " removed.";
			} else {
				return "Invalid taskId.";
			}
		} else {
			return "Invalid label Name. Label does not exist.";
		}
	}

	@Override
	public String undo(TDTStorage storage) {
		Task temp = getTask();
		temp.getReminder().cancelReminder();
		temp.setReminder(null);
		temp.setRemindDateTime("null");
		return "Undo reminder.";
	}

	
	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public String getCommandDetails() {
		return commandDetails;
	}

	public void setCommandDetails(String commandDetails) {
		this.commandDetails = commandDetails;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
