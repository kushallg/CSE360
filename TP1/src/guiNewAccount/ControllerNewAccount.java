package guiNewAccount;

import java.sql.SQLException;
import database.Database;
import entityClasses.User;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import passwordPopUpWindow.Model;
import userNameRecognizer.UserNameRecognizer;

public class ControllerNewAccount {
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;
	
	protected static void doCreateUser() {
		
		String username = ViewNewAccount.text_Username.getText();
		String password = ViewNewAccount.text_Password1.getText();
		
		String usernameErr = UserNameRecognizer.checkForValidUserName(username);
		if(!usernameErr.isEmpty()) {
			System.out.println("\n*** ERROR ***: " + usernameErr);
			
			Label label = new Label(usernameErr);
		    label.setWrapText(true);
		    label.setMaxWidth(400);
		    
			ViewNewAccount.alertUsernamePasswordError.setTitle("*** ERROR ***");
		    ViewNewAccount.alertUsernamePasswordError.setHeaderText("Username Validation Error");
		    ViewNewAccount.alertUsernamePasswordError.getDialogPane().setContent(label);
		    ViewNewAccount.alertUsernamePasswordError.showAndWait();
			return;
		}
		
		String passwordErr = Model.evaluatePassword(password);
		if (!passwordErr.isEmpty()) {
			System.out.println("\n*** ERROR ***: " + passwordErr);
			
			Label label = new Label(passwordErr);
		    label.setWrapText(true);
		    label.setMaxWidth(400);
		    
		    ViewNewAccount.alertUsernamePasswordError.setTitle("*** ERROR ***");
		    ViewNewAccount.alertUsernamePasswordError.setHeaderText("Password Validation Error");
		    ViewNewAccount.alertUsernamePasswordError.getDialogPane().setContent(label);
		    ViewNewAccount.alertUsernamePasswordError.showAndWait();
		    return;
		}
		
		System.out.println("** Account for Username: " + username + "; theInvitationCode: "+
				ViewNewAccount.theInvitationCode + "; email address: " + 
				ViewNewAccount.emailAddress + "; Role: " + ViewNewAccount.theRole);
		
		int roleCode = 0;
		User user = null;

		if (ViewNewAccount.text_Password1.getText().compareTo(ViewNewAccount.text_Password2.getText()) == 0) {
			
			// *** CHANGE ***: This block parses the role string from the database.
			// It splits the string by the comma (e.g., "Admin, Student") and loops through the
			// resulting roles to set the correct boolean flags for the new User object.
			String[] roles = ViewNewAccount.theRole.split(",");
            boolean isAdmin = false;
            boolean isStudent = false;
            boolean isStaff = false;

            for (String role : roles) {
                if (role.trim().equalsIgnoreCase("Admin")) {
                    isAdmin = true;
                    roleCode = 1; // Prioritize Admin for the initial home page if multiple roles exist
                } else if (role.trim().equalsIgnoreCase("Student")) {
                    isStudent = true;
                    if (roleCode == 0) roleCode = 2;
                } else if (role.trim().equalsIgnoreCase("Staff")) {
                    isStaff = true;
                    if (roleCode == 0) roleCode = 3;
                }
            }
            
            user = new User(username, password, "", "", "", "", "", isAdmin, isStudent, isStaff);
			
        	user.setEmailAddress(ViewNewAccount.emailAddress);
			applicationMain.FoundationsMain.activeHomePage = roleCode;
			
            try {
            	theDatabase.register(user);
            } catch (SQLException e) {
                System.err.println("*** ERROR *** Database error: " + e.getMessage());
                e.printStackTrace();
                System.exit(0);
            }
            
            theDatabase.removeInvitationAfterUse(ViewNewAccount.theInvitationCode); //deletes from the database right after successful account creation
            
            // *** CHANGE ***: This section implements the redirect after account creation.
            // An Alert box is created to inform the user that their account was created successfully.
            Alert successAlert = new Alert(AlertType.INFORMATION);
            successAlert.setTitle("Account Created");
            successAlert.setHeaderText("Account created successfully!");
            successAlert.setContentText("Please log in with your new credentials.");
            successAlert.showAndWait();
            
            // After the user clicks "OK" on the alert, the application navigates back to the login screen.
            guiUserLogin.ViewUserLogin.displayUserLogin(ViewNewAccount.theStage);
		}
		else {
			ViewNewAccount.text_Password1.setText("");
			ViewNewAccount.text_Password2.setText("");
			ViewNewAccount.alertUsernamePasswordError.showAndWait();
		}
	}

	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}	
}