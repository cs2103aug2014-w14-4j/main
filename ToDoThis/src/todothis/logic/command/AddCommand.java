//@author A0110398H
package todothis.logic.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.commons.TDTCommons;
import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class AddCommand extends Command {
	private static final String MESSAGE_UNDO_ADD = "Undo add.";
	public static final String MESSAGE_INVALID_END_TIME = "Invalid end time! End Time should be after start time!";
	public static final String MESSAGE_INVALID_END_DATE = "Invalid end date! End date should be after start date!";
	public static final String MESSAGE_INVALID_DATE_TIME_FORMAT = "Invalid date/time format.";
	public static final String MESSAGE_ADD_FEEDBACK = "Task added to %s.";
	public static final String MESSAGE_ADD_CLASH = "Clashes detected. %s \n%d task(s) found to have same time range on %s";

	private int taskID;
	private String labelName;
	private String commandDetails;
	private TDTDateAndTime dateAndTime;
	private boolean isHighPriority;
	private ArrayList<Task> targetTask;
	private Task addedTask;
	private String feedback = "";
	private boolean gotClashes = false;
	
	/**
	 * Construct the AddCommand object.
	 * @param commandDetails
	 * @param dateAndTime
	 * @param isHighPriority
	 */
	public AddCommand(String commandDetails, TDTDateAndTime dateAndTime,
			boolean isHighPriority) {
		super(COMMANDTYPE.ADD);
		this.setHighPriority(isHighPriority);
		this.setCommandDetails(commandDetails);
		this.setDateAndTime(dateAndTime);
		this.setTargetTask(new ArrayList<Task>());
	}
	
	/**
	 * Create a Task object and add into the TDTdataStore 
	 */
	@Override
	public String execute(TDTDataStore data) {
		setLabelName(data.getCurrLabel());
		setTaskID(data.getLabelSize(getLabelName()) + 1);
		Task task = new Task(getTaskID(), labelName, getCommandDetails(),
				getDateAndTime(), isHighPriority());
		setAddedTask(task);
		TDTDateAndTime dnt = getDateAndTime();

		if (!TDTCommons.isValidDateTimeRange(dnt)) {
			return MESSAGE_INVALID_DATE_TIME_FORMAT;
		}

		if (!TDTCommons.isValidStartEndDate(dnt)) {
			return MESSAGE_INVALID_END_DATE;
		}

		if (!TDTCommons.isValidStartEndTime(dnt)) {
			return MESSAGE_INVALID_END_TIME;
		}

		checkForClash(task, data.getTaskIterator());
		data.addTask(task);
		setTaskID(TDTCommons.sort(data.getTaskMap().get(getLabelName()), task));
		data.insertToUndoStack(this);

		return gotClashes ? this.getFeedback() : String.format(
				MESSAGE_ADD_FEEDBACK, data.getCurrLabel());
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
			String feedback = String.format(MESSAGE_ADD_CLASH,
					String.format(MESSAGE_ADD_FEEDBACK, target.getLabelName()),
					numClash, target.getDateAndTime().getStartDate());
			this.setFeedback(feedback);
		}
	}
	
	/**
	 * Reverses the effect of execute
	 */
	@Override
	public String undo(TDTDataStore data) {
		targetTask.clear();
		DeleteCommand comd = new DeleteCommand(getLabelName(), getTaskID());
		comd.execute(data);
		assert (data.getUndoStack().size() > 0) : "undostack is empty";
		data.getUndoStack().pop();
		return MESSAGE_UNDO_ADD;
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

	public Task getAddedTask() {
		return addedTask;
	}

	public void setAddedTask(Task addedTask) {
		this.addedTask = addedTask;
	}

}
