package todothis;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

public class TDTStorageTest {


	@Test
	public void testRead() throws Exception {
		TDTStorage storage = new TDTStorage("TestStorage.txt");
		storage.readInitialise();
		Iterator<Task> iter = storage.getTaskIterator();
		ArrayList<Task> ans = new ArrayList<Task>();
		ans.add(new Task(1, "Today", "Buy egg", "20092014", "1400", true));
		ans.add(new Task(2, "Today", "Buy rice", "20092014", "1500", false));
		ans.add(new Task(3, "Today", "Buy rice", "19092014", "", false));
		ans.add(new Task(1, "Ytd", "Buy nothing", "12122012", "0600", true));
		int total = 0;
		
		while(iter.hasNext()) {
			Task task = iter.next();
			assertEquals(task.getTaskID(), ans.get(total).getTaskID());
			assertEquals(task.getDetails(), ans.get(total).getDetails());
			assertEquals(task.getDueDate(), ans.get(total).getDueDate());
			assertEquals(task.getDueTime(), ans.get(total).getDueTime());
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
		storage.addTask(new Task(1, "Today", "Buy egg", "20092014", "1400", true));
		storage.addTask(new Task(2, "Today", "Buy rice", "20092014", "1500", false));
		storage.addTask(new Task(3, "Today", "Buy rice", "19092014", "", false));
		storage.addTask(new Task(1, "Ytd", "Buy nothing", "12122012", "0600", true));
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
			assertEquals(task.getDueDate(), params[2]);
			assertEquals(task.getDueTime(), params[3]);
			if(params[4].equals("true")) {
				p = true;
			}
			assertEquals(task.isHighPriority(), p);
		}
		
	}
}
