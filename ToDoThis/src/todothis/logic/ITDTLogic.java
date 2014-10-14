package todothis.logic;

import java.util.Iterator;

import todothis.command.Command;


//Interface for Logic
public interface ITDTLogic {
	public String executeCommand(Command command);
	public Iterator<Task> getTaskIterator();
	public Iterator<String> getLabelIterator();
}
