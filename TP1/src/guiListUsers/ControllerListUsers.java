package guiListUsers;

import database.Database;
import entityClasses.UserForList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import entityClasses.UserForList;
import guiListUsers.ViewListUsers;

public class ControllerListUsers {

    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static void populateUserList() {
        ObservableList<UserForList> users = FXCollections.observableArrayList(theDatabase.getAllUsersForList());
        ViewListUsers.userTable.setItems(users);
    }

    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(ViewListUsers.theStage, ViewListUsers.theUser);
    }
}