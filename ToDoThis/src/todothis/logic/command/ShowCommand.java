//@author A0110398H
package todothis.logic.command;

import java.util.ArrayList;
import java.util.Iterator;

import todothis.logic.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTDataStore;

public class ShowCommand extends Command {
	public static final String MESSAGE_UNDO_SHOW = "Undo show";
	public static final String MESSAGE_SHOW_FEEDBACK = "Show selected labels";
	
	private String labelName;
	private ArrayList<String> prevHideList;
	
	/**
	 * Construct a ShowCommand object.
	 * @param labelName
	 */
	public ShowCommand(String labelName) {
		super(COMMANDTYPE.SHOW);
		this.setLabelName(labelName.toUpperCase());
	}
	
	
	/**
	 * Show all the given labels and hide the rest.
	 */
	public String execute(TDTDataStore data) {
		prevHideList = copyHideList(data.getHideList());
		String[] labelNames = getLabelName().split(" ");
		Iterator<String> iter = data.getLabelIterator();

		if(labelNames[0].equals("")){
			data.getHideList().clear();
		}else {
			while(iter.hasNext()){
				String temp = iter.next();
				if(containInArray(temp, labelNames)){
					data.getHideList().remove(temp);
				}else{
					data.insertToHideList(temp);
				}
			}
		}
		
		data.insertToUndoStack(this);
		return MESSAGE_SHOW_FEEDBACK;
	}
	
	/**
	 * Reverses the effect of execute.
	 */
	@Override
	public String undo(TDTDataStore data) {
		data.setHideList(prevHideList);
		return MESSAGE_UNDO_SHOW;
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
	//@author A0110398H-unused
	/*
	Unused code. Previously able to show/hide specific task. Change to show/hide labels only.
	@Override
	public String execute(TDTDataStore data) {
		String[] labelNames = getLabelName().split(" ");
		Iterator<Task> iter;

		if(labelNames[0].equals("")){
			iter = data.getTaskIterator();
			while(iter.hasNext()){
				Task temp = iter.next();
				temp.setHide(false);
			}
		}else {
			iter = data.getTaskIterator();
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

}
