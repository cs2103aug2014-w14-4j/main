package todothis.test;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import todothis.command.Command;
import todothis.logic.Task;
import todothis.parser.TDTDateAndTime;
import todothis.parser.TDTParser;
import todothis.storage.TDTStorage;

public class TDTSystemTest {
	private TDTStorage testStorage = new TDTStorage("testing.txt");
	private TDTParser testParser = new TDTParser();
	private Task current;
	private TDTDateAndTime dnt;
	private String details = "";
	private String label = "";
	private String reminderDateAndTime = "";
	private boolean isDone = false;
	private boolean isPriority = false;
	@Test
	public void floatingTaskWithPriorityTest() {
		Command cmd = testParser.parse("lets play games!");
		cmd.execute(testStorage);
		Iterator<Task> i = testStorage.getTaskIterator();
		while(i.hasNext()){
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			
			assertEquals(dnt.getStartDate(), "null");
			assertEquals(dnt.getStartTime(), "null");
			assertEquals(dnt.getEndDate(), "null");
			assertEquals(dnt.getEndTime(), "null");
			
			assertEquals(details, " lets play games");
			
			assertTrue(isPriority);
		}
		
	}
	
	@Test
	public void deadlineTaskTest() {
		Command cmd = testParser.parse("cs2106 homework done by 31-10-14 2359hrs");
		cmd.execute(testStorage);
		Iterator<Task> i = testStorage.getTaskIterator();
		while(i.hasNext()){
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			
			assertEquals(dnt.getStartDate(), "null");
			assertEquals(dnt.getStartTime(), "null");
			assertEquals(dnt.getEndDate(), "31/10/2014");
			assertEquals(dnt.getEndTime(), "23:59");
			
			assertEquals(details, " cs2106 homework done");
			
			assertFalse(isPriority);
		}
		
	}
	@Test
	public void timedTaskTest() {
		Command cmd = testParser.parse("incamp training from 29/10/2014 8am to 3/11/2014 9pm");
		cmd.execute(testStorage);
		Iterator<Task> i = testStorage.getTaskIterator();
		while(i.hasNext()){
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			
			assertEquals(dnt.getStartDate(), "29/10/2014");
			assertEquals(dnt.getStartTime(), "8:00");
			assertEquals(dnt.getEndDate(), "3/11/2014");
			assertEquals(dnt.getEndTime(), "21:00");
			
			assertEquals(details, " incamp training");
			
			assertFalse(isPriority);
		}
		
	}
	
	@Test
	public void doneTaskTest() {
		Command cmd = testParser.parse("test done");
		cmd.execute(testStorage);
		cmd = testParser.parse("done 1");
		cmd.execute(testStorage);
		Iterator<Task> i = testStorage.getTaskIterator();
		while(i.hasNext()){
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			isDone = current.isDone();
			
			assertEquals(dnt.getStartDate(), "null");
			assertEquals(dnt.getStartTime(), "null");
			assertEquals(dnt.getEndDate(), "null");
			assertEquals(dnt.getEndTime(), "null");
			
			assertEquals(details, " test done");
			
			assertFalse(isPriority);
			assertTrue(isDone);
		}
		
	}
	@Test
	public void labelTest() {
		Command cmd = testParser.parse("label homework");
		cmd.execute(testStorage);
		cmd = testParser.parse("label check!");
		cmd.execute(testStorage);
		Iterator<Task> i = testStorage.getTaskIterator();
		while(i.hasNext()){
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			isDone = current.isDone();
			label = current.getLabelName();
			
			assertEquals(dnt.getStartDate(), "null");
			assertEquals(dnt.getStartTime(), "null");
			assertEquals(dnt.getEndDate(), "null");
			assertEquals(dnt.getEndTime(), "null");
			
			assertEquals(details, " test done");
			
			assertTrue(isPriority);
			assertFalse(isDone);
			
			assertEquals(label, "homework");
		}
		
	}
	@Test
	public void editTest() {
		Command cmd = testParser.parse("do homework from 1pm - 3pm");
		cmd.execute(testStorage);
		cmd = testParser.parse("edit 1 dun wan do homework liao");
		cmd.execute(testStorage);
		Iterator<Task> i = testStorage.getTaskIterator();
		while(i.hasNext()){
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			isDone = current.isDone();
			label = current.getLabelName();
			
			assertEquals(dnt.getStartDate(), "null");
			assertEquals(dnt.getStartTime(), "null");
			assertEquals(dnt.getEndDate(), "null");
			assertEquals(dnt.getEndTime(), "null");
			
			assertEquals(details, " dun wan do homework liao");
			
			assertFalse(isPriority);
			assertFalse(isDone);
		}
	}
	@Test
	public void deleteTest() {
		Command cmd = testParser.parse("testing delete task 1");
		cmd.execute(testStorage);
		cmd = testParser.parse("testing delete task 2");
		cmd.execute(testStorage);
		cmd = testParser.parse("delete 1");
		cmd.execute(testStorage);
		Iterator<Task> i = testStorage.getTaskIterator();
		while(i.hasNext()){
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			isDone = current.isDone();
			label = current.getLabelName();
			
			
			assertEquals(dnt.getStartDate(), "null");
			assertEquals(dnt.getStartTime(), "null");
			assertEquals(dnt.getEndDate(), "null");
			assertEquals(dnt.getEndTime(), "null");
			
			assertEquals(details, " testing delete task 2");
			
			assertFalse(isPriority);
			assertFalse(isDone);
		}
	}
	@Test
	public void reminderTest() {
		Command cmd = testParser.parse("testing reminder task");
		cmd.execute(testStorage);
		cmd = testParser.parse("remind 1 20-12-14 330pm");
		cmd.execute(testStorage);
		Iterator<Task> i = testStorage.getTaskIterator();
		while(i.hasNext()){
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			isDone = current.isDone();
			label = current.getLabelName();
			reminderDateAndTime = current.getRemindDateTime();
			
			assertEquals(dnt.getStartDate(), "null");
			assertEquals(dnt.getStartTime(), "null");
			assertEquals(dnt.getEndDate(), "null");
			assertEquals(dnt.getEndTime(), "null");
			
			assertEquals(details, " testing reminder task");
			assertEquals(reminderDateAndTime, "20/12/2014 15:30");
			assertFalse(isPriority);
			assertFalse(isDone);
		}
	}

}
