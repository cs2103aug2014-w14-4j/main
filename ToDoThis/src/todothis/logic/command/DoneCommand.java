package todothis.logic.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.commons.Task;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class DoneCommand extends Command {
	private static final String MESSAGE_DONE_TASK_FEEDBACK = "Task done";
	private static final String MESSAGE_UNDO_DONE = "Undo done";
	private static final String MESSAGE_INVALID_COMMAND = "Invalid command.";
	private static final String MESAGE_INVALID_LABEL_TASKID = "Invalid Command. Label does not exist or invalid task number.";
	private static final String MESSAGE_DONE_ALL_FEEDBACK = "All tasks are done!";
	private static final String MESSAGE_DONE_LABEL_FEEDBACK = "Tasks under %s are done.";

	private int taskID;
	private String labelName;
	private boolean isDone = true;

	/**
	 * Construct a DoneCommand object
	 * 
	 * @param labelName
	 * @param taskID
	 */
	public DoneCommand(String labelName, int taskID) {
		super(COMMANDTYPE.DONE);
		this.setTaskID(taskID);
		this.setLabelName(labelName.toUpperCase());
	}

	/**
	 * Mark all the task as done if labelName is blank and taskID = -1 Mark all
	 * the task as done under a label if taskID = -1 Mark task from current
	 * label as done if taskID != -1 and label is blank Mark task from specific
	 * label as done if taskID != -1 and label is not blank
	 */
	@Override
	public String execute(TDTDataStore data) {
		String label = getLabelName().toUpperCase();
		int taskId = getTaskID();

		// done
		if (label.equals("") && taskId == -1) {
			Iterator<Task> iter = data.getTaskIterator();
			while (iter.hasNext()) {
				Task next = iter.next();
				next.setDone(isDone);
			}

			data.insertToUndoStack(this);
			return MESSAGE_DONE_ALL_FEEDBACK;
		}

		// done label
		if (!label.equals("") && taskId == -1) {
			if (data.getTaskMap().containsKey(label)) {
				ArrayList<Task> array = data.getTaskMap().get(label);
				for (int i = 0; i < array.size(); i++) {
					Task task = array.get(i);
					task.setDone(isDone);
				}

				data.insertToUndoStack(this);
				return MESSAGE_DONE_LABEL_FEEDBACK;
			} else {
				return MESAGE_INVALID_LABEL_TASKID;
			}
		}

		// done task from current label
		if (label.equals("") && taskId != -1) {
			ArrayList<Task> array = data.getTaskMap().get(data.getCurrLabel());
			if (taskId <= array.size() && getTaskID() > 0) {
				Task task = array.get(taskId - 1);
				task.setDone(isDone);

				data.insertToUndoStack(this);
				return MESSAGE_DONE_TASK_FEEDBACK;
			} else {
				return MESAGE_INVALID_LABEL_TASKID;
			}
		}

		// delete task from specific label
		if (!label.equals("") && taskId != -1) {
			if (data.getTaskMap().containsKey(label)) {
				ArrayList<Task> array = data.getTaskMap().get(label);
				if (taskId <= array.size() && getTaskID() > 0) {
					Task task = array.get(taskId - 1);
					task.setDone(isDone);

					data.insertToUndoStack(this);
					return MESSAGE_DONE_TASK_FEEDBACK;
				} else {
					return MESAGE_INVALID_LABEL_TASKID;
				}
			} else {
				return MESAGE_INVALID_LABEL_TASKID;
			}
		}
		// Shouldnt reach here
		return MESSAGE_INVALID_COMMAND;
	}

	/**
	 * Reverses the effect of execute.
	 */
	@Override
	public String undo(TDTDataStore data) {
		DoneCommand comd = new DoneCommand(getLabelName(), getTaskID());
		comd.setDone(false);
		comd.execute(data);
		assert (data.getUndoStack().size() > 0) : "undostack is empty";
		data.getUndoStack().pop();
		return MESSAGE_UNDO_DONE;
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

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

}
