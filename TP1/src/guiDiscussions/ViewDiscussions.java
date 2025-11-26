// BOLD CHANGE:
package guiDiscussions;

import entityClasses.Post;
import entityClasses.Reply;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import database.Database; // <<< NEW
import java.util.List; // <<< NEW

/**
 * <p>
 * Title: ViewDiscussions Class
 * </p>
 *
 * <p>
 * Description: The Java/FX-based view for the discussion forum. This class
 * is responsible for displaying the list of posts and replies, and providing
 * controls for the user to interact with them.
 * </p>
 *
 * <p>
 * Copyright: Lynn Robert Carter © 2025
 * </p>
 *
 * @author Kushal Gadamsetty
 *
 * @version 1.05 2025-10-20 Added unread reply UI
 */
public class ViewDiscussions {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    protected static Label label_PageTitle = new Label("Discussion Forum");
    protected static ListView<Post> listView_Posts = new ListView<>();
    protected static TextArea textArea_PostContent = new TextArea();
    protected static ListView<Reply> listView_Replies = new ListView<>();

    protected static Button button_CreatePost = new Button("Create New Post");
    protected static Button button_EditPost = new Button("Edit Selected Post");
    protected static Button button_DeletePost = new Button("Delete Selected Post");
    protected static Button button_AddReply = new Button("Add Reply");
    protected static Button button_Return = new Button("Return to Home");
    protected static Button button_MyPosts = new Button("My Posts");
    protected static Button button_Unread = new Button("Unread");
    protected static Button button_EditReply = new Button("Edit Reply");
    protected static Button button_DeleteReply = new Button("Delete Reply");
    // Button to filter and display only unread replies for the selected post
    protected static Button button_UnreadReplies = new Button("Unread Replies");
    // Moderation and Visibility buttons
    protected static Button button_ToggleVisibility = new Button("Hide / Unhide");
    protected static Button button_FlagContent = new Button("Flag Content");

    // UI components for threads and search
    protected static ComboBox<String> comboBox_Threads = new ComboBox<>();
    protected static TextField textField_Search = new TextField();
    protected static Button button_Search = new Button("Search");

    protected static Stage theStage;
    protected static User theUser;
    private static Pane theRootPane;
    public static Scene theDiscussionsScene = null;
    protected static Label label_PostSummary = new Label();
    protected static Label label_ReplySummary = new Label();

    // <<< NEW: database reference for computing student-visible reply counts >>>
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**
     * <p>
     * Method: void displayDiscussions(Stage ps, User user)
     * </p>
     * 
     * <p>
     * Description: Displays the discussions view for the given user by setting up
     * the scene
     * and initializing the interface if it hasn't been created yet.
     * </p>
     * 
     * @param ps   is the stage
     * 
     * @param user is the user that is logged in
     * 
     */
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

        boolean isStaffOrAdmin = theUser != null &&
                (theUser.getAdminRole() ||
                        (theUser.getNewStaff() && !theUser.getNewStudent()));

        // Hide from students completely
        button_ToggleVisibility.setVisible(isStaffOrAdmin);
        button_ToggleVisibility.setManaged(isStaffOrAdmin);
        button_FlagContent.setVisible(isStaffOrAdmin);
        button_FlagContent.setManaged(isStaffOrAdmin);

        // Also disable them when hidden, just to be safe
        button_ToggleVisibility.setDisable(!isStaffOrAdmin);
        button_FlagContent.setDisable(!isStaffOrAdmin);
    }

    /**
     * <p>
     * Method: void setupUI()
     * </p>
     * 
     * <p>
     * Description: Builds and configures all UI elements for the discussions view.
     * </p>
     * 
     */
    private static void setupUI() {
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        // Search and Thread Filter UI
        comboBox_Threads.setLayoutX(20);
        comboBox_Threads.setLayoutY(50);
        comboBox_Threads.setPrefWidth(150);
        // Determine if user is Staff or Admin
        boolean isStaffOrAdmin = theUser != null &&
                (theUser.getAdminRole() || (theUser.getNewStaff() && !theUser.getNewStudent()));

        java.util.List<String> threadTitles;
        if (isStaffOrAdmin) {
            // Staff/Admin see ALL threads (including hidden "Staff Only" threads)
            threadTitles = ControllerDiscussions.theDatabase.getAllThreadTitles();
        } else {
            // Students only see threads marked as visible
            threadTitles = ControllerDiscussions.theDatabase.getVisibleThreadTitles();
        }

        comboBox_Threads.getItems().setAll(threadTitles);
        comboBox_Threads.getSelectionModel().selectFirst();

        textField_Search.setLayoutX(180);
        textField_Search.setLayoutY(50);
        textField_Search.setPrefWidth(180);
        textField_Search.setPromptText("Search by keyword");

        button_Search.setLayoutX(370);
        button_Search.setLayoutY(50);
        button_Search.setPrefWidth(80);
        button_Search.setOnAction(event -> ControllerDiscussions.searchPosts());

        button_MyPosts.setLayoutX(460);
        button_MyPosts.setLayoutY(50);
        button_MyPosts.setPrefWidth(80);
        button_MyPosts.setOnAction(event -> ControllerDiscussions.viewMyPosts());

        button_Unread.setLayoutX(550);
        button_Unread.setLayoutY(50);
        button_Unread.setPrefWidth(80);
        button_Unread.setOnAction(event -> ControllerDiscussions.viewUnreadPosts());

        // NEW: Setup the Unread Replies button and attach its event handler
        button_UnreadReplies.setLayoutX(640);
        button_UnreadReplies.setLayoutY(50);
        button_UnreadReplies.setPrefWidth(110);
        button_UnreadReplies.setOnAction(event -> ControllerDiscussions.viewUnreadReplies());

        // Posts List
        listView_Posts.setLayoutX(20);
        listView_Posts.setLayoutY(90);
        listView_Posts.setPrefSize(760, 200);
        listView_Posts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ControllerDiscussions.postSelected(newSelection);

            label_PostSummary.setLayoutX(540);
            label_PostSummary.setLayoutY(270);
            label_PostSummary.setFont(Font.font("Arial", 14));
            label_PostSummary.setStyle("-fx-font-weight: bold;");

            // Position the reply summary label (shows total and unread reply counts)
            label_ReplySummary.setLayoutX(330);
            label_ReplySummary.setLayoutY(270);
            label_ReplySummary.setFont(Font.font("Arial", 14));
            label_ReplySummary.setStyle("-fx-font-weight: bold;");

        });

        // Custom ListCell for showing a blue dot for unread posts
        listView_Posts.setCellFactory(param -> new ListCell<Post>() {
            /**
             * <p>
             * Method: void updateItem(Post post, boolean empty)
             * </p>
             * 
             * <p>
             * Description: Adds a blue dot next to unread posts in the list view.
             * </p>
             * 
             * @param post  is the post object
             * 
             * @param empty is a boolean indicating whether this cell is empty; true if the
             *              cell should be cleared
             */
            @Override
            protected void updateItem(Post post, boolean empty) {
                super.updateItem(post, empty);
                if (empty || post == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5);
                    Circle dot = new Circle(5, Color.BLUE);
                    if (post.isViewed()) {
                        dot.setVisible(false);
                    }

                    // <<< NEW: for students, recompute reply counts using only visible replies >>>
                    int activeRole = applicationMain.FoundationsMain.activeHomePage;
                    boolean isStudentRole = (activeRole == 2);

                    String labelText;
                    if (isStudentRole && theUser != null) {
                        List<Reply> allReplies = theDatabase.getRepliesForPost(post.getPostID(), theUser.getUserName());

                        long visibleCount = allReplies.stream()
                                .filter(Reply::isVisible)
                                .count();

                        long unreadVisible = allReplies.stream()
                                .filter(r -> r.isVisible() && !r.isViewed())
                                .count();

                        labelText = String.format(
                                "%s (%s) - Replies: %d (Unread: %d)",
                                post.getTitle(),
                                post.getThread(),
                                visibleCount,
                                unreadVisible);
                    } else {
                        // Admin/staff (or no user) see the original DB-counted values
                        labelText = post.toString();
                    }

                    hbox.getChildren().addAll(dot, new Label(labelText));
                    setGraphic(hbox);
                }
            }
        });

        // Post Content
        textArea_PostContent.setLayoutX(20);
        textArea_PostContent.setLayoutY(300);
        textArea_PostContent.setPrefSize(370, 200);
        textArea_PostContent.setEditable(false);

        // Replies List
        listView_Replies.setLayoutX(410);
        listView_Replies.setLayoutY(300);
        listView_Replies.setPrefSize(370, 200);
        listView_Replies.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ControllerDiscussions.replySelected(newSelection);
        });

        // Custom ListCell for showing a blue dot for unread replies
        listView_Replies.setCellFactory(param -> new ListCell<Reply>() {
            /**
             * <p>
             * Method: updateItem(Reply reply, boolean empty)
             * </p>
             * 
             * <p>
             * Description: Adds a blue dot next to unread replies in the list view.
             * </p>
             * 
             * @param reply is the reply object
             * 
             * @param empty is a boolean indicating whether this cell is empty; true if the
             *              cell should be cleared
             * 
             */
            @Override
            protected void updateItem(Reply reply, boolean empty) {
                super.updateItem(reply, empty);
                if (empty || reply == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                HBox hbox = new HBox(5);
                Circle dot = new Circle(5, Color.BLUE);
                if (reply.isViewed())
                    dot.setVisible(false);

                // current session info
                String currentUser = (ViewDiscussions.theUser == null) ? null : ViewDiscussions.theUser.getUserName();
                boolean isStaffOrAdmin = (ViewDiscussions.theUser != null)
                        && (ViewDiscussions.theUser.getAdminRole() || ViewDiscussions.theUser.getNewStaff());

                boolean isPrivate = "private".equalsIgnoreCase(reply.getVisibility());
                boolean isHidden = !reply.isVisible();

                String labelText;
                if (isHidden && isStaffOrAdmin) {
                    // Hidden reply visible to staff/admin (keep original behavior)
                    labelText = reply.getAuthorUsername() + ": " + reply.getContent() + " [Hidden by Staff]";
                } else if (isPrivate) {
                    // Private feedback: decide whether current session can see the content
                    boolean canSeePrivateContent = reply.isVisibleTo(currentUser);
                    if (canSeePrivateContent) {
                        // show content but mark as private
                        labelText = reply.getAuthorUsername() + ": " + reply.getContent() + " [private feedback]";
                    } else {
                        // do NOT show the private content to unauthorized users — show a neutral label
                        labelText = reply.getAuthorUsername() + ": " + "[private feedback]";
                    }
                } else {
                    // Normal public reply
                    labelText = reply.getAuthorUsername() + ": " + reply.getContent();
                }

                hbox.getChildren().addAll(dot, new Label(labelText));
                setGraphic(hbox);
            }
        });

        // Buttons
        setupButtonUI(button_CreatePost, "Dialog", 14, 150, Pos.CENTER, 20, 520);
        button_CreatePost.setOnAction(event -> ControllerDiscussions.createPost());

        setupButtonUI(button_EditPost, "Dialog", 14, 150, Pos.CENTER, 170, 520);
        button_EditPost.setOnAction(event -> ControllerDiscussions.editPost());

        setupButtonUI(button_DeletePost, "Dialog", 14, 150, Pos.CENTER, 320, 520);
        button_DeletePost.setOnAction(event -> ControllerDiscussions.deletePost());

        setupButtonUI(button_AddReply, "Dialog", 14, 150, Pos.CENTER, 470, 520);
        button_AddReply.setOnAction(event -> ControllerDiscussions.addReply());

        setupButtonUI(button_EditReply, "Dialog", 14, 150, Pos.CENTER, 20, 560);
        button_EditReply.setOnAction(event -> ControllerDiscussions.editReply());

        setupButtonUI(button_DeleteReply, "Dialog", 14, 150, Pos.CENTER, 170, 560);
        button_DeleteReply.setOnAction(event -> ControllerDiscussions.deleteReply());

        setupButtonUI(button_Return, "Dialog", 14, 150, Pos.CENTER, 620, 520);
        button_Return.setOnAction(event -> ControllerDiscussions.returnToHome());

        // Normal buttons (everyone sees these)
        theRootPane.getChildren().addAll(
                label_PageTitle, comboBox_Threads, textField_Search, button_Search,
                button_MyPosts, button_Unread, button_UnreadReplies,
                listView_Posts, label_PostSummary, textArea_PostContent,
                listView_Replies, label_ReplySummary,
                button_CreatePost, button_EditPost, button_DeletePost,
                button_AddReply, button_EditReply, button_DeleteReply,
                button_Return);

        // They will be shown/hidden per user in displayDiscussions
        setupButtonUI(button_ToggleVisibility, "Dialog", 14, 150, Pos.CENTER, 320, 560);
        button_ToggleVisibility.setOnAction(event -> ControllerDiscussions.toggleVisibilityForSelection());

        setupButtonUI(button_FlagContent, "Dialog", 14, 150, Pos.CENTER, 470, 560);
        button_FlagContent.setOnAction(event -> ControllerDiscussions.flagSelectedContent());

        theRootPane.getChildren().addAll(button_ToggleVisibility, button_FlagContent);
    }

    /**
     * <p>
     * Method: void setupLabelUI(Label l, String ff, double f, double w, Pos p,
     * double x, double y)
     * </p>
     * <p>
     * Description: Sets up label UI elements.
     * </p>
     * 
     * @param l  is the label object being configured
     * @param ff is the font family name used for the label text
     * @param f  is the font size of the label text
     * @param w  is the minimum width of the label
     * @param p  is the Pos alignment of the label text within the button
     * @param x  is the X coordinate for the label's position on the pane
     * @param y  is the Y coordinate for the label's position on the pane
     */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**
     * <p>
     * Method: void setupButtonUI(Button b, String ff, double f, double w, Pos p,
     * double x, double y)
     * </p>
     * 
     * <p>
     * Description: Sets up button UI elements.
     * </p>
     * 
     * @param b  is the button object being configured
     * @param ff is the font family name used for the button text
     * @param f  is the font size of the button text
     * @param w  is the minimum width of the button
     * @param p  is the Pos alignment of the button text within the button
     * @param x  is the X coordinate for the button’s position on the pane
     * @param y  is the Y coordinate for the button’s position on the pane
     * 
     */
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }
}
