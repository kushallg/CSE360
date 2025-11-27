package guiGradingParameters;

import database.Database;
import entityClasses.GradingParameter;
import entityClasses.User;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

/**
 * <p>
 * Title: Controller Class for Grading Parameters
 * </p>
 * *
 * <p>
 * Description: Handles logic for Create, Read, Update, and Delete operations
 * for Grading Parameters.
 * </p>
 * *
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 */
public class Controller {

    private static Database db = applicationMain.FoundationsMain.database;

    protected static void createParameter() {
        // Dialog for Name
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Create Parameter");
        nameDialog.setHeaderText("Enter Parameter Name (e.g., 'Post Quality')");
        nameDialog.setContentText("Name:");

        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isPresent() && !nameResult.get().trim().isEmpty()) {
            String name = nameResult.get().trim();

            // Dialog for Description
            TextInputDialog descDialog = new TextInputDialog();
            descDialog.setTitle("Create Parameter");
            descDialog.setHeaderText("Enter Description for '" + name + "'");
            descDialog.setContentText("Description:");

            Optional<String> descResult = descDialog.showAndWait();
            if (descResult.isPresent() && !descResult.get().trim().isEmpty()) {
                String description = descResult.get().trim();

                // Execute DB Action
                db.createGradingParameter(name, description);

                // Confirmation
                showConfirmation("Grading Parameter created successfully.");
                refreshList();
            } else {
                showError("Description cannot be empty.");
            }
        } else {
            // If name was empty but OK was clicked, or cancelled.
            if (nameResult.isPresent())
                showError("Parameter Name cannot be empty.");
        }
    }

    protected static void updateParameter() {
        GradingParameter selected = View.table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a parameter to update.");
            return;
        }

        TextInputDialog nameDialog = new TextInputDialog(selected.getName());
        nameDialog.setTitle("Update Parameter");
        nameDialog.setHeaderText("Update Name");
        nameDialog.setContentText("Name:");

        Optional<String> nameResult = nameDialog.showAndWait();
        if (nameResult.isPresent() && !nameResult.get().trim().isEmpty()) {
            String newName = nameResult.get().trim();

            TextInputDialog descDialog = new TextInputDialog(selected.getDescription());
            descDialog.setTitle("Update Parameter");
            descDialog.setHeaderText("Update Description");
            descDialog.setContentText("Description:");

            Optional<String> descResult = descDialog.showAndWait();
            if (descResult.isPresent() && !descResult.get().trim().isEmpty()) {
                String newDesc = descResult.get().trim();

                // Update Object and DB
                selected.setName(newName);
                selected.setDescription(newDesc);
                db.updateGradingParameter(selected);

                showConfirmation("Grading Parameter updated successfully.");
                refreshList();
            } else {
                showError("Description cannot be empty.");
            }
        }
    }

    protected static void deleteParameter() {
        GradingParameter selected = View.table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a parameter to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete parameter '" + selected.getName() + "'?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            db.deleteGradingParameter(selected.getId());
            showConfirmation("Grading Parameter deleted successfully.");
            refreshList();
        }
    }

    protected static void refreshList() {
        View.table.getItems().setAll(db.getAllGradingParameters());
    }

    protected static void performReturn() {
        guiStaff.ViewStaffHome.displayStaffHome(View.theStage, View.theUser);
    }

    private static void showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}