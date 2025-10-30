// BOLD CHANGE:
package guiDiscussions;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import guiStudent.ViewStudentHome;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
 * @version 1.08	2025-10-20 Corrected UI refresh exception
 */
public class ControllerDiscussions {

    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**
     * Initializes the view by loading all posts from the database into the ListView.
     */
    protected static void initializeView() {
        List<Post> posts = theDatabase.getAllPosts(ViewDiscussions.theUser.getUserName());
        ObservableList<Post> observablePosts = FXCollections.observableArrayList(posts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);

        // Clear the selection and text areas
        ViewDiscussions.listView_Posts.getSelectionModel().clearSelection();
        ViewDiscussions.textArea_PostContent.clear();
        ViewDiscussions.listView_Replies.getItems().clear();
        
        updatePostSummary();
    }

    /**
     * Handles the event when a post is selected from the list. It displays the post's
     * content, loads its replies, and marks the post as read.
     * @param selectedPost The post that was selected by the user.
     */
    protected static void postSelected(Post selectedPost) {
        if (selectedPost == null) {
            ViewDiscussions.textArea_PostContent.clear();
            ViewDiscussions.listView_Replies.getItems().clear();
            return;
        }

        theDatabase.markPostAsRead(selectedPost.getPostID(), ViewDiscussions.theUser.getUserName());
        selectedPost.setViewed(true);
        ViewDiscussions.listView_Posts.refresh();

        if (selectedPost.isDeleted()) {
            ViewDiscussions.textArea_PostContent.setText("This post has been deleted.");
            ViewDiscussions.listView_Replies.getItems().clear();
            return;
        }

        // Display the post content
        String postDetails = "Title: " + selectedPost.getTitle() + "\n" +
                             "Author: " + selectedPost.getAuthorUsername() + "\n" +
                             "Thread: " + selectedPost.getThread() + "\n\n" +
                             selectedPost.getContent();
        ViewDiscussions.textArea_PostContent.setText(postDetails);

        // Fetch and display replies
        List<Reply> replies = theDatabase.getRepliesForPost(selectedPost.getPostID(), ViewDiscussions.theUser.getUserName());
        ObservableList<Reply> observableReplies = FXCollections.observableArrayList(replies);
        ViewDiscussions.listView_Replies.setItems(observableReplies);
        
        updateReplySummary();
    }

    /**
     * Handles the event when a reply is selected from the list. It marks the reply as read.
     * @param selectedReply The reply that was selected by the user.
     */
    protected static void replySelected(Reply selectedReply) {
        if (selectedReply != null && !selectedReply.isViewed()) {
            theDatabase.markReplyAsRead(selectedReply.getReplyID(), ViewDiscussions.theUser.getUserName());
            selectedReply.setViewed(true);
            ViewDiscussions.listView_Replies.refresh();
            
            // By using Platform.runLater, we schedule the post list update to happen
            // after the current UI event is finished, preventing the crash.
            Platform.runLater(() -> {
                int selectedIndex = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedIndex();
                List<Post> posts = theDatabase.getAllPosts(ViewDiscussions.theUser.getUserName());
                ViewDiscussions.listView_Posts.setItems(FXCollections.observableArrayList(posts));
                if (selectedIndex != -1) {
                    ViewDiscussions.listView_Posts.getSelectionModel().select(selectedIndex);
                }
                
                updatePostSummary();
                updateReplySummary();
            });
        }
        
        
        
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

            // Dialog for selecting a thread
            List<String> threadChoices = Arrays.asList("General", "Homework", "Exams");
            ChoiceDialog<String> threadDialog = new ChoiceDialog<>("General", threadChoices);
            threadDialog.setTitle("Create New Post");
            threadDialog.setHeaderText("Select a thread for your post.");
            threadDialog.setContentText("Thread:");
            Optional<String> threadResult = threadDialog.showAndWait();

            String thread = threadResult.orElse("General");

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
                Post newPost = new Post(0, ViewDiscussions.theUser.getUserName(), title, content, thread, false, false, 0, 0);
                theDatabase.createPost(newPost);
                initializeView(); // This already calls updatePostSummary()
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
                initializeView(); // This already calls updatePostSummary()
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
            postSelected(selectedPost); // This now calls updateReplySummary()
        } else {
            showError("Reply content cannot be empty.");
        }
    }

    /**
     * Searches for posts based on the keyword and thread selected in the UI.
     */
    protected static void searchPosts() {
        String keyword = ViewDiscussions.textField_Search.getText();
        String thread = ViewDiscussions.comboBox_Threads.getValue();
        List<Post> posts = theDatabase.searchPosts(keyword, thread, ViewDiscussions.theUser.getUserName());
        ObservableList<Post> observablePosts = FXCollections.observableArrayList(posts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);
    }

    /**
     * Filters the posts to show only those created by the current user.
     */
    protected static void viewMyPosts() {
        List<Post> allPosts = theDatabase.getAllPosts(ViewDiscussions.theUser.getUserName());
        List<Post> myPosts = allPosts.stream()
                .filter(post -> post.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName()))
                .collect(Collectors.toList());
        ObservableList<Post> observablePosts = FXCollections.observableArrayList(myPosts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);
        updatePostSummary();
    }

    /**
     * Filters the posts to show only those that are unread.
     */
    protected static void viewUnreadPosts() {
        List<Post> allPosts = theDatabase.getAllPosts(ViewDiscussions.theUser.getUserName());
        List<Post> unreadPosts = allPosts.stream()
                .filter(post -> !post.isViewed())
                .collect(Collectors.toList());
        ObservableList<Post> observablePosts = FXCollections.observableArrayList(unreadPosts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);
        
        updatePostSummary();
    }

    /**
     * NEW METHOD: Filters the replies for the currently selected post to show only unread replies.
     * If no post is selected, an error message is displayed to the user.
     */
    protected static void viewUnreadReplies() {
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showError("Please select a post first to view its unread replies.");
            return;
        }
        // Fetch all replies for the selected post
        List<Reply> allReplies = theDatabase.getRepliesForPost(selectedPost.getPostID(), ViewDiscussions.theUser.getUserName());
        // Filter to only include replies that have not been viewed by the current user
        List<Reply> unreadReplies = allReplies.stream()
                .filter(reply -> !reply.isViewed())
                .collect(Collectors.toList());
        // Update the replies ListView to display only the unread replies
        ObservableList<Reply> observableReplies = FXCollections.observableArrayList(unreadReplies);
        ViewDiscussions.listView_Replies.setItems(observableReplies);
        
        updatePostSummary();
    }

    /**
     * Allows the author of a reply to edit its content.
     */
    protected static void editReply() {
        Reply selectedReply = ViewDiscussions.listView_Replies.getSelectionModel().getSelectedItem();
        if (selectedReply == null) {
            showError("Please select a reply to edit.");
            return;
        }

        if (!selectedReply.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName())) {
            showError("You can only edit your own replies.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedReply.getContent());
        dialog.setTitle("Edit Reply");
        dialog.setHeaderText("Update the content of your reply.");
        dialog.setContentText("Reply:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            selectedReply.setContent(result.get());
            theDatabase.updateReply(selectedReply);
            postSelected(ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem()); // This now calls updateReplySummary()
        } else {
            showError("Reply content cannot be empty.");
        }
    }

    /**
     * Deletes a selected reply after user confirmation.
     */
    protected static void deleteReply() {
        Reply selectedReply = ViewDiscussions.listView_Replies.getSelectionModel().getSelectedItem();
        if (selectedReply == null) {
            showError("Please select a reply to delete.");
            return;
        }

        if (!selectedReply.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName())) {
            showError("You can only delete your own replies.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this reply?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            theDatabase.deleteReply(selectedReply.getReplyID());
            postSelected(ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem()); // This now calls updateReplySummary()
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
    
    private static void updatePostSummary() {
        List<Post> currentPosts = ViewDiscussions.listView_Posts.getItems();
        if (currentPosts == null || currentPosts.isEmpty()) {
            ViewDiscussions.label_PostSummary.setText("No posts to display");
            return;
        }
        
        long unreadCount = currentPosts.stream()
                .filter(post -> !post.isViewed())
                .count();
        
        ViewDiscussions.label_PostSummary.setText(
            String.format("Showing %d posts (%d unread)", 
                currentPosts.size(), unreadCount)
        );
    }
    
    
    private static void updateReplySummary() {
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        
        if (selectedPost == null) {
            ViewDiscussions.label_ReplySummary.setText("");
            return;
        }
        
        List<Reply> currentReplies = ViewDiscussions.listView_Replies.getItems();
        if (currentReplies == null || currentReplies.isEmpty()) {
            ViewDiscussions.label_ReplySummary.setText("No replies");
            return;
        }
        
        long unreadCount = currentReplies.stream()
                .filter(reply -> !reply.isViewed())
                .count();
        
        ViewDiscussions.label_ReplySummary.setText(
            String.format("%d replies (%d unread)", 
                currentReplies.size(), unreadCount)
        );
    }
}