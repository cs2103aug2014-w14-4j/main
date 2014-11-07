package todothis.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import todothis.dateandtime.TDTDateAndTime;
import todothis.dateandtime.TDTDateMethods;
import todothis.dateandtime.TDTTimeMethods;
import todothis.logic.TDTReminder;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;
import todothis.storage.TDTStorage;

public class RemindCommand extends Command {
	private String labelName;
	private int taskID;
	private String commandDetails;
	private Task task;
	private boolean isRemoveReminder = false;
	private String prevReminder;
	
	private static Calendar cal;
	
	public RemindCommand(String labelName, int taskId, String commandDetails) {
		super(COMMANDTYPE.REMIND);
		this.setLabelName(labelName.toUpperCase());
		this.setTaskID(taskId);
		this.setCommandDetails(commandDetails);
	}

	@Override
	public String execute(TDTStorage storage) {
		if(labelName.equalsIgnoreCase("")) {
			setLabelName(storage.getCurrLabel());
		}
		
		if(getCommandDetails().equals("")) {
			isRemoveReminder = true;
			return removeReminder(storage);
		}
		if(storage.getLabelMap().containsKey(getLabelName())) {
			ArrayList<Task> array = storage.getLabelMap().get(getLabelName());
			if(getTaskID() > 0 && getTaskID() <= array.size()) {
				Task temp = array.get(getTaskID() - 1);
				String remindDateTime = decodeReminderDetails(getCommandDetails());
				if(!remindDateTime.equals("null")) {
					temp.setRemindDateTime(remindDateTime);
					temp.setReminder(new TDTReminder(TDTTimeMethods.calculateRemainingTime(remindDateTime), temp));
					setTask(temp);
					storage.insertToUndoStack(this);
					
					return "Reminder set at " + remindDateTime;
				} else {
					return "Invalid command. Invalid date/time for reminder.";
				}
			} else {
				return "Invalid command. Invalid taskId.";
			}
		} else {
			return "Invalid command.Invalid label name.";
		}
	}

	private String removeReminder(TDTStorage storage) {
		if(storage.getLabelMap().containsKey(getLabelName())) {
			ArrayList<Task> array = storage.getLabelMap().get(getLabelName());
			if(getTaskID() > 0 && getTaskID() <= array.size()) {
				Task temp = array.get(getTaskID() - 1);
				if(temp.getReminder() == null) {
					return "Invalid command. No reminder to remove.";
				}
				String dateTime = temp.getRemindDateTime();
				prevReminder = dateTime;
				setTask(temp);
				temp.getReminder().cancelReminder();
				temp.setReminder(null);
				temp.setRemindDateTime("null");
				storage.insertToUndoStack(this);
				return "Reminder at " + dateTime +  " removed.";
			} else {
				return "Invalid command. Invalid taskId.";
			}
		} else {
			return "Invalid command.Invalid label name.";
		}
	}

	@Override
	public String undo(TDTStorage storage) {
		if(!isRemoveReminder) {
			Task temp = getTask();
			if(temp.getReminder() != null) {
				temp.getReminder().cancelReminder();
				temp.setReminder(null);
				temp.setRemindDateTime("null");
			}
			return "Undo reminder.";
		} else {
			Task temp = getTask();
			temp.setRemindDateTime(prevReminder);
			temp.setReminder(new TDTReminder(TDTTimeMethods.calculateRemainingTime(prevReminder), temp));
			return "Undo remove reminder.";
		}
	}
	
	// ---------------CALCULATE REMAINING TIME FOR REMINDER--------------------

	private static String decodeReminderDetails(String reminderString) {
		String[] reminderParts = reminderString.toLowerCase().split(" ");
		int thisOrNextOrFollowing = 0; // this = 1 next = 2 following = 3
		String decodedReminderDate = "null";
		String decodedReminderTime = "null";
		int nextCount = 0;
		int followingCount = 0;

		cal = Calendar.getInstance(TimeZone.getDefault());
		int currentDay = cal.get(Calendar.DATE);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR);
		int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

		int currentHour = cal.get(Calendar.HOUR_OF_DAY);
		int currentMinute = cal.get(Calendar.MINUTE);

		String currentTime = currentHour + ":" + currentMinute;
		String currentDate = currentDay + "/" + currentMonth + "/"
				+ currentYear;

		for (int i = 0; i < reminderParts.length; i++) {
			reminderParts[i] = TDTDateAndTime.replaceEndStringPunctuation(reminderParts[i]);

			if (reminderParts[i].equals("this")) {
				thisOrNextOrFollowing = 1;
			} else if (reminderParts[i].equals("next")) {
				nextCount++;
				thisOrNextOrFollowing = 2;
			} else if (reminderParts[i].equals("following")) {
				followingCount++;
				thisOrNextOrFollowing = 3;
			}

			if (TDTDateMethods.checkDate(reminderParts[i])) {
				decodedReminderDate = TDTDateMethods.decodeDate(reminderParts, i, currentYear,
						currentMonth);
			} else if (TDTDateMethods.checkDay(reminderParts[i]) != 0) {
				int numOfDaysToAdd = TDTDateMethods.determineDaysToBeAdded(
						thisOrNextOrFollowing, reminderParts, i,
						currentDayOfWeek, nextCount, followingCount);
				decodedReminderDate = TDTDateMethods.addDaysToCurrentDate(currentDay,
						currentMonth, currentYear, numOfDaysToAdd);
			} else if (TDTDateMethods.checkMonth(reminderParts[i]) != 0) {
				boolean isValidDayYear = true;
				if (i != 0 && i != reminderParts.length - 1) {
					String before = reminderParts[i - 1];
					int month = TDTDateMethods.checkMonth(reminderParts[i]);
					String after = reminderParts[i + 1];
					try {
						Integer.parseInt(before);
						Integer.parseInt(after);
					} catch (NumberFormatException e) {
						isValidDayYear = false;
					}
					if (isValidDayYear) {
						int day = Integer.parseInt(before);
						int year = 0;

						if (after.length() == 2) {
							after = "20" + after;
						}
						year = Integer.parseInt(after);

						decodedReminderDate = day + "/" + month + "/" + year;
					} else {
						decodedReminderDate = before + "/" + month + "/"
								+ after;
					}
				}
			} else if (TDTTimeMethods.checkTime(reminderParts[i])) {
				decodedReminderTime = TDTTimeMethods.decodeTime(reminderParts, i);
			}
		}

		if (decodedReminderTime.equals("null")) {
			return "null";
		} else if (decodedReminderDate.equals("null")) { // today
			if (TDTTimeMethods.compareToTime(currentTime, decodedReminderTime) == 1) {
				return currentDate + " " + decodedReminderTime;
			}
		} else {
			if (TDTDateMethods.isValidDateRange(decodedReminderDate)) {
				if (TDTDateMethods.compareToDate(currentDate, decodedReminderDate) == 0) {
					if (TDTTimeMethods.compareToTime(currentTime, decodedReminderTime) == 1) {
						return decodedReminderDate + " " + decodedReminderTime;
					}
				} else if (TDTDateMethods.compareToDate(currentDate, decodedReminderDate) == 1) {
					return decodedReminderDate + " " + decodedReminderTime;
				}
			}
		}
		return "null";
	}
	
	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public String getCommandDetails() {
		return commandDetails;
	}

	public void setCommandDetails(String commandDetails) {
		this.commandDetails = commandDetails;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
