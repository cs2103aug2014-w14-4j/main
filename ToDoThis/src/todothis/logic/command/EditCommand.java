package todothis.logic.command;

import java.util.ArrayList;

import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

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
	public String execute(TDTDataStore data) {
		String label = getLabelName().toUpperCase();
		int taskId = getTaskID();
		String commandDetails = getCommandDetails();
		boolean isHighPriority = isHighPriority();
		TDTDateAndTime dateAndTime = getDateAndTime();

		//edit task from current label
		if(label.equals("") && taskId != -1) {
			setLabelName(data.getCurrLabel());
			ArrayList<Task> array = data.getTaskMap().get(getLabelName());
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
				data.insertToUndoStack(this);
				return "Task edited";
			} else {
				return "Invalid Command. Label does not exist or invalid task number.";
			}
		}

		//edit task from specific label
		if(!label.equals("") && taskId != -1) {
			if(data.getTaskMap().containsKey(label)) {
				ArrayList<Task> array = data.getTaskMap().get(label);
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
					data.insertToUndoStack(this);
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
	public String undo(TDTDataStore data) {
		EditCommand comd = new EditCommand(getLabelName(), getTaskID(), 
								prevDetails, prevDNT, prevPriority);
		comd.execute(data);
		assert (data.getUndoStack().size() > 0) : "undostack is empty";
		data.getUndoStack().pop();
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