// BOLD CHANGE
package guiDiscussions;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import guiStudent.ViewStudentHome;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p> Title: ControllerDiscussions Class </p>
 *
 * <p> Description: This class handles the logic for the discussion forum,
 * including creating, editing, and deleting posts and replies. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Kushal Gadamsetty
 *
 * @version 1.01	2025-10-18 Implemented CRUD logic
 */
public class ControllerDiscussions {

    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**
     * Initializes the view by loading all posts from the database into the ListView.
     */
    protected static void initializeView() {
        List<Post> posts = theDatabase.getAllPosts();
        ObservableList<Post> observablePosts = FXCollections.observableArrayList(posts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);

        // Clear the selection and text areas
        ViewDiscussions.listView_Posts.getSelectionModel().clearSelection();
        ViewDiscussions.textArea_PostContent.clear();
        ViewDiscussions.listView_Replies.getItems().clear();
    }

    /**
     * Handles the event when a post is selected from the list. It displays the post's
     * content and loads its replies.
     * @param selectedPost The post that was selected by the user.
     */
    protected static void postSelected(Post selectedPost) {
        if (selectedPost == null) {
            ViewDiscussions.textArea_PostContent.clear();
            ViewDiscussions.listView_Replies.getItems().clear();
            return;
        }

        // Display the post content
        String postDetails = "Title: " + selectedPost.getTitle() + "\n" +
                             "Author: " + selectedPost.getAuthorUsername() + "\n\n" +
                             selectedPost.getContent();
        ViewDiscussions.textArea_PostContent.setText(postDetails);

        // Fetch and display replies
        List<Reply> replies = theDatabase.getRepliesForPost(selectedPost.getPostID());
        List<String> formattedReplies = replies.stream()
                .map(reply -> reply.getAuthorUsername() + ": " + reply.getContent())
                .collect(Collectors.toList());
        ViewDiscussions.listView_Replies.setItems(FXCollections.observableArrayList(formattedReplies));
    }

    /**
     * Guides the user through creating a new post and saves it to the database.
     */
    protected static void createPost() {
        TextInputDialog titleDialog = new TextInputDialog();
        titleDialog.setTitle("Create New Post");
        titleDialog.setHeaderText("Enter the title for your new post.");
        titleDialog.setContentText("Title:");
        Optional<String> titleResult = titleDialog.showAndWait();

        if (titleResult.isPresent() && !titleResult.get().trim().isEmpty()) {
            String title = titleResult.get();

            // Use a TextArea in a custom dialog for multi-line content input
            TextInputDialog contentDialog = new TextInputDialog();
            contentDialog.setTitle("Create New Post");
            contentDialog.setHeaderText("Enter the content for your post.");
            contentDialog.setContentText("Content:");
            
            // Customize dialog to use a TextArea
            TextArea textArea = new TextArea();
            DialogPane dialogPane = contentDialog.getDialogPane();
            dialogPane.setContent(textArea);

            Optional<String> contentResult = contentDialog.showAndWait();

            if (contentResult.isPresent() && !textArea.getText().trim().isEmpty()) {
                String content = textArea.getText();
                Post newPost = new Post(0, ViewDiscussions.theUser.getUserName(), title, content);
                theDatabase.createPost(newPost);
                initializeView(); // Refresh the view to show the new post
            } else {
                showError("Post content cannot be empty.");
            }
        } else {
            showError("Post title cannot be empty.");
        }
    }

    /**
     * Allows the author of a post to edit its title and content.
     */
    protected static void editPost() {
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showError("Please select a post to edit.");
            return;
        }

        if (!selectedPost.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName())) {
            showError("You can only edit your own posts.");
            return;
        }

        // Dialog for editing title
        TextInputDialog titleDialog = new TextInputDialog(selectedPost.getTitle());
        titleDialog.setTitle("Edit Post");
        titleDialog.setHeaderText("Update the title of your post.");
        titleDialog.setContentText("Title:");
        Optional<String> titleResult = titleDialog.showAndWait();

        if (titleResult.isPresent() && !titleResult.get().trim().isEmpty()) {
            selectedPost.setTitle(titleResult.get());

            // Dialog for editing content
            TextInputDialog contentDialog = new TextInputDialog(selectedPost.getContent());
            contentDialog.setTitle("Edit Post");
            contentDialog.setHeaderText("Update the content of your post.");
            contentDialog.setContentText("Content:");

            TextArea textArea = new TextArea(selectedPost.getContent());
            DialogPane dialogPane = contentDialog.getDialogPane();
            dialogPane.setContent(textArea);

            Optional<String> contentResult = contentDialog.showAndWait();

            if (contentResult.isPresent() && !textArea.getText().trim().isEmpty()) {
                selectedPost.setContent(textArea.getText());
                theDatabase.updatePost(selectedPost);
                initializeView(); // Refresh view
            } else {
                showError("Post content cannot be empty.");
            }
        }
    }


    /**
     * Deletes a selected post after user confirmation.
     */
    protected static void deletePost() {
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showError("Please select a post to delete.");
            return;
        }

        if (!selectedPost.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName())) {
            showError("You can only delete your own posts.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this post?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            theDatabase.deletePost(selectedPost.getPostID());
            initializeView(); // Refresh the view
        }
    }

    /**
     * Allows a user to add a reply to the selected post.
     */
    protected static void addReply() {
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showError("Please select a post to reply to.");
            return;
        }

        TextInputDialog replyDialog = new TextInputDialog();
        replyDialog.setTitle("Add Reply");
        replyDialog.setHeaderText("Enter your reply for the post: " + selectedPost.getTitle());
        replyDialog.setContentText("Reply:");
        Optional<String> result = replyDialog.showAndWait();

        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String content = result.get();
            Reply newReply = new Reply(0, selectedPost.getPostID(), ViewDiscussions.theUser.getUserName(), content);
            theDatabase.createReply(newReply);
            postSelected(selectedPost); // Refresh the replies for the current post
        } else {
            showError("Reply content cannot be empty.");
        }
    }

    /**
     * Returns the user to their home page.
     */
    protected static void returnToHome() {
        ViewStudentHome.displayStudentHome(ViewDiscussions.theStage, ViewDiscussions.theUser);
    }

    /**
     * A helper method to quickly show an error alert.
     * @param message The error message to display.
     */
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}