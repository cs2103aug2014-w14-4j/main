package todothis;

import java.util.ArrayList;

//Interface for Logic
public interface ITDTLogic {
	public String executeCommand(Command command);
	
	//Public????
	public String doADD(Command command);
	public String doDelete(Command command);
	public ArrayList<Task> doSearch(Command command);
	public String doSort(Command command);
	public String doEdit(Command command);
	public String doHide(Command command);
	public String doUndo(Command command);
	public String doDone(Command command);
	public String doDisplay(Command command);
	public String doLabel(Command command);
}
