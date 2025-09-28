package guiUserUpdate;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;

public class ControllerUserUpdate {
	/*-********************************************************************************************

	The Controller for ViewUserUpdate 
	
	**********************************************************************************************/
	
	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	
	/**********
	 * <p> Title: ControllerUserUpdate Class</p>
	 * 
	 * <p> Description: This static class supports the actions initiated by the ViewUserUpdate
	 * class. In this case, there is just one method, no constructors, and no attributes.</p>
	 *
	 */

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	**********************************************************************************************/

	
	/**********
	 * <p> Method: public goToUserHomePage(Stage theStage, User theUser) </p>
	 * 
	 * <p> Description: This method is called when the user has clicked on the button to
	 * proceed to the user's home page.
	 * 
	 * @param theStage specifies the JavaFX Stage for next next GUI page and it's methods
	 * 
	 * @param theUser specifies the user so we go to the right page and so the right information
	 */
	protected static void goToUserHomePage(Stage theStage, User theUser) {
		
		// Get the roles the user selected during login
		int theRole = applicationMain.FoundationsMain.activeHomePage;

		// Use that role to proceed to that role's home page
		switch (theRole) {
		case 1:
			guiAdminHome.ViewAdminHome.displayAdminHome(theStage, theUser);
			break;
		case 2:
			guiStudent.ViewStudentHome.displayStudentHome(theStage, theUser);
			break;
		case 3:
			guiStaff.ViewStaffHome.displayStaffHome(theStage, theUser);
			break;
		default: 
			System.out.println("*** ERROR *** UserUpdate goToUserHome has an invalid role: " + 
					theRole);
			System.exit(0);
		}
 	}
	
	public static void otpPasswordReset(Stage theStage) {
		User theUser = new User(
				theDatabase.getCurrentUsername(),
	            theDatabase.getCurrentPassword(),
	            theDatabase.getCurrentFirstName(),
	            theDatabase.getCurrentMiddleName(),
	            theDatabase.getCurrentLastName(),
	            theDatabase.getCurrentPreferredFirstName(),
	            theDatabase.getCurrentEmailAddress(),
	            theDatabase.getCurrentAdminRole(),
	            theDatabase.getCurrentNewStudent(),
	            theDatabase.getCurrentNewStaff()
		);
		
		//Opens user update screen in OTP reset mode
		guiUserUpdate.ViewUserUpdate.displayOtpPasswordReset(theStage,theUser);
	}
}
