package todothis.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.commons.Task;
import todothis.logic.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class DoneCommand extends Command {
	private int taskID;
	private String labelName;
	
	public DoneCommand(String labelName, int taskID) {
		super(COMMANDTYPE.DONE);
		this.setTaskID(taskID);
		this.setLabelName(labelName.toUpperCase());
	}
	@Override
	public String execute(TDTStorage storage) {
		String label = getLabelName().toUpperCase();
		int taskId = getTaskID();
		
		//done
		if(label.equals("") && taskId == -1) {
			Iterator<Task> iter = storage.getTaskIterator();
			while(iter.hasNext()) {
				Task next = iter.next();
				next.setDone(!next.isDone());
			}
			
			storage.insertToUndoStack(this);
			return "All tasks are done!";
		}
		
		//done label
		if(!label.equals("") && taskId == -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				for(int i = 0 ; i < array.size(); i ++) {
					Task task = array.get(i);
					task.setDone(!task.isDone());
				}
				
				storage.insertToUndoStack(this);
				return "Tasks under " + label + "are done.";
			} else {
				return "Invalid Command. Label does not exist or invalid task number.";
			}
		}
		
		//done task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = storage.getLabelMap().get(storage.getCurrLabel());
			if(taskId <= array.size() && getTaskID() > 0) {
				Task task = array.get(taskId - 1);
				task.setDone(!task.isDone());
				
				storage.insertToUndoStack(this);
				return "Task done";
			} else {
				return "Invalid Command. Label does not exist or invalid task number.";
			}
		}
		
		//delete task from specific label
		if(!label.equals("") && taskId != -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(taskId <= array.size() && getTaskID() > 0) {
					Task task = array.get(taskId - 1);
					task.setDone(!task.isDone());
					
					storage.insertToUndoStack(this);
					return "Task done";
				} else {
					return "Invalid Command. Label does not exist or invalid task number.";
				}
			} else {
				return "Invalid Command. Label does not exist or invalid task number.";
			}
		}
		//Shouldnt reach here
		return "Invalid command.";
	}
	
	@Override
	public String undo(TDTStorage storage) {
		DoneCommand comd = new DoneCommand(getLabelName(), getTaskID());
		comd.execute(storage);
		assert (storage.getUndoStack().size() > 0) : "undostack is empty";
		storage.getUndoStack().pop();
		return "Undo done command";
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
	

}
