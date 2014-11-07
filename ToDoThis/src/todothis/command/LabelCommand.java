package todothis.command;

import java.util.ArrayList;

import todothis.commons.Task;
import todothis.logic.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

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
	public String execute(TDTStorage storage) {
		prevLabel = storage.getCurrLabel();
		String[] label = getLabelName().toUpperCase().split(" ");
		
		if(label.length > 1 || label.length <= 0) {
			return "Invalid command. Invalid Label name.";
		}
		
		if(storage.getLabelMap().containsKey(label[0])) {
			storage.setCurrLabel(label[0]);
			storage.getHideList().remove(label[0]);
			setUndoFeedback("Current label change to: " + prevLabel);
			storage.insertToUndoStack(this);
			return "Current label change to: " + label[0];
		} else if(label[0].equals("") || label[0].matches("\\d+")) {
			return "Invalid command. Label name cannot be blank or digits only.";
		} else {
			storage.getLabelMap().put(label[0], new ArrayList<Task>());
			storage.insertToAutoWords(label[0]);
			storage.setCurrLabel(label[0]);
			newLabelCreated = true;
			
			setUndoFeedback("Label " + label[0] + " deleted");
			storage.insertToUndoStack(this);
			return "Label " + label[0] + " created";
		}
	}
	
	@Override
	public String undo(TDTStorage storage) {
		storage.setCurrLabel(prevLabel);
		if(newLabelCreated) {
			DeleteCommand comd = new DeleteCommand(labelName, -1);
			comd.execute(storage);
			assert (storage.getUndoStack().size() > 0) : "undostack is empty";
			storage.getUndoStack().pop();
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
