package todothis.command;

import java.util.Iterator;

import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class HideCommand extends Command {
	private String labelName;
	
	public HideCommand(String labelName) {
		super(COMMANDTYPE.HIDE);
		this.setLabelName(labelName);
	}

	@Override
	public String execute(TDTStorage storage) {
		Iterator <Task> i;
		String labelName = getLabelName().toUpperCase();

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
				return "Label name cannot be found!";
			}
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
