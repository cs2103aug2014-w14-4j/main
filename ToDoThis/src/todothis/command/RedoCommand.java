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
			Command comd = storage.getRedoStack().pop();
			return comd.execute(storage);
		} else {
			return "Invalid command. No command to redo.";
		}
	}

	@Override
	public String undo(TDTStorage storage) {
		return null;
	}

}
