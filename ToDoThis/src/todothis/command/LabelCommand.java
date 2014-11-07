package todothis.command;

import java.util.ArrayList;

import todothis.commons.Task;
import todothis.logic.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class LabelCommand extends Command {
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
			return "Invalid command. Invalid Label name.";
		}
		
		if(data.getTaskMap().containsKey(label[0])) {
			data.setCurrLabel(label[0]);
			data.getHideList().remove(label[0]);
			setUndoFeedback("Current label change to: " + prevLabel);
			data.insertToUndoStack(this);
			return "Current label change to: " + label[0];
		} else if(label[0].equals("") || label[0].matches("\\d+")) {
			return "Invalid command. Label name cannot be blank or digits only.";
		} else {
			data.getTaskMap().put(label[0], new ArrayList<Task>());
			data.insertToAutoWords(label[0]);
			data.setCurrLabel(label[0]);
			newLabelCreated = true;
			
			setUndoFeedback("Label " + label[0] + " deleted");
			data.insertToUndoStack(this);
			return "Label " + label[0] + " created";
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
