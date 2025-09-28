package guiAdminHome;

import database.Database;
import emailAddressValidator.EmailAddressRecognizer;
import javafx.scene.control.Label;
import java.util.List; // Required for using the List interface

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
		System.out.println("\n*** WARNING ***: One-Time Password Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("One-Time Password Issue");
		ViewAdminHome.alertNotImplemented.setContentText("One-Time Password Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	protected static void deleteUser() {
		System.out.println("\n*** WARNING ***: Delete User Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Delete User Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Delete User Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	protected static void listUsers() {
		System.out.println("\n*** WARNING ***: List Users Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("List User Issue");
		ViewAdminHome.alertNotImplemented.setContentText("List Users Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
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