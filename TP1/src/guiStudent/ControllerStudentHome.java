// BOLD CHANGE
package guiStudent;

import guiDiscussions.ViewDiscussions; // Import the new Discussions view

public class ControllerStudentHome {

	/*-*******************************************************************************************

	User Interface Actions for this page

	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and
	the Model is often just a stub, or will be a singleton instantiated object.

	 */

    /**
     * Navigates the user to the main discussion forum view.
     */
    protected static void goToDiscussions() {
        ViewDiscussions.displayDiscussions(ViewStudentHome.theStage, ViewStudentHome.theUser);
    }


 	/**********
	 * <p> Method: performLogout() </p>
	 *
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 *
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewStudentHome.theStage);
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
		System.exit(0);
	}
}