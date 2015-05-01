import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * HeistController.java
 * 
 * $Id: HeistController.java,v 1.1 2014/12/06 19:53:43 ask7708 Exp $
 * 
 * $Log: HeistController.java,v $
 * Revision 1.1  2014/12/06 19:53:43  ask7708
 * *** empty log message ***
 *
 * 
 * 
 */
/**
 * Controls actions between the model and the GUI
 * 
 * @author Arshdeep Khalsa
 *
 */
public class HeistController {

	private HeistGUI theGUI;
	private HeistModel theModel;
	
	public HeistController(HeistGUI gui, HeistModel model){
		
		theGUI = gui;
		theModel = model;
		
		this.theGUI.addResetListener(new resetListener());
		this.theGUI.addEmpListener(new empListener() );
		
		
		
	}
	
	class resetListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
					
			theModel.reset();
			
		}
		
		
	}
	
	class empListener implements ActionListener{
		
		public void actionPerformed(ActionEvent arg0){
			
			theModel.disableAlarm();
		
			
		}
		
	}
	
}
