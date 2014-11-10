//@author A0111211L
package todothis.test;

import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.junit.Test;
import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.storage.TDTDataStore;

public class TDTFileHandlerTest {

	private static final int TEST_SIZE = 100;

	@Test // Normal case of reading 2 labels from file.
	public void testRead1() throws IOException {
		int i = 0;
		int[] testArray = new int[TEST_SIZE];
		TDTDataStore testData = new TDTDataStore("testFH.txt");
		testData.readAndInitialize();
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
	
	@Test // Normal case of reading 4 labels from file and checking if the Strings are equal.
	public void testRead2() throws IOException {
		int i = 0;
		String[] labelArray = new String[TEST_SIZE];
		TDTDataStore testData = new TDTDataStore("testFH2.txt");
		testData.readAndInitialize();
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
	
	@Test // Boundary case of writing no labels to file.
	public void testRead3() throws IOException {
		TDTDataStore testData = new TDTDataStore("testFH3.txt");
		testData.readAndInitialize();
		Iterator<Task> taskIte = testData.getTaskIterator();
		 
		assertEquals(false, taskIte.hasNext());
		
	}
	
	@Test // Normal case of reading a priority and done task from file.
	public void testRead4() throws IOException {
		TDTDataStore testData = new TDTDataStore("testFH4.txt");
		testData.readAndInitialize();
		Iterator<Task> taskIte = testData.getTaskIterator();
		Task task = taskIte.next();
		Task toTest = new Task(1, "test 1", "test 1", new TDTDateAndTime(), true, true, "null");
		
		assertEquals(toTest.isHighPriority(), task.isHighPriority());
		assertEquals(toTest.isDone(), task.isDone());
	}
	
	@Test // Normal case of reading a timed task from file.
	public void testRead5() throws IOException {
		TDTDataStore testData = new TDTDataStore("testFH5.txt");
		testData.readAndInitialize();
		Iterator<Task> taskIte = testData.getTaskIterator();
		Task task = taskIte.next();
		TDTDateAndTime testTime = new TDTDateAndTime("12/12/2014", "14/12/2014", "12:00", "14:00");
		TDTDateAndTime taskTime = task.getDateAndTime();
		
		assertEquals(testTime.getStartTime(), taskTime.getStartTime());
		assertEquals(testTime.getEndTime(), taskTime.getEndTime());
		assertEquals(testTime.getStartDate(), taskTime.getStartDate());
		assertEquals(testTime.getEndDate(), taskTime.getEndDate());
		
	}
	
	@Test // Normal case of writing 2 labels to file.
	public void testWrite() throws IOException {
		TDTDataStore storage = new TDTDataStore("TestStorage.txt");
		
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
		storage.writeToFile();
		
		BufferedReader br = new BufferedReader(new FileReader("TestStorage.txt"));
		ArrayList<String> lines = new ArrayList<String>();
		while(br.ready()) {
			lines.add(br.readLine());
		}
		br.close();
		
		assertEquals("3", lines.get(0));
		assertEquals("TODAY", lines.get(1));
		assertEquals("TODOTHIS", lines.get(2));
		assertEquals("SOON", lines.get(3));
		
	}
	
	@Test // Boundary case of writing no labels to file.
	public void testWrite2() throws IOException {
		TDTDataStore storage = new TDTDataStore("TestStorage.txt");
		storage.writeToFile();
		
		BufferedReader br = new BufferedReader(new FileReader("TestStorage.txt"));
		ArrayList<String> lines = new ArrayList<String>();
		while(br.ready()) {
			lines.add(br.readLine());
		}
		br.close();

		assertEquals("1", lines.get(0));
		assertEquals("TODOTHIS", lines.get(1));
		
	}

	@Test // Normal case of writing tasks into file.
	public void testWrite3() throws IOException {
		TDTDataStore storage = new TDTDataStore("TestStorage2.txt");	
		Task task1 = new Task(1, "test", "test1", new TDTDateAndTime(), false);
		Task task2 = new Task(2, "test", "test2", new TDTDateAndTime(), false);
		Task task3 = new Task(3, "test", "test3", new TDTDateAndTime(), false);
		Task task4 = new Task(4, "test", "test4", new TDTDateAndTime(), false);
		Task task5 = new Task(5, "test", "test5", new TDTDateAndTime(), false);
		Task task6 = new Task(6, "test", "test6", new TDTDateAndTime(), false);
		Task task7 = new Task(7, "test", "test7", new TDTDateAndTime(), false);
		Task task8 = new Task(8, "test", "test8", new TDTDateAndTime(), false);
		Task task9 = new Task(9, "test", "test9", new TDTDateAndTime(), false);
		ArrayList<Task> al1 = new ArrayList<Task>();
		al1.add(task1);
		al1.add(task2);
		al1.add(task3);
		al1.add(task4);
		al1.add(task5);
		al1.add(task6);
		al1.add(task7);
		al1.add(task8);
		al1.add(task9);
		storage.getTaskMap().put("SOON", al1);
		storage.writeToFile();
		
		BufferedReader br = new BufferedReader(new FileReader("TestStorage2.txt"));
		ArrayList<String> lines = new ArrayList<String>();
		while(br.ready()) {
			lines.add(br.readLine());
		}
		br.close();

		assertEquals("2", lines.get(0));
		assertEquals("TODOTHIS", lines.get(1));
		assertEquals("SOON", lines.get(2));
		assertEquals(task1.getDetails(), lines.get(3).substring(5, 10));
		assertEquals(task2.getDetails(), lines.get(4).substring(5, 10));
		assertEquals(task3.getDetails(), lines.get(5).substring(5, 10));
		assertEquals(task4.getDetails(), lines.get(6).substring(5, 10));
		assertEquals(task5.getDetails(), lines.get(7).substring(5, 10));
		assertEquals(task6.getDetails(), lines.get(8).substring(5, 10));
		assertEquals(task7.getDetails(), lines.get(9).substring(5, 10));
		assertEquals(task8.getDetails(), lines.get(10).substring(5, 10));
		assertEquals(task9.getDetails(), lines.get(11).substring(5, 10));
		
	}
}
