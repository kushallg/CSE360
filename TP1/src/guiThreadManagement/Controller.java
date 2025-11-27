package guiThreadManagement;

import database.Database;
import entityClasses.DiscussionThread;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

public class Controller {
    private static Database db = applicationMain.FoundationsMain.database;

    protected static void createThread() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Thread");
        dialog.setHeaderText("Enter thread title:");
        dialog.showAndWait().ifPresent(title -> {
            if (!title.trim().isEmpty()) {
                db.createThread(title.trim());
                refreshList();
                showInformation("Thread '" + title.trim() + "' created successfully.");
            }
        });
    }

    protected static void toggleVisibility() {
        DiscussionThread dt = View.listView.getSelectionModel().getSelectedItem();
        if (dt == null)
            return;

        // Toggle
        db.updateThread(dt.getTitle(), dt.getTitle(), !dt.isVisible());
        refreshList();
        String status = !dt.isVisible() ? "visible" : "hidden"; // !dt.isVisible() because we just toggled it in DB but
                                                                // dt object is stale
        showInformation("Thread '" + dt.getTitle() + "' is now " + status + ".");
    }

    protected static void renameThread() {
        DiscussionThread dt = View.listView.getSelectionModel().getSelectedItem();
        if (dt == null)
            return;

        TextInputDialog dialog = new TextInputDialog(dt.getTitle());
        dialog.setTitle("Rename Thread");
        dialog.setHeaderText("Enter new title (Updates existing posts):");
        dialog.showAndWait().ifPresent(newTitle -> {
            if (!newTitle.trim().isEmpty()) {
                db.updateThread(dt.getTitle(), newTitle.trim(), dt.isVisible());
                refreshList();
                showInformation("Thread renamed to '" + newTitle.trim() + "' successfully.");
            }
        });
    }

    protected static void deleteThread() {
        DiscussionThread dt = View.listView.getSelectionModel().getSelectedItem();
        if (dt == null)
            return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete thread '" + dt.getTitle() + "'? This cannot be undone.", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.YES) {
                db.deleteThread(dt.getTitle());
                refreshList();
                showInformation("Thread '" + dt.getTitle() + "' deleted successfully.");
            }
        });
    }

    protected static void refreshList() {
        View.listView.getItems().setAll(db.getAllThreads());
    }

    protected static void performReturn() {
        // Dynamic return based on user role
        entityClasses.User u = View.theUser;
        if (u.getAdminRole()) {
            guiAdminHome.ViewAdminHome.displayAdminHome(View.theStage, u);
        } else if (u.getNewStaff() && !u.getNewStudent()) {
            guiStaff.ViewStaffHome.displayStaffHome(View.theStage, u);
        } else {
            // Fallback, though students shouldn't be here
            guiStaff.ViewStaffHome.displayStaffHome(View.theStage, u);
        }
    }

    private static void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
