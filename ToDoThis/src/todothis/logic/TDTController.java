package todothis.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import todothis.commons.Task;
import todothis.logic.command.AddCommand;
import todothis.logic.command.Command;
import todothis.logic.command.EditCommand;
import todothis.logic.command.SearchCommand;
import todothis.logic.parser.TDTParser;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;



public class TDTController  {
	public static final int TASK_VIEW = 0;
	public static final int SEARCH_VIEW = 1;
	public static final int HELP_VIEW = 2;
	
	private TDTParser parser;
	private TDTDataStore dataStore;
	private int viewMode = 0;
	private int scrollVal = -1;
	private ArrayList<Task> highlightTask;
	private Task addedTask;
	private ArrayList<Task> searchedTask;
	
	public TDTController(String fileName) {
		this.parser = new TDTParser();
		this.setData(new TDTDataStore(fileName));
		
	}
	
	public String executeCommand(String userCommand) {
		Command command = parser.parse(userCommand);
		String feedback = command.execute(dataStore);
		
		if(command.getCommandType() != COMMANDTYPE.UNDO &&
				command.getCommandType() != COMMANDTYPE.REDO) {
			dataStore.getRedoStack().clear();
		}
		
		if(command.getCommandType() == COMMANDTYPE.SEARCH) {
			setViewMode(SEARCH_VIEW);
			setSearchedTask(((SearchCommand)command).getSearchedResult());
		} else if(command.getCommandType() == COMMANDTYPE.HELP) {
			setViewMode(HELP_VIEW);
		} else {
			setViewMode(TASK_VIEW);
		}
		
		if(command.getCommandType() == COMMANDTYPE.EDIT) {
			EditCommand comd = (EditCommand)command;
			setHighlightTask(comd.getTargetTask());
		} else if(command.getCommandType() == COMMANDTYPE.ADD) {
			AddCommand comd = (AddCommand)command;
			setHighlightTask(comd.getTargetTask());
		} else {
			setHighlightTask(null);
		}
		
		if(command.getCommandType() == COMMANDTYPE.ADD) {
			AddCommand comd = (AddCommand)command;
			setScrollVal(comd.getTaskID());
			setAddedTask(comd.getAddedTask());
		} else if(command.getCommandType() == COMMANDTYPE.LABEL) {
			setScrollVal(0);
		} else if(command.getCommandType() == COMMANDTYPE.EDIT) {
			EditCommand comd = (EditCommand)command;
			setAddedTask(comd.getEditedTask());
			setScrollVal(-1);
		}else {
			setScrollVal(-1);
		}
		

		this.writeToFile();
		return feedback;
		
	}
	
	public Iterator<String> getHideIter() {
		return dataStore.getHideList().iterator();
	}
	
	public boolean isInLabelMap(String label) {
		return dataStore.getTaskMap().containsKey(label.toUpperCase());
	}
	
	public boolean isHideLabel(String label) {
		return dataStore.getHideList().contains(label);
	}

	public Iterator<Task> getTaskIterator() {
		return dataStore.getTaskIterator();
	}

	public Iterator<String> getLabelIterator() {
		return dataStore.getLabelIterator();
	}
	
	public ArrayList<Task> getTaskListFromLabel(String label) {
		return dataStore.getTaskMap().get(label.toUpperCase());
	}
	
	public int getLabelSize(String label) {
		return dataStore.getLabelSize(label.toUpperCase());
	}
	
	public Task getTask(String label, int id) {
		return this.getTaskListFromLabel(label.toUpperCase()).get(id - 1);
	}

	
	public String getCurrLabel() {
		return dataStore.getCurrLabel();
	}
	
	public void readAndInitialize() throws IOException {
		this.getData().read();
	}
	
	public void writeToFile() {
		this.getData().write();
	}
	
	public ArrayList<String> getAutoWords() {
		return dataStore.getAutoWords();
	}
	
	//-----------------------------------GETTERS & SETTERS-----------------------------------

	public int getViewMode() {
		return viewMode;
	}

	public void setViewMode(int viewMode) {
		this.viewMode = viewMode;
	}

	public ArrayList<Task> getHighlightTask() {
		return highlightTask;
	}

	public void setHighlightTask(ArrayList<Task> highlightTask) {
		this.highlightTask = highlightTask;
	}

	public int getScrollVal() {
		return scrollVal;
	}

	public void setScrollVal(int scrollVal) {
		this.scrollVal = scrollVal;
	}

	public ArrayList<Task> getSearchedTask() {
		return searchedTask;
	}

	public void setSearchedTask(ArrayList<Task> searchedTask) {
		this.searchedTask = searchedTask;
	}

	public Task getAddedTask() {
		return addedTask;
	}

	public void setAddedTask(Task addedTask) {
		this.addedTask = addedTask;
	}

	public TDTDataStore getData() {
		return dataStore;
	}

	public void setData(TDTDataStore data) {
		this.dataStore = data;
	}




}
