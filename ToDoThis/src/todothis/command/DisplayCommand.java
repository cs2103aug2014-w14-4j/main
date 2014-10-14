package todothis.command;

import java.util.Iterator;

import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class DisplayCommand extends Command {
	private String labelName;
	
	public DisplayCommand(String labelName) {
		super(COMMANDTYPE.DISPLAY);
		this.setLabelName(labelName);
	}

	@Override
	public String execute(TDTStorage storage) {
		String labelName = getLabelName().toUpperCase();
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
			return "Display command invalid!";
		}
		return "";
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

}
