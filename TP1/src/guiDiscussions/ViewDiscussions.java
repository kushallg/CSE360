package guiDiscussions;

import entityClasses.Post;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * <p> Title: ViewDiscussions Class </p>
 *
 * <p> Description: The Java/FX-based view for the discussion forum. This class
 * is responsible for displaying the list of posts and replies, and providing
 * controls for the user to interact with them. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Kushal Gadamsetty
 *
 * @version 1.00	2025-10-18 Initial version
 */
public class ViewDiscussions {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    protected static Label label_PageTitle = new Label("Discussion Forum");
    protected static ListView<Post> listView_Posts = new ListView<>();
    protected static TextArea textArea_PostContent = new TextArea();
    protected static ListView<String> listView_Replies = new ListView<>();

    protected static Button button_CreatePost = new Button("Create New Post");
    protected static Button button_EditPost = new Button("Edit Selected Post");
    protected static Button button_DeletePost = new Button("Delete Selected Post");
    protected static Button button_AddReply = new Button("Add Reply");
    protected static Button button_Return = new Button("Return to Home");

    protected static Stage theStage;
    protected static User theUser;
    private static Pane theRootPane;
    public static Scene theDiscussionsScene = null;

    public static void displayDiscussions(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theDiscussionsScene == null) {
            theRootPane = new Pane();
            theDiscussionsScene = new Scene(theRootPane, width, height);
            setupUI();
        }

        ControllerDiscussions.initializeView();
        theStage.setTitle("CSE 360 Foundations: Discussion Forum");
        theStage.setScene(theDiscussionsScene);
        theStage.show();
    }

    private static void setupUI() {
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        // Posts List
        listView_Posts.setLayoutX(20);
        listView_Posts.setLayoutY(50);
        listView_Posts.setPrefSize(300, 450);
        listView_Posts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ControllerDiscussions.postSelected(newSelection);
        });


        // Post Content
        textArea_PostContent.setLayoutX(340);
        textArea_PostContent.setLayoutY(50);
        textArea_PostContent.setPrefSize(440, 200);
        textArea_PostContent.setEditable(false);

        // Replies List
        listView_Replies.setLayoutX(340);
        listView_Replies.setLayoutY(270);
        listView_Replies.setPrefSize(440, 230);

        // Buttons
        setupButtonUI(button_CreatePost, "Dialog", 14, 150, Pos.CENTER, 20, 520);
        button_CreatePost.setOnAction(event -> ControllerDiscussions.createPost());

        setupButtonUI(button_EditPost, "Dialog", 14, 150, Pos.CENTER, 170, 520);
        button_EditPost.setOnAction(event -> ControllerDiscussions.editPost());

        setupButtonUI(button_DeletePost, "Dialog", 14, 150, Pos.CENTER, 320, 520);
        button_DeletePost.setOnAction(event -> ControllerDiscussions.deletePost());
        
        setupButtonUI(button_AddReply, "Dialog", 14, 150, Pos.CENTER, 470, 520);
        button_AddReply.setOnAction(event -> ControllerDiscussions.addReply());

        setupButtonUI(button_Return, "Dialog", 14, 150, Pos.CENTER, 620, 520);
        button_Return.setOnAction(event -> ControllerDiscussions.returnToHome());


        theRootPane.getChildren().addAll(label_PageTitle, listView_Posts, textArea_PostContent, listView_Replies,
                button_CreatePost, button_EditPost, button_DeletePost, button_AddReply, button_Return);
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