package todothis.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import todothis.logic.TDTDateAndTime;
import todothis.logic.Task;
import todothis.storage.TDTStorage;

public class TDTStorageTest {

	/*
	@Test
	public void testRead() throws Exception {
		TDTStorage storage = new TDTStorage("TestStorage.txt");
		storage.readInitialise();
		Iterator<Task> iter = storage.getTaskIterator();
		ArrayList<Task> ans = new ArrayList<Task>();
		ans.add(new Task(1, "Today", "Buy egg", new TDTDateAndTime("","","",""), true));
		ans.add(new Task(2, "Today", "Buy rice", new TDTDateAndTime("","","",""), false));
		ans.add(new Task(3, "Today", "Buy rice", new TDTDateAndTime("","","",""), false));
		ans.add(new Task(1, "Ytd", "Buy nothing", new TDTDateAndTime("","","",""), true));
		int total = 0;
		
		while(iter.hasNext()) {
			Task task = iter.next();
			assertEquals(task.getTaskID(), ans.get(total).getTaskID());
			assertEquals(task.getDetails(), ans.get(total).getDetails());
			assertEquals(task.isHighPriority(), ans.get(total).isHighPriority());
			total++;
		}
		assertEquals(total, ans.size());
	}*/
	
	
	
	//Normal case of writing 2 labels to file.
	@Test
	public void testWrite1() throws Exception {
		TDTStorage storage = new TDTStorage("TestStorage.txt");
		storage.getLabelMap().put("TODAY", new ArrayList<Task>());
		storage.getLabelMap().put("YTD", new ArrayList<Task>());
		storage.write();
		
		BufferedReader br = new BufferedReader(new FileReader("TestStorage.txt"));
		ArrayList<String> lines = new ArrayList<String>();
		while(br.ready()) {
			lines.add(br.readLine());
		}
		br.close();
		
		assertEquals("3", lines.get(0));
		assertEquals("TODOTHIS", lines.get(1));
		assertEquals("TODAY", lines.get(2));
		assertEquals("YTD", lines.get(3));
	}
	
	//Boundary case of writing no labels to file.
	@Test
	public void testWrite2() throws Exception {
		TDTStorage storage = new TDTStorage("TestStorage.txt");
		storage.write();

		BufferedReader br = new BufferedReader(new FileReader("TestStorage.txt"));
		ArrayList<String> lines = new ArrayList<String>();
		while(br.ready()) {
			lines.add(br.readLine());
		}
		br.close();

		assertEquals("1", lines.get(0));
		assertEquals("TODOTHIS", lines.get(1));

	}
	/*
	//Normal case of writing tasks to file.
		@Test
		public void testWrite3() throws Exception {
			TDTStorage storage = new TDTStorage("TestStorage.txt");
			storage.write();

			BufferedReader br = new BufferedReader(new FileReader("TestStorage.txt"));
			ArrayList<String> lines = new ArrayList<String>();
			while(br.ready()) {
				lines.add(br.readLine());
			}
			br.close();

			assertEquals("1", lines.get(0));
			assertEquals("TODOTHIS", lines.get(1));
			
				for(int i = 0; i < lines.size(); i++) {
					String[] params = lines.get(i);
					boolean p = false;
					Task task = storage.getLabelMap().get(params[0]).get(i%3);
					assertEquals(task.getLabelName(), params[0]);
					assertEquals(task.getDetails(), params[1]);
					if(params[4].equals("true")) {
						p = true;
					}
					assertEquals(task.isHighPriority(), p);
				}

		}*/
}
