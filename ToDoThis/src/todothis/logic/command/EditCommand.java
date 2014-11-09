package todothis.logic.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.commons.TDTCommons;
import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class EditCommand extends Command {
	private static final String MESSAGE_UNDO_EDIT = "Undo edit";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid Command";
	private static final String MESSAGE_INVALID_LABEL_TASKID = "Invalid Command. Label does not exist or invalid task number.";
	private static final String MESSAGE_INVALID_END_TIME = "Invalid end time! End Time should be after start time!";
	private static final String MESSAGE_INVALID_END_DATE = "Invalid end date! End date should be after start date!";
	private static final String MESSAGE_INVALID_DATE_TIME_FORMAT = "Invalid date/time format.";
	private static final String MESSAGE_EDIT_FEEDBACK = "Task edited.";
	private static final String MESSAGE_EDIT_CLASH = "Clashes detected. Task edited.\n%d task(s) found to have same time range on %s";

	private int taskID;
	private String feedback;
	private String labelName;
	private String commandDetails;
	private TDTDateAndTime dateAndTime;
	private boolean isHighPriority;
	private String prevDetails;
	private TDTDateAndTime prevDNT;
	private boolean prevPriority;
	private Task editedTask;
	private boolean gotClashes = false;
	private ArrayList<Task> targetTask;
	
	/**
	 * Construct a EditCommand object
	 * 
	 * @param labelName
	 * @param taskID
	 * @param commandDetails
	 * @param dateAndTime
	 * @param isHighPriority
	 */
	public EditCommand(String labelName, int taskID, String commandDetails,
			TDTDateAndTime dateAndTime, boolean isHighPriority) {
		super(COMMANDTYPE.EDIT);
		this.setTaskID(taskID);
		this.setHighPriority(isHighPriority);
		this.setLabelName(labelName.toUpperCase());
		this.setCommandDetails(commandDetails);
		this.setDateAndTime(dateAndTime);
		this.setTargetTask(new ArrayList<Task>());
	}
	
	/**
	 * Edit a task from current label or a specific label.
	 */
	@Override
	public String execute(TDTDataStore data) {
		String label = getLabelName().toUpperCase();
		int taskId = getTaskID();
		String commandDetails = getCommandDetails();
		boolean isHighPriority = isHighPriority();
		TDTDateAndTime dateAndTime = getDateAndTime();

		// edit task from current label
		if (label.equals("") && taskId != -1) {
			setLabelName(data.getCurrLabel());
			ArrayList<Task> array = data.getTaskMap().get(getLabelName());
			if (taskId <= array.size() && getTaskID() > 0) {
				Task task = array.get(taskId - 1);
				return editTask(data, commandDetails, isHighPriority,
						dateAndTime, task);
			} else {
				return MESSAGE_INVALID_LABEL_TASKID;
			}
		}

		// edit task from specific label
		if (!label.equals("") && taskId != -1) {
			if (data.getTaskMap().containsKey(label)) {
				ArrayList<Task> array = data.getTaskMap().get(label);
				if (taskId <= array.size() && getTaskID() > 0) {
					Task task = array.get(taskId - 1);
					return editTask(data, commandDetails, isHighPriority,
							dateAndTime, task);
				} else {
					return MESSAGE_INVALID_LABEL_TASKID;
				}
			} else {
				return MESSAGE_INVALID_LABEL_TASKID;
			}
		}

		return MESSAGE_INVALID_COMMAND;
	}

	private String editTask(TDTDataStore data, String commandDetails,
			boolean isHighPriority, TDTDateAndTime dateAndTime, Task task) {
		if (!TDTCommons.isValidDateTimeRange(dateAndTime)) {
			return MESSAGE_INVALID_DATE_TIME_FORMAT;
		}

		if (!TDTCommons.isValidStartEndDate(dateAndTime)) {
			return MESSAGE_INVALID_END_DATE;
		}

		if (!TDTCommons.isValidStartEndTime(dateAndTime)) {
			return MESSAGE_INVALID_END_TIME;
		}
		setEditDetails(commandDetails, isHighPriority, dateAndTime, task);
		checkForClash(task, data.getTaskIterator());
		data.insertToUndoStack(this);
		return gotClashes ? this.getFeedback() : MESSAGE_EDIT_FEEDBACK;
	}

	private void checkForClash(Task target, Iterator<Task> iter) {
		int numClash = 0;
		while (iter.hasNext()) {
			Task task = iter.next();
			if (task != target && !task.isDone()) {
				if (target.getDateAndTime().isClash(task.getDateAndTime())) {
					targetTask.add(task);
					numClash++;
					gotClashes = true;
				}
			}
		}
		if (gotClashes) {
			String feedback = String.format(MESSAGE_EDIT_CLASH, numClash,
					target.getDateAndTime().getStartDate());

			this.setFeedback(feedback);
		}
	}

	private void setEditDetails(String commandDetails, boolean isHighPriority,
			TDTDateAndTime dateAndTime, Task task) {
		setEditedTask(task);
		prevDetails = task.getDetails();
		prevDNT = task.getDateAndTime();
		prevPriority = task.isHighPriority();
		task.setDetails(commandDetails);
		task.setDateAndTime(dateAndTime);
		task.setHighPriority(isHighPriority);
	}
	
	/**
	 * Reverses the effect of execute.
	 */
	@Override
	public String undo(TDTDataStore data) {
		targetTask.clear();
		EditCommand comd = new EditCommand(getLabelName(), getTaskID(),
				prevDetails, prevDNT, prevPriority);
		comd.execute(data);
		assert (data.getUndoStack().size() > 0) : "undostack is empty";
		data.getUndoStack().pop();
		return MESSAGE_UNDO_EDIT;
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

	public Task getEditedTask() {
		return editedTask;
	}

	public void setEditedTask(Task editedTask) {
		this.editedTask = editedTask;
	}

	public ArrayList<Task> getTargetTask() {
		return targetTask;
	}

	public void setTargetTask(ArrayList<Task> targetTask) {
		this.targetTask = targetTask;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

}
