package todothis.logic.command;

import java.util.ArrayList;

import todothis.commons.TDTReminder;
import todothis.commons.TDTTimeMethods;
import todothis.commons.Task;
import todothis.logic.parser.TDTDateAndTimeParser;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class RemindCommand extends Command {
	private static final String MESSAGE_UNDO_REMOVE_REMIND = "Undo remove reminder.";
	private static final String MESSAGE_UNDO_REMIND = "Undo reminder.";
	private static final String MESSAGE_INVALID_REMOVE_REMIND = "Invalid command. No reminder to remove.";
	private static final String MESSAGE_INVALID_LABEL = "Invalid command. Invalid label name.";
	private static final String MESSAGE_INVALID_TASKID = "Invalid command. Invalid taskId.";
	private static final String MESSAGE_INVALID_REMIND = "Invalid command. Invalid date/time for reminder.";
	private static final String MESSAGE_REMIND_FEEDBACK = "Reminder set at %s";
	private static final String MESSAGE__REMOVE_REMIND_FEEDBACK = "Reminder at %s removed.";

	private String labelName;
	private int taskID;
	private String commandDetails;
	private Task task;
	private boolean isRemoveReminder = false;
	private String prevReminder;
	
	/**
	 * Construct a RemindCommand object
	 * 
	 * @param labelName
	 * @param taskId
	 * @param commandDetails
	 */
	public RemindCommand(String labelName, int taskId, String commandDetails) {
		super(COMMANDTYPE.REMIND);
		this.setLabelName(labelName.toUpperCase());
		this.setTaskID(taskId);
		this.setCommandDetails(commandDetails);
	}
	
	/**
	 * Set/Update/Remove reminder on a specific task from current label or a specific label.
	 */
	@Override
	public String execute(TDTDataStore data) {
		if (labelName.equalsIgnoreCase("")) {
			setLabelName(data.getCurrLabel());
		}

		if (getCommandDetails().equals("")) {
			isRemoveReminder = true;
			return removeReminder(data);
		}
		if (data.getTaskMap().containsKey(getLabelName())) {
			ArrayList<Task> array = data.getTaskMap().get(getLabelName());
			if (getTaskID() > 0 && getTaskID() <= array.size()) {
				Task temp = array.get(getTaskID() - 1);
				String remindDateTime = TDTDateAndTimeParser
						.decodeReminderDetails(getCommandDetails());
				if (!remindDateTime.equals("null")) {
					setReminderForTask(data, temp, remindDateTime);
					return String.format(MESSAGE_REMIND_FEEDBACK,
							remindDateTime);
				} else {
					return MESSAGE_INVALID_REMIND;
				}
			} else {
				return MESSAGE_INVALID_TASKID;
			}
		} else {
			return MESSAGE_INVALID_LABEL;
		}
	}

	private void setReminderForTask(TDTDataStore data, Task temp, String remindDateTime) {
		temp.setRemindDateTime(remindDateTime);
		temp.setReminder(new TDTReminder(TDTTimeMethods
				.calculateRemainingTime(remindDateTime), temp));
		setTask(temp);
		data.insertToUndoStack(this);
	}

	private String removeReminder(TDTDataStore data) {
		if (data.getTaskMap().containsKey(getLabelName())) {
			ArrayList<Task> array = data.getTaskMap().get(getLabelName());
			if (getTaskID() > 0 && getTaskID() <= array.size()) {
				Task temp = array.get(getTaskID() - 1);
				if (temp.getReminder() == null) {
					return MESSAGE_INVALID_REMOVE_REMIND;
				}
				String dateTime = temp.getRemindDateTime();
				prevReminder = dateTime;
				setTask(temp);
				temp.getReminder().cancelReminder();
				temp.setReminder(null);
				temp.setRemindDateTime("null");
				data.insertToUndoStack(this);
				return String.format(MESSAGE__REMOVE_REMIND_FEEDBACK, dateTime);
			} else {
				return MESSAGE_INVALID_TASKID;
			}
		} else {
			return MESSAGE_INVALID_LABEL;
		}
	}
	
	/**
	 * Reverses the effect of execute.
	 */
	@Override
	public String undo(TDTDataStore data) {
		if (!isRemoveReminder) {
			Task temp = getTask();
			if (temp.getReminder() != null) {
				temp.getReminder().cancelReminder();
				temp.setReminder(null);
				temp.setRemindDateTime("null");
			}
			return MESSAGE_UNDO_REMIND;
		} else {
			Task temp = getTask();
			temp.setRemindDateTime(prevReminder);
			temp.setReminder(new TDTReminder(TDTTimeMethods
					.calculateRemainingTime(prevReminder), temp));
			return MESSAGE_UNDO_REMOVE_REMIND;
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
