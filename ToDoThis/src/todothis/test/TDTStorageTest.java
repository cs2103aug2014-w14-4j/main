package todothis.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

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
	}
	
	@Test
	public void testWrite() throws Exception {
		TDTStorage storage = new TDTStorage("TestStorage.txt");
		storage.getLabelMap().put("Today", new ArrayList<Task>());
		storage.getLabelMap().put("Ytd", new ArrayList<Task>());
		storage.addTask(new Task(1, "Today", "Buy egg", new TDTDateAndTime("","","",""), true));
		storage.addTask(new Task(2, "Today", "Buy rice",new TDTDateAndTime("","","",""), false));
		storage.addTask(new Task(3, "Today", "Buy rice", new TDTDateAndTime("","","",""), false));
		storage.addTask(new Task(1, "Ytd", "Buy nothing", new TDTDateAndTime("","","",""), true));
		storage.write();
		
		BufferedReader br = new BufferedReader(new FileReader("TestStorage.txt"));
		ArrayList<String[]> lines = new ArrayList<String[]>();
		while(br.ready()) {
			lines.add(br.readLine().split("\t"));
		}
		br.close();
		
		assertEquals(4, lines.size());
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
