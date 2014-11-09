package todothis.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.logic.TDTController;

public class TDTSystemTest {
	private String fileName = "testing.txt";
	private Task current;
	private TDTDateAndTime dnt;
	private String details = "";
	private String label = "";
	private String reminderDateAndTime = "";
	private boolean isDone = false;
	private boolean isPriority = false;
	@Test
	public void floatingTaskWithPriorityTest() {
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("lets play games!");
		Iterator<Task> i = control.getTaskIterator();
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
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("cs2106 homework done by 31-10-14 2359hrs");
		Iterator<Task> i = control.getTaskIterator();
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
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("incamp training from 29/10/2014 8am to 3/11/2014 9pm");
		Iterator<Task> i = control.getTaskIterator();
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
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("test done");
		String feedback1 = control.executeCommand("done 1");
		Iterator<Task> i = control.getTaskIterator();
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
		TDTController control = new TDTController(fileName);
		control.executeCommand("label homework");
		control.executeCommand("label check!");
		Iterator<Task> i = control.getTaskIterator();
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
		TDTController control = new TDTController(fileName);
		control.executeCommand("do homework from 1pm - 3pm");
		control.executeCommand("edit 1 dun wan do homework liao");
		Iterator<Task> i = control.getTaskIterator();
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
		TDTController control = new TDTController(fileName);
		control.executeCommand("testing delete task 1");
		control.executeCommand("testing delete task 2");
		control.executeCommand("delete 1");
		Iterator<Task> i = control.getTaskIterator();
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
		TDTController control = new TDTController(fileName);
		control.executeCommand("testing reminder task");
		control.executeCommand("remind 1 20-12-14 330pm");
		Iterator<Task> i = control.getTaskIterator();
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
