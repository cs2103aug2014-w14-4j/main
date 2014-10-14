package todothis.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class DoneCommand extends Command {
	private int taskID;
	private String labelName;
	
	public DoneCommand(String labelName, int taskID) {
		super(COMMANDTYPE.DONE);
		this.setTaskID(taskID);
		this.setLabelName(labelName);
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
				next.setDone(true);
			}
			return "All tasks are done!";
		}
		
		//done label
		if(!label.equals("") && taskId == -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				for(int i = 0 ; i < array.size(); i ++) {
					array.get(i).setDone(true);
				}
				return "Tasks under " + label + "are done.";
			} else {
				return "Error. Label does not exist.";
			}
		}
		
		//done task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = storage.getLabelMap().get(storage.getCurrLabel());
			if(taskId <= array.size() && getTaskID() > 0) {
				array.get(taskId - 1).setDone(true);
				return "Task done";
			} else {
				return "error. Invalid task number.";
			}
		}
		
		//delete task from specific label
		if(!label.equals("") && taskId != -1) {
			if(storage.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = storage.getLabelMap().get(label);
				if(taskId <= array.size() && getTaskID() > 0) {
					array.get(taskId - 1).setDone(true);
					return "Task done";
				} else {
					return "error. Invalid task number.";
				}
			} else {
				return "Error. Label does not exist.";
			}
		}
		//Shouldnt reach here
		return "Error. Invalid done.";
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
