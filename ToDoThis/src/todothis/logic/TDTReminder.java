package todothis.logic;

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;


public class TDTReminder {
	Timer timer;

	public TDTReminder(long sec, Task task) {
		timer = new Timer();
		timer.schedule(new TDTReminderTask(task), sec * 1000);
	}
	
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
			JOptionPane.showMessageDialog(null, "REMINDER!\nTask: " +getTask().getDetails());   
			timer.cancel(); 
		}
		public Task getTask() {
			return task;
		}
		public void setTask(Task task) {
			this.task = task;
		}
	}


}
