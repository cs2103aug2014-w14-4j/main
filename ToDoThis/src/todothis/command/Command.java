package todothis.command;

import todothis.logic.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;


public abstract class Command {
	private COMMANDTYPE commandType;
	
	public Command(COMMANDTYPE commandType) {
		this.commandType = commandType;
	}
	
	public abstract String execute(TDTDataStore data);
	public abstract String undo(TDTDataStore data);
	
	public COMMANDTYPE getCommandType() {
		return this.commandType;
	}
}

