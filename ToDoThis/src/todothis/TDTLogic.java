package todothis;

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
	
	/**
	 * public Task(int taskID, String labelName, String details, String dueDate,
			String dueTime, boolean p) {
	 */
	@Override
	public String doADD(Command command) {
		String labelName = storage.getCurrLabel();
		int labelId = 1;
		
		
		Task task = new Task(labelId, labelName, command.getCommandDetails(),
				command.getDueDate(), command.getDueTime(), command.isHighPriority());
		
		return null;
	}
	

	@Override
	public String doDelete(Command command) {
		// TODO Auto-generated method stub
		return null;
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
		String labelName = command.getLabelName();
		int taskID = command.getTaskID() - 1;
		
		return null;
	}


	@Override
	public String doUndo(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public void doDisplay(Command command) {
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
		
	}

	@Override
	public void doHide(Command command) {
		// TODO Auto-generated method stub
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
		
	}
	
	
	@Override
	public void doDone(Command command) {
		// TODO Auto-generated method stub
		//storage.getUndoStack().push(storage.copyLabelMap());
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
	}

}
