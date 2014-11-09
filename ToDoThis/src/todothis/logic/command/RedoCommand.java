package todothis.logic.command;

import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class RedoCommand extends Command{
	private Command comd;
	private static final String MESSAGE_INVALID_REDO = "Invalid command. No command to redo.";

	public RedoCommand() {
		super(COMMANDTYPE.REDO);
	}
	
	/**
	 * Pop the previous command from redoStack and execute.
	 */
	@Override
	public String execute(TDTDataStore data) {
		if(!data.getRedoStack().isEmpty()) {
			setComd(data.getRedoStack().pop());
			return comd.execute(data);
		} else {
			return MESSAGE_INVALID_REDO;
		}
	}

	@Override
	public String undo(TDTDataStore data) {
		return null;
	}

	public Command getComd() {
		return comd;
	}

	public void setComd(Command comd) {
		this.comd = comd;
	}

}
