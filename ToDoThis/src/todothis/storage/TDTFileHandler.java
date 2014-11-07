package todothis.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import todothis.commons.TDTReminder;
import todothis.commons.TDTTimeMethods;
import todothis.commons.Task;
import todothis.logic.TDTDateAndTime;

public class TDTFileHandler {
	private String fileName;
	private BufferedWriter bw;
	
	public TDTFileHandler(String fileName) {
		setFileName(fileName);
	}
	
	public void readInitialise(TDTDataStore data) throws IOException {
		HashMap<String,ArrayList<Task>> taskMap = data.getTaskMap();
		ArrayList<String> autoWords = data.getAutoWords();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			int totalLabel = Integer.parseInt(br.readLine());
			for(int i = 0; i < totalLabel; i ++) {
				String label = br.readLine();
				taskMap.put(label, new ArrayList<Task>());
				autoWords.add(label);
			}
			Collections.sort(autoWords);
			while(br.ready()) {
				String line = br.readLine();
				String[] params = line.split("\t");
				assert(params.length == 9);
				TDTDateAndTime date = new TDTDateAndTime(params[4], params[5], 
						params[6], params[7] );
				Task task = new Task(0, params[0], params[1] , date, false, false, params[8]);
				if(!taskMap.containsKey(params[0])) {
					taskMap.put(params[0], new ArrayList<Task>());
				}
				task.setTaskID(data.getLabelSize(params[0]) + 1);
				if(params[2].equals("true")) {
					task.setHighPriority(true);
				}
				if(params[3].equals("true")) {
					task.setDone(true);
				}
				if(!params[8].equals("null")) {
					task.setReminder(new TDTReminder(
							TDTTimeMethods.calculateRemainingTime(params[8]), task));
				}
				data.addTask(task);	
			}
			br.close();
		} catch(Exception e) {
			bw = new BufferedWriter(new FileWriter(fileName));
		}
	}
	
	public void write(TDTDataStore data){
		try {
			bw = new BufferedWriter(new FileWriter(fileName));
			bw.write(data.getTaskMap().size()+ "");
			bw.newLine();
			Iterator<String> labelIter = data.getLabelIterator();
			while(labelIter.hasNext()) {
				bw.write(labelIter.next());
				bw.newLine();
			}
			
			Iterator<Task> iter = data.getTaskIterator();
			while(iter.hasNext()) {
				Task task = iter.next();
				bw.write(task.getLabelName() + "\t" + task.getDetails() + "\t" + 
				task.isHighPriority() + "\t" + task.isDone() + "\t" +
				task.getDateAndTime().getStartDate() + "\t" +
				task.getDateAndTime().getEndDate() + "\t" +
				task.getDateAndTime().getStartTime() + "\t" +
				task.getDateAndTime().getEndTime() + "\t" +
				task.getRemindDateTime());
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			System.out.println("Error. Unable to initialise write file.");
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
