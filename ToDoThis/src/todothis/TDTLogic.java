package todothis;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.ITDTParser.COMMANDTYPE;


public class TDTLogic implements ITDTLogic {
	private TDTStorage storage;
	
	public TDTLogic(TDTStorage storage) {
		this.storage = storage;
	}
	
	@Override
	public String executeCommand(Command command) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// public Task(int taskID, String labelName, String details, String dueDate,
	//			String dueTime, boolean p) {
	//public Command(COMMANDTYPE commandType, String labelName, int taskID,
	//		String commandDetails, String dueDate, String dueTime, 
	//		boolean isHighPriority) {
	/**
	 *
	 * @throws Exception 
	 */
	@Override
	public String doADD(Command command){
		String labelName = storage.getCurrLabel();
		int taskId = storage.getLabelSize(labelName) + 1;
		Task task = new Task(taskId, labelName, command.getCommandDetails(),
				command.getDueDate(), command.getDueTime(), command.isHighPriority());
		storage.addTask(task);
		storage.write();
		return "Add success";
	}
	

	@Override
	public String doDelete(Command command) {
		if(command.getTaskID() != -1) {
			//Deleting a task
			if(storage.getLabelMap().containsKey(command.getLabelName())) {
				ArrayList<Task> array = storage.getLabelMap().get(command.getLabelName());
				if(command.getTaskID() <= array.size() && command.getTaskID() > 0) {
					array.remove(command.getTaskID() - 1);
					renumberTaskID(array);
					return "Task deleted";
				} else {
					return "error";
				} 
			} else {
				return "error";
			}
		} else {
			//Deleting a label
			if(storage.getLabelMap().containsKey(command.getLabelName())) {
				storage.getLabelMap().remove(command.getLabelName());
				return "Label Deleted";
			} else {
				return "Label does not exist";
			}
		}
		
	}

	private void renumberTaskID(ArrayList<Task> array) {
		for(int i = 0; i < array.size(); i++) {
			Task task = array.get(i);
			task.setTaskID(i + 1);
		}
	}

	@Override
	public String doSearch(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doSort(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doEdit(Command command) {
		// TODO Auto-generated method stub
		
		
		
		
		
		//testing
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return null;
	}


	@Override
	public String doUndo(Command command) {
		if(command.getCommandType() != COMMANDTYPE.SEARCH) {
			if(!storage.getUndoStack().isEmpty()) {
				storage.setLabelMap(storage.getUndoStack().pop());
				return "Undo success!";
			} else {
				return "No command to undo.";
			}
		} else {
			return "";
		}
	}

	

	@Override
	public String doDisplay(Command command) {
		// TODO Auto-generated method stub
		String labelName = command.getLabelName();
		Iterator<Task> i;
		if(labelName.equals("")){
			i = storage.getTaskIterator();
			while(i.hasNext()){
				Task temp = i.next();
				temp.setHide(false);
			}
		}else if(storage.getLabelMap().containsKey(labelName)){
			i = storage.getTaskIterator();
			while(i.hasNext()){
				Task temp = i.next();
				if(temp.getLabelName().equals(labelName)){
					temp.setHide(false);
				}else{
					temp.setHide(true);
				}
			}
		}else{
			System.out.println("Display command invalid!");
		}
		return null;
	}

	@Override
	public String doHide(Command command) {
		Iterator <Task> i;
		String labelName = command.getLabelName();

		if(labelName.equals("")){
			i = storage.getTaskIterator();
			while(i.hasNext()){
				i.next().setHide(true);
			}
		}else{
			if(storage.getLabelMap().containsKey(labelName)){
				i = storage.getLabelMap().get(labelName).iterator();
				while(i.hasNext()){
					i.next().setHide(true);
				}
			}else{
				System.out.println("Label name cannot be found!");
			}
		}
		return null;
	}
	
	
	@Override
	public String doDone(Command command) {
		String labelName = command.getLabelName();
		int taskID = command.getTaskID() - 1;
		Iterator <Task> i;
		int counter = 0;
		
		if(!storage.getLabelMap().containsKey(labelName)){
			System.out.println("Label Name cannot be found!");
		}else if(storage.getLabelMap().get(labelName).size() < taskID ||
				taskID <=0){
			System.out.println("TaskID to be marked done cannot be found! OUT OF RANGE!");
			
		}else{
			i = storage.getLabelMap().get(labelName).iterator();
			while(i.hasNext()){
				Task temp = i.next();
				if(counter == taskID){
					temp.setDone(true);
					break;
				}
				counter++;
			}
		}
		return null;
	}
	
	@Override
	public String doLabel(Command command) {
		if(storage.getLabelMap().containsKey(command.getLabelName())) {
			storage.setCurrLabel(command.getLabelName());
		} else {
			storage.getLabelMap().put(command.getLabelName(), new ArrayList<Task>());
		}
		return "";
	}

}
