package todothis.logic;

import java.util.Iterator;

import todothis.commons.Task;



//Interface for Logic
public interface ITDTLogic {
	public String executeCommand(String userCommand);
	public Iterator<Task> getTaskIterator();
	public Iterator<String> getLabelIterator();
}
