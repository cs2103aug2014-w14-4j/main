package todothis;


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
		return null;
	}


	@Override
	public String doUndo(Command command) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public void doShow(Command command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doHide(Command command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doDone(Command command) {
		// TODO Auto-generated method stub
		
	}

}
