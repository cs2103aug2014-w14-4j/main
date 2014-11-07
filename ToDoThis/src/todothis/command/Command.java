package todothis.command;

import todothis.logic.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;


public abstract class Command {
	private COMMANDTYPE commandType;
	
	public Command(COMMANDTYPE commandType) {
		this.commandType = commandType;
	}
	
	public abstract String execute(TDTStorage storage);
	public abstract String undo(TDTStorage storage);
	
	public COMMANDTYPE getCommandType() {
		return this.commandType;
	}
}

