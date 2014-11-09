//@author A0110398H
package todothis;

import java.awt.EventQueue;

import todothis.gui.TDTGUI;

public class TodoThis {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final TDTGUI frame = new TDTGUI();
					frame.getFeedbackArea().setText(frame.doInit());
					
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							frame.getScrollPane().getVerticalScrollBar().setValue(0);
						}
					});
					frame.sysTray();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
