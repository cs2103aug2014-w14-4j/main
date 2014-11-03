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
		case SHOW:
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
	
	@Override
	public String undo(TDTStorage storage) {
		
		return "";
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
		
		return "Ctrl+Z - Shortcut for undo\n" + "Able to undo previous command";
	}
	private String helpRedo(){
		
		return "Ctrl+Y - Shortcut for redo\n" + "Able to reverse previous undo command";
	}
	private String helpSearch(){
		
		return "To search for a date/day, simply enter Search @tmr or @24/12/14\n" + "To search for a keyword, simply enter @keyword";
	}
	private String helpDisplay(){
		
		return "To display one or more label names, simply enter Display labelName1 labelName2";
	}
	private String helpHide(){
		
		return "To hide one or more label names, simply enter Hide labelName1 labelName2";
	}
	private String helpDone(){
		
		return "To mark everything as done, simple enter Done\n" + "To mark a task under current label as done, simply enter Done taskNum";
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
		} else if (commandTypeString.equalsIgnoreCase("show")) {
			return COMMANDTYPE.SHOW;
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
