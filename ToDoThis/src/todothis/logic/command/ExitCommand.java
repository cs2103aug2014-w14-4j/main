//@author A0110398H
package todothis.logic.command;

import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class ExitCommand extends Command {

	/**
	 * Constructor for ExitCommand
	 */
	public ExitCommand() {
		super(COMMANDTYPE.EXIT);
	}

	/**
	 * Executing ExitCommand will exit TodoThis
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
