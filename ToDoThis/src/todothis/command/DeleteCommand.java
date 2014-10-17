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
	
	public DeleteCommand(String labelName, int taskID) {
		super(COMMANDTYPE.DELETE);
		this.setTaskID(taskID);
		this.setLabelName(labelName);
	}

	@Override
	public String execute(TDTStorage storage) {
		String label = getLabelName().toUpperCase();
		int taskId = getTaskID();
		
		//delete
		if(label.equals("") && taskId == -1) {
			storage.setLabelMap(new HashMap<String, ArrayList<Task>>());
			storage.getLabelMap().put(TDTGUI.DEFAULT_LABEL, new ArrayList<Task>());
			storage.setCurrLabel(TDTGUI.DEFAULT_LABEL);
			return storage.getFileName() + " is cleared!";
		}
		
		//delete label
		if(!label.equals("") && taskId == -1) {
			if(storage.getLabelMap().containsKey(label)) {
				storage.getLabelMap().remove(label);
				if(label.equals(TDTGUI.DEFAULT_LABEL)) {
					storage.getLabelMap().put(TDTGUI.DEFAULT_LABEL, new ArrayList<Task>());
				}
				return "Label deleted.";
			} else {
				return "Error. Label does not exist.";
			}
		}
		
		//delete task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = storage.getLabelMap().get(storage.getCurrLabel());
			if(taskId <= array.size() && getTaskID() > 0) {
				array.remove(taskId - 1);
				TDTLogic.renumberTaskID(array);
				return "Task deleted";
			} else {
				return "error. Invalid task number.";
			}
		}
		
		//delete task from specific label
		if(!label.equals("") && taskId != -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(taskId <= array.size() && getTaskID() > 0) {
					array.remove(taskId - 1);
					TDTLogic.renumberTaskID(array);
					return "Task deleted";
				} else {
					return "error. Invalid task number.";
				}
			} else {
				return "error. Label does not exist.";
			}
		}
		//Shouldnt reach here
		return "Error. Invalid delete.";
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

}
