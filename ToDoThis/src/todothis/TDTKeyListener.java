package todothis;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import todothis.command.AddCommand;
import todothis.command.Command;
import todothis.command.RedoCommand;
import todothis.command.SearchCommand;
import todothis.command.UndoCommand;
import todothis.logic.TDTDateAndTime;
import todothis.logic.Task;
import todothis.parser.ITDTParser.COMMANDTYPE;

public class TDTKeyListener implements KeyListener {
	private TDTGUI gui;

	public TDTKeyListener(TDTGUI gui) {
		this.gui = gui;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
		if (arg0.isControlDown() && keyCode == KeyEvent.VK_Z) {
			UndoCommand undo = new UndoCommand();
			String feedback = gui.getLogic().executeCommand(undo);
			gui.updateGUI(feedback, gui.displayTask(0));
			scrollTo(0);
		}

		if (arg0.isControlDown() && keyCode == KeyEvent.VK_Y) {
			RedoCommand redo = new RedoCommand();
			String feedback = gui.getLogic().executeCommand(redo);
			gui.updateGUI(feedback, gui.displayTask(0));
			scrollTo(0);
		}

		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			gui.setUserCommand(gui.commandField.getText());
			gui.getCommandHistory().add(gui.getUserCommand());
			gui.setHistoryPointer(gui.getCommandHistory().size());
			gui.commandField.setText("");

			Command command = gui.getParser().parse(gui.getUserCommand());
			
			if (command.getCommandType() != COMMANDTYPE.SEARCH) {
				String feedback = gui.getLogic().executeCommand(command);
				if(command.getCommandType() == COMMANDTYPE.ADD) {
					int id = ((AddCommand)command).getTaskID();
					gui.updateGUI(feedback, gui.displayTask(id));
					scrollTo(id*30);
				}else {
					gui.updateGUI(feedback, gui.displayTask(0));
					scrollTo(0);
				}
			} else {
				String feedback = gui.getLogic().executeCommand(command);
				ArrayList<Task> searched = ((SearchCommand) command)
						.getSearchedResult();
				gui.updateGUI(feedback, gui.displaySearch(searched));
				scrollTo(0);
			}

			break;

		case KeyEvent.VK_UP:
			gui.setHistoryPointer(gui.getHistoryPointer() - 1);
			if (gui.getHistoryPointer() < 0) {
				gui.commandField.setText("");
				gui.setHistoryPointer(-1);
			} else {
				gui.commandField.setText(gui.getCommandHistory().get(
						gui.getHistoryPointer()));
			}
			break;
		case KeyEvent.VK_DOWN:
			gui.setHistoryPointer(gui.getHistoryPointer() + 1);
			if (gui.getHistoryPointer() >= gui.getCommandHistory().size()) {
				gui.commandField.setText("");
				gui.setHistoryPointer(gui.getCommandHistory().size());
			} else {
				gui.commandField.setText(gui.getCommandHistory().get(
						gui.getHistoryPointer()));
			}
			break;
		case KeyEvent.VK_SPACE:
			gui.setUserCommand(gui.commandField.getText());
			String[] words = gui.getUserCommand().split(" ");
			if (words.length == 2) {
				if (words[0].equalsIgnoreCase("edit")) {
					try {
						int id = Integer.parseInt(words[1]);
						int size = gui.getLogic().getLabelSize(gui.getLogic().getCurrLabel());
						if (id > 0 && id <= size) {
							Task task = gui.getLogic().getTask(gui.getLogic().getCurrLabel(), id);
							TDTDateAndTime dat = task.getDateAndTime();
							String datString = gui.getDateTimeStringForEdit(dat);
							gui.commandField.setText(gui.getUserCommand() + " "
											+ task.getDetails() + datString);
							
							javax.swing.SwingUtilities
									.invokeLater(new Runnable() {
										public void run() {
											gui.commandField.select(
													gui.getUserCommand()
															.length() + 1,
													gui.commandField.getText()
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
						if (gui.getLogic().isInLabelMap(words[1].toUpperCase())) {
							int size = gui.getLogic().getLabelSize(words[1]);
							if (id > 0 && id <= size) {
								Task task = gui.getLogic().getTask(words[1], id);
								TDTDateAndTime dat = task.getDateAndTime();
								String datString = gui
										.getDateTimeStringForEdit(dat);
								gui.commandField.setText(gui.getUserCommand()
										+ " " + task.getDetails() + datString);
								javax.swing.SwingUtilities
										.invokeLater(new Runnable() {
											public void run() {
												gui.commandField.select(gui
														.getUserCommand()
														.length() + 1,
														gui.commandField
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
			gui.setBounds(300, 0, 500, 110);
			break;
		case KeyEvent.VK_F2:
			gui.setBounds(300, 100, 800, 600);
			break;
		default:
			break;
		}
	}

	private void scrollTo(final int value) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.scrollPane.getVerticalScrollBar().setValue(value);
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
