package guiStaff;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiDiscussions.ViewDiscussions; // (Optional but mirrors ViewStudentHome)
import guiUserUpdate.ViewUserUpdate; // To mirror student home "Account Update" behavior, if desired

/*******
 * <p>
 * Title: ViewStaffHome Class.
 * </p>
 * 
 * <p>
 * Description: The Java/FX-based Staff Home Page. The page is a stub for some
 * role needed for
 * the application. The widgets on this page are likely the minimum number and
 * kind for other role
 * pages that may be needed.
 * </p>
 * 
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.01 2025-10-18 Added Discussions button to match Student Home
 * 
 */

public class ViewStaffHome {
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");

	protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);

	protected static Button button_GoToDiscussions = new Button("Go to Discussions");

	// NEW BUTTONS
	protected static Button button_AdminRequests = new Button("Admin Requests");
	protected static Button button_ManageThreads = new Button("Manage Threads");

	protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");
	protected static Button button_GradingParameters = new Button("Grading Parameters");

	private static ViewStaffHome theView;
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;
	protected static Pane theRootPane;
	protected static User theUser;

	private static Scene theStaffHomeScene;
	protected static final int theRole = 3;

	public static void displayStaffHome(Stage ps, User user) {
		theStage = ps;
		theUser = user;

		if (theView == null)
			theView = new ViewStaffHome();

		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		label_UserDetails.setText("User: " + theUser.getUserName()); // Set the username

		// Set the title for the window, display the page, and wait for the Staff to do
		// something
		theStage.setTitle("CSE 360 Foundations: Staff Home Page");
		theStage.setScene(theStaffHomeScene);
		theStage.show();
	}

	private ViewStaffHome() {
		theRootPane = new Pane();
		theStaffHomeScene = new Scene(theRootPane, width, height); // Create the scene

		// GUI Area 1
		label_PageTitle.setText("Staff Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);

		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) -> {
			ViewUserUpdate.displayUserUpdate(theStage, theUser);
		});
		// GUI Area 2: Staff entry to student discussion board
		setupButtonUI(button_GoToDiscussions, "Dialog", 18, 250, Pos.CENTER, (width - 250) / 2, 150);
		button_GoToDiscussions.setOnAction((event) -> {
			ControllerStaffHome.goToDiscussions();
		});

		// SETUP NEW BUTTONS
		setupButtonUI(button_AdminRequests, "Dialog", 18, 250, Pos.CENTER, (width - 250) / 2, 220);
		button_AdminRequests.setOnAction((event) -> {
			guiAdminRequests.View.display(theStage, theUser);
		});

		setupButtonUI(button_ManageThreads, "Dialog", 18, 250, Pos.CENTER, (width - 250) / 2, 290);
		button_ManageThreads.setOnAction((event) -> {
			guiThreadManagement.View.display(theStage, theUser);
		});

		setupButtonUI(button_GradingParameters, "Dialog", 18, 250, Pos.CENTER, (width - 250) / 2, 360);
		button_GradingParameters.setOnAction((event) -> {
			guiGradingParameters.View.display(theStage, theUser);
		});

		// GUI Area 3
		setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
		button_Logout.setOnAction((event) -> {
			ControllerStaffHome.performLogout();
		});

		setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
		button_Quit.setOnAction((event) -> {
			ControllerStaffHome.performQuit();
		});

		// Place all of the widget items into the Root Pane's list of children
		theRootPane.getChildren().addAll(
        label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
        button_GoToDiscussions, button_AdminRequests, button_ManageThreads,
        button_GradingParameters, // <--- ADD THIS
        line_Separator4, button_Logout, button_Quit);
	}

	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);
	}
}
