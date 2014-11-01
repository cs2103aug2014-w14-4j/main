package todothis.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import todothis.TDTGUI;
import todothis.logic.TDTLogic;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class DeleteCommand extends Command {
	private static final int DELETE_ALL = 0;
	private static final int DELETE_LABELTASK = 1;
	private static final int DELETE_LABEL = 2;
	private static final int DELETE_TASK = 3;
	
	private int deleteState = -1;
	private int taskID;
	private String labelName;
	private HashMap<String, ArrayList<Task>> prevState;
	private String prevLabelPointer;
	private ArrayList<RemindCommand> undoReminderList = new ArrayList<RemindCommand>();
	private ArrayList<Task> undoTaskList = new ArrayList<Task>();

	private String undoLabel;
	private String undoFeedback = "";
	
	public DeleteCommand(String labelName, int taskID) {
		super(COMMANDTYPE.DELETE);
		this.setTaskID(taskID);
		this.setLabelName(labelName.toUpperCase());
	}
	
	@Override
	public String execute(TDTStorage storage) {
		prevLabelPointer = storage.getCurrLabel();
		String label = getLabelName();
		int taskId = getTaskID();
		
		
		//delete
		if(label.equals("") && taskId == -1) {
			deleteEverything(storage);
			storage.insertToUndoStack(this);
			setUndoFeedback("Undo clear TodoThis.");
			return storage.getFileName() + " is cleared!";
		}
		
		//delete label
		if(!label.equals("") && taskId == -1) {
			if(storage.getLabelMap().containsKey(label)) {
				//If label is empty, delete label
				if(storage.getLabelSize(label) == 0) {
					deleteLabel(storage, label);
					storage.insertToUndoStack(this);
					setUndoFeedback("Undo delete " + label);
					return "Label deleted.";
				} else {
					//label not empty, clear task in label
					deleteLabelTask(storage, label);
					storage.insertToUndoStack(this);
					setUndoFeedback("Undo delete " + label);
					return "Task under " + label + " deleted.";
				}
			} else {
				return "Invalid command. Label does not exist.";
			}
		}
		
		//delete task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = storage.getLabelMap().get(storage.getCurrLabel());
			if(taskId <= array.size() && getTaskID() > 0) {
				deleteTask(storage.getCurrLabel(),taskId, storage);
				storage.insertToUndoStack(this);
				setUndoFeedback("Undo delete task.");
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
					deleteTask(label,taskId, storage);
					storage.insertToUndoStack(this);
					setUndoFeedback("Undo delete task.");
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

	private void deleteTask(String label, int taskId, TDTStorage storage) {
		setDeleteState(DELETE_TASK);
		undoLabel = label;
		ArrayList<Task> taskList = storage.getLabelMap().get(label);
		undoTaskList = storage.copyTaskList(taskList);
		Task task = taskList.get(taskId - 1);
		if(task.hasReminder()) {
			task.getReminder().cancelReminder();
			undoReminderList.add(new RemindCommand(task.getLabelName(), task.getTaskID(), 
						task.getRemindDateTime()));
		}
		taskList.remove(taskId - 1);
		TDTLogic.renumberTaskID(taskList, null);
	}

	
	private void deleteEverything(TDTStorage storage) {
		prevState = storage.copyLabelMap();
		setDeleteState(DELETE_ALL);
		Iterator<String> iter = storage.getLabelIterator();
		while(iter.hasNext()) {
			String next = iter.next();
			ArrayList<Task> taskList = storage.getLabelMap().get(next);
			stopReminderInTaskList(taskList);
		}
		
		storage.setLabelMap(new HashMap<String, ArrayList<Task>>());
		storage.getLabelMap().put(TDTGUI.DEFAULT_LABEL, new ArrayList<Task>());
		storage.setCurrLabel(TDTGUI.DEFAULT_LABEL);
	}
	

	private void deleteLabel(TDTStorage storage, String label) {
		undoLabel = label;
		setDeleteState(DELETE_LABEL);
		storage.getLabelMap().remove(label);
		storage.getAutoWords().remove(label);
		storage.setCurrLabel(TDTGUI.DEFAULT_LABEL);
		if(label.equals(TDTGUI.DEFAULT_LABEL)) {
			storage.getLabelMap().put(TDTGUI.DEFAULT_LABEL, new ArrayList<Task>());
			storage.insertToAutoWords(TDTGUI.DEFAULT_LABEL);
		}
	}
	
	private void deleteLabelTask(TDTStorage storage, String label) {
		undoLabel = label;
		ArrayList<Task> taskList = storage.getLabelMap().get(label);
		undoTaskList = storage.copyTaskList(taskList);
		setDeleteState(DELETE_LABELTASK);
		stopReminderInTaskList(taskList);
		taskList.clear();
	}
	
	private void stopReminderInTaskList(ArrayList<Task> array) {
		Iterator<Task> iter = array.iterator();
		while(iter.hasNext()) {
			Task task = iter.next();
			if(task.hasReminder()) {
				task.getReminder().cancelReminder();
				undoReminderList.add(new RemindCommand(task.getLabelName(), task.getTaskID(), 
						task.getRemindDateTime()));
			}
		}
	}
	
	
	@Override
	public String undo(TDTStorage storage) {
		storage.setCurrLabel(prevLabelPointer);
		if(getDeleteState() == DELETE_ALL) {
			undoDeleteAll(storage);
		}
		
		if(getDeleteState() == DELETE_LABEL) {
			undoDeleteLabel(storage);
		}
		
		if(getDeleteState() == DELETE_LABELTASK) {
			undoDeleteLabelTask(storage);
		}
		
		if(getDeleteState() == DELETE_TASK) {
			undoDeleteTask(storage);
		}

		return getUndoFeedback();
	}
	
	private void undoDeleteTask(TDTStorage storage) {
		storage.getLabelMap().put(undoLabel, undoTaskList);
		restartReminder(storage);
	}

	private void undoDeleteLabelTask(TDTStorage storage) {
		storage.getLabelMap().put(undoLabel, undoTaskList);
		restartReminder(storage);
	}

	private void undoDeleteLabel(TDTStorage storage) {
		storage.getLabelMap().put(undoLabel, new ArrayList<Task>());
		storage.setCurrLabel(prevLabelPointer);
	}

	private void undoDeleteAll(TDTStorage storage) {
		storage.setLabelMap(prevState);
		restartReminder(storage);
	}
	
	private void restartReminder(TDTStorage storage) {
		for(int i = 0; i < undoReminderList.size(); i++) {
			undoReminderList.get(i).execute(storage);
			assert(storage.getUndoStack().size() > 0);
			storage.getUndoStack().pop();
		}
		undoReminderList.clear();
	}
	/*
	@Override
	public String execute(TDTStorage storage) {
		prevState = storage.copyLabelMap();
		prevLabelPointer = storage.getCurrLabel();
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
		storage.setCurrLabel(prevLabelPointer);
		return getUndoFeedback();
	}*/
	
	
	
	//------------------------------GETTERS & SETTERS -------------------------------
	
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

	public int getDeleteState() {
		return deleteState;
	}

	public void setDeleteState(int deleteState) {
		this.deleteState = deleteState;
	}

	

}
