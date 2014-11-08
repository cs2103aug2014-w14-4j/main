package todothis.logic.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import todothis.commons.TDTCommons;
import todothis.commons.Task;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class DeleteCommand extends Command {
	private static final String MESSAGE_UNDO_DELETE_TASK = "Undo delete task.";
	private static final String MESSAGE_DELETE_TASK = "Task deleted";
	private static final String MESSAGE_INVALID = "Invalid command.";
	private static final String MESSAGE_INVALID_TASKID = "Invalid command. Invalid task number.";
	private static final String MESSAGE_INVALID_LABEL = "Invalid command. Label does not exist.";
	private static final String MESSAGE_DELETE_LABEL = "Label deleted.";
	private static final String MESSAGE_UNDO_CLEAR = "Undo clear TodoThis.";
	private static final String MESSAGE_CLEAR = "TodoThis is cleared!";
	private static final String MESSAGE_DELETE_LABEL_TASK = "Task under %s deleted.";
	private static final String MESSAGE_UNDO_DELETE_LABEL_TASK = "Undo delete %s.";
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
	public String execute(TDTDataStore data) {
		prevLabelPointer = data.getCurrLabel();
		String label = getLabelName();
		int taskId = getTaskID();
		
		
		//delete
		if(label.equals("") && taskId == -1) {
			deleteEverything(data);
			data.insertToUndoStack(this);
			setUndoFeedback(MESSAGE_UNDO_CLEAR);
			return MESSAGE_CLEAR;
		}
		
		//delete label
		if(!label.equals("") && taskId == -1) {
			if(data.getTaskMap().containsKey(label)) {
				//If label is empty, delete label
				if(data.getLabelSize(label) == 0) {
					deleteLabel(data, label);
					data.insertToUndoStack(this);
					setUndoFeedback(String.format(MESSAGE_UNDO_DELETE_LABEL_TASK, label));
					return MESSAGE_DELETE_LABEL;
				} else {
					//label not empty, clear task in label
					deleteLabelTask(data, label);
					data.insertToUndoStack(this);
					setUndoFeedback(String.format(MESSAGE_UNDO_DELETE_LABEL_TASK, label));
					return String.format(MESSAGE_DELETE_LABEL_TASK, label);
				}
			} else {
				return MESSAGE_INVALID_LABEL;
			}
		}
		
		//delete task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = data.getTaskMap().get(data.getCurrLabel());
			if(taskId <= array.size() && getTaskID() > 0) {
				deleteTask(data.getCurrLabel(),taskId, data);
				data.insertToUndoStack(this);
				setUndoFeedback(MESSAGE_UNDO_DELETE_TASK);
				return MESSAGE_DELETE_TASK;
			} else {
				return MESSAGE_INVALID_TASKID;
			}
		}
		
		//delete task from specific label
		if(!label.equals("") && taskId != -1) {
			if(data.getTaskMap().containsKey(label)) {
				ArrayList<Task> array = data.getTaskMap().get(label);
				if(taskId <= array.size() && getTaskID() > 0) {
					deleteTask(label,taskId, data);
					data.insertToUndoStack(this);
					setUndoFeedback(MESSAGE_UNDO_DELETE_TASK);
					return MESSAGE_DELETE_TASK;
				} else {
					return MESSAGE_INVALID_TASKID;
				}
			} else {
				return MESSAGE_INVALID_LABEL;
			}
		}
		//Shouldnt reach here
		return MESSAGE_INVALID;
	}

	private void deleteTask(String label, int taskId, TDTDataStore data) {
		setDeleteState(DELETE_TASK);
		undoLabel = label;
		ArrayList<Task> taskList = data.getTaskMap().get(label);
		undoTaskList = copyTaskList(taskList);
		Task task = taskList.get(taskId - 1);
		if(task.hasReminder()) {
			task.getReminder().cancelReminder();
			undoReminderList.add(new RemindCommand(task.getLabelName(), task.getTaskID(), 
						task.getRemindDateTime()));
		}
		taskList.remove(taskId - 1);
		TDTCommons.renumberTaskID(taskList, null);
	}

	
	private void deleteEverything(TDTDataStore data) {
		prevState = copyLabelMap(data);
		setDeleteState(DELETE_ALL);
		Iterator<String> iter = data.getLabelIterator();
		while(iter.hasNext()) {
			String next = iter.next();
			ArrayList<Task> taskList = data.getTaskMap().get(next);
			stopReminderInTaskList(taskList);
		}
		
		data.setTaskMap(new HashMap<String, ArrayList<Task>>());
		data.getTaskMap().put(TDTCommons.DEFAULT_LABEL, new ArrayList<Task>());
		data.setCurrLabel(TDTCommons.DEFAULT_LABEL);
	}
	

	private void deleteLabel(TDTDataStore data, String label) {
		undoLabel = label;
		setDeleteState(DELETE_LABEL);
		data.getTaskMap().remove(label);
		data.getAutoWords().remove(label);
		data.setCurrLabel(TDTCommons.DEFAULT_LABEL);
		if(label.equals(TDTCommons.DEFAULT_LABEL)) {
			data.getTaskMap().put(TDTCommons.DEFAULT_LABEL, new ArrayList<Task>());
			data.insertToAutoWords(TDTCommons.DEFAULT_LABEL);
		}
	}
	
	private void deleteLabelTask(TDTDataStore data, String label) {
		undoLabel = label;
		ArrayList<Task> taskList = data.getTaskMap().get(label);
		undoTaskList = copyTaskList(taskList);
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
	public String undo(TDTDataStore data) {
		data.setCurrLabel(prevLabelPointer);
		if(getDeleteState() == DELETE_ALL) {
			undoDeleteAll(data);
		}
		
		if(getDeleteState() == DELETE_LABEL) {
			undoDeleteLabel(data);
		}
		
		if(getDeleteState() == DELETE_LABELTASK) {
			undoDeleteLabelTask(data);
		}
		
		if(getDeleteState() == DELETE_TASK) {
			undoDeleteTask(data);
		}

		return getUndoFeedback();
	}
	
	private void undoDeleteTask(TDTDataStore data) {
		data.getTaskMap().put(undoLabel, undoTaskList);
		restartReminder(data);
	}

	private void undoDeleteLabelTask(TDTDataStore data) {
		data.getTaskMap().put(undoLabel, undoTaskList);
		restartReminder(data);
	}

	private void undoDeleteLabel(TDTDataStore data) {
		data.getTaskMap().put(undoLabel, new ArrayList<Task>());
		data.setCurrLabel(prevLabelPointer);
	}

	private void undoDeleteAll(TDTDataStore data) {
		data.setTaskMap(prevState);
		restartReminder(data);
	}
	
	private void restartReminder(TDTDataStore data) {
		for(int i = 0; i < undoReminderList.size(); i++) {
			undoReminderList.get(i).execute(data);
			assert(data.getUndoStack().size() > 0);
			data.getUndoStack().pop();
		}
		undoReminderList.clear();
	}
	
	private HashMap<String, ArrayList<Task>> copyLabelMap(TDTDataStore data) {
		HashMap<String, ArrayList<Task>> hmap = new HashMap<String, ArrayList<Task>>();
		Iterator<String> labelIter = data.getLabelIterator();
		Iterator<Task> taskIter = data.getTaskIterator();
		
		while(labelIter.hasNext()) {
			String next = labelIter.next();
			hmap.put(next, new ArrayList<Task>());
		}
		while(taskIter.hasNext()) {
			Task task =  taskIter.next();
			hmap.get(task.getLabelName()).add(new Task(task.getTaskID(), task.getLabelName(),
					 task.getDetails(), task.getDateAndTime(), task.isHighPriority(), 
					 task.isDone(), "null"));
		}
		return hmap;
	}
	
	private ArrayList<Task> copyTaskList(ArrayList<Task> array) {
		ArrayList<Task> res = new ArrayList<Task>();
		Iterator<Task> taskIter = array.iterator();
		while(taskIter.hasNext()) {
			Task task =  taskIter.next();
			res.add(new Task(task.getTaskID(), task.getLabelName(),
					 task.getDetails(), task.getDateAndTime(), task.isHighPriority(), 
					 task.isDone(), "null"));
		}
		return res;
	}
	
	/*
	@Override
	public String execute(TDTDataStore data) {
		prevState = data.copyLabelMap();
		prevLabelPointer = data.getCurrLabel();
		String label = getLabelName().toUpperCase();
		int taskId = getTaskID();
		
		data.insertToUndoStack(this);
		//delete
		if(label.equals("") && taskId == -1) {
			deleteEverything(data);
			
			setUndoFeedback("Undo delete");
			return data.getFileName() + " is cleared!";
		}
		
		//delete label
		if(!label.equals("") && taskId == -1) {
			if(data.getLabelMap().containsKey(label)) {
				//If label is empty, delete label
				if(data.getLabelSize(label) == 0) {
					deleteLabel(data, label);
					
					setUndoFeedback("Undo delete " + label);
					return "Label deleted.";
				} else {
					//label not empty, clear task in label
					data.getLabelMap().get(label).clear();
				}
			} else {
				return "Invalid command. Label does not exist.";
			}
		}
		
		//delete task from current label
		if(label.equals("") && taskId != -1) {
			ArrayList<Task> array = data.getLabelMap().get(data.getCurrLabel());
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
			if(data.getLabelMap().containsKey(label)) {
				ArrayList<Task> array = data.getLabelMap().get(label);
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
	public String undo(TDTDataStore data) {
		//May not undo the reminders.
		//Delete may not off the reminders.
		//Suggest
		//Delete all go thru each task to off reminders
		//Undo go thru each task to add back reminder
		data.setLabelMap(prevState);
		data.setCurrLabel(prevLabelPointer);
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
