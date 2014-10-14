package todothis.command;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class UndoCommand extends Command {

	public UndoCommand() {
		super(COMMANDTYPE.UNDO);
	}

	@Override
	public String execute(TDTStorage storage) {
		if(!storage.getUndoStack().isEmpty()) {
			storage.getLabelPointerStack().pop();
			storage.getUndoStack().pop();
			
			storage.setLabelMap(storage.getUndoStack().pop());
			storage.setCurrLabel(storage.getLabelPointerStack().pop());
			return "Undo success!";
		} else {
			return "No command to undo.";
		}
	}

}
