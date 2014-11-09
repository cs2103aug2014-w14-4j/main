//@author A0110398H
package todothis.logic.command;

import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class UndoCommand extends Command {
	private Command comd;
	
	public static final String MESSAGE_INVALID_UNDO = "Invalid command. No command to undo.";
	
	/**
	 * Construct a UndoCommand object.
	 */
	public UndoCommand() {
		super(COMMANDTYPE.UNDO);
	}

	/**
	 * Pop the previous command from undoStack and execute the undo method.
	 * Add the command into RedoStack.
	 */
	@Override
	public String execute(TDTDataStore data) {

		if (!data.getUndoStack().isEmpty()) {

			Command comd = data.getUndoStack().pop();
			setComd(comd);
			data.getRedoStack().push(comd);

			return comd.undo(data);
		} else {
			return MESSAGE_INVALID_UNDO;
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

	
	//@author A0110398H-unused
	/*
	 * Unused code. Previously undo copy the whole state of the program.
	 * Inefficient.
	 * 
	 * @Override public String execute(TDTDataStore data) { assert
	 * (data.getUndoStack().size() > 0) : "undostack is empty";
	 * data.getRedoStack().push(data.getUndoStack().pop());
	 * data.getRedoLabelPointerStack().push(data.getLabelPointerStack().pop());
	 * if(!data.getUndoStack().isEmpty()) {
	 * data.setLabelMap(data.getUndoStack().pop());
	 * data.setCurrLabel(data.getLabelPointerStack().pop());
	 * 
	 * return "Undo success!"; } else { data.getRedoStack().pop();
	 * data.getRedoLabelPointerStack().pop(); return "No command to undo."; } }
	 */

}
