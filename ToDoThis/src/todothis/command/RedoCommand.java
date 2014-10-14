package todothis.command;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class RedoCommand extends Command{

	public RedoCommand() {
		super(COMMANDTYPE.REDO);
	}

	@Override
	public String execute(TDTStorage storage) {
		if(!storage.getRedoStack().isEmpty()) {
			storage.setLabelMap(storage.getRedoStack().pop());
			storage.setCurrLabel(storage.getRedoLabelPointerStack().pop());
			
			return "Redo success!";
		} else {
			storage.getUndoStack().pop();
			storage.getLabelPointerStack().pop();
			return "No command to redo.";
		}
	}

}
