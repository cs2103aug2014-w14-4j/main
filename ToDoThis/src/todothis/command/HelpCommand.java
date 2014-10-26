package todothis.command;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class HelpCommand extends Command{
	COMMANDTYPE commandTypeForHelp;
	
	public HelpCommand(String command) {
		super(COMMANDTYPE.HELP);
		commandTypeForHelp = determineHelpCommandType(command);
	}

	@Override
	public String execute(TDTStorage storage) {
		switch(getCommandForHelp()){
		case ADD:
			return helpAdd();
		case DELETE:
			return helpDelete();
		case EDIT:
			return helpEdit();
		case LABEL:
			return helpLabel();
		case UNDO:
			return helpUndo();
		case REDO:
			return helpRedo();
		case SEARCH:
			return helpSearch();
		case DISPLAY:
			return helpDisplay();
		case HIDE:
			return helpHide();
		case DONE:
			return helpDone();
		case HELP:
			return helpAll();
		case INVALID:
			break;
		default:
			break;
		}
		return "INVALID HELP COMMAND!";
	}
	
	private String helpAdd(){
		
		return null;
	}
	private String helpDelete(){
		
		return null;
	}
	private String helpEdit(){
		
		return null;
	}
	private String helpLabel(){
		
		return null;
	}
	private String helpUndo(){
		
		return null;
	}
	private String helpRedo(){
		
		return null;
	}
	private String helpSearch(){
		
		return null;
	}
	private String helpDisplay(){
		
		return null;
	}
	private String helpHide(){
		
		return null;
	}
	private String helpDone(){
		
		return null;
	}
	private String helpAll(){
		
		return null;
	}
	
	public COMMANDTYPE getCommandForHelp(){
		return commandTypeForHelp;
	}
	
	private static COMMANDTYPE determineHelpCommandType(String commandTypeString) {
		if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMANDTYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("hide")) {
			return COMMANDTYPE.HIDE;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMANDTYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMANDTYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("label")) {
			return COMMANDTYPE.LABEL;
		} else if (commandTypeString.equalsIgnoreCase("edit")) {
			return COMMANDTYPE.EDIT;
		} else if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMANDTYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
			return COMMANDTYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return COMMANDTYPE.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("done")) {
			return COMMANDTYPE.DONE;
		} else if (commandTypeString.equalsIgnoreCase("redo")) {
			return COMMANDTYPE.REDO;
		} else if (commandTypeString == ""){
			return COMMANDTYPE.HELP;
		} else {
			return COMMANDTYPE.INVALID;
		}
	}

}
