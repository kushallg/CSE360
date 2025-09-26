package guiFirstAdmin;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
//import guiNewAccount.ViewNewAccount;
import javafx.scene.control.Label;
import javafx.stage.Stage;
//import nameValidation.NameValidation;
import passwordPopUpWindow.Model;
import userNameRecognizer.UserNameRecognizer;

public class ControllerFirstAdmin {
	/*-********************************************************************************************

	The controller attributes for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/
	
	private static String adminUsername = "";
	private static String adminPassword1 = "";
	private static String adminPassword2 = "";
	/** remove
	private static String adminFirstName = "";
	private static String adminMiddleName = "";
	private static String adminLastName = "";
	private static String adminEmail = "";
	*/
	protected static Database theDatabase = applicationMain.FoundationsMain.database;		

	/*-********************************************************************************************

	The User Interface Actions for this page
	
	*/
	
	
	/**********
	 * <p> Method: setAdminUsername() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the username field in the
	 * View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminUsername() {
		adminUsername = ViewFirstAdmin.text_AdminUsername.getText();
	}
	
	
	/**********
	 * <p> Method: setAdminPassword1() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 1 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword1() {
		adminPassword1 = ViewFirstAdmin.text_AdminPassword1.getText();
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	
	/**********
	 * <p> Method: setAdminPassword2() </p>
	 * 
	 * <p> Description: This method is called when the user adds text to the password 2 field in
	 * the View.  A private local copy of what was last entered is kept here.</p>
	 * 
	 */
	protected static void setAdminPassword2() {
		adminPassword2 = ViewFirstAdmin.text_AdminPassword2.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}

	/** remove
	protected static void setAdminFirstName() {
		adminPassword2 = ViewFirstAdmin.text_AdminFirstName.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}

	protected static void setAdminMiddleName() {
		adminPassword2 = ViewFirstAdmin.text_AdminMiddleName.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	protected static void setAdminLastName() {
		adminPassword2 = ViewFirstAdmin.text_AdminLastName.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	
	protected static void setAdminEmail() {
		adminPassword2 = ViewFirstAdmin.text_AdminEmail.getText();		
		ViewFirstAdmin.label_PasswordsDoNotMatch.setText("");
	}
	*/
	
	/**********
	 * <p> Method: doSetupAdmin() </p>
	 * 
	 * <p> Description: This method is called when the user presses the button to set up the Admin
	 * account.  It start by trying to establish a new user and placing that user into the
	 * database.  If that is successful, we proceed to the UserUpdate page.</p>
	 * 
	 */
	protected static void doSetupAdmin(Stage ps, int r) {
		
		//use UserNameRecognizer validity method to detect and output username errors
		String usernameErr = UserNameRecognizer.checkForValidUserName(adminUsername);
		if(!usernameErr.isEmpty()) {
			System.out.println("\n*** ERROR ***: " + usernameErr);
			
			Label label = new Label(usernameErr);
		    label.setWrapText(true);
		    label.setMaxWidth(400);
		    
			ViewFirstAdmin.alertUsernamePasswordError.setTitle("*** ERROR ***");
			ViewFirstAdmin.alertUsernamePasswordError.setHeaderText("Username Validation Error");
			ViewFirstAdmin.alertUsernamePasswordError.getDialogPane().setContent(label); //Display specific username error
			ViewFirstAdmin.alertUsernamePasswordError.showAndWait();
			return;
		}
				
		//use PasswordEvaluation evaluate method to detect and output password errors
		String passwordErr = Model.evaluatePassword(adminPassword1);
		if (!passwordErr.isEmpty()) {
			System.out.println("\n*** ERROR ***: " + passwordErr);
			
			Label label = new Label(passwordErr);
		    label.setWrapText(true);
		    label.setMaxWidth(400);
		    
			ViewFirstAdmin.alertUsernamePasswordError.setTitle("*** ERROR ***");
			ViewFirstAdmin.alertUsernamePasswordError.setHeaderText("Password Validation Error");
			ViewFirstAdmin.alertUsernamePasswordError.getDialogPane().setContent(label); //Display specific password error
			ViewFirstAdmin.alertUsernamePasswordError.showAndWait();
			return;
		}
		
		/** remove this
		String firstNameErr = NameValidation.checkForValidName(adminFirstName);
		if (!firstNameErr.isEmpty()) {
			System.out.println("\n*** ERROR ***: " + firstNameErr);
			
			Label label = new Label(firstNameErr);
		    label.setWrapText(true);
		    label.setMaxWidth(400);
		    
		    ViewFirstAdmin.alertUsernamePasswordError.setTitle("*** ERROR ***");
		    ViewFirstAdmin.alertUsernamePasswordError.setHeaderText("First Name Validation Error");
		    ViewFirstAdmin.alertUsernamePasswordError.getDialogPane().setContent(label); //Display specific password error
		    ViewFirstAdmin.alertUsernamePasswordError.showAndWait();
		    return;
		}
		
		if (!adminMiddleName.isEmpty()) {
			String middleNameErr = NameValidation.checkForValidName(adminMiddleName);
			if (!middleNameErr.isEmpty()) {
				System.out.println("\n*** ERROR ***: " + middleNameErr);
				
				Label label = new Label(middleNameErr);
			    label.setWrapText(true);
			    label.setMaxWidth(400);
			    
			    ViewFirstAdmin.alertUsernamePasswordError.setTitle("*** ERROR ***");
			    ViewFirstAdmin.alertUsernamePasswordError.setHeaderText("Middle Name Validation Error");
			    ViewFirstAdmin.alertUsernamePasswordError.getDialogPane().setContent(label); //Display specific password error
			    ViewFirstAdmin.alertUsernamePasswordError.showAndWait();
			    return;
			}
		}
		
		String lastNameErr = NameValidation.checkForValidName(adminLastName);
		if (!lastNameErr.isEmpty()) {
			System.out.println("\n*** ERROR ***: " + lastNameErr);
			
			Label label = new Label(lastNameErr);
		    label.setWrapText(true);
		    label.setMaxWidth(400);
		    
		    ViewFirstAdmin.alertUsernamePasswordError.setTitle("*** ERROR ***");
		    ViewFirstAdmin.alertUsernamePasswordError.setHeaderText("Last Name Validation Error");
		    ViewFirstAdmin.alertUsernamePasswordError.getDialogPane().setContent(label); //Display specific password error
		    ViewFirstAdmin.alertUsernamePasswordError.showAndWait();
		    return;
		}
		
		*/
		
		// Make sure the two passwords are the same
		if (adminPassword1.compareTo(adminPassword2) == 0) {
        	// Create the passwords and proceed to the user home page
        	User user = new User(adminUsername, adminPassword1, "", "", "", "", "", true, false, 
        			false);
            try {
            	// Create a new User object with admin role and register in the database
            	theDatabase.register(user);
            	}
            catch (SQLException e) {
                System.err.println("*** ERROR *** Database error trying to register a user: " + 
                		e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            // User was established in the database, so navigate to the User Update Page
        	guiUserUpdate.ViewUserUpdate.displayUserUpdate(ViewFirstAdmin.theStage, user);
		}
		else {
			// The two passwords are NOT the same, so clear the passwords, explain the passwords
			// must be the same, and clear the message as soon as the first character is typed.
			ViewFirstAdmin.text_AdminPassword1.setText("");
			ViewFirstAdmin.text_AdminPassword2.setText("");
			ViewFirstAdmin.label_PasswordsDoNotMatch.setText(
					"The two passwords must match. Please try again!");
		}
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}

