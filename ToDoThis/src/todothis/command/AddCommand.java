package todothis.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.dateandtime.TDTDateAndTime;
import todothis.logic.TDTLogic;
import todothis.logic.Task;
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
	private Task addedTask;
	private String feedback = "";
	private ArrayList<Task> clashedTask = new ArrayList<Task>();
	private boolean gotClashes = false;

	public AddCommand(String commandDetails, TDTDateAndTime dateAndTime, boolean isHighPriority ) {
		super(COMMANDTYPE.ADD);
		this.setHighPriority(isHighPriority);
		this.setCommandDetails(commandDetails);
		this.setDateAndTime(dateAndTime);
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
		if(TDTDateAndTime.compareToDate(dnt.getStartDate(), dnt.getEndDate()) == 0) {
			if(!dnt.getStartTime().equals("null") && !dnt.getEndTime().equals("null")){
				if(TDTDateAndTime.compareToTime(dnt.getStartTime(), dnt.getEndTime()) == -1){
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidStartEndDate(TDTDateAndTime dnt) {
		if(!dnt.getStartDate().equals("null") && !dnt.getEndDate().equals("null")) {
			if(TDTDateAndTime.compareToDate(dnt.getStartDate(), dnt.getEndDate()) == -1) {
				return false;
			}
		}
		return true;
	}

	private void checkForClash(Task target, Iterator<Task> iter) {
		while(iter.hasNext()) {
			Task task = iter.next();
			if(target.getDateAndTime().isClash(task.getDateAndTime())) {
				clashedTask.add(task);
			}
		}
		if(!clashedTask.isEmpty()) {
			gotClashes = true;
			String feedback = "Clashes detected. " + String.format(MESSAGE_ADD_FEEDBACK,
					target.getLabelName()) + "\n" + clashedTask.size() 
					+ " task(s) found to have same time range on " 
					+ target.getDateAndTime().getStartDate();
			this.setFeedback(feedback);
		}
	}

	@Override
	public String undo(TDTStorage storage) {
		DeleteCommand comd = new DeleteCommand(getLabelName(), getTaskID());
		comd.execute(storage);
		assert (storage.getUndoStack().size() > 0) : "undostack is empty";
		storage.getUndoStack().pop();
		return "Undo add!";
	}
	
	
	private boolean isValidDateTimeRange(TDTDateAndTime dnt) {
		return TDTDateAndTime.isValidDateRange(dnt.getStartDate()) && 
			   TDTDateAndTime.isValidDateRange(dnt.getEndDate())   &&
			   TDTDateAndTime.isValidTimeRange(dnt.getStartTime()) &&
			   TDTDateAndTime.isValidTimeRange(dnt.getEndTime());
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

	public Task getAddedTask() {
		return addedTask;
	}

	public void setAddedTask(Task addedTask) {
		this.addedTask = addedTask;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	

}
