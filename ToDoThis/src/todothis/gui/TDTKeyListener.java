package todothis.gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;

import todothis.commons.TDTDateAndTime;
import todothis.commons.Task;
import todothis.logic.TDTController;

public class TDTKeyListener implements KeyListener {
	private static final int _SCROLLFACTOR = 30;
	private TDTGUI gui;

	public TDTKeyListener(TDTGUI gui) {
		this.gui = gui;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		int scrollVal = gui.getScrollPane().getVerticalScrollBar().getValue();
		
		if (arg0.isShiftDown() && keyCode == KeyEvent.VK_UP) {
			scrollTo(scrollVal - _SCROLLFACTOR);
		}
		
		if (arg0.isShiftDown() && keyCode == KeyEvent.VK_DOWN) {
			scrollTo(scrollVal + _SCROLLFACTOR);
		}
		
		if (arg0.isControlDown() && keyCode == KeyEvent.VK_Z) {
			String feedback = gui.getControl().executeCommand("undo");
			updateView(scrollVal, feedback);
		}
		
		if (arg0.isControlDown() && keyCode == KeyEvent.VK_R) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(null, "REMINDER!\nTask: Reminder POPUP!.");
		}
		
		if (arg0.isAltDown()  && keyCode == KeyEvent.VK_S) {
			String feedback = gui.getControl().executeCommand("show");
			gui.updateGUI(feedback, gui.displayTask(null, null));
			scrollTo(0);
		}
		
		if (arg0.isAltDown()  && keyCode == KeyEvent.VK_E) {
			gui.getControl().executeCommand("exit");
		}
		
		if (arg0.isAltDown() && keyCode == KeyEvent.VK_H) {
			String feedback = gui.getControl().executeCommand("hide");
			gui.updateGUI(feedback, gui.displayTask(null, null));
			scrollTo(0);
		}

		if (arg0.isControlDown() && keyCode == KeyEvent.VK_Y) {
			String feedback = gui.getControl().executeCommand("redo");
			updateView(scrollVal, feedback);
		}

		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			gui.setUserCommand(gui.getCommandField().getText());
			gui.getCommandHistory().add(gui.getUserCommand());
			gui.setHistoryPointer(gui.getCommandHistory().size());
			gui.getCommandField().setText("");
			
			String feedback = gui.getControl().executeCommand(gui.getUserCommand());
			updateView(scrollVal, feedback);
			break;

		case KeyEvent.VK_UP:
			if(gui.getCommandField().hasFocus()) {
				gui.setHistoryPointer(gui.getHistoryPointer() - 1);
				if (gui.getHistoryPointer() < 0) {
					gui.getCommandField().setText("");
					gui.setHistoryPointer(-1);
				} else {
					gui.getCommandField().setText(gui.getCommandHistory().get(
							gui.getHistoryPointer()));
				}
			}
			break;
		case KeyEvent.VK_DOWN:
			if(gui.getCommandField().hasFocus()) {
				gui.setHistoryPointer(gui.getHistoryPointer() + 1);
				if (gui.getHistoryPointer() >= gui.getCommandHistory().size()) {
					gui.getCommandField().setText("");
					gui.setHistoryPointer(gui.getCommandHistory().size());
				} else {
					gui.getCommandField().setText(gui.getCommandHistory().get(
							gui.getHistoryPointer()));
				}
			}
			break;
		case KeyEvent.VK_SPACE:
			gui.setUserCommand(gui.getCommandField().getText());
			String[] words = gui.getUserCommand().split(" ");
			if (words.length == 2) {
				if (words[0].equalsIgnoreCase("edit")) {
					try {
						int id = Integer.parseInt(words[1]);
						int size = gui.getControl().getLabelSize(gui.getControl().getCurrLabel());
						if (id > 0 && id <= size) {
							Task task = gui.getControl().getTask(gui.getControl().getCurrLabel(), id);
							TDTDateAndTime dat = task.getDateAndTime();
							String datString = gui.getDateTimeStringForEdit(dat);
							gui.getCommandField().setText(gui.getUserCommand() 
									+ task.getDetails() + datString);
							
							javax.swing.SwingUtilities
									.invokeLater(new Runnable() {
										public void run() {
											gui.getCommandField().select(
													gui.getUserCommand()
															.length() + 1,
													gui.getCommandField().getText()
															.length() - 1);
										}
									});
						}
					} catch (NumberFormatException e) {
						;
					}
				}
			}
			if (words.length == 3) {
				if (words[0].equalsIgnoreCase("edit")) {
					try {
						int id = Integer.parseInt(words[2]);
						if (gui.getControl().isInLabelMap(words[1].toUpperCase())) {
							int size = gui.getControl().getLabelSize(words[1]);
							if (id > 0 && id <= size) {
								Task task = gui.getControl().getTask(words[1], id);
								TDTDateAndTime dat = task.getDateAndTime();
								String datString = gui
										.getDateTimeStringForEdit(dat);
								gui.getCommandField().setText(gui.getUserCommand()
										+ task.getDetails() + datString);
								javax.swing.SwingUtilities
										.invokeLater(new Runnable() {
											public void run() {
												gui.getCommandField().select(gui
														.getUserCommand()
														.length() + 1,
														gui.getCommandField()
																.getText()
																.length() - 1);
											}
										});
							}
						}
					} catch (NumberFormatException e) {
						;
					}
				}
			}
			break;
		case KeyEvent.VK_F1:
			gui.setBounds(300, 0, 500, 125);
			break;
		case KeyEvent.VK_F2:
			gui.setBounds(300, 100, 800, 600);
			break;
		default:
			break;
		}
	}

	private void updateView(int scrollVal, String feedback) {
		int value = gui.getControl().getScrollVal();
		if(value != -1) {
			scrollVal = value;
		}
		if(gui.getControl().getViewMode() == TDTController.SEARCH_VIEW) {
			gui.updateGUI(feedback, gui.displaySearch(gui.getControl().getSearchedTask()));
			scrollTo(0);
		} else if(gui.getControl().getViewMode() == TDTController.HELP_VIEW) {
			gui.updateGUI("Displaying Help text.", feedback);
			scrollTo(0);
		} else {
			gui.updateGUI(feedback, gui.displayTask(gui.getControl().getHighlightTask(),
					gui.getControl().getAddedTask()));
			scrollTo(scrollVal * _SCROLLFACTOR);
		}
	}

	private void scrollTo(final int value) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.getScrollPane().getVerticalScrollBar().setValue(value);
			}
		});
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
