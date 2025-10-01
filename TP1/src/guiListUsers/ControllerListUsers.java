package guiListUsers;

import database.Database;
import entityClasses.UserForList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import entityClasses.UserForList;
import guiListUsers.ViewListUsers;

/**
 * The controller for the List Users window. This class handles the logic for
 * populating the user table and returning to the admin home screen.
 */
public class ControllerListUsers {

    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**
     * Fetches all users from the database using the getAllUsersForList method
     * and populates the userTable TableView with the results.
     */
    protected static void populateUserList() {
        ObservableList<UserForList> users = FXCollections.observableArrayList(theDatabase.getAllUsersForList());
        ViewListUsers.userTable.setItems(users);
    }

    /**
     * Handles the "Return" button action. It closes the current "List Users"
     * window and displays the main Admin Home window.
     */
    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(ViewListUsers.theStage, ViewListUsers.theUser);
    }
}