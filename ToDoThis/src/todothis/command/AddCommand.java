package todothis.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.commons.TDTDateMethods;
import todothis.commons.TDTTimeMethods;
import todothis.logic.TDTLogic;
import todothis.logic.Task;
import todothis.parser.TDTDateAndTime;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;



public class AddCommand extends Command {
	private static final String MESSAGE_INVALID_END_TIME = "Invalid end time! End Time should be after start time!";
	private static final String MESSAGE_INVALID_END_DATE = "Invalid end date! End date should be after start date!";
	private static final String MESSAGE_INVALID_DATE_TIME_FORMAT = "Invalid date/time format.";
	private static final String MESSAGE_ADD_FEEDBACK = "Task added to %s.";
	
	private int taskID;
	private String labelName;
	private String commandDetails;
	private TDTDateAndTime dateAndTime;
	private boolean isHighPriority;
	private ArrayList<Task> targetTask;
	private Task addedTask;
	private String feedback = "";
	private boolean gotClashes = false;

	public AddCommand(String commandDetails, TDTDateAndTime dateAndTime, boolean isHighPriority ) {
		super(COMMANDTYPE.ADD);
		this.setHighPriority(isHighPriority);
		this.setCommandDetails(commandDetails);
		this.setDateAndTime(dateAndTime);
		this.setTargetTask(new ArrayList<Task>());
	}

	@Override
	public String execute(TDTStorage storage) {
		setLabelName(storage.getCurrLabel());
		setTaskID(storage.getLabelSize(getLabelName()) + 1);
		Task task = new Task(getTaskID(), labelName, getCommandDetails(),
				getDateAndTime(), isHighPriority());
		setAddedTask(task);
		TDTDateAndTime dnt = getDateAndTime();
		
		if(!isValidDateTimeRange(dnt)) {
			return MESSAGE_INVALID_DATE_TIME_FORMAT;
		}
		
		if(!isValidStartEndDate(dnt)) {
			return MESSAGE_INVALID_END_DATE;
		}
		
		if(!isValidStartEndTime(dnt)) {
			return MESSAGE_INVALID_END_TIME;
		}
		
		checkForClash(task, storage.getTaskIterator());
		storage.addTask(task);
		setTaskID(TDTLogic.sort(storage.getLabelMap().get(getLabelName()), task));
		storage.insertToUndoStack(this);
		
		//Proper return statement!
		return gotClashes?this.getFeedback():String.format(MESSAGE_ADD_FEEDBACK,
															storage.getCurrLabel());
	}
	
	private boolean isValidStartEndTime(TDTDateAndTime dnt) {
		if(TDTDateMethods.compareToDate(dnt.getStartDate(), dnt.getEndDate()) == 0) {
			if(!dnt.getStartTime().equals("null") && !dnt.getEndTime().equals("null")){
				if(TDTTimeMethods.compareToTime(dnt.getStartTime(), dnt.getEndTime()) == -1){
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidStartEndDate(TDTDateAndTime dnt) {
		if(!dnt.getStartDate().equals("null") && !dnt.getEndDate().equals("null")) {
			if(TDTDateMethods.compareToDate(dnt.getStartDate(), dnt.getEndDate()) == -1) {
				return false;
			}
		}
		return true;
	}

	private void checkForClash(Task target, Iterator<Task> iter) {
		int numClash = 0;
		while(iter.hasNext()) {
			Task task = iter.next();
			if(task != target && !task.isDone()) {
				if(target.getDateAndTime().isClash(task.getDateAndTime())) {
					targetTask.add(task);
					numClash++;
					gotClashes = true;
				}
			}
		}
		if(gotClashes) {
			String feedback = "Clashes detected. " + String.format(MESSAGE_ADD_FEEDBACK,
					target.getLabelName()) + "\n" + numClash 
					+ " task(s) found to have same time range on " 
					+ target.getDateAndTime().getStartDate();
			this.setFeedback(feedback);
		}
	}

	@Override
	public String undo(TDTStorage storage) {
		targetTask.clear();
		DeleteCommand comd = new DeleteCommand(getLabelName(), getTaskID());
		comd.execute(storage);
		assert (storage.getUndoStack().size() > 0) : "undostack is empty";
		storage.getUndoStack().pop();
		return "Undo add!";
	}
	
	
	private boolean isValidDateTimeRange(TDTDateAndTime dnt) {
		return TDTDateMethods.isValidDateRange(dnt.getStartDate())
				&& TDTDateMethods.isValidDateRange(dnt.getEndDate())
				&& TDTTimeMethods.isValidTimeRange(dnt.getStartTime())
				&& TDTTimeMethods.isValidTimeRange(dnt.getEndTime());
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
