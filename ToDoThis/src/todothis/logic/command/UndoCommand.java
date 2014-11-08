package todothis.logic.command;

import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class UndoCommand extends Command {

	public UndoCommand() {
		super(COMMANDTYPE.UNDO);
	}
	/*
	@Override
	public String execute(TDTDataStore data) {
		assert (data.getUndoStack().size() > 0) : "undostack is empty";
		data.getRedoStack().push(data.getUndoStack().pop());
		data.getRedoLabelPointerStack().push(data.getLabelPointerStack().pop());
		if(!data.getUndoStack().isEmpty()) {
			data.setLabelMap(data.getUndoStack().pop());
			data.setCurrLabel(data.getLabelPointerStack().pop());
			
			return "Undo success!";
		} else {
			data.getRedoStack().pop();
			data.getRedoLabelPointerStack().pop();
			return "No command to undo.";
		}
	}*/
	
	@Override
	public String execute(TDTDataStore data) {
		
		if(!data.getUndoStack().isEmpty()) {
			
			Command comd = data.getUndoStack().pop();
			data.getRedoStack().push(comd);
			
			return comd.undo(data);
		} else {
			return "Invalid command. No command to undo.";
		}
	}

	@Override
	public String undo(TDTDataStore data) {
		
		return null;
	}

}
