package todothis.command;

import java.util.ArrayList;

import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class LabelCommand extends Command {
	private String labelName;
	
	public LabelCommand(String labelName) {
		super(COMMANDTYPE.LABEL);
		this.setLabelName(labelName);
	}

	@Override
	public String execute(TDTStorage storage) {
		String[] label = getLabelName().toUpperCase().split(" ");
		
		if(label.length > 1 || label.length <= 0) {
			return "Error. Invalid Label name.";
		}
		
		if(storage.getLabelMap().containsKey(label[0])) {
			storage.setCurrLabel(label[0]);
			return "Current label changed.";
		} else if(label[0].equals("") || label[0].matches("\\d+")) {
			return "Error. Label name cannot be blank or digits only.";
		} else {
			storage.getLabelMap().put(label[0], new ArrayList<Task>());
			storage.setCurrLabel(label[0]);
			storage.write();
			return "Label created";
		}
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

}
