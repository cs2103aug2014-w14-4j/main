package todothis.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.logic.TDTController;
import todothis.logic.command.AddCommand;
import todothis.logic.command.DeleteCommand;
import todothis.logic.command.DoneCommand;
import todothis.logic.command.EditCommand;
import todothis.logic.command.LabelCommand;
import todothis.logic.command.RemindCommand;
import todothis.logic.command.SearchCommand;

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
		
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK,
				control.getCurrLabel()), feedback);
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
			
			assertEquals(details, "lets play games");
			
			assertTrue(isPriority);
		}
		
	}
	
	@Test
	public void quotationTaskTest() {
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("\"the day after tomorrow at 5pm!\" movie showing at 8pm ");
		
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK,
				control.getCurrLabel()), feedback);
		Iterator<Task> i = control.getTaskIterator();
		while(i.hasNext()) {
			current = i.next();
			dnt = current.getDateAndTime();
			details = current.getDetails();
			isPriority = current.isHighPriority();
			
			assertEquals(dnt.getStartDate(), "null");
			assertEquals(dnt.getStartTime(), "20:00");
			assertEquals(dnt.getEndDate(), "null");
			assertEquals(dnt.getEndTime(), "null");
			
			assertEquals(details, "the day after tomorrow at 5pm! movie showing");
			
			assertFalse(isPriority);
		}
		
	}
	
	@Test
	public void deadlineTaskTest() {
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("cs2106 homework done by 31-10-14 2359hrs");
		
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK,
				control.getCurrLabel()), feedback);
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
			
			assertEquals(details, "cs2106 homework done");
			
			assertFalse(isPriority);
		}
		
	}
	@Test
	public void timedTaskTest() {
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("incamp training from 29/10/2014 8am to 3/11/2014 9pm");
		
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, control.getCurrLabel()), feedback);
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
			
			assertEquals(details, "incamp training");
			
			assertFalse(isPriority);
		}
	}
	
	@Test
	public void doneTaskTest() {
		TDTController control = new TDTController(fileName);
		String testDetails = "do homework";
		control.executeCommand(testDetails);
		String feedback = control.executeCommand("done 1");
		
		assertEquals(DoneCommand.MESSAGE_DONE_TASK_FEEDBACK, feedback);
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
			
			assertEquals(details, testDetails);
			
			assertFalse(isPriority);
			assertTrue(isDone);
		}
		
	}
	
	/**
	 * Done a task twice doesn't change to undone 
	 */
	@Test
	public void doneTaskTest2() {
		TDTController control = new TDTController(fileName);
		String testDetails = "test done";
		control.executeCommand(testDetails);
		control.executeCommand("done 1");
		String feedback = control.executeCommand("done 1");
		
		assertEquals(DoneCommand.MESSAGE_DONE_TASK_FEEDBACK, feedback);
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
			
			assertEquals(details, testDetails);
			
			assertFalse(isPriority);
			assertTrue(isDone);
		}
		
	}
	
	@Test
	public void labelTest() {
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("label homework");
		assertEquals(String.format(LabelCommand.MESSAGE_CREATE_LABEL_FEEDBACK, control.getCurrLabel()), feedback);
		
		feedback = control.executeCommand("do cs2103 homework!");
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, control.getCurrLabel()), feedback);
		
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
			
			assertEquals(details, "do cs2103 homework");
			
			assertTrue(isPriority);
			assertFalse(isDone);
			
			assertEquals(label, "HOMEWORK");
		}
	}

	@Test
	public void editTest() {
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("do homework from 1pm - 3pm");
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, control.getCurrLabel()), feedback);

		feedback = control.executeCommand("edit 1 dun wan do homework liao");
		assertEquals(String.format(EditCommand.MESSAGE_EDIT_FEEDBACK, control.getCurrLabel()), feedback);
		
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
			
			assertEquals(details, "dun wan do homework liao");
			
			assertFalse(isPriority);
			assertFalse(isDone);
		}
	}
	@Test
	public void deleteTest() {
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("testing delete task 1");
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, control.getCurrLabel()), feedback);
		
		feedback = control.executeCommand("testing delete task 2");
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, control.getCurrLabel()), feedback);
		
		feedback = control.executeCommand("delete 1");
		assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_TASK, control.getCurrLabel()), feedback);
		
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
			
			assertEquals(details, "testing delete task 2");
			
			assertFalse(isPriority);
			assertFalse(isDone);
		}
	}
	@Test
	public void undoTest(){
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("testing undo command");
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, control.getCurrLabel()), feedback);
		
		feedback = control.executeCommand("task to be undo");
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, control.getCurrLabel()), feedback);
		
		control.executeCommand("undo");
		
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
			
			assertEquals(details, "testing undo command");

			assertFalse(isPriority);
			assertFalse(isDone);
		}
	}
	
	@Test
	public void searchTest(){
		TDTController control = new TDTController(fileName);
		control.executeCommand("buy eggs 10/11/2014 at 10pm");
		control.executeCommand("watch tv");
		control.executeCommand("do homework and eat eggs 10/11/2014");
		control.executeCommand("wash eggs 11/11/2014");
		control.executeCommand("lets play game 10/11/2014 at 6am");
		
		String feedback = control.executeCommand("search eggs@10/11/2014");
		//2 results found
		assertEquals(String.format(SearchCommand.SEARCH_FEEDBACK, 2), feedback);
		
		SearchCommand comd = ((SearchCommand)control.getCmd());	
		Iterator <Task>i = comd.getSearchedResult().iterator();
		
		int count = 0;
		while(i.hasNext()){
			Task t = i.next();
			if(count == 0){//first search result
				assertEquals(t.getDetails(), "buy eggs");
			}
			if(count == 1){//second search result
				assertEquals(t.getDetails(), "do homework and eat eggs");
			}
			count++;
		}
	}
	
	@Test
	public void reminderTest() {
		TDTController control = new TDTController(fileName);
		String feedback = control.executeCommand("testing reminder task");
		Task task = control.getAddedTask();
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, control.getCurrLabel()), feedback);
		
		feedback = control.executeCommand("remind 1 20-12-14 330pm");
		assertEquals(String.format(RemindCommand.MESSAGE_REMIND_FEEDBACK, task.getRemindDateTime()), feedback);
		
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
			
			assertEquals(details, "testing reminder task");
			assertEquals(reminderDateAndTime, "20/12/2014 15:30");
			assertFalse(isPriority);
			assertFalse(isDone);
		}
	}
	@Test
	public void fullSystemTest(){
		TDTController control = new TDTController(fileName);
		control.executeCommand("lets play games");
		control.executeCommand("label project");
		control.executeCommand("submit proposal by 2359hrs!");
		control.executeCommand("label todothis");
		control.executeCommand("need to buy dinner at 6pm");
		control.executeCommand("edit project 1 help help");
		control.executeCommand("lets play pool 15/11/2014 9pm");
		control.executeCommand("label project");
		control.executeCommand("delete todothis");
		control.executeCommand("undo");
		control.executeCommand("redo");
		
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
			
			assertEquals(details, "help help");
			assertFalse(isPriority);
			assertFalse(isDone);
			assertEquals(label, "PROJECT");
		}
	}
}
