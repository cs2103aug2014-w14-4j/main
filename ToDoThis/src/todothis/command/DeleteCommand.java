package todothis.command;

import java.util.ArrayList;
import java.util.HashMap;

import todothis.TDTGUI;
import todothis.logic.TDTLogic;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class DeleteCommand extends Command {
	private int taskID;
	private String labelName;
	private HashMap<String, ArrayList<Task>> prevState;
	private String prevLabel;
	private String undoFeedback = "";
	
	public DeleteCommand(String labelName, int taskID) {
		super(COMMANDTYPE.DELETE);
		this.setTaskID(taskID);
		this.setLabelName(labelName.toUpperCase());
	}

	@Override
	public String execute(TDTStorage storage) {
		prevState = storage.copyLabelMap();
		prevLabel = storage.getCurrLabel();
		String label = getLabelName().toUpperCase();
		int taskId = getTaskID();
		
		storage.insertToUndoStack(this);
		//delete
		if(label.equals("") && taskId == -1) {
			deleteEverything(storage);
			
			setUndoFeedback("Undo delete");
			return storage.getFileName() + " is cleared!";
		}
		
		//delete label
		if(!label.equals("") && taskId == -1) {
			if(storage.getLabelMap().containsKey(label)) {
				//If label is empty, delete label
				if(storage.getLabelSize(label) == 0) {
					deleteLabel(storage, label);
					
					setUndoFeedback("Undo delete " + label);
					return "Label deleted.";
				} else {
					//label not empty, clear task in label
					storage.getLabelMap().get(label).clear();
				}
			} else {
				return "Invalid command. Label does not exist.";
			}
		}
		
		//delete task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = storage.getLabelMap().get(storage.getCurrLabel());
			if(taskId <= array.size() && getTaskID() > 0) {
				array.remove(taskId - 1);
				TDTLogic.renumberTaskID(array, null);
				
				return "Task deleted";
			} else {
				return "Invalid command. Invalid task number.";
			}
		}
		
		//delete task from specific label
		if(!label.equals("") && taskId != -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(taskId <= array.size() && getTaskID() > 0) {
					array.remove(taskId - 1);
					TDTLogic.renumberTaskID(array, null);
					return "Task deleted";
				} else {
					return "Invalid command. Invalid task number.";
				}
			} else {
				return "Invalid command. Label does not exist.";
			}
		}
		//Shouldnt reach here
		return "Invalid command.";
	}

	@Override
	public String undo(TDTStorage storage) {
		//May not undo the reminders.
		//Delete may not off the reminders.
		//Suggest
		//Delete all go thru each task to off reminders
		//Undo go thru each task to add back reminder
		storage.setLabelMap(prevState);
		storage.setCurrLabel(prevLabel);
		return getUndoFeedback();
	}
	
	private void deleteEverything(TDTStorage storage) {
		storage.setLabelMap(new HashMap<String, ArrayList<Task>>());
		storage.getLabelMap().put(TDTGUI.DEFAULT_LABEL, new ArrayList<Task>());
		storage.setCurrLabel(TDTGUI.DEFAULT_LABEL);
	}
	

	private void deleteLabel(TDTStorage storage, String label) {
		storage.getLabelMap().remove(label);
		storage.getAutoWords().remove(label);
		if(label.equals(TDTGUI.DEFAULT_LABEL)) {
			storage.getLabelMap().put(TDTGUI.DEFAULT_LABEL, new ArrayList<Task>());
			storage.insertToAutoWords(TDTGUI.DEFAULT_LABEL);
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

	public String getUndoFeedback() {
		return undoFeedback;
	}

	public void setUndoFeedback(String undoFeedback) {
		this.undoFeedback = undoFeedback;
	}

	

}
