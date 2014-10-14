package todothis.command;

import java.util.ArrayList;
import java.util.Iterator;

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
		boolean found = false;
		String[] params = searchedWords.replaceAll("[\\W]", " ").split(" ");
		Iterator<Task> iter = storage.getTaskIterator();
		
		while(iter.hasNext()) {
			Task task = iter.next();
			String[] words = task.getDetails().replaceAll("[\\W]", " ").split(" ");
			for(int j = 0; j < params.length; j++) {
				for(int k = 0; k < words.length; k++) {
					if(words[k].equalsIgnoreCase(params[j])) {
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
		
		String feedback = searchedResult.size() + " result(s) found for \"" 
				+ searchedWords + "\".";
		return feedback;
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
