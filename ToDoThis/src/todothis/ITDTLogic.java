package todothis;

//Interface for Logic
public interface ITDTLogic {
	public String executeCommand(Command command);
	
	//Public????
	public String doADD(Command command);
	public String doDelete(Command command);
	public String doSearch(Command command);
	public String doSort(Command command);
	public String doEdit(Command command);
	public void doHide(Command command);
	public String doUndo(Command command);
	public void doDone(Command command);
	public void doShow(Command command);
}
