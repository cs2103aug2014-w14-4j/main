package todothis.logic.command;

import java.util.ArrayList;

import todothis.commons.Task;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class LabelCommand extends Command {
	private static final String MESSAGE_INVALID_ADD_LABEL = "Invalid command. Label name cannot be blank or digits only.";
	private static final String MESSAGE_INVALID_LABEL = "Invalid command. Invalid Label name.";
	private static final String MESSAGE_LABEL_FEEDBACK = "Current label change to: %s";
	private static final String MESSAGE_CREATE_LABEL_FEEDBACK = "Label %s created.";
	private static final String MESSAGE_UNDO_LABEL_FEEDBACK = "Label %s deleted.";
	
	private String labelName;
	private String prevLabel;
	private String undoFeedback;
	private boolean newLabelCreated = false;
	
	public LabelCommand(String labelName) {
		super(COMMANDTYPE.LABEL);
		this.setLabelName(labelName);
	}

	@Override
	public String execute(TDTDataStore data) {
		prevLabel = data.getCurrLabel();
		String[] label = getLabelName().toUpperCase().split(" ");
		
		if(label.length > 1 || label.length <= 0) {
			return MESSAGE_INVALID_LABEL;
		}
		
		if(data.getTaskMap().containsKey(label[0])) {
			data.setCurrLabel(label[0]);
			data.getHideList().remove(label[0]);
			setUndoFeedback(String.format(MESSAGE_LABEL_FEEDBACK, prevLabel));
			data.insertToUndoStack(this);
			return String.format(MESSAGE_LABEL_FEEDBACK, label[0]);
		} else if(label[0].equals("") || label[0].matches("\\d+")) {
			return MESSAGE_INVALID_ADD_LABEL;
		} else {
			data.getTaskMap().put(label[0], new ArrayList<Task>());
			data.insertToAutoWords(label[0]);
			data.setCurrLabel(label[0]);
			newLabelCreated = true;
			
			setUndoFeedback(String.format(MESSAGE_UNDO_LABEL_FEEDBACK, label[0]));
			data.insertToUndoStack(this);
			return String.format(MESSAGE_CREATE_LABEL_FEEDBACK, label[0]);
		}
	}
	
	@Override
	public String undo(TDTDataStore data) {
		data.setCurrLabel(prevLabel);
		if(newLabelCreated) {
			DeleteCommand comd = new DeleteCommand(labelName, -1);
			comd.execute(data);
			assert (data.getUndoStack().size() > 0) : "undostack is empty";
			data.getUndoStack().pop();
		}
		return getUndoFeedback();
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getUndoFeedback() {
		return undoFeedback;
	}

	public void setUndoFeedback(String undoFeedback) {
		this.undoFeedback = undoFeedback;
	}

	

}
