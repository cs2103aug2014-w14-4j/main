//@author A0110398H
package todothis.commons;

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

/**
 * TDTReminder class
 *
 */
public class TDTReminder {
	Timer timer;

	/**
	 * Construct a TDTReminder object
	 * 
	 * @param sec
	 *            Remaining time left to reminder timing in seconds.
	 * @param task
	 *            The task to be reminded
	 */
	public TDTReminder(long sec, Task task) {
		timer = new Timer();
		try {
			timer.schedule(new TDTReminderTask(task), sec * 1000);
		} catch (IllegalArgumentException e) {
			// Undoing a already past reminder
			task.setRemindDateTime("null");
			task.setReminder(null);
		}
	}

	/**
	 * Cancel the reminder.
	 */
	public void cancelReminder() {
		timer.cancel();
	}

	private class TDTReminderTask extends TimerTask {
		private Task task;

		public TDTReminderTask(Task task) {
			this.setTask(task);
		}

		public void run() {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "REMINDER!\nTask: "
					+ getTask().getDetails());
			timer.cancel();
			getTask().setRemindDateTime("null");
			getTask().setReminder(null);
		}

		public Task getTask() {
			return task;
		}

		public void setTask(Task task) {
			this.task = task;
		}
	}

}
