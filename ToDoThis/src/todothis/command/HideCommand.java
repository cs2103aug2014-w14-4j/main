package todothis.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class HideCommand extends Command {
	private String labelName;
	private ArrayList<String> prevHideList;
	
	public HideCommand(String labelName) {
		super(COMMANDTYPE.HIDE);
		this.setLabelName(labelName.toUpperCase());
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
				temp.setHide(true);
			}
		}else {
			iter = storage.getTaskIterator();
			while(iter.hasNext()){
				Task temp = iter.next();
				if(containInArray(temp.getLabelName(), labelNames)){
					temp.setHide(true);
				}
			}
		}
		
		return "";
	}*/
	
	@Override
	public String execute(TDTStorage storage) {
		prevHideList = storage.copyHideList();
		String[] labelNames = getLabelName().split(" ");
		Iterator<String> iter = storage.getLabelIterator();

		if(labelNames[0].equals("")){
			while(iter.hasNext()){
				String temp = iter.next();
				storage.insertToHideList(temp);
			}
		}else {
			while(iter.hasNext()){
				String temp = iter.next();
				if(containInArray(temp, labelNames)){
					storage.insertToHideList(temp);
				}
			}
		}
		
		storage.insertToUndoStack(this);
		return "Hide selected labels.";
	}
	
	@Override
	public String undo(TDTStorage storage) {
		storage.setHideList(prevHideList);
		return "Undo hide";
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
