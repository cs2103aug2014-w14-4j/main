package todothis.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TimeZone;

import todothis.dateandtime.TDTDateAndTime;
import todothis.dateandtime.TDTDateMethods;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class SearchCommand extends Command {
	private static final String SEARCH_FEEDBACK = "%d results found.";
	
	private String searchedWords;
	private ArrayList<Task> searchedResult;
	private String searchDate;
	
	private static Calendar cal;
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
	public String execute(TDTStorage storage) {
		Iterator<Task> iter = storage.getTaskIterator();
		if(searchedWords.equals("")) {
			searchEveryTask(iter);
		} else if(searchedWords.trim().equalsIgnoreCase("done") ){
			searchDoneTask(iter);
		} else if(searchedWords.trim().equalsIgnoreCase("overdue") ){
			searchOverdueTask(iter);
		} else {
			this.setSearchedWords(getSearchedWords().replaceAll("\"", ""));
			if(searchedWords.indexOf('@') != -1) {
				this.setSearchDate(decodeSearchDetails(
						searchedWords.substring(searchedWords.indexOf('@') + 1)));
				this.setSearchedWords(searchedWords.substring(0, searchedWords.indexOf('@')));
				
				searchKeyWords(iter);
				searchByDate(searchedResult.iterator());
			} else {
				searchKeyWords(iter);
			}
		}
		storage.insertToUndoStack(this);
		return String.format(SEARCH_FEEDBACK, searchedResult.size());	
	}
	
	/**
	 * Undo method for search. Undoing search will just return to the main screen.
	 * Unable to redo search.
	 */
	@Override
	public String undo(TDTStorage storage) {
		//Since we do not allow to redo search, we need to pop the 
		//comd added to redo stack by undo
		assert(!storage.getRedoStack().isEmpty());
		storage.getRedoStack().pop();
		return "";
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
		//String[] keyWords = searchedWords.replaceAll("[\\W]", " ").split(" ");
		String[] keyWords = searchedWords.split(" ");
		
		while(iter.hasNext()) {
			Task task = iter.next();
			//String[] details = task.getDetails().replaceAll("[\\W]", " ").split(" ");
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
	
	// -------------------------Decode Search Details------------
	private static String decodeSearchDetails(String searchString) {
		String[] searchParts = searchString.toLowerCase().split(" ");
		int thisOrNextOrFollowing = 0; // this = 1 next = 2 following = 3
		String decodedSearchString = "";
		String decodedDate = "";
		int nextCount = 0;
		int followingCount = 0;
		String startSearchDate = "";
		String endSearchDate = "";
		boolean isSearchDateRange = false;

		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

		for (int i = 0; i < searchParts.length; i++) {
			searchParts[i] = TDTDateAndTime.replaceEndStringPunctuation(searchParts[i]);

			if (TDTDateAndTime.isPrepositionTo(searchParts, i) && !startSearchDate.equals("")) {
				isSearchDateRange = true;
			}

			if (searchParts[i].equals("this")) {
				thisOrNextOrFollowing = 1;
			} else if (searchParts[i].equals("next")) {
				nextCount++;
				thisOrNextOrFollowing = 2;
			} else if (searchParts[i].equals("following")) {
				followingCount++;
				thisOrNextOrFollowing = 3;
			}

			if (TDTDateMethods.checkDate(searchParts[i])) {
				decodedDate = TDTDateMethods.decodeDate(searchParts, i, currentYear,
						currentMonth);
				if (TDTDateMethods.isValidDateRange(decodedDate)) {
					decodedDate = TDTDateMethods.changeDateFormat(decodedDate);
				}
				if (isSearchDateRange) {
					endSearchDate = decodedDate;
				} else {
					startSearchDate = decodedDate;
				}
				decodedSearchString = decodedSearchString + decodedDate + " ";
			} else if (TDTDateMethods.checkDay(searchParts[i]) != 0) {
				int numOfDaysToAdd = TDTDateMethods.determineDaysToBeAdded(
						thisOrNextOrFollowing, searchParts, i,
						currentDayOfWeek, nextCount, followingCount);
				decodedDate = TDTDateMethods.addDaysToCurrentDate(currentDay, currentMonth,
						currentYear, numOfDaysToAdd);
				if (TDTDateMethods.isValidDateRange(decodedDate)) {
					decodedDate = TDTDateMethods.changeDateFormat(decodedDate);
				}
				if (isSearchDateRange) {
					endSearchDate = decodedDate;
				} else {
					startSearchDate = decodedDate;
				}
				decodedSearchString = decodedSearchString + decodedDate + " ";
			} else if (TDTDateMethods.checkMonth(searchParts[i]) != 0) {
				boolean isValidDayYear = true;
				if (i != 0 && i != searchParts.length - 1) {
					String before = searchParts[i - 1];
					String after = searchParts[i + 1];
					try {
						Integer.parseInt(before);
						Integer.parseInt(after);
					} catch (NumberFormatException e) {
						isValidDayYear = false;
					}
					if (isValidDayYear) {
						int day = Integer.parseInt(before);
						int month = TDTDateMethods.checkMonth(searchParts[i]);
						int year = 0;

						if (after.length() == 2) {
							after = "20" + after;
						}
						year = Integer.parseInt(after);

						decodedDate = day + "/" + month + "/" + year;
						if (TDTDateMethods.isValidDateRange(decodedDate)) {
							decodedDate = TDTDateMethods.changeDateFormat(decodedDate);
						}
						if (isSearchDateRange) {
							endSearchDate = decodedDate;
						} else {
							startSearchDate = decodedDate;
						}
						decodedSearchString = decodedSearchString + decodedDate
								+ " ";
					}
				}
			} else if (TDTDateMethods.checkWeekMonthYear(searchParts[i]) != 0) {
				if (TDTDateMethods.checkWeekMonthYear(searchParts[i]) == 1) {
					// this week next week following week
					decodedSearchString = searchWeek(thisOrNextOrFollowing,
							decodedSearchString, currentDay, currentMonth,
							currentYear, currentDayOfWeek, nextCount,
							followingCount);
				} else if (TDTDateMethods.checkWeekMonthYear(searchParts[i]) == 2) {
					// this month next month following month
					decodedSearchString = searchMonth(thisOrNextOrFollowing,
							decodedSearchString, currentDay, currentMonth,
							currentYear, currentDayOfMonth, nextCount,
							followingCount);
				} else if (TDTDateMethods.checkWeekMonthYear(searchParts[i]) == 3) {
					// this year next year following year
					decodedSearchString = searchYear(thisOrNextOrFollowing,
							decodedSearchString, currentYear, nextCount,
							followingCount);
				}
			}

			if (isSearchDateRange && !endSearchDate.equals("")) {
				if (TDTDateMethods.isValidDateRange(startSearchDate)
						&& TDTDateMethods.isValidDateRange(endSearchDate)) {
					if (TDTDateMethods.compareToDate(startSearchDate, endSearchDate) == 1) {
						decodedSearchString = searchDateRange(
								decodedSearchString, startSearchDate,
								endSearchDate);
					}
				}
				isSearchDateRange = false;
				startSearchDate = "";
				endSearchDate = "";
			}
		}
		return decodedSearchString.trim();
	}

	private static String searchDateRange(String decodedSearchString,
			String startSearchDate, String endSearchDate) {
		String dateTemp = "";
		dateTemp = startSearchDate;
		while (!dateTemp.equals(endSearchDate)) {
			String[] dateParts = dateTemp.split("/");
			int dayTemp = Integer.parseInt(dateParts[0]);
			int monthTemp = Integer.parseInt(dateParts[1]);
			int yearTemp = Integer.parseInt(dateParts[2]);
			dateTemp = TDTDateMethods.addDaysToCurrentDate(dayTemp, monthTemp, yearTemp, 1);
			decodedSearchString = decodedSearchString + dateTemp + " ";
		}
		return decodedSearchString;
	}

	private static String searchWeek(int thisOrNextOrFollowing,
			String decodedSearchString, int currentDay, int currentMonth,
			int currentYear, int currentDayOfWeek, int nextCount,
			int followingCount) {
		int dayOfWeek = currentDayOfWeek;
		String startDayOfWeek = TDTDateMethods.addDaysToCurrentDate(currentDay, currentMonth,
				currentYear, Integer.parseInt("-" + (dayOfWeek - 1)));
		String[] dateParts;
		String dateTemp;
		dateParts = startDayOfWeek.split("/");
		int dayTemp = Integer.parseInt(dateParts[0]);
		int monthTemp = Integer.parseInt(dateParts[1]);
		int yearTemp = Integer.parseInt(dateParts[2]);
		if (thisOrNextOrFollowing == 0) {
			return decodedSearchString;
		} else if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
			startDayOfWeek = TDTDateMethods.addDaysToCurrentDate(dayTemp, monthTemp, yearTemp,
					(7 * nextCount) + (14 * followingCount));
		}
		decodedSearchString = decodedSearchString + startDayOfWeek + " ";
		dateTemp = startDayOfWeek;
		for (int z = 0; z < 6; z++) {
			dateParts = dateTemp.split("/");
			dayTemp = Integer.parseInt(dateParts[0]);
			monthTemp = Integer.parseInt(dateParts[1]);
			yearTemp = Integer.parseInt(dateParts[2]);

			dateTemp = TDTDateMethods.addDaysToCurrentDate(dayTemp, monthTemp, yearTemp, 1);
			decodedSearchString = decodedSearchString + dateTemp + " ";
		}
		return decodedSearchString;
	}

	private static String searchMonth(int thisOrNextOrFollowing,
			String decodedSearchString, int currentDay, int currentMonth,
			int currentYear, int currentDayOfMonth, int nextCount,
			int followingCount) {
		int dayOfMonth = currentDayOfMonth - 1;
		String startDayOfMonth = TDTDateMethods.addDaysToCurrentDate(currentDay, currentMonth,
				currentYear, Integer.parseInt("-" + dayOfMonth));
		String[] dateParts;
		String dateTemp = "";
		dateParts = startDayOfMonth.split("/");
		int dayTemp = Integer.parseInt(dateParts[0]);
		int monthTemp = Integer.parseInt(dateParts[1]);
		int yearTemp = Integer.parseInt(dateParts[2]);

		if (thisOrNextOrFollowing == 0) {
			return decodedSearchString;
		} else if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
			int numOfMthToAdd = nextCount + followingCount * 2;
			for (int i = 0; i < numOfMthToAdd; i++) {
				if (monthTemp == 12) {
					monthTemp = 1;
					yearTemp = yearTemp + 1;
				} else {
					monthTemp++;
				}
			}
			startDayOfMonth = dayTemp + "/" + monthTemp + "/" + yearTemp;
		}

		decodedSearchString = decodedSearchString + startDayOfMonth + " ";
		dateTemp = startDayOfMonth;
		dateParts = dateTemp.split("/");
		if (dateParts.length == 3) { // ensure dateTemp not = ""
			int numDayOfMonth = TDTDateMethods.getNumOfDaysFromMonth(
					Integer.parseInt(dateParts[1]),
					Integer.parseInt(dateParts[2]));
			for (int z = 0; z < numDayOfMonth - 1; z++) {
				dateParts = dateTemp.split("/");
				dayTemp = Integer.parseInt(dateParts[0]);
				monthTemp = Integer.parseInt(dateParts[1]);
				yearTemp = Integer.parseInt(dateParts[2]);

				dateTemp = TDTDateMethods.addDaysToCurrentDate(dayTemp, monthTemp, yearTemp, 1);
				decodedSearchString = decodedSearchString + dateTemp + " ";
			}
		}
		return decodedSearchString;
	}

	private static String searchYear(int thisOrNextOrFollowing,
			String decodedSearchString, int currentYear, int nextCount,
			int followingCount) {
		int yearTemp = currentYear;
		if (thisOrNextOrFollowing == 0) {
			return decodedSearchString;
		} else if (thisOrNextOrFollowing == 2 || thisOrNextOrFollowing == 3) {
			yearTemp = yearTemp + (nextCount + followingCount * 2);
		}

		for (int a = 1; a <= 12; a++) {
			int numDayOfMonth = TDTDateMethods.getNumOfDaysFromMonth(a, yearTemp);
			for (int b = 1; b <= numDayOfMonth; b++) {
				String date = b + "/" + a + "/" + yearTemp;
				decodedSearchString = decodedSearchString + date + " ";
			}
		}
		return decodedSearchString;
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
