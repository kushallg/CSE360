package guiAdminRequests;

import entityClasses.AdminRequest;
import entityClasses.User;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class View {
    public static Stage theStage;
    public static User theUser;
    public static TableView<AdminRequest> table = new TableView<>();

    public static void display(Stage stage, User user) {
        theStage = stage;
        theUser = user;

        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);

        Label title = new Label("Admin Requests & Ticketing");
        title.setFont(Font.font("Arial", 24));
        title.setLayoutX(20);
        title.setLayoutY(20);

        table.setLayoutX(20);
        table.setLayoutY(70);
        table.setPrefSize(760, 400);

        TableColumn<AdminRequest, String> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("requestID"));

        TableColumn<AdminRequest, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<AdminRequest, String> colReq = new TableColumn<>("Requester");
        colReq.setCellValueFactory(new PropertyValueFactory<>("requesterUsername"));

        TableColumn<AdminRequest, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(250);

        TableColumn<AdminRequest, String> colComm = new TableColumn<>("Admin Log");
        colComm.setCellValueFactory(new PropertyValueFactory<>("adminComments"));
        colComm.setPrefWidth(200);

        table.getColumns().setAll(colID, colStatus, colReq, colDesc, colComm);

        HBox controls = new HBox(10);
        controls.setLayoutX(20);
        controls.setLayoutY(490);

        Button btnCreate = new Button("Create Request");
        btnCreate.setOnAction(e -> Controller.createRequest());

        Button btnResolve = new Button("Document & Close (Admin)");
        btnResolve.setOnAction(e -> Controller.resolveRequest());

        Button btnReopen = new Button("Reopen & Update (Staff)");
        btnReopen.setOnAction(e -> Controller.reopenRequest());

        Button btnReturn = new Button("Return");
        btnReturn.setOnAction(e -> Controller.performReturn());

        // Role based visibility
        boolean isAdmin = applicationMain.FoundationsMain.activeHomePage == 1; // 1=Admin

        if (isAdmin) {
            controls.getChildren().addAll(btnResolve, btnReturn);
        } else {
            controls.getChildren().addAll(btnCreate, btnReopen, btnReturn);
        }

        root.getChildren().addAll(title, table, controls);

        Controller.refreshList();

        stage.setTitle("Admin Requests");
        stage.setScene(scene);
        stage.show();
    }
}
