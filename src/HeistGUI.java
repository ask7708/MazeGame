import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * HesitGUI.java
 * 
 * $Id: HeistGUI.java,v 1.9 2014/12/09 19:29:07 ask7708 Exp $
 * 
 * $Log: HeistGUI.java,v $
 * Revision 1.9  2014/12/09 19:29:07  ask7708
 * final commit working GUI
 *
 * Revision 1.8  2014/12/09 19:28:40  ask7708
 * EMP - working only emps one tile
 *
 * Revision 1.7  2014/12/09 19:28:10  ask7708
 * Tested reset - working
 *
 * Revision 1.6  2014/12/09 19:11:47  ask7708
 * patterns updating - timer implemented
 *
 * Revision 1.5  2014/12/09 05:50:58  ask7708
 * update created for HeistGUI
 *
 * Revision 1.4  2014/12/07 05:40:19  ask7708
 * displaying images
 *
 * Revision 1.3  2014/12/06 19:16:30  ask7708
 * Creates enter/exit field at bottom left
 *
 * Revision 1.2  2014/12/06 19:16:00  ask7708
 * emp and reset buttons created - no listeners
 *
 * Revision 1.1  2014/12/06 19:14:52  ask7708
 * *** empty log message ***
 *
 * 
 */
/**
 * A GUI implementation of the Heist game. This game will be played using the
 * HeistModel class. To win the game the user will have to make his/her way to
 * the jewels and succcesfuly bring them back to the enter/exit spot. There will
 * be a reset to bring the game back to it's original state. An EMP button to
 * disable a cell.
 * 
 * 
 * @author Arshdeep Khalsa
 *
 */
public class HeistGUI extends JFrame implements Observer {

	/**
	 * The list of buttons for the JFrame
	 * 
	 */
	ArrayList<JButton> buttons = new ArrayList<JButton>();

	/**
	 * Button to reset the GUI
	 * 
	 */
	private JButton reset = new JButton("Reset");

	/**
	 * Button for EMP
	 * 
	 */
	private JButton emp = new JButton("EMP");

	/**
	 * Label for the move counter
	 * 
	 */
	private JLabel moveCount;

	/**
	 * Model from HeistModel
	 * 
	 */
	HeistModel theModel;

	/**
	 * Variable for the starting position of the thief
	 * 
	 */
	private int startingPos;

	/**
	 * Timer object used to update alarm patterns
	 * 
	 */
	private Timer timer;

	/**
	 * List of alarm pattern
	 * 
	 */
	List<Boolean> alarmList = new ArrayList<Boolean>();

	/**
	 * Creates a JFrame and the corresponding panels and container given the
	 * model. This will create three main panels. The bottom, for the
	 * enter/exit, emp, and reset. The center for the buttons. The top for the
	 * move counter. All three are added to container.
	 * 
	 * 
	 * @param heistModel
	 */
	public HeistGUI(HeistModel heistModel) {

		theModel = heistModel;
		theModel.addObserver(this);
		alarmList = theModel.getAlarms();

		timer = new Timer(theModel.getRefreshRate(), new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// ...Update the progress bar...

				theModel.updateAlarmPattern();

			}
		});

		timer.start();
		startingPos = theModel.getThiefLocation();
		JPanel bottomPanel = new JPanel();
		JPanel topPanel = new JPanel();
		JPanel centerPanel = new JPanel();
		JPanel bottomRightPanel = new JPanel();
		JPanel bottomLeftPanel = new JPanel();

		centerPanel.setLayout(new GridLayout(heistModel.getDim(), heistModel
				.getDim()));
		for (int i = 0; i < heistModel.getDim() * heistModel.getDim(); i++) {
			buttons.add(new JButton());
			if (alarmList.get(i) == false) {
				buttons.get(i).setBackground(Color.WHITE);
			} else {
				buttons.get(i).setBackground(Color.BLUE);
			}
			centerPanel.add(buttons.get(i));

		}
		buttons.get(heistModel.getThiefLocation()).setIcon(
				new ImageIcon(HeistGUI.class.getResource("Thief.jpg")));
		buttons.get(heistModel.getJewelsLocation()).setIcon(
				new ImageIcon(HeistGUI.class.getResource("Jewels.jpg")));

		// Creates top panel for the GUI
		topPanel.setLayout(new BorderLayout());

		topPanel.add(createMoveCounter(heistModel.getMoveCount()),
				BorderLayout.WEST);
		// topPanel.setBackground(Color.GRAY);

		// Creates buttons and text box for bottom of frame
		bottomPanel.setLayout(new BorderLayout());

		// Adds the bottom right panels
		bottomRightPanel.add(emp);
		bottomRightPanel.add(reset);

		// Adds the listeners using functions listed below
		this.addResetListener(new resetListener());
		this.addEmpListener(new empListener());
		this.addButtonListener(new buttonListener());

		bottomRightPanel.setBackground(Color.GRAY);

		bottomLeftPanel.add(createEnterExit());
		bottomLeftPanel.setBackground(Color.GRAY);

		// Adds left and right panels to one whole bottom panel
		bottomPanel.add(bottomLeftPanel, BorderLayout.WEST);
		bottomPanel.add(bottomRightPanel, BorderLayout.EAST);
		bottomPanel.setBackground(Color.GRAY);

		Container heistContainer = this.getContentPane();

		// Adds all panels to the container
		heistContainer.add(centerPanel, BorderLayout.CENTER);
		heistContainer.add(topPanel, BorderLayout.NORTH);
		heistContainer.add(bottomPanel, BorderLayout.SOUTH);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 400);
		this.setTitle("Heist Game");

	}

	/**
	 * Creates a panel for the top of the GUI for the counter of moves
	 * 
	 * @param moves
	 * @return JPanel
	 */
	private Component createMoveCounter(int moves) {

		JPanel movePanel = new JPanel();
		moveCount = new JLabel("Moves: 0");
		movePanel.add(moveCount);

		return movePanel;

	}

	/**
	 * Creates textbox for enter/exit field
	 * 
	 * @return JTextField
	 */
	private Component createEnterExit() {

		JTextField enterExitText = new JTextField();
		enterExitText.setText("ENTER/EXIT");
		enterExitText.setBackground(Color.WHITE);
		enterExitText.setEditable(false);

		return enterExitText;
	}

	/**
	 * Adds the action listener for the buttons
	 * 
	 * @param buttonListener
	 */
	void addButtonListener(ActionListener buttonListener) {

		for (JButton b : buttons) {

			b.addActionListener(buttonListener);
		}
	}

	/**
	 * Adds the action listener for the reset listener
	 * 
	 * @param resetListener
	 */
	void addResetListener(ActionListener resetListener) {

		reset.addActionListener(resetListener);
	}

	/**
	 * Adds the "EMP" listener for the bottom of the JFrame
	 * 
	 * @param empListener
	 */
	void addEmpListener(ActionListener empListener) {

		emp.addActionListener(empListener);
	}

	/**
	 * Actions performed for the reset button such as bringing thief back to
	 * starting and jewels back to original location.
	 * 
	 * @author Arshdeep Khalsa
	 *
	 */
	class resetListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {

			buttons.get(theModel.getThiefLocation()).setIcon(null);
			buttons.get(startingPos).setIcon(
					new ImageIcon(HeistGUI.class.getResource("Thief.jpg")));
			buttons.get(theModel.getJewelsLocation()).setIcon(
					new ImageIcon(HeistGUI.class.getResource("Jewels.jpg")));
			theModel.reset();

		}

	}

	/**
	 * Actions performed for the EMP button. Uses a model to disable an alarm
	 * that the user is currently on.
	 * 
	 * 
	 * @author Arshdeep Khalsa
	 *
	 */
	class empListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {

			theModel.disableAlarm();

		}

	}

	/**
	 * Performs actions for the buttons which deal with choosing different cells
	 * on the board.
	 * 
	 * @author Arshdeep Khalsa
	 *
	 */
	class buttonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			int oldPos = theModel.getThiefLocation();

			for (int i = 0; i < buttons.size(); i++) {

				if (e.getSource() == buttons.get(i)) {

					theModel.selectCell(i);

					if (theModel.getThiefLocation() == i) {
						buttons.get(oldPos).setIcon(null);

						if (theModel.getAreJewelsStolen() == true) {
							buttons.get(i).setIcon(
									new ImageIcon(HeistGUI.class
											.getResource("Escape.jpg")));
						} else {
							buttons.get(i).setIcon(
									new ImageIcon(HeistGUI.class
											.getResource("Thief.jpg")));
						}
					}
				}
			}

		}

	}

	/**
	 * Main program for the heist, file name is taken as param.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 1) {
			String us = "Usage: java Heist <config-file>";
			System.out.println(us);
			return;
		}

		HeistGUI theGUI = new HeistGUI(new HeistModel(args[0]));

		theGUI.setVisible(true);

	}

	/**
	 * Updates with actions that are performed. This method will update alarm
	 * patterns, and the move counter.
	 * 
	 */
	public void update(Observable arg0, Object arg1) {

		alarmList = theModel.getAlarms();

		for (int i = 0; i < buttons.size(); i++) {

			if (alarmList.get(i) == false) {
				buttons.get(i).setBackground(Color.WHITE);
			} else {
				buttons.get(i).setBackground(Color.BLUE);
			}

		}

		if (theModel.getGameStatus() == 1) {
			this.moveCount.setText("Move: " + theModel.getMoveCount());
		} else if (theModel.getGameStatus() == 0) {
			this.moveCount.setText("Move: " + theModel.getMoveCount()
					+ " You lose");
		} else {
			this.moveCount.setText("Move: " + theModel.getMoveCount()
					+ " You win!");
		}

	}

}
