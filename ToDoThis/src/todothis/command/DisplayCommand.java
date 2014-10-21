package todothis.command;

import java.util.Iterator;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class DisplayCommand extends Command {
	private String labelName;
	
	public DisplayCommand(String labelName) {
		super(COMMANDTYPE.DISPLAY);
		this.setLabelName(labelName);
	}
	/*
	@Override
	public String execute(TDTStorage storage) {
		String[] labelNames = getLabelName().split(" ");
		Iterator<Task> iter;

		if(labelNames[0].equals("")){
			iter = storage.getTaskIterator();
			while(iter.hasNext()){
				Task temp = iter.next();
				temp.setHide(false);
			}
		}else {
			iter = storage.getTaskIterator();
			while(iter.hasNext()){
				Task temp = iter.next();
				if(containInArray(temp.getLabelName(), labelNames)){
					temp.setHide(false);
				}else{
					temp.setHide(true);
				}
			}
		}
		
		return "";
	}*/
	
	public String execute(TDTStorage storage) {
		String[] labelNames = getLabelName().split(" ");
		Iterator<String> iter = storage.getLabelIterator();

		if(labelNames[0].equals("")){
			storage.getHideList().clear();
		}else {
			while(iter.hasNext()){
				String temp = iter.next();
				if(containInArray(temp, labelNames)){
					storage.getHideList().remove(temp);
				}else{
					storage.insertToHideList(temp);
				}
			}
		}
		
		return "";
	}

	private static boolean containInArray(String label, String[] labelNames) {
		for(int i = 0; i < labelNames.length; i++) {
			if(labelNames[i].toUpperCase().equals(label)) {
				return true;
			}
		}
		return false;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

}
