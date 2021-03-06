//@author A0110398H
package todothis.logic.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.logic.parser.TDTDateAndTimeParser;
import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class SearchCommand extends Command {
	public static final String MESSAGE_UNDO_SEARCH = "Undo Search";
	public static final String SEARCH_FEEDBACK = "%d results found.";
	
	private String searchedWords;
	private ArrayList<Task> searchedResult;
	private String searchDate;

	/**
	 * Constructor for SearchCommand. Able to search by done, overdue, keywords, date.
	 * @param searchedWords
	 */
	public SearchCommand(String searchedWords) {
		super(COMMANDTYPE.SEARCH);
		this.setSearchedWords(searchedWords.trim());
		this.setSearchedResult(new ArrayList<Task>());
		this.setSearchDate("");
	}
	
	/**
	 * Execute the search command, returning the number of results found and storing
	 * the searched tasks in a ArrayList.
	 */
	@Override
	public String execute(TDTDataStore data) {
		Iterator<Task> iter = data.getTaskIterator();
		if(searchedWords.equals("")) {
			searchEveryTask(iter);
		} else if(searchedWords.trim().equalsIgnoreCase("done") ){
			searchDoneTask(iter);
		} else if(searchedWords.trim().equalsIgnoreCase("overdue") ){
			searchOverdueTask(iter);
		} else {
			this.setSearchedWords(getSearchedWords().replaceAll("\"", ""));
			if(searchedWords.indexOf('@') != -1) {
				this.setSearchDate(TDTDateAndTimeParser.decodeSearchDetails(
						searchedWords.substring(searchedWords.indexOf('@') + 1)));
				this.setSearchedWords(searchedWords.substring(0, searchedWords.indexOf('@')));
				
				searchKeyWords(iter);
				searchByDate(searchedResult.iterator());
			} else {
				searchKeyWords(iter);
			}
		}
		data.insertToUndoStack(this);
		return String.format(SEARCH_FEEDBACK, searchedResult.size());	
	}
	
	/**
	 * Undo method for search. Undoing search will just return to the main screen.
	 */
	@Override
	public String undo(TDTDataStore data) {
		searchedResult.clear();
		return MESSAGE_UNDO_SEARCH;
	}
	
	//-------------------------------Private methods---------------------------------------
	
	//Search the task list by dates. If either the start or end dates matches the search,
	//the task is added to the search result.
	private void searchByDate(Iterator<Task> iter) {
		String[] params = this.getSearchDate().split(" ");
		ArrayList<Task> newSearchResult = new ArrayList<Task>();
		while(iter.hasNext()) {
			Task task = iter.next();
			String startDate = task.getDateAndTime().getStartDate();
			String endDate = task.getDateAndTime().getEndDate();
			
			for(int i = 0; i < params.length; i++) {
				if(params[i].equals(startDate) || params[i].equals(endDate)) {
					if(!newSearchResult.contains(task)) {
						newSearchResult.add(task);
					}
				}
			}
		}
		this.setSearchedResult(newSearchResult);
	}
	
	//Search the task list by keywords. If the task details matches ALL the key words,
	//the task is added to search result. The more key words the user specify, the 
	//narrow the search result will be.
	private void searchKeyWords(Iterator<Task> iter) {
		boolean found = false;
		String[] keyWords = searchedWords.split(" ");
		
		while(iter.hasNext()) {
			Task task = iter.next();
			String[] details = task.getDetails().split(" ");
			
			//For each of the keywords, compare with each word of task details. 
			//If the keyword is found in the task details, found = true, else found = false
			//After comparing with all the keywords, if found remain true, add the task into
			//search result. Reset found to false and repeat for the next task.
			for(int j = 0; j < keyWords.length; j++) {
				for(int k = 0; k < details.length; k++) {
					if(details[k].toLowerCase().startsWith(keyWords[j].toLowerCase())) {
						found = true;
						break;
					} else {
						found = false;
					}
				}
				if(!found) {
					break;
				}
			}
			if(found) {
				searchedResult.add(task);
			} 
			found = false;
		}
	}
	
	//Search all the task.
	private void searchEveryTask(Iterator<Task> iter) {
		while(iter.hasNext()) {
			searchedResult.add(iter.next());
		}
	}
	
	//Search all task that is done.
	private void searchDoneTask(Iterator<Task> iter) {
		while(iter.hasNext()) {
			Task task = iter.next();
			if(task.isDone()) {
				searchedResult.add(task);
			}
		}
	}
	
	//Search all task that is overdue.
	private void searchOverdueTask(Iterator<Task> iter) {
		while(iter.hasNext()) {
			Task task = iter.next();
			TDTDateAndTime dnt = task.getDateAndTime();
			if(dnt.isOverdue()) {
				searchedResult.add(task);
			}
		}
	}
	
	

	//-------------------------------Getters & Setters-----------------------------------
	public String getSearchedWords() {
		return searchedWords;
	}
	public void setSearchedWords(String searchedWords) {
		this.searchedWords = searchedWords;
	}

	public ArrayList<Task> getSearchedResult() {
		return searchedResult;
	}

	public void setSearchedResult(ArrayList<Task> searchedResult) {
		this.searchedResult = searchedResult;
	}

	private String getSearchDate() {
		return searchDate;
	}

	private void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}



}
