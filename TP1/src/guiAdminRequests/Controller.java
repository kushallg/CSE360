package guiAdminRequests;

import database.Database;
import entityClasses.AdminRequest;
import entityClasses.User;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class Controller {
    private static Database db = applicationMain.FoundationsMain.database;

    protected static void createRequest() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Admin Request");
        dialog.setHeaderText("Create a task for Admins");
        dialog.setContentText("Description:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(desc -> {
            if (!desc.trim().isEmpty()) {
                db.createAdminRequest(View.theUser.getUserName(), desc);
                refreshList();
            }
        });
    }

    protected static void resolveRequest() {
        AdminRequest req = View.table.getSelectionModel().getSelectedItem();
        if (req == null)
            return;

        TextInputDialog dialog = new TextInputDialog(req.getAdminComments());
        dialog.setTitle("Resolve Request");
        dialog.setHeaderText("Document actions and close request.");
        dialog.setContentText("Actions Taken:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(comments -> {
            req.setAdminComments(comments);
            req.setStatus("Closed");
            db.updateAdminRequest(req);
            refreshList();
        });
    }

    protected static void reopenRequest() {
        AdminRequest req = View.table.getSelectionModel().getSelectedItem();
        if (req == null)
            return;

        TextInputDialog dialog = new TextInputDialog(req.getDescription());
        dialog.setTitle("Reopen Request");
        dialog.setHeaderText("Update description for reopening.");
        dialog.setContentText("New Description:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(desc -> {
            req.setDescription(desc);
            req.setStatus("Reopened");
            db.updateAdminRequest(req);
            refreshList();
        });
    }

    protected static void refreshList() {
        View.table.getItems().setAll(db.getAllAdminRequests());
    }

    protected static void performReturn() {
        User u = View.theUser;
        if (u.getAdminRole() && applicationMain.FoundationsMain.activeHomePage == 1) {
            guiAdminHome.ViewAdminHome.displayAdminHome(View.theStage, u);
        } else {
            guiStaff.ViewStaffHome.displayStaffHome(View.theStage, u);
        }
    }
}
