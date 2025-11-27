package guiGradingParameters;

import entityClasses.GradingParameter;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * <p>
 * Title: View Class for Grading Parameters
 * </p>
 * *
 * <p>
 * Description: GUI for managing grading parameters. Contains a table of
 * parameters and buttons to Create, Update, and Delete them.
 * </p>
 * *
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 */
public class View {

    public static Stage theStage;
    public static User theUser;
    public static TableView<GradingParameter> table = new TableView<>();

    public static void display(Stage stage, User user) {
        theStage = stage;
        theUser = user;

        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        // Title
        Label titleLabel = new Label("Manage Grading Parameters");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setLayoutX(20);
        titleLabel.setLayoutY(20);

        // Table View Setup
        table.setLayoutX(20);
        table.setLayoutY(70);
        table.setPrefSize(760, 400);

        TableColumn<GradingParameter, String> nameCol = new TableColumn<>("Parameter Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<GradingParameter, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(550);

        table.getColumns().setAll(nameCol, descCol);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setLayoutX(20);
        buttonBox.setLayoutY(490);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button btnCreate = new Button("Create Parameter");
        btnCreate.setPrefWidth(150);
        btnCreate.setOnAction(e -> Controller.createParameter());

        Button btnUpdate = new Button("Update Selected");
        btnUpdate.setPrefWidth(150);
        btnUpdate.setOnAction(e -> Controller.updateParameter());

        Button btnDelete = new Button("Delete Selected");
        btnDelete.setPrefWidth(150);
        btnDelete.setOnAction(e -> Controller.deleteParameter());

        Button btnReturn = new Button("Return to Home");
        btnReturn.setPrefWidth(150);
        btnReturn.setOnAction(e -> Controller.performReturn());

        buttonBox.getChildren().addAll(btnCreate, btnUpdate, btnDelete, btnReturn);

        root.getChildren().addAll(titleLabel, table, buttonBox);

        // Initial Load
        Controller.refreshList();

        stage.setTitle("Grading Parameters Management");
        stage.setScene(scene);
        stage.show();
    }
}