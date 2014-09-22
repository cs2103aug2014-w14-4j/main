package todothis;

public class Task implements Comparable<Task> {
	private int taskID;
	private String labelName;
	private String dueDate;
	private String dueTime;
	private String details;
	private boolean isHighPriority;
	private boolean hide;
	private boolean isDone;
	
//---------------------Task constructor---------------------------------------
	public Task(int taskID, String labelName, String details, String dueDate,
			String dueTime, boolean p) {
		this.setDetails(details);
		this.setTaskID(taskID);
		this.setLabelName(labelName);
		this.setDueDate(dueDate);
		this.setDueTime(dueTime);
		this.setHide(false);
		this.setDone(false);
		this.setHighPriority(p);
	}

	
	public Task(int taskID, String labelName, String details,
			String dueDate, String dueTime, boolean p,
			boolean done, boolean hide) {
		this.setDetails(details);
		this.setTaskID(taskID);
		this.setLabelName(labelName);
		this.setDueDate(dueDate);
		this.setDueTime(dueTime);
		this.setHide(hide);
		this.setDone(done);
		this.setHighPriority(p);
	}
	
	//-------------------------------------------------------------------------------




	@Override
	public int compareTo(Task arg0) {
		// TODO Auto-generated method stub
		return 0;
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
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getDueTime() {
		return dueTime;
	}
	public void setDueTime(String dueTime) {
		this.dueTime = dueTime;
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
}
