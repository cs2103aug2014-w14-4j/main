package todothis.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.junit.Test;

import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.storage.TDTDataStore;
import todothis.storage.TDTFileHandler;

import org.junit.Test;

public class TDTFileHandlerTest {

	private static final int TEST_SIZE = 100;

	@Test // Test if the label size is similar
	public void testRead1() throws IOException {
		int i = 0;
		int[] testArray = new int[TEST_SIZE];
		TDTDataStore testData = new TDTDataStore("testFH.txt");
		testData.read();
		Iterator<String> testIterator = testData.getLabelIterator();
		while (testIterator.hasNext()) {
			String ln = testIterator.next();
			int labelSize = testData.getLabelSize(ln);
			testArray[i] = labelSize;
			i++;
		}
		TDTDataStore storage = new TDTDataStore("storage.txt");
		
		Task task1 = new Task(1, "test 1", "test 1", new TDTDateAndTime(), false);
		Task task2 = new Task(2, "test 2", "test 2", new TDTDateAndTime(), false);
		Task task3 = new Task(3, "test 3", "test 3", new TDTDateAndTime(), false);
		ArrayList<Task> al1 = new ArrayList<Task>();
		al1.add(task1);
		al1.add(task2);
		al1.add(task3);
		storage.getTaskMap().put("SOON", al1);
		Task task4 = new Task(4, "test 4", "test 4", new TDTDateAndTime(), false);
		Task task5 = new Task(5, "test 5", "test 5", new TDTDateAndTime(), false);	
		Task task6 = new Task(6, "test 6", "test 6", new TDTDateAndTime(), false);
		Task task7 = new Task(7, "test 7", "test 7", new TDTDateAndTime(), false);
		ArrayList<Task> al2 = new ArrayList<Task>();
		al2.add(task4);
		al2.add(task5);
		al2.add(task6);
		al2.add(task7);
		storage.getTaskMap().put("TODAY", al2);
		
		assertEquals(al2.size(), testArray[0]);
		assertEquals(0, testArray[1]);
		assertEquals(al1.size(), testArray[2]);
		
	}
	
	@Test // Test if the number of labels is accurate
	public void testRead2() throws IOException {
		int i = 0;
		String[] labelArray = new String[TEST_SIZE];
		TDTDataStore testData = new TDTDataStore("testFH2.txt");
		testData.read();
		Iterator<String> testIterator = testData.getLabelIterator();
		while (testIterator.hasNext()) {
			String ln = testIterator.next();
			labelArray[i] = ln;
			i++;
		}
		
		assertEquals("PLAY", labelArray[0]);
		assertEquals("TODAY", labelArray[1]);
		assertEquals("TOMORROW", labelArray[2]);
		assertEquals("WORK", labelArray[3]);
		assertEquals("TODOTHIS", labelArray[4]);
	}
	
	@Test // Test if startup file is correct
	public void testRead3() throws IOException {
		TDTDataStore testData = new TDTDataStore("testFH3.txt");
		testData.read();
		Iterator<Task> taskIte = testData.getTaskIterator();
		 
		assertEquals(false, taskIte.hasNext());
		
	}
	
	@Test // Test if priority task and done task added in file is correct
	public void testRead4() throws IOException {
		TDTDataStore testData = new TDTDataStore("testFH4.txt");
		testData.read();
		Iterator<Task> taskIte = testData.getTaskIterator();
		Task task = taskIte.next();
		Task toTest = new Task(1, "test 1", "test 1", new TDTDateAndTime(), true, true, "null");
		
		assertEquals(toTest.isHighPriority(), task.isHighPriority());
		assertEquals(toTest.isDone(), task.isDone());
	}
	
	@Test // Test if date and time task added in file is correct
	public void testRead5() throws IOException {
		TDTDataStore testData = new TDTDataStore("testFH5.txt");
		testData.read();
		Iterator<Task> taskIte = testData.getTaskIterator();
		Task task = taskIte.next();
		TDTDateAndTime testTime = new TDTDateAndTime("12/12/2014", "14/12/2014", "12:00", "14:00");
		TDTDateAndTime taskTime = task.getDateAndTime();
		
		assertEquals(testTime.getStartTime(), taskTime.getStartTime());
		assertEquals(testTime.getEndTime(), taskTime.getEndTime());
		assertEquals(testTime.getStartDate(), taskTime.getStartDate());
		assertEquals(testTime.getEndDate(), taskTime.getEndDate());
		
	}
	
	
	
	
	
	
	

}
