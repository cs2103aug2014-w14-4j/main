package todothis.logic.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class HideCommand extends Command {
	private static final String MESSAGE_UNDO_HIDE = "Undo hide";
	private static final String MESSAGE_HIDE_FEEDBACK = "Hide selected labels.";
	private String labelName;
	private ArrayList<String> prevHideList;
	
	public HideCommand(String labelName) {
		super(COMMANDTYPE.HIDE);
		this.setLabelName(labelName.toUpperCase());
	}
	/*
	Unused code. Previously able to hide specific task. Changed to able to hide labels only.
	@Override
	public String execute(TDTDataStore data) {
		String[] labelNames = getLabelName().split(" ");
		Iterator<Task> iter;

		if(labelNames[0].equals("")){
			iter = data.getTaskIterator();
			while(iter.hasNext()){
				Task temp = iter.next();
				temp.setHide(true);
			}
		}else {
			iter = data.getTaskIterator();
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
	public String execute(TDTDataStore data) {
		prevHideList = copyHideList(data.getHideList());
		String[] labelNames = getLabelName().split(" ");
		Iterator<String> iter = data.getLabelIterator();

		if(labelNames[0].equals("")){
			while(iter.hasNext()){
				String temp = iter.next();
				data.insertToHideList(temp);
			}
		}else {
			while(iter.hasNext()){
				String temp = iter.next();
				if(containInArray(temp, labelNames)){
					data.insertToHideList(temp);
				}
			}
		}
		
		data.insertToUndoStack(this);
		return MESSAGE_HIDE_FEEDBACK;
	}
	
	@Override
	public String undo(TDTDataStore data) {
		data.setHideList(prevHideList);
		return MESSAGE_UNDO_HIDE;
	}
	
	private ArrayList<String> copyHideList(ArrayList<String> hideList) {
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0 ; i < hideList.size(); i++) {
			list.add(hideList.get(i));
		}
		return list;
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
