package todothis.storage;

import java.util.Iterator;

import todothis.logic.Task;

public interface ITDTStorage{
	/**
	 * Read from data file and initialise TDT
	 * @throws Exception
	 */
	public void readInitialise() throws Exception;
	
	/**
	 * Writes data to data file
	 * @throws Exception
	 */
	public void write() throws Exception;
	
	public Iterator<Task> getTaskIterator();
	
}
