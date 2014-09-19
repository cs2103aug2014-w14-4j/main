package todothis;

public interface ITDTLogic {
	public String executeCommand(Command command);
	public String doADD(Command command);
	public String doDelete(Command command);
	public String doSearch(Command command);
	public String doSort(Command command);
	public String doEdit(Command command);
	public String doHide(Command command);
	public String doUndo(Command command);
	public String doDone(Command command);
}
