package todothis.logic.command;

import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class RedoCommand extends Command{
	
	public RedoCommand() {
		super(COMMANDTYPE.REDO);
	}

	@Override
	public String execute(TDTDataStore data) {
		if(!data.getRedoStack().isEmpty()) {
			Command comd = data.getRedoStack().pop();
			return comd.execute(data);
		} else {
			return "Invalid command. No command to redo.";
		}
	}

	@Override
	public String undo(TDTDataStore data) {
		return null;
	}

}
