package todothis.command;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class UndoCommand extends Command {

	public UndoCommand() {
		super(COMMANDTYPE.UNDO);
	}

	@Override
	public String execute(TDTStorage storage) {
		storage.getRedoStack().push(storage.getUndoStack().pop());
		storage.getRedoLabelPointerStack().push(storage.getLabelPointerStack().pop());
		if(!storage.getUndoStack().isEmpty()) {
			storage.setLabelMap(storage.getUndoStack().pop());
			storage.setCurrLabel(storage.getLabelPointerStack().pop());
			
			return "Undo success!";
		} else {
			storage.getRedoStack().clear();
			storage.getRedoLabelPointerStack().clear();
			return "No command to undo.";
		}
	}

}
