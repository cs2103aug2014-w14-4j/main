package todothis.command;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class UndoCommand extends Command {

	public UndoCommand() {
		super(COMMANDTYPE.UNDO);
	}

	@Override
	public String execute(TDTStorage storage) {
		assert (storage.getUndoStack().size() > 0) : "undostack is empty";
		storage.getRedoStack().push(storage.getUndoStack().pop());
		storage.getRedoLabelPointerStack().push(storage.getLabelPointerStack().pop());
		if(!storage.getUndoStack().isEmpty()) {
			storage.setLabelMap(storage.getUndoStack().pop());
			storage.setCurrLabel(storage.getLabelPointerStack().pop());
			
			return "Undo success!";
		} else {
			storage.getRedoStack().pop();
			storage.getRedoLabelPointerStack().pop();
			return "No command to undo.";
		}
	}

}
