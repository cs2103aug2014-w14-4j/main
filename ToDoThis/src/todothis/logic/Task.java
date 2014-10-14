package todothis.logic;

public class Task implements Comparable<Task> {
	private int taskID;
	private String labelName;
	private TDTDateAndTime dateAndTime;
	private String details;
	private boolean isHighPriority;
	private boolean hide;
	private boolean isDone;
	
//---------------------Task constructor---------------------------------------
	public Task(int taskID, String labelName, String details, TDTDateAndTime dateAndTime, boolean p) {
		this.setDetails(details);
		this.setTaskID(taskID);
		this.setLabelName(labelName);
		this.setHide(false);
		this.setDone(false);
		this.setHighPriority(p);
		this.setDateAndTime(dateAndTime);
	}

	
	public Task(int taskID, String labelName, String details,
			TDTDateAndTime dateAndTime, boolean p,
			boolean done, boolean hide) {
		this.setDetails(details);
		this.setTaskID(taskID);
		this.setLabelName(labelName);
		this.setHide(hide);
		this.setDone(done);
		this.setHighPriority(p);
		this.setDateAndTime(dateAndTime);
	}
	
	//-------------------------------------------------------------------------------




	@Override
	public int compareTo(Task task) {
		if (this.isHighPriority && !task.isHighPriority) {
			return -1;
		} else if(!this.isHighPriority && task.isHighPriority) {
			return 1;
		}
		else {
			if (this.getDateAndTime().compareTo(task.getDateAndTime()) == 1) {
				return 1;
			}
			else if (this.getDateAndTime().compareTo(task.getDateAndTime()) == -1) {
				return -1;
			}
			else {
				return 0;
			}
		}
	}
	
	//------------------Getters & Setters--------------------------------
	
	public int getTaskID() {
		return taskID;
	}
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public boolean isHighPriority() {
		return isHighPriority;
	}
	public void setHighPriority(boolean isHighPriority) {
		this.isHighPriority = isHighPriority;
	}
	public boolean isHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	public boolean isDone() {
		return isDone;
	}
	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}


	public TDTDateAndTime getDateAndTime() {
		return dateAndTime;
	}


	public void setDateAndTime(TDTDateAndTime dateAndTime) {
		this.dateAndTime = dateAndTime;
	}
}