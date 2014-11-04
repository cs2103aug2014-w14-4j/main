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
		sb.append("<span color = purple><b>ADD - input task</b></span>");
		sb.append("<br>");
		sb.append("<b>To add a task under current label</b>: add <span color = blue>taskDetails</span>");
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
		sb.append("<table class = helptable>");
		sb.append("<caption>Date and Time formats</caption>");
		sb.append("<tr>");
	    sb.append("<th><b>Time</b></th>");
	    sb.append("<th><b>Date</b></th>");
	    sb.append("</tr>");
	    sb.append("<tr>");
	    sb.append("<td>12 Hours Format</td>");
	    sb.append("<td>27 October 2014 / 27 October 14</td>");
	    sb.append("</tr>");
	    sb.append("<tr>");
	    sb.append("<td>3pm</td>");
	    sb.append("<td>27 Oct 2014 / 27 Oct 14</td>");
	    sb.append("</tr>");
	    sb.append("</table>");
	    sb.append("<br>");
	    sb.append("<br>");

	    
		
		
		return sb.toString();
	}
	private String helpDelete(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>DELETE - remove task completely</b></span>");
		sb.append("<br>");
		sb.append("<b>To delete task from the current label</b>: delete <span color = blue>taskID</span>");
		sb.append("<br>");
		sb.append("<b>To delete task from a different label</b>: delete <span color = blue>labelName</span> <span color = green>taskID</span>");
		sb.append("<br>");
		sb.append("<b>To delete an entire label and its contents</b>: delete <span color = blue>labelName</span>");
		sb.append("<br>");
		sb.append("<b>To delete all labels and tasks</b>: delete");
		sb.append("<br>");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpEdit(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>EDIT - change task details</b></span>");
		sb.append("<br>");
		sb.append("Task details of the corresponding task ID will be auto completed for convenience");
		sb.append("<br>");
		sb.append("<b>To edit task under current label</b>: edit taskID detailsToBeChanged");
		sb.append("<br>");
		sb.append("<b>To edit a task under a different label</b>: edit <span color = blue>labelName</span> <span color = green>taskID</span> <span color = orange>detailsToBeChanged</span>");
		sb.append("<br>");	
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpLabel(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>LABEL - categorize tasks</b></span>");
		sb.append("<br>");
		sb.append("A valid label has to comprise of only one word");
		sb.append("<br>");
		sb.append("<b>To create a new label</b>: label <span color = blue>labelName</span>");
		sb.append("<br>");
		sb.append("<b>To change current directory to a different label</b>: label <span color = blue>labelName</span>");
		sb.append("<br>");	
		sb.append("Note that this is similar to creating a new label");
		sb.append("<br>");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpUndo(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>UNDO - reverse the effect of previous command</b></span>");
		sb.append("<br>");
		sb.append("<b>Keyboard shortcut available</b>: Ctrl + Z");
		sb.append("<br>");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpRedo(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>REDO - reverse the effect of undo</b></span>");
		sb.append("<br>");
		sb.append("<b>Keyboard shortcut available</b>: Ctrl + Y");
		sb.append("<br>");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpSearch(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>SEARCH - locate tasks with given keyword or date</b></span>");
		sb.append("<br>");
		sb.append("<b>To search for a keyword</b>: search <span color = blue>keyword</span>");
		sb.append("<br>");
		sb.append("<b>To search for a day/date</b>: search <span color = blue>@tmr</span> OR Search <span color = blur>@24/12/14</span>");
		sb.append("<br>");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpShow(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>SHOW - display labels desired</b></span>");
		sb.append("<br>");
		sb.append("<b>To show one or more labels from view</b>: show <span color = blue>labelName1</span> <span color = green>labelName2</span> ...");
		sb.append("<br>");
		sb.append("<b>To show all labels available</b>: show");
		sb.append("<br>");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpHide(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>HIDE - hide labels from view</b></span>");
		sb.append("<br>");
		sb.append("<b>To hide one or more labels from view</b>: hide <span color = blue>labelName1</span> <span color = green>labelName2</span> ...");
		sb.append("<br>");
		sb.append("<b>To hide all labels from view</b>: hide");
		sb.append("<br>");
		sb.append("<br>");
		
		return sb.toString();
	}
	private String helpDone(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = purple><b>DONE - mark task as completed</b></span>");
		sb.append("<br>");
		sb.append("Marking a task which has already been marked done will switch it back to undone");
		sb.append("<br>");
		sb.append("<b>To mark a task from the current label as done</b>: done <span color = blue>taskID</span>");
		sb.append("<br>");
		sb.append("<b>To mark a task from a different label as done</b>: done <span color = blue>labelName</span> <span color = green>taskID</span>");
		sb.append("<br>");
		sb.append("<b>To mark all tasks under a label as complete</b>: done <span color = blue>labelName</span>");
		sb.append("<br>");
		sb.append("<b>To mark everything as done</b>: done");
		sb.append("<br>");
		sb.append("<br>");
		 
		 return sb.toString();	
	}
	private String helpAll(){
		StringBuilder sb = new StringBuilder();
		sb.append("<span color = red><b>HELP - List of available commands</b></span>");
		sb.append("<br>");
		sb.append("<b>For specific command help: help <span color = blue>commandName</span></b>");
		// list of shortcuts!!!
		sb.append("<br>");
		sb.append("<br>");
		sb.append(helpAdd());
		sb.append(helpLabel());
		sb.append(helpDelete());
		sb.append(helpEdit());
		sb.append(helpSearch());
		sb.append(helpDone());
		sb.append(helpHide());
		sb.append(helpShow());
		sb.append(helpUndo());
		sb.append(helpRedo());
				
		return sb.toString();
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
