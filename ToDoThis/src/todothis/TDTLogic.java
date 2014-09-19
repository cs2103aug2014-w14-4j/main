package todothis;


public class TDTLogic implements ITDTLogic {

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
		String labelName = command.getLabelName();
		int labelId = 1;
		
		if(TodoThis.labelMap.containsKey(labelName)) {
			TodoThis.labelMap.put(labelName, TodoThis.labelMap.get(labelName) + 1);
			labelId = TodoThis.labelMap.get(labelName);
		} else {
			TodoThis.labelMap.put(labelName, labelId);
		}
		
		Task task = new Task(labelId, labelName, command.getCommandDetails(),
				command.getDueDate(), command.getDueTime(), command.isHighPriority());
		TodoThis.allTask.add(task);
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
		return null;
	}

	@Override
	public String doHide(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doUndo(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doDone(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

}
