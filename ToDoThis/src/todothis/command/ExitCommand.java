package todothis.command;

import todothis.logic.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class ExitCommand extends Command {
	
	/**
	 * Constructor for ExitCommand
	 */
	public ExitCommand() {
		super(COMMANDTYPE.EXIT);
	}
	
	/**
	 * Executing ExitCommand will save the file and exits.
	 */
	@Override
	public String execute(TDTDataStore data) {
		System.exit(0);
		return "";
	}

	@Override
	public String undo(TDTDataStore data) {
		return "";
	}
	
}
