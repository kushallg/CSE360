package guiThreadManagement;

import entityClasses.DiscussionThread;
import entityClasses.User;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View {
    public static Stage theStage;
    public static User theUser;
    public static ListView<DiscussionThread> listView = new ListView<>();

    public static void display(Stage stage, User user) {
        theStage = stage;
        theUser = user;

        Pane root = new Pane();
        Scene scene = new Scene(root, 600, 500);

        Label lblTitle = new Label("Discussion Thread Management");
        lblTitle.setStyle("-fx-font-size: 20px;");
        lblTitle.setLayoutX(20);
        lblTitle.setLayoutY(20);

        listView.setLayoutX(20);
        listView.setLayoutY(60);
        listView.setPrefSize(400, 300);

        VBox buttons = new VBox(10);
        buttons.setLayoutX(440);
        buttons.setLayoutY(60);

        Button btnCreate = new Button("Create Thread");
        btnCreate.setPrefWidth(140);
        btnCreate.setOnAction(e -> Controller.createThread());

        Button btnRename = new Button("Rename Thread");
        btnRename.setPrefWidth(140);
        btnRename.setOnAction(e -> Controller.renameThread());

        Button btnToggle = new Button("Toggle Visibility");
        btnToggle.setPrefWidth(140);
        btnToggle.setOnAction(e -> Controller.toggleVisibility());

        Button btnDelete = new Button("Delete Thread");
        btnDelete.setPrefWidth(140);
        btnDelete.setOnAction(e -> Controller.deleteThread());

        Button btnReturn = new Button("Return");
        btnReturn.setPrefWidth(140);
        btnReturn.setOnAction(e -> Controller.performReturn());

        buttons.getChildren().addAll(btnCreate, btnRename, btnToggle, btnDelete, btnReturn);
        root.getChildren().addAll(lblTitle, listView, buttons);

        Controller.refreshList();

        stage.setTitle("Thread Management");
        stage.setScene(scene);
        stage.show();
    }
}
