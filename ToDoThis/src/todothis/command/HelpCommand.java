package todothis.command;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class HelpCommand extends Command{
	COMMANDTYPE commandTypeForHelp;
	
	public HelpCommand(String command) {
		super(COMMANDTYPE.HELP);
		commandTypeForHelp = determineHelpCommandType(command);
	}
	
	/*
	 * NO need create new frame
	public static class HtmlContent extends JFrame {
	
		private static final long serialVersionUID = 1L;
		
		void start() {
			String html;
			html="<html><head><title>Simple Page</title></head>"; 
			html+="<body bgcolor='#777779'><hr/><font size=50>Help Command</font><hr/>"; 
			html+="</body></html>";
			JEditorPane ed1=new JEditorPane("text/html",html);
			add(ed1);
			setVisible(true);
			setSize(600,600);
			setDefaultCloseOperation(EXIT_ON_CLOSE);

		}
	}
	*/

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
			return helpShow();
		case HIDE:
			return helpHide();
		case DONE:
			return helpDone();
		default:
			break;
		}
		return helpAll();
	}
	
	@Override
	public String undo(TDTStorage storage) {
		
		return "";
	}
	
	private String helpAdd(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>ADD - input task</b>");
		sb.append("<br>");
		sb.append("To add a task under current label: add taskDetails");
		sb.append("<br>");
		sb.append("Note that the word 'add' can be omitted and the only required field is taskDetails");
		sb.append("<br>");
		sb.append("List of preposition words available: ON, AT, BY, TO, FROM, TILL, UNTIL, ABOUT, THE, NEXT, FOLLOWING, THIS, '-'");
		sb.append("<br>");
		sb.append("Examples:");
		sb.append("<br>");
		sb.append("complete assignment by monday at 2pm");
		sb.append("<br>");
		sb.append("study in school from 6am - 8am on the following tuesday");
		sb.append("<br>");
		sb.append("Date and Time formats");
		
		
		return sb.toString();
	}
	private String helpDelete(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>DELETE - remove task completely</b>");
		sb.append("<br>");
		sb.append("To delete task from the current label: delete taskID");
		sb.append("<br>");
		sb.append("To delete task from a different label: delete labelName taskID");
		sb.append("<br>");
		sb.append("To delete an entire label and its contents: delete labelName");
		sb.append("<br>");
		sb.append("To delete all labels and tasks: delete");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpEdit(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>EDIT - change task details</b>");
		sb.append("<br>");
		sb.append("Task details of the corresponding task ID will be auto completed for convenience");
		sb.append("<br>");
		sb.append("To edit task under current label: edit taskID detailsToBeChanged");
		sb.append("<br>");
		sb.append("To edit a task under a different label: edit labelName taskID detailsToBeChanged");
		sb.append("<br>");	
		
		return sb.toString();
	}
	private String helpLabel(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>LABEL - categorize tasks</b>");
		sb.append("<br>");
		sb.append("A valid label has to comprise of only one word");
		sb.append("<br>");
		sb.append("To create a new label: label labelName");
		sb.append("<br>");
		sb.append("To change current directory to a different label: label labelName");
		sb.append("<br>");	
		sb.append("Note that this is similar to creating a new label");
		
		return sb.toString();
	}
	private String helpUndo(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>UNDO - reverse the effect of previous command</b>");
		sb.append("<br>");
		sb.append("Keyboard shortcut available: Ctrl + Z");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpRedo(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>REDO - reverse the effect of undo</b>");
		sb.append("<br>");
		sb.append("Keyboard shortcut available: Ctrl + Y");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpSearch(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>SEARCH - locate tasks with given keyword or date</b>");
		sb.append("<br>");
		sb.append("To search for a keyword: search keyword");
		sb.append("<br>");
		sb.append("To search for a day/date: search @tmr OR Search @24/12/14");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpShow(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>SHOW - display labels desired</b>");
		sb.append("<br>");
		sb.append("To show one or more labels from view: show labelName1 labelName2 ...");
		sb.append("<br>");
		sb.append("To show all labels available: show");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpHide(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>HIDE - hide labels from view</b>");
		sb.append("<br>");
		sb.append("To hide one or more labels from view: hide labelName1 labelName2 ...");
		sb.append("<br>");
		sb.append("To hide all labels from view: hide");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpDone(){
		StringBuilder sb = new StringBuilder();
		sb.append("<b>DONE - mark task as completed</b>");
		sb.append("<br>");
		sb.append("Marking a task which has already been marked done will switch it back to undone");
		sb.append("<br>");
		sb.append("To mark a task from the current label as done: done taskID");
		sb.append("<br>");
		sb.append("To mark a task from a different label as done: done labelName taskID");
		sb.append("<br>");
		sb.append("To mark all tasks under a label as complete: done labelName");
		sb.append("<br>");
		sb.append("To mark everything as done: done");
		sb.append("<br>");
		 
		 return sb.toString();	
	}
	private String helpAll(){
		
		return "<span color = red><b>HELP</b></span>";
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
