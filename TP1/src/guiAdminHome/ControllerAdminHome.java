package guiAdminHome;

import database.Database;
import emailAddressValidator.EmailAddressRecognizer;
import javafx.scene.control.Label;
import guiListUsers.ViewListUsers;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.List; 

import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;


/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */



public class ControllerAdminHome {
	
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static void performInvitation () {
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		
		String emailErr = EmailAddressRecognizer.checkEmailAddress(emailAddress);
		if (!emailErr.isEmpty()) {
			System.out.println("\n*** ERROR ***: " + emailErr);
			
			Label label = new Label(emailErr);
		    label.setWrapText(true);
		    label.setMaxWidth(400);
		    
		    ViewAdminHome.alertEmailError.setTitle("*** ERROR ***");
		    ViewAdminHome.alertEmailError.setHeaderText("Email Address Validation Error");
		    ViewAdminHome.alertEmailError.getDialogPane().setContent(label);
		    ViewAdminHome.alertEmailError.showAndWait();
		    return;
		}
		else if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// *** CHANGE ***: This block retrieves the list of selected roles from the ListView.
		// The getSelectedItems() method returns a List of all roles the user has selected.
		List<String> selectedRoles = ViewAdminHome.listView_Roles.getSelectionModel().getSelectedItems();

        // Ensure at least one role is selected before proceeding.
        if (selectedRoles.isEmpty()) {
            ViewAdminHome.alertEmailError.setContentText("Please select at least one role for the invitation.");
            ViewAdminHome.alertEmailError.showAndWait();
            return;
        }

        // *** CHANGE ***: Combines the list of selected roles into a single comma-separated string.
        // For example, if "Admin" and "Student" are selected, this will create the string "Admin, Student".
        // This string is what gets stored in the database.
        String roles = String.join(", ", selectedRoles);
		
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				roles);
		String msg = "Code: " + invitationCode + " for role(s) " + roles + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	protected static void setOnetimePassword () {
		//System.out.println("\n*** WARNING ***: One-Time Password Not Yet Implemented");
		//ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		//ViewAdminHome.alertNotImplemented.setHeaderText("One-Time Password Issue");
		//ViewAdminHome.alertNotImplemented.setContentText("One-Time Password Not Yet Implemented");
		//ViewAdminHome.alertNotImplemented.showAndWait();
		TextInputDialog askUsername = new TextInputDialog();
		askUsername.setTitle("Set One-Time Password");
		askUsername.setHeaderText("Allow user's to reset their password with a one-time password");
		askUsername.setContentText("Enter username: ");
		java.util.Optional<String> u = askUsername.showAndWait();
		if (!u.isPresent()) {
			return;
		}
		String username = u.get();
		if (username.length() == 0) {
			return;
		}
		
		//check if Username exists in the first place
		if(!theDatabase.doesUserExist(username)) {
			Alert a = new Alert(AlertType.INFORMATION);
			a.setTitle("*** ERROR ***");
			a.setHeaderText("User not found");
			a.setContentText("User \"" + username + "\" was not found.");
			a.showAndWait();
			return;
		}
		
		String otp = theDatabase.generateOTPCode(username);
		if (otp==null || otp.length() == 0) {
			Alert a = new Alert(AlertType.INFORMATION);
			a.setTitle("*** ERROR ***");
			a.setHeaderText("Couldn't set one-time password");
			a.setContentText("An unexpected error occurred setting the one-time password.");
			a.showAndWait();
		    return;
		}
		
		String msg = "One-time password set for \"" + username + "\"\n\n" + "OTP: " + otp;
		ViewAdminHome.alertEmailSent.setTitle("One-Time Password Set!");
		ViewAdminHome.alertEmailSent.setHeaderText("Success");
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
	}
	
	protected static void deleteUser() {
	    // Step 1: Get all usernames from the database.
	    List<String> users = new ArrayList<>(theDatabase.getUserList());
	    users.remove("<Select a User>"); // Remove the placeholder text.

	    // Check if there are any other users to delete.
	    if (users.isEmpty()) {
	        Alert noUsersAlert = new Alert(AlertType.INFORMATION);
	        noUsersAlert.setTitle("Delete User");
	        noUsersAlert.setHeaderText("No Other Users Found");
	        noUsersAlert.setContentText("There are no other user accounts available to delete.");
	        noUsersAlert.showAndWait();
	        return;
	    }

	    // Step 2: Create a dialog to let the admin choose a user to delete.
	    ChoiceDialog<String> dialog = new ChoiceDialog<>(users.get(0), users);
	    dialog.setTitle("Delete User");
	    dialog.setHeaderText("Select the user account to delete.");
	    dialog.setContentText("User:");
	    Optional<String> result = dialog.showAndWait();

	    // Step 3: If the admin selected a user, proceed.
	    if (result.isPresent()){
	        String usernameToDelete = result.get();

	        // IMPORTANT: Prevent an admin from deleting their own account.
	        if (ViewAdminHome.theUser.getUserName().equals(usernameToDelete)) {
	            Alert selfDeleteAlert = new Alert(AlertType.ERROR);
	            selfDeleteAlert.setTitle("Action Not Allowed");
	            selfDeleteAlert.setHeaderText("Admins cannot delete their own account.");
	            selfDeleteAlert.showAndWait();
	            return;
	        }

	        // Step 4: Show the "Are you sure?" confirmation dialog.
	        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
	        confirmAlert.setTitle("Confirm Deletion");
	        confirmAlert.setHeaderText("Are you sure you want to delete this account?");
	        confirmAlert.setContentText("User: " + usernameToDelete + "\nThis action cannot be undone.");
	        Optional<ButtonType> confirmResult = confirmAlert.showAndWait();

	        // Step 5: If the admin clicks "OK", perform the deletion.
	        if (confirmResult.isPresent() && confirmResult.get() == ButtonType.OK) {
	            theDatabase.deleteUser(usernameToDelete);

	            // Show a final success message.
	            Alert finalAlert = new Alert(AlertType.INFORMATION);
	            finalAlert.setTitle("Success");
	            finalAlert.setHeaderText("User Deleted");
	            finalAlert.setContentText("The account for '" + usernameToDelete + "' has been deleted.");
	            finalAlert.showAndWait();

	            // Refresh the user count display on the main admin screen.
	            ViewAdminHome.label_NumberOfUsers.setText("Number of users: " + theDatabase.getNumberOfUsers());
	        }
	    }
	}
	
	protected static void listUsers() {
	    guiListUsers.ViewListUsers.displayListUsers(ViewAdminHome.theStage, ViewAdminHome.theUser);
	}
	
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	protected static void performQuit() {
		System.exit(0);
	}
}