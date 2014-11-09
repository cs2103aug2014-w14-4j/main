package todothis.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import todothis.commons.TDTCommons;
import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.logic.command.AddCommand;
import todothis.logic.command.DeleteCommand;
import todothis.logic.command.DoneCommand;
import todothis.logic.command.EditCommand;
import todothis.logic.command.HideCommand;
import todothis.logic.command.LabelCommand;
import todothis.logic.command.RemindCommand;
import todothis.logic.command.SearchCommand;
import todothis.logic.command.ShowCommand;
import todothis.storage.TDTDataStore;

public class TDTCommandTest {
	private TDTDataStore testData;
	
	@Before
	public void resetDataStore() {
		testData = new TDTDataStore("testCommand");
	}
	
	//Normal case of adding a floating task and priority task
	@Test
	public void testAddCommand1() {
		String task1 = "normal task";
		String task2 = "priority task";
		String label = testData.getCurrLabel();
		
		AddCommand comd = new AddCommand(task1, new TDTDateAndTime(), false);
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, label), comd.execute(testData));
		
		comd = new AddCommand(task2, new TDTDateAndTime(), true);
		assertEquals(String.format(AddCommand.MESSAGE_ADD_FEEDBACK, label), comd.execute(testData));
		
		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(2, taskList.size());
		assertTrue(task2.equals(taskList.get(0).getDetails()));
		assertTrue(taskList.get(0).isHighPriority());
		assertTrue(task1.equals(taskList.get(1).getDetails()));
		assertFalse(taskList.get(1).isHighPriority());
	}
	
	//Error case of adding task with invalid data/time
	@Test
	public void testAddCommand2() {
		String task1 = "invalid task";
		String label = testData.getCurrLabel();
		
		AddCommand comd = new AddCommand(task1, new TDTDateAndTime("04/11/2014",
				"03/11/2014", "null", "null"), false);
		assertEquals(AddCommand.MESSAGE_INVALID_END_DATE, comd.execute(testData));
		
		comd = new AddCommand(task1, new TDTDateAndTime("04/20/2014",
				"40/11/2014", "null", "null"), false);
		assertEquals(AddCommand.MESSAGE_INVALID_DATE_TIME_FORMAT, comd.execute(testData));
		
		comd = new AddCommand(task1, new TDTDateAndTime("null",
				"null", "22:00", "20:00"), false);
		assertEquals(AddCommand.MESSAGE_INVALID_END_TIME, comd.execute(testData));
		
		comd = new AddCommand(task1, new TDTDateAndTime("null",
				"null", "30:00", "40:00"), false);
		assertEquals(AddCommand.MESSAGE_INVALID_DATE_TIME_FORMAT, comd.execute(testData));
		
		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(0, taskList.size());
	}
	
	//Normal case of deleting specific task from current label
	@Test
	public void testDeleteTask1() {
		String label = testData.getCurrLabel();
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));
		
		DeleteCommand comd = new DeleteCommand("", 2);
		assertEquals(DeleteCommand.MESSAGE_DELETE_TASK, comd.execute(testData));
		
		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(2, taskList.size());
		assertTrue(taskList.get(0).getDetails().equals("Task 1"));
		assertTrue(taskList.get(1).getDetails().equals("Task 3"));
	}
	
	//Normal case of deleting specific task from specific label
	@Test
	public void testDeleteTask2() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		DeleteCommand comd = new DeleteCommand(label, 2);
		comd.execute(testData);

		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(2, taskList.size());
		assertTrue(taskList.get(0).getDetails().equals("Task 1"));
		assertTrue(taskList.get(1).getDetails().equals("Task 3"));
	}
	
	//Boundary case of deleting specific task from specific label
	@Test
	public void testDeleteTask3() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		DeleteCommand comd = new DeleteCommand(label, 3);
		comd.execute(testData);

		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(2, taskList.size());
		assertTrue(taskList.get(0).getDetails().equals("Task 1"));
		assertTrue(taskList.get(1).getDetails().equals("Task 2"));
	}

	//Error case of deleting specific task from specific label
	@Test
	public void testDeleteTask4() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		DeleteCommand comd = new DeleteCommand(label, 5);
		assertEquals(DeleteCommand.MESSAGE_INVALID_LABEL_TASKID, comd.execute(testData));
		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(3, taskList.size());
		
		comd = new DeleteCommand(label, -2);
		assertEquals(DeleteCommand.MESSAGE_INVALID_LABEL_TASKID, comd.execute(testData));
		assertEquals(3, taskList.size());
	}
	
	//Normal Case of editing a task
	@Test
	public void testEditCommand1() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));
		
		EditCommand comd = new EditCommand(label, 2, "EDITED", new TDTDateAndTime(), false);
		assertEquals(EditCommand.MESSAGE_EDIT_FEEDBACK, comd.execute(testData));
		
		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(3, taskList.size());
		assertEquals("EDITED", taskList.get(1).getDetails());
		
	}
	
	//Boundary Case of editing a task
	@Test
	public void testEditCommand2() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		EditCommand comd = new EditCommand(label, 3, "EDITED", new TDTDateAndTime(), false);
		assertEquals(EditCommand.MESSAGE_EDIT_FEEDBACK, comd.execute(testData));

		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(3, taskList.size());
		assertEquals("EDITED", taskList.get(2).getDetails());

	}
	
	//Error Case of editing a task
	@Test
	public void testEditCommand3() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		EditCommand comd = new EditCommand(label, 5, "EDITED",
				new TDTDateAndTime("04/11/2014", "01/11/2014", "null", "null"), false);
		assertEquals(EditCommand.MESSAGE_INVALID_LABEL_TASKID, comd.execute(testData));

		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(3, taskList.size());
		assertEquals("Task 3", taskList.get(2).getDetails());

	}
	
	//Normal case of marking a task done
	@Test
	public void testDoneCommand1() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		DoneCommand comd = new DoneCommand(label, 2);
		assertEquals(DoneCommand.MESSAGE_DONE_TASK_FEEDBACK, comd.execute(testData));

		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(3, taskList.size());
		assertTrue(taskList.get(1).isDone());
		assertFalse(taskList.get(0).isDone());
		assertFalse(taskList.get(2).isDone());
	}
	
	//Boundary case of marking a task done
	@Test
	public void testDoneCommand2() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		DoneCommand comd = new DoneCommand(label, 3);
		assertEquals(DoneCommand.MESSAGE_DONE_TASK_FEEDBACK, comd.execute(testData));

		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(3, taskList.size());
		assertTrue(taskList.get(2).isDone());
		assertFalse(taskList.get(0).isDone());
		assertFalse(taskList.get(1).isDone());
	}
	
	//Error case of marking a task done
	@Test
	public void testDoneCommand3() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		DoneCommand comd = new DoneCommand(label, -5);
		assertEquals(DoneCommand.MESAGE_INVALID_LABEL_TASKID, comd.execute(testData));

		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals(3, taskList.size());
		assertFalse(taskList.get(2).isDone());
		assertFalse(taskList.get(0).isDone());
		assertFalse(taskList.get(1).isDone());
	}
	
	//Normal case of hiding two label
	@Test
	public void testHideCommand1() {
		String label1 = "TEST1";
		String label2 = "TEST2";
		testData.getTaskMap().put(label1, new ArrayList<Task>());
		testData.getTaskMap().put(label2, new ArrayList<Task>());
		
		HideCommand comd = new HideCommand(label1 + " " + label2);
		assertEquals(HideCommand.MESSAGE_HIDE_FEEDBACK, comd.execute(testData));
		
		assertTrue(testData.getHideList().contains(label1));
		assertTrue(testData.getHideList().contains(label2));
		assertFalse(testData.getHideList().contains(testData.getCurrLabel()));
	}
	
	//Error case of hiding invalid label
	@Test
	public void testHideCommand2() {
		String label1 = "TEST1";
		String label2 = "TEST2";
		testData.getTaskMap().put(label1, new ArrayList<Task>());
		testData.getTaskMap().put(label2, new ArrayList<Task>());
		
		HideCommand comd = new HideCommand("Invalid");
		assertEquals(HideCommand.MESSAGE_HIDE_FEEDBACK, comd.execute(testData));
		
		assertFalse(testData.getHideList().contains(label1));
		assertFalse(testData.getHideList().contains(label2));
		assertFalse(testData.getHideList().contains(testData.getCurrLabel()));
	}
	
	//Test creating label and changing label directory
	@Test
	public void testLabelCommand() {
		String label = "TEST";
		
		LabelCommand comd = new LabelCommand(label);
		
		assertEquals(String.format(LabelCommand.MESSAGE_CREATE_LABEL_FEEDBACK, label), 
				comd.execute(testData));
		assertEquals(2, testData.getTaskMap().size());
		assertEquals(label, testData.getCurrLabel());
		
		comd = new LabelCommand(TDTCommons.DEFAULT_LABEL);
		assertEquals(String.format(LabelCommand.MESSAGE_LABEL_FEEDBACK, TDTCommons.DEFAULT_LABEL),
				comd.execute(testData));
		assertEquals(TDTCommons.DEFAULT_LABEL, testData.getCurrLabel());
	}
	
	//Normal case of showing two label
	@Test
	public void testShowCommand1() {
		String label1 = "TEST1";
		String label2 = "TEST2";
		testData.getTaskMap().put(label1, new ArrayList<Task>());
		testData.getTaskMap().put(label2, new ArrayList<Task>());
		
		ShowCommand comd = new ShowCommand(label1 + " " + label2);
		assertEquals(ShowCommand.MESSAGE_SHOW_FEEDBACK, comd.execute(testData));
		
		assertFalse(testData.getHideList().contains(label1));
		assertFalse(testData.getHideList().contains(label2));
		assertTrue(testData.getHideList().contains(testData.getCurrLabel()));
	}

	//Error case of showing invalid label
	@Test
	public void testShowCommand2() {
		String label1 = "TEST1";
		String label2 = "TEST2";
		testData.getTaskMap().put(label1, new ArrayList<Task>());
		testData.getTaskMap().put(label2, new ArrayList<Task>());

		ShowCommand comd = new ShowCommand("Invalid");
		assertEquals(ShowCommand.MESSAGE_SHOW_FEEDBACK, comd.execute(testData));

		assertTrue(testData.getHideList().contains(label1));
		assertTrue(testData.getHideList().contains(label2));
		assertTrue(testData.getHideList().contains(testData.getCurrLabel()));
	}
	
	//Normal case of adding and removing reminder 
	@Test
	public void testRemindCommand() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));

		RemindCommand comd = new RemindCommand(label, 2, "25/12/2014 12pm");
		assertEquals(String.format(RemindCommand.MESSAGE_REMIND_FEEDBACK, "25/12/2014 12:00"),
				comd.execute(testData));
		ArrayList<Task> taskList = testData.getTaskListFromLabel(label);
		assertEquals("25/12/2014 12:00", taskList.get(1).getRemindDateTime());
		assertTrue(taskList.get(1).hasReminder());
		
		comd = new RemindCommand(label, 2, "");
		assertEquals(String.format(RemindCommand.MESSAGE__REMOVE_REMIND_FEEDBACK, "25/12/2014 12:00"),
				comd.execute(testData));
		assertFalse(taskList.get(1).hasReminder());
	}
	
	//Test search everything
	@Test
	public void testSearchCommand1() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "This is task 4",
				new TDTDateAndTime(), false));
		
		SearchCommand comd = new SearchCommand("");
		assertEquals(String.format(SearchCommand.SEARCH_FEEDBACK, 4), comd.execute(testData));
	}

	//Test search keyWords
	@Test
	public void testSearchCommand2() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "This is task 4",
				new TDTDateAndTime(), false));

		SearchCommand comd = new SearchCommand("th");
		assertEquals(String.format(SearchCommand.SEARCH_FEEDBACK, 1), comd.execute(testData));
		assertEquals("This is task 4", comd.getSearchedResult().get(0).getDetails());
		
		comd = new SearchCommand("task 3");
		assertEquals(String.format(SearchCommand.SEARCH_FEEDBACK, 1), comd.execute(testData));
		assertEquals("Task 3", comd.getSearchedResult().get(0).getDetails());
	}
	
	//Test search Date and keyword and Date
	@Test
	public void testSearchCommand3() {
		String label = "TEST";
		testData.getTaskMap().put(label, new ArrayList<Task>());
		testData.addTask(new Task(1, label , "Task 1",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(2, label, "Task 2",
				new TDTDateAndTime(), false));
		testData.addTask(new Task(3, label, "Task 3",
				new TDTDateAndTime("12/12/2014", "null", "null", "null"), false));
		testData.addTask(new Task(3, label, "This is task 4",
				new TDTDateAndTime("12/12/2014", "null", "null", "null"), false));

		SearchCommand comd = new SearchCommand("@12/12/2014");
		assertEquals(String.format(SearchCommand.SEARCH_FEEDBACK, 2), comd.execute(testData));
		assertEquals("Task 3", comd.getSearchedResult().get(0).getDetails());
		assertEquals("This is task 4", comd.getSearchedResult().get(1).getDetails());

		comd = new SearchCommand("this @12/12/2014");
		assertEquals(String.format(SearchCommand.SEARCH_FEEDBACK, 1), comd.execute(testData));
		assertEquals("This is task 4", comd.getSearchedResult().get(0).getDetails());
	}

}
