package todothis.command;

import java.util.ArrayList;

import todothis.logic.Task;
import todothis.parser.TDTDateAndTime;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class EditCommand extends Command {
	private int taskID;
	private String labelName;
	private String commandDetails;
	private TDTDateAndTime dateAndTime;
	private boolean isHighPriority;
	private String prevDetails;
	private TDTDateAndTime prevDNT;
	private boolean prevPriority;
	private ArrayList<Task> editedTask;

	
	public EditCommand(String labelName, int taskID,
			String commandDetails, TDTDateAndTime dateAndTime, 
			boolean isHighPriority ) {
		super(COMMANDTYPE.EDIT);
		this.setTaskID(taskID);
		this.setHighPriority(isHighPriority);
		this.setLabelName(labelName.toUpperCase());
		this.setCommandDetails(commandDetails);
		this.setDateAndTime(dateAndTime);
		this.setEditedTask(new ArrayList<Task>());
	}

	@Override
	public String execute(TDTStorage storage) {
		String label = getLabelName().toUpperCase();
		int taskId = getTaskID();
		String commandDetails = getCommandDetails();
		boolean isHighPriority = isHighPriority();
		TDTDateAndTime dateAndTime = getDateAndTime();

		//edit task from current label
		if(label.equals("") && taskId != -1) {
			setLabelName(storage.getCurrLabel());
			ArrayList<Task> array = storage.getLabelMap().get(getLabelName());
			if(taskId <= array.size() && getTaskID() > 0) {
				Task task = array.get(taskId - 1);
				editedTask.add(task);
				prevDetails = task.getDetails();
				prevDNT = task.getDateAndTime();
				prevPriority = task.isHighPriority();
				task.setDetails(commandDetails);
				task.setDateAndTime(dateAndTime);
				task.setHighPriority(isHighPriority);
				
				//setTaskID(TDTLogic.sort(array, task));
				storage.insertToUndoStack(this);
				return "Task edited";
			} else {
				return "Invalid Command. Label does not exist or invalid task number.";
			}
		}

		//edit task from specific label
		if(!label.equals("") && taskId != -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(taskId <= array.size() && getTaskID() > 0) {
					Task task = array.get(taskId - 1);
					editedTask.add(task);
					prevDetails = task.getDetails();
					prevDNT = task.getDateAndTime();
					prevPriority = task.isHighPriority();
					task.setDetails(commandDetails);
					task.setDateAndTime(dateAndTime);
					task.setHighPriority(isHighPriority);
					
					//setTaskID(TDTLogic.sort(array, task));
					storage.insertToUndoStack(this);
					return "Task edited";
				} else {
					return "Invalid Command. Label does not exist or invalid task number.";
				}
			} else {
				return "Invalid Command. Label does not exist or invalid task number.";
			}
		}
		
		return "Invalid Command";
	}
	

	@Override
	public String undo(TDTStorage storage) {
		EditCommand comd = new EditCommand(getLabelName(), getTaskID(), 
								prevDetails, prevDNT, prevPriority);
		comd.execute(storage);
		assert (storage.getUndoStack().size() > 0) : "undostack is empty";
		storage.getUndoStack().pop();
		return "Undo edit";
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

	public ArrayList<Task> getEditedTask() {
		return editedTask;
	}

	public void setEditedTask(ArrayList<Task> editedTask) {
		this.editedTask = editedTask;
	}


}
