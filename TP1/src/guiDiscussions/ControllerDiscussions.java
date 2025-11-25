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

	// variable for database
    private static Database theDatabase = applicationMain.FoundationsMain.database;
    
    /*****
     * <p> Method: boolean isCurrentUserStudent() </p>
     * 
     * <p> Description: Check if User is a student. </p>
     * 
     */
    private static boolean isCurrentUserStudent() {
        return applicationMain.FoundationsMain.activeHomePage == 2;
    }

    /*****
     * <p> Method: boolean isCurrentUserStaffOrAdmin() </p>
     * 
     * <p> Description: Check if User is a staff or admin (authorized users). </p>
     * 
     */
    private static boolean isCurrentUserStaffOrAdmin() {
    	int role = applicationMain.FoundationsMain.activeHomePage;
        return (role == 1 || role == 3);
    }

    /*****
     * <p> Method: void initializeView() </p>
     * 
     * <p> Description: Initializes the view by loading all posts from the database into the ListView. </p>
     * 
     */
    protected static void initializeView() {
    	// loads all the posts
        List<Post> posts = theDatabase.getAllPosts(ViewDiscussions.theUser.getUserName());

        // Students should only see visible posts
        if (isCurrentUserStudent()) {
            posts = posts.stream()
                    .filter(Post::isVisible)
                    .collect(Collectors.toList());
        }

        ObservableList<Post> observablePosts = FXCollections.observableArrayList(posts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);

        // Clear selection
        ViewDiscussions.listView_Posts.getSelectionModel().clearSelection();
        ViewDiscussions.textArea_PostContent.clear();
        ViewDiscussions.listView_Replies.getItems().clear();

        updatePostSummary();
        updateReplySummary();
    }

    /**
     * <p> Method: void postSelected(Post selectedPost) </p>
     * 
     * <p> Description:Handles the event when a post is selected from the list. It displays the post's
     * content, loads its replies, and marks the post as read. </p>
     * 
     * @param selectedPost The post that was selected by the user.
     */
    protected static void postSelected(Post selectedPost) {
        if (selectedPost == null) {
            ViewDiscussions.textArea_PostContent.clear();
            ViewDiscussions.listView_Replies.getItems().clear();
            return;
        }

        // Unauthorized access handling for students
        if (!selectedPost.isVisible() && isCurrentUserStudent()) {
            ViewDiscussions.textArea_PostContent.setText("Content Not Available");
            ViewDiscussions.listView_Replies.getItems().clear();
            // Don't mark as read since student shouldn't have access
            return;
        }
        
        theDatabase.markPostAsRead(selectedPost.getPostID(), ViewDiscussions.theUser.getUserName());
        selectedPost.setViewed(true);
        ViewDiscussions.listView_Posts.refresh();

        if (selectedPost.isDeleted()) {
            ViewDiscussions.textArea_PostContent.setText("Title: deleted\nAuthor: " + 
                selectedPost.getAuthorUsername() + "\nThread: " + selectedPost.getThread() + 
                "\n\ndeleted");
            // Don't clear replies - keep them displayed
            List<Reply> replies = theDatabase.getRepliesForPost(selectedPost.getPostID(), ViewDiscussions.theUser.getUserName());
            
            if (isCurrentUserStudent()) {
                replies = replies.stream()
                        .filter(Reply::isVisible)
                        .collect(java.util.stream.Collectors.toList());
            }
            
            ObservableList<Reply> observableReplies = FXCollections.observableArrayList(replies);
            ViewDiscussions.listView_Replies.setItems(observableReplies);
            updateReplySummary();
            updatePostSummary();
            return;
        }

        // Display the post content
        String postDetails = "";
        
        // label for staff/admin when post is hidden
        if (!selectedPost.isVisible() && isCurrentUserStaffOrAdmin()) {
            postDetails += "[Hidden by Staff/Admin]\n\n";
        }
        
        postDetails += "Title: " + selectedPost.getTitle() + "\n" +
                             "Author: " + selectedPost.getAuthorUsername() + "\n" +
                             "Thread: " + selectedPost.getThread() + "\n\n" +
                             selectedPost.getContent();
        ViewDiscussions.textArea_PostContent.setText(postDetails);

        // Fetch and display replies of a selected post
        List<Reply> replies = theDatabase.getRepliesForPost(selectedPost.getPostID(), ViewDiscussions.theUser.getUserName());
        
        // Students cannot see hidden replies
        if (isCurrentUserStudent()) {
            replies = replies.stream()
                    .filter(Reply::isVisible)
                    .collect(Collectors.toList());
        }
        
        ObservableList<Reply> observableReplies = FXCollections.observableArrayList(replies);
        ViewDiscussions.listView_Replies.setItems(observableReplies);
        
        updateReplySummary();
        updatePostSummary();
        
    }

    /**
     * <p> Method: void replySelected(Reply selectedReply) </p>
     * 
     * <p> Description: Handles the event when a reply is selected from the list. It marks the reply as read. </p>
     * 
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
                
                // Visbility filtering
                if (isCurrentUserStudent()) {
                    posts = posts.stream()
                            .filter(Post::isVisible)
                            .collect(Collectors.toList());
                }
                
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
     * <p> Method: void createPost() </p>
     * 
     * <p> Description: Guides the user through creating a new post and saves it to the database. </p>
     * 
     */
    protected static void createPost() {
        TextInputDialog titleDialog = new TextInputDialog();
        titleDialog.setTitle("Create New Post");
        titleDialog.setHeaderText("Enter the title for your new post.");
        titleDialog.setContentText("Title:");
        Optional<String> titleResult = titleDialog.showAndWait();

        //Conduct input validation to make sure title content is not empty
        if (titleResult.isPresent() && !titleResult.get().trim().isEmpty()) {
            String title = titleResult.get();

            // Dialog for selecting a thread
            List<String> threadChoices = Arrays.asList("General", "Homework", "Exams");
            ChoiceDialog<String> threadDialog = new ChoiceDialog<>("General", threadChoices);
            threadDialog.setTitle("Create New Post");
            threadDialog.setHeaderText("Select a thread for your post.");
            threadDialog.setContentText("Thread:");
            Optional<String> threadResult = threadDialog.showAndWait();

            //Default to General Thread
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

            //Conduct input validation to make sure post content is not empty
            if (contentResult.isPresent() && !textArea.getText().trim().isEmpty()) {
                String content = textArea.getText();
                Post newPost = new Post(0, ViewDiscussions.theUser.getUserName(), title, content, thread, false, false, 0, 0);
                theDatabase.create(newPost); //change
                initializeView(); // This already calls updatePostSummary()
            } else {
                showError("Post content cannot be empty.");
            }
        } else {
            showError("Post title cannot be empty.");
        }
    }

    /**
     * <p> Method:  void editPost() </p>
     * 
     * <p> Description: Allows the author of a post to edit its title and content. </p>
     * 
     */
    protected static void editPost() {
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showError("Please select a post to edit.");
            return;
        }
        // Checks to see if user trying to update post is post author, otherwise throws error.
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

      //Conduct input validation to make sure post title is not empty
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

          //Conduct input validation to make sure post content is not empty
            if (contentResult.isPresent() && !textArea.getText().trim().isEmpty()) {
                selectedPost.setContent(textArea.getText());
                theDatabase.update(selectedPost); //change
                initializeView(); // This already calls updatePostSummary()
            } else {
                showError("Post content cannot be empty.");
            }
        }
    }


    /**
     * <p> Method: void deletePost() </p>
     * 
     * <p> Description: Deletes a selected post after user confirmation. </p>
     * 
     */
    protected static void deletePost() {
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showError("Please select a post to delete.");
            return;
        }
        // Checks to see if user trying to delete post is post author, otherwise throws error.
        if (!selectedPost.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName())) {
            showError("You can only delete your own posts.");
            return;
        }

        //Create a message for confirmation of deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this post?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	theDatabase.delete(selectedPost); //change
            initializeView(); // Refresh the view
        }
    }

    /**
     * <p> Method:  void addReply()</p>
     * 
     * <p> Description: Allows a user to add a reply to the selected post.</p>
     * 
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

        //Conduct input validation to make sure reply content is not empty
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String content = result.get();
            Reply newReply = new Reply(0, selectedPost.getPostID(), ViewDiscussions.theUser.getUserName(), content);
            theDatabase.create(newReply); //change
            postSelected(selectedPost); // This now calls updateReplySummary()
        } else {
            showError("Reply content cannot be empty.");
        }
    }

    /**
     * <p> Method: void searchPosts() </p>
     * 
     * <p> Description: Searches for posts based on the keyword and thread selected in the UI.</p>
     * 
     */
    protected static void searchPosts() {
        String keyword = ViewDiscussions.textField_Search.getText();
        String thread = ViewDiscussions.comboBox_Threads.getValue();

        // Search by keyword and by keyword+thread
        List<Post> posts = theDatabase.searchPosts(
                keyword,
                thread,
                ViewDiscussions.theUser.getUserName()
        );

        // Students should not see hidden posts in search results
        boolean isStudent = ViewDiscussions.theUser != null
                && ViewDiscussions.theUser.getNewStudent();  // or whatever your "student" flag is

        if (isStudent) {
            posts = posts.stream()
                    .filter(Post::isVisible)
                    .collect(java.util.stream.Collectors.toList());
        }

        ObservableList<Post> observablePosts = FXCollections.observableArrayList(posts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);

        // Updates total/unread post counts after search filter
        updatePostSummary();
        updateReplySummary();
    }

    
    

    /**
     * <p> Method: void viewMyPosts() </p>
     * 
     * <p> Description: Filters the posts to show only those created by the current user.</p>
     * 
     */
    protected static void viewMyPosts() {
        // Load all posts visible
        List<Post> allPosts = theDatabase.getAllPosts(ViewDiscussions.theUser.getUserName());
        List<Post> myPosts = allPosts.stream()
        		//filter based on if the post's author username is the same as the current logged in user
                .filter(post -> post.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName()))
                .collect(Collectors.toList());

        if (isCurrentUserStudent()) {
            myPosts = myPosts.stream()
                    .filter(Post::isVisible)
                    .collect(Collectors.toList());
        }

        ObservableList<Post> observablePosts = FXCollections.observableArrayList(myPosts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);
        updatePostSummary();
    }

    /**
     * <p> Method: void viewUnreadPosts()</p>
     * 
     * <p> Description: Filters the posts to show only those that are unread.</p>
     * 
     */
    protected static void viewUnreadPosts() {
    	// Load all posts visible
        List<Post> allPosts = theDatabase.getAllPosts(ViewDiscussions.theUser.getUserName());
        List<Post> unreadPosts = allPosts.stream()
        		//filter based on if the post has been read yet using the viewed attribute
                .filter(post -> !post.isViewed())
                .collect(Collectors.toList());
        if (isCurrentUserStudent()) {
            unreadPosts = unreadPosts.stream()
                    .filter(Post::isVisible)
                    .collect(Collectors.toList());
        }
        ObservableList<Post> observablePosts = FXCollections.observableArrayList(unreadPosts);
        ViewDiscussions.listView_Posts.setItems(observablePosts);
        
        updatePostSummary();
    }

    /**
     * <p> Method: void viewUnreadReplies() </p>
     * 
     * <p> Description: Filters the replies for the currently selected post to show only unread replies.
     * If no post is selected, an error message is displayed to the user. </p>
     * 
     */
    protected static void viewUnreadReplies() {
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedPost == null) {
            showError("Please select a post first to view its unread replies.");
            return;
        }
        // Fetch all replies for the selected post
        List<Reply> allReplies = theDatabase.getRepliesForPost(
                selectedPost.getPostID(),
                ViewDiscussions.theUser.getUserName()
        );
        // Only unread replies
        List<Reply> unreadReplies = allReplies.stream()
                .filter(reply -> !reply.isViewed())
                .collect(Collectors.toList());
        // Students must not see hidden replies
        if (isCurrentUserStudent()) {
            unreadReplies = unreadReplies.stream()
                    .filter(Reply::isVisible)
                    .collect(Collectors.toList());
        }
        // Update list view to only show unread
        ObservableList<Reply> observableReplies = FXCollections.observableArrayList(unreadReplies);
        ViewDiscussions.listView_Replies.setItems(observableReplies);
        updatePostSummary();
        updateReplySummary();
    }

    /**
     * <p> Method: void editReply()</p>
     * 
     * <p> Description: Allows the author of a reply to edit its content. </p>
     * 
     */
    protected static void editReply() {
        Reply selectedReply = ViewDiscussions.listView_Replies.getSelectionModel().getSelectedItem();
        if (selectedReply == null) {
            showError("Please select a reply to edit.");
            return;
        }
     // Checks to see if user trying to update reply is reply author, otherwise throws error.
        if (!selectedReply.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName())) {
            showError("You can only edit your own replies.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedReply.getContent());
        dialog.setTitle("Edit Reply");
        dialog.setHeaderText("Update the content of your reply.");
        dialog.setContentText("Reply:");

        Optional<String> result = dialog.showAndWait();
        //Conduct input validation to make sure reply content is not empty
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            selectedReply.setContent(result.get());
            theDatabase.update(selectedReply); //change
            postSelected(ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem()); //reload replies
        } else {
            showError("Reply content cannot be empty.");
        }
        updatePostSummary();
        updateReplySummary();
    }

    /**
     * <p> Method: void deleteReply()</p>
     * 
     * <p> Description: Deletes a selected reply after user confirmation.</p>
     * 
     */
    protected static void deleteReply() {
        Reply selectedReply = ViewDiscussions.listView_Replies.getSelectionModel().getSelectedItem();
        if (selectedReply == null) {
            showError("Please select a reply to delete.");
            return;
        }

        // Checks to see if user trying to update reply is reply author, otherwise throws error.
        if (!selectedReply.getAuthorUsername().equals(ViewDiscussions.theUser.getUserName())) {
            showError("You can only delete your own replies.");
            return;
        }

        //Create a message for confirmation of reply deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Are you sure you want to delete this reply?");
        confirmation.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        	theDatabase.delete(selectedReply); //change
            postSelected(ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem()); // reload replies
        }
        updatePostSummary();
        updateReplySummary();
    }

    /**
     * <p> Method: void returnToHome()</p>
     * 
     * <p> Description: Returns the user to their home page. </p>
     * 
     */
    protected static void returnToHome() {
        ViewStudentHome.displayStudentHome(ViewDiscussions.theStage, ViewDiscussions.theUser);
    }

    /**
     * <p> Method: void showError(String message)</p>
     * 
     * <p> Description: A helper method to quickly show an error alert. </p>
     * 
     * @param message The error message to display.
     */
    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * <p> Method: void updatePostSummary() </p>
     * 
     * <p> Description: Updates the discussions view summary with the total and unread post counts,
     * or shows a default message if no posts are available. </p>
     * 
     */
    private static void updatePostSummary() {
    	// Get currently displayed posts (can be all, filtered, or searched)
        List<Post> currentPosts = ViewDiscussions.listView_Posts.getItems();
        
        // If the list is empty, display default text
        if (currentPosts == null || currentPosts.isEmpty()) {
            ViewDiscussions.label_PostSummary.setText("No posts to display");
            return;
        }
        
        // Count how many of those posts are unread
        long unreadCount = currentPosts.stream()
                .filter(post -> !post.isViewed())
                .count();
        // Display the total and unread counts
        ViewDiscussions.label_PostSummary.setText(
            String.format("Showing %d posts (%d unread)", 
                currentPosts.size(), unreadCount)
        );
    }
    
    
    /**
     * <p> Method: void updateReplySummary()</p>
     * 
     * <p> Description: Updates the reply summary label with the total and unread reply counts,
     * or clears/displays a default message when no replies are available.</p>
     * 
     */
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
        
        // Count how many of replies posts are unread
        long unreadCount = currentReplies.stream()
                .filter(reply -> !reply.isViewed())
                .count();
        // Display the total and unread counts
        ViewDiscussions.label_ReplySummary.setText(
            String.format("%d replies (%d unread)", 
                currentReplies.size(), unreadCount)
        );
    }
    
    /**
     * <p> Method: void toggleVisibilityForSelection()</p>
     * 
     * <p> Description: Implements functionality of toggle visibility (hide/unhide) for selected post/reply.</p>
     * 
     */
    protected static void toggleVisibilityForSelection() {
        if (!isCurrentUserStaffOrAdmin()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("You do not have permission to moderate content.");
            alert.showAndWait();
            return;
        }
        Reply selectedReply = ViewDiscussions.listView_Replies.getSelectionModel().getSelectedItem();
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedReply == null && selectedPost == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please select a post or reply to hide/unhide.");
            alert.showAndWait();
            return;
        }
        // Ask for a reason (required)
        TextInputDialog reasonDialog = new TextInputDialog();
        reasonDialog.setTitle("Moderation Reason");
        reasonDialog.setHeaderText("Enter the reason for this visibility change.");
        reasonDialog.setContentText("Reason:");

        Optional<String> reasonResult = reasonDialog.showAndWait();
        String reason = (reasonResult.isPresent() ? reasonResult.get().trim() : "");

        if (reason.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Must Have Reason to Complete Action");
            alert.showAndWait();
            return;
        }
        String currentUser = ViewDiscussions.theUser.getUserName();
        if (selectedReply != null) {
            boolean currentlyVisible = selectedReply.isVisible();
            int replyID = selectedReply.getReplyID();
            int parentPostID = selectedReply.getPostID();
            if (currentlyVisible) {
                theDatabase.hideReply(replyID, parentPostID, currentUser, reason);
                selectedReply.setVisible(false);
            } else {
                theDatabase.unhideReply(replyID, parentPostID, currentUser, reason);
                selectedReply.setVisible(true);
            }
            ViewDiscussions.listView_Replies.refresh();
        } else if (selectedPost != null) {
            boolean currentlyVisible = selectedPost.isVisible();
            int postID = selectedPost.getPostID();
            if (currentlyVisible) {
                theDatabase.hidePost(postID, currentUser, reason);
                selectedPost.setVisible(false);
            } else {
                theDatabase.unhidePost(postID, currentUser, reason);
                selectedPost.setVisible(true);
            }
            // Re-apply filtering and refresh
            initializeView();
        }
    }

    
    /**
     * <p> Method: void flagSelectedContent()</p>
     * 
     * <p> Description: Flag content with a required reason.</p>
     * 
     */
    protected static void flagSelectedContent() {
        if (!isCurrentUserStaffOrAdmin()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("You do not have permission to moderate content.");
            alert.showAndWait();
            return;
        }

        Reply selectedReply = ViewDiscussions.listView_Replies.getSelectionModel().getSelectedItem();
        Post selectedPost = ViewDiscussions.listView_Posts.getSelectionModel().getSelectedItem();
        if (selectedReply == null && selectedPost == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Please select a post or reply to flag.");
            alert.showAndWait();
            return;
        }
        TextInputDialog reasonDialog = new TextInputDialog();
        reasonDialog.setTitle("Flag Content");
        reasonDialog.setHeaderText("Provide a reason for flagging this content.");
        reasonDialog.setContentText("Reason:");

        Optional<String> reasonResult = reasonDialog.showAndWait();
        String reason = (reasonResult.isPresent() ? reasonResult.get().trim() : "");
        if (reason.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Must Have Reason to Complete Action");
            alert.showAndWait();
            return;
        }

        String currentUser = ViewDiscussions.theUser.getUserName();
        if (selectedReply != null) {
            theDatabase.flagReply(selectedReply.getReplyID(), selectedReply.getPostID(), currentUser, reason);
        } else if (selectedPost != null) {
            theDatabase.flagPost(selectedPost.getPostID(), currentUser, reason);
        }

        Alert ok = new Alert(Alert.AlertType.INFORMATION);
        ok.setHeaderText(null);
        ok.setContentText("Content has been flagged.");
        ok.showAndWait();
    }

}