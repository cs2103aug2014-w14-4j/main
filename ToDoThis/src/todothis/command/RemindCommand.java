package todothis.command;

import java.util.ArrayList;

import todothis.commons.TDTTimeMethods;
import todothis.logic.TDTReminder;
import todothis.logic.Task;
import todothis.parser.TDTDateAndTime;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class RemindCommand extends Command {
	private String labelName;
	private int taskID;
	private String commandDetails;
	private Task task;
	private boolean isRemoveReminder = false;
	private String prevReminder;
	
	public RemindCommand(String labelName, int taskId, String commandDetails) {
		super(COMMANDTYPE.REMIND);
		this.setLabelName(labelName.toUpperCase());
		this.setTaskID(taskId);
		this.setCommandDetails(commandDetails);
	}

	@Override
	public String execute(TDTStorage storage) {
		if(labelName.equalsIgnoreCase("")) {
			setLabelName(storage.getCurrLabel());
		}
		
		if(getCommandDetails().equals("")) {
			isRemoveReminder = true;
			return removeReminder(storage);
		}
		if(storage.getLabelMap().containsKey(getLabelName())) {
			ArrayList<Task> array = storage.getLabelMap().get(getLabelName());
			if(getTaskID() > 0 && getTaskID() <= array.size()) {
				Task temp = array.get(getTaskID() - 1);
				String remindDateTime = TDTDateAndTime.decodeReminderDetails(getCommandDetails());
				if(!remindDateTime.equals("null")) {
					temp.setRemindDateTime(remindDateTime);
					temp.setReminder(new TDTReminder(TDTTimeMethods.calculateRemainingTime(remindDateTime), temp));
					setTask(temp);
					storage.insertToUndoStack(this);
					
					return "Reminder set at " + remindDateTime;
				} else {
					return "Invalid command. Invalid date/time for reminder.";
				}
			} else {
				return "Invalid command. Invalid taskId.";
			}
		} else {
			return "Invalid command.Invalid label name.";
		}
	}

	private String removeReminder(TDTStorage storage) {
		if(storage.getLabelMap().containsKey(getLabelName())) {
			ArrayList<Task> array = storage.getLabelMap().get(getLabelName());
			if(getTaskID() > 0 && getTaskID() <= array.size()) {
				Task temp = array.get(getTaskID() - 1);
				if(temp.getReminder() == null) {
					return "Invalid command. No reminder to remove.";
				}
				String dateTime = temp.getRemindDateTime();
				prevReminder = dateTime;
				setTask(temp);
				temp.getReminder().cancelReminder();
				temp.setReminder(null);
				temp.setRemindDateTime("null");
				storage.insertToUndoStack(this);
				return "Reminder at " + dateTime +  " removed.";
			} else {
				return "Invalid command. Invalid taskId.";
			}
		} else {
			return "Invalid command.Invalid label name.";
		}
	}

	@Override
	public String undo(TDTStorage storage) {
		if(!isRemoveReminder) {
			Task temp = getTask();
			if(temp.getReminder() != null) {
				temp.getReminder().cancelReminder();
				temp.setReminder(null);
				temp.setRemindDateTime("null");
			}
			return "Undo reminder.";
		} else {
			Task temp = getTask();
			temp.setRemindDateTime(prevReminder);
			temp.setReminder(new TDTReminder(TDTTimeMethods.calculateRemainingTime(prevReminder), temp));
			return "Undo remove reminder.";
		}
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
