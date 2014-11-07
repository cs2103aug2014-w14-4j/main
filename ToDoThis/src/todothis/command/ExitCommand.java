package todothis.command;

import todothis.logic.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

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
	public String execute(TDTStorage storage) {
		storage.write();
		System.exit(0);
		return "";
	}

	@Override
	public String undo(TDTStorage storage) {
		return "";
	}
	
}
