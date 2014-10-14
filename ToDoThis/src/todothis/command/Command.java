package todothis.command;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;


public abstract class Command {
	private COMMANDTYPE commandType;
	
	public Command(COMMANDTYPE commandType) {
		this.commandType = commandType;
	}
	
	public abstract String execute(TDTStorage storage);
	
	public COMMANDTYPE getCommandType() {
		return this.commandType;
	}
}

