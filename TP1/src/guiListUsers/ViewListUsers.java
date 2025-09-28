package guiListUsers;


import entityClasses.User;
import entityClasses.UserForList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewListUsers {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    protected static Label label_PageTitle = new Label("User List");
    protected static TableView<UserForList> userTable = new TableView<>();
    protected static Button button_Return = new Button("Return");
    protected static Stage theStage;
    protected static User theUser;
    private static Pane theRootPane;
    public static Scene theListUsersScene = null;

    public static void displayListUsers(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theListUsersScene == null) {
            theRootPane = new Pane();
            theListUsersScene = new Scene(theRootPane, width, height);
            setupUI();
        }

        ControllerListUsers.populateUserList();
        theStage.setTitle("CSE 360 Foundation Code: User List");
        theStage.setScene(theListUsersScene);
        theStage.show();
    }

    private static void setupUI() {
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        userTable.setLayoutX(50);
        userTable.setLayoutY(50);
        userTable.setPrefWidth(700);
        userTable.setPrefHeight(450);

        TableColumn<UserForList, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);

        TableColumn<UserForList, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<UserForList, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);

        TableColumn<UserForList, String> rolesCol = new TableColumn<>("Roles");
        rolesCol.setCellValueFactory(new PropertyValueFactory<>("roles"));
        rolesCol.setPrefWidth(150);

        userTable.getColumns().addAll(usernameCol, nameCol, emailCol, rolesCol);

        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, (width - 210) / 2, 540);
        button_Return.setOnAction((event) -> {
            ControllerListUsers.performReturn();
        });

        theRootPane.getChildren().addAll(label_PageTitle, userTable, button_Return);
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