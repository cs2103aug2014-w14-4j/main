//@author A0110398H
package todothis.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import todothis.commons.Task;
import todothis.logic.command.AddCommand;
import todothis.logic.command.Command;
import todothis.logic.command.EditCommand;
import todothis.logic.command.RedoCommand;
import todothis.logic.command.SearchCommand;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.logic.parser.TDTParser;
import todothis.storage.TDTDataStore;


/**
 * 
 * TDTController class control the logic flow of TodoThis
 *
 */
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
	private Command cmd;
	
	public TDTController(String fileName) {
		this.parser = new TDTParser();
		this.setData(new TDTDataStore(fileName));
		
	}
	
	/**
	 * Parse the userCommand and execute the returned command. 
	 * Writes to file after each operation.
	 * @param userCommand
	 * @return feedback of execution
	 */
	public String executeCommand(String userCommand) {
		Command command = parser.parse(userCommand);
		this.setCmd(command);
		String feedback = command.execute(dataStore);
		
		//Clear Redo stack if comd != undo/redo
		if(command.getCommandType() != COMMANDTYPE.UNDO &&
				command.getCommandType() != COMMANDTYPE.REDO) {
			dataStore.getRedoStack().clear();
		}
		
		if(command.getCommandType() == COMMANDTYPE.REDO) {
			if(((RedoCommand)command).getComd() != null) {
				command = ((RedoCommand)command).getComd();
			}
		}
		
		//Change view mode
		if(command.getCommandType() == COMMANDTYPE.SEARCH) {
			setViewMode(SEARCH_VIEW);
			setSearchedTask(((SearchCommand)command).getSearchedResult());
		} else if(command.getCommandType() == COMMANDTYPE.HELP) {
			setViewMode(HELP_VIEW);
		} else {
			setViewMode(TASK_VIEW);
		}
		
		//Set tasks to be highlighted
		if(command.getCommandType() == COMMANDTYPE.EDIT) {
			EditCommand comd = (EditCommand)command;
			setHighlightTask(comd.getTargetTask());
		} else if(command.getCommandType() == COMMANDTYPE.ADD) {
			AddCommand comd = (AddCommand)command;
			setHighlightTask(comd.getTargetTask());
		} else {
			setHighlightTask(null);
		}
		
		//Set scroll value
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
	
	/**
	 * Get the iterator of hide label.
	 * @return
	 */
	public Iterator<String> getHideIter() {
		return dataStore.getHideList().iterator();
	}
	
	/**
	 * Return if the given laben is in the taskMap
	 * @param label
	 * @return
	 */
	public boolean isInLabelMap(String label) {
		return dataStore.getTaskMap().containsKey(label.toUpperCase());
	}
	
	/**
	 * Return true if the given label is in the hideList. Return false otherwise.
	 * @param label
	 * @return
	 */
	public boolean isHideLabel(String label) {
		return dataStore.getHideList().contains(label);
	}
	
	/**
	 * Get all the task in the taskMap and return as a iterator.
	 * @return the iterator containing all the task
	 */
	public Iterator<Task> getTaskIterator() {
		return dataStore.getTaskIterator();
	}
	
	/**
	 * Get all the label in the taskMap and return it as a iterator
	 * @return the iterator containing all the labels in taskMap.
	 */
	public Iterator<String> getLabelIterator() {
		return dataStore.getLabelIterator();
	}
	
	/**
	 * Return the ArrayList of task associated with the given label
	 * @param label
	 * @return ArrayList of task
	 */
	public ArrayList<Task> getTaskListFromLabel(String label) {
		return dataStore.getTaskMap().get(label.toUpperCase());
	}
	
	/**
	 * Return the number of task in the label.
	 * @param label
	 * @return
	 */
	public int getLabelSize(String label) {
		return dataStore.getLabelSize(label.toUpperCase());
	}
	
	/**
	 * Get the specific task object from labelName and taskId.
	 * @param label
	 * @param id
	 * @return
	 */
	public Task getTask(String label, int id) {
		return this.getTaskListFromLabel(label.toUpperCase()).get(id - 1);
	}

	/**
	 * Return the string of current label.
	 * @return
	 */
	public String getCurrLabel() {
		return dataStore.getCurrLabel();
	}
	
	/**
	 * Read from the file fileName.txt and store the content in the TDTDataStore object .
	 * @throws IOException if unable to initialize
	 */
	public void readAndInitialize() throws IOException {
		this.getData().readAndInitialize();
	}
	
	/**
	 * Write to the file at fileName.txt
	 */
	public void writeToFile() {
		this.getData().writeToFile();
	}
	
	//-----------------------------------GETTERS & SETTERS-----------------------------------
	public ArrayList<String> getAutoWords() {
		return dataStore.getAutoWords();
	}
	
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

	public Command getCmd() {
		return cmd;
	}

	public void setCmd(Command cmd) {
		this.cmd = cmd;
	}




}
