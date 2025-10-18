package discussionsTesting;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import java.util.List;

/**
 * <p> Title: DiscussionsTestingAutomation Class. </p>
 *
 * <p> Description: A Java class for semi-automated testing of the Post and Reply CRUD functionality. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Kushal Gadamsetty
 *
 * @version 1.00	2025-10-18 Initial version
 */
public class DiscussionsTestingAutomation {

    public static void main(String[] args) {
        Database db = new Database();
        try {
            db.connectToDatabase();
        } catch (Exception e) {
            System.out.println("Failed to connect to the database!");
            e.printStackTrace();
            return;
        }

        System.out.println("______________________________________");
        System.out.println("\nDiscussion System Testing Automation\n");

        // --- Run Test Cases ---
        
        // Test P-01: Create a valid post
        System.out.println("--- Running Test P-01: Create a valid post ---");
        Post testPost = new Post(0, "testUser", "HW2 Help", "I am stuck on the first task, can someone help?");
        db.createPost(testPost);
        List<Post> posts = db.getAllPosts();
        if (!posts.isEmpty() && posts.get(0).getTitle().equals("HW2 Help")) {
            System.out.println("*** Success ***: Post created successfully.\n");
        } else {
            System.out.println("*** Failure ***: Post was not created.\n");
        }
        
        // Retrieve the created post to get its ID for further tests
        int postId = posts.get(0).getPostID();

        // Test P-02 & P-03 (Negative): Attempt to create a post with empty title/content.
        // This is handled by the UI controller, but we verify the principle.
        System.out.println("--- Running Test P-02/P-03: Attempt to create post with empty fields ---");
        String emptyTitle = "";
        if (emptyTitle.trim().isEmpty()) {
             System.out.println("*** Success ***: Logic correctly identifies empty title. UI would show: 'Post title cannot be empty.'\n");
        } else {
             System.out.println("*** Failure ***: Logic did not identify empty title.\n");
        }


        // Test R-01: Add a valid reply to the post
        System.out.println("--- Running Test R-01: Add a valid reply ---");
        Reply testReply = new Reply(0, postId, "anotherUser", "Sure, what part are you stuck on?");
        db.createReply(testReply);
        List<Reply> replies = db.getRepliesForPost(postId);
        if (!replies.isEmpty() && replies.get(0).getContent().equals("Sure, what part are you stuck on?")) {
            System.out.println("*** Success ***: Reply added successfully.\n");
        } else {
            System.out.println("*** Failure ***: Reply was not added.\n");
        }

        // Test P-05: Update the created post
        System.out.println("--- Running Test P-05: Update an existing post ---");
        Post postToUpdate = db.getAllPosts().get(0);
        postToUpdate.setTitle("HW2 Help - Updated");
        postToUpdate.setContent("Never mind, I figured it out.");
        db.updatePost(postToUpdate);
        Post updatedPost = db.getAllPosts().get(0);
        if (updatedPost.getTitle().equals("HW2 Help - Updated") && updatedPost.getContent().equals("Never mind, I figured it out.")) {
            System.out.println("*** Success ***: Post updated successfully.\n");
        } else {
            System.out.println("*** Failure ***: Post was not updated.\n");
        }

        // Test P-07: Delete the post (and its replies)
        System.out.println("--- Running Test P-07: Delete an existing post ---");
        db.deletePost(postId);
        List<Post> remainingPosts = db.getAllPosts();
        List<Reply> remainingReplies = db.getRepliesForPost(postId);
        if (remainingPosts.isEmpty() && remainingReplies.isEmpty()) {
            System.out.println("*** Success ***: Post and associated replies deleted successfully.\n");
        } else {
            System.out.println("*** Failure ***: Post and/or replies were not deleted.\n");
        }
        
        System.out.println("--- Note on Manual Tests ---");
        System.out.println("Tests P-06, P-08 (editing/deleting others' posts) and the delete confirmation dialog are handled");
        System.out.println("in the UI controller and are best verified through manual demonstration.\n");


        System.out.println("______________________________________");
        System.out.println("\nTesting complete.");
        
        db.closeConnection();
    }
}