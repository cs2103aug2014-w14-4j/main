package todothis.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.logic.TDTDateAndTime;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class SearchCommand extends Command {
	private String searchedWords;
	private ArrayList<Task> searchedResult;
	
	public SearchCommand(String searchedWords) {
		super(COMMANDTYPE.SEARCH);
		this.setSearchedWords(searchedWords);
		this.setSearchedResult(new ArrayList<Task>());
	}
	
	@Override
	public String execute(TDTStorage storage) {
		
		Iterator<Task> iter = storage.getTaskIterator();
		if(searchedWords.equals("")) {
			searchEveryTask(iter);
		} else if(searchedWords.trim().charAt(0) != '@') {
			searchKeyWords(iter);
		} else {
			searchByDate(iter);
		}
		String feedback = searchedResult.size() + " result(s) found for \"" 
				+ searchedWords + "\".";
		
		return feedback;
	}
	
	@Override
	public String undo(TDTStorage storage) {
		return "";
	}

	private void searchByDate(Iterator<Task> iter) {
		String[] params = TDTDateAndTime.decodeSearchDetails(searchedWords.trim().substring(1).trim()).split(" ");
		while(iter.hasNext()) {
			Task task = iter.next();
			String startDate = task.getDateAndTime().getStartDate();
			String endDate = task.getDateAndTime().getEndDate();
			
			for(int i = 0; i < params.length; i++) {
				if(params[i].equals(startDate) || params[i].equals(endDate)) {
					if(!searchedResult.contains(task)) {
						searchedResult.add(task);
					}
				}
			}
		}
	}

	private void searchKeyWords(Iterator<Task> iter) {
		boolean found = false;
		String[] params = searchedWords.replaceAll("[\\W]", " ").split(" ");
		//String[] params = searchedWords.split(" ");
		while(iter.hasNext()) {
			Task task = iter.next();
			String[] words = task.getDetails().replaceAll("[\\W]", " ").split(" ");
			//String[] words = task.getDetails().split(" ");
			for(int j = 0; j < params.length; j++) {
				for(int k = 0; k < words.length; k++) {
					if(words[k].startsWith(params[j])) {
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

	private void searchEveryTask(Iterator<Task> iter) {
		while(iter.hasNext()) {
			searchedResult.add(iter.next());
		}
	}
	
	private void searchDoneTask(Iterator<Task> iter) {
		while(iter.hasNext()) {
			Task task = iter.next();
			if(task.isDone()) {
				searchedResult.add(task);
			}
		}
	}


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

	

}
