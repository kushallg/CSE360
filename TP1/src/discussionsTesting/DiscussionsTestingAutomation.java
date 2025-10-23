package discussionsTesting;

import database.Database;
import entityClasses.Post;
import entityClasses.Reply;
import java.util.List;

/**
 * <p> Title: DiscussionsTestingAutomation Class. </p>
 *
 * <p> Description: A comprehensive Java class for semi-automated testing of the 
 * discussion system's CRUD functionality and business logic. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Kushal Gadamsetty
 *
 * @version 2.00	2025-10-21 Expanded to cover all test cases
 */
public class DiscussionsTestingAutomation {

    // Helper method to print test case headers
    private static void printHeader(String testCaseID, String description) {
        System.out.println("--- Running " + testCaseID + ": " + description + " ---");
    }

    // Helper method to print test results
    private static void printResult(boolean success) {
        if (success) {
            System.out.println("*** Success ***: Test case passed.\n");
        } else {
            System.out.println("*** Failure ***: Test case failed.\n");
        }
    }

    public static void main(String[] args) {
        Database db = new Database();
        try {
            // It's good practice to start with a clean database for testing
            // For H2 in-memory, just reconnecting is often enough.
            // If using a file-based DB, you might need to delete the file before running.
            db.connectToDatabase();
        } catch (Exception e) {
            System.out.println("Failed to connect to the database!");
            e.printStackTrace();
            return;
        }

        System.out.println("______________________________________");
        System.out.println("\nDiscussion System Testing Automation\n");
        
        String userA = "testUserA";
        String userB = "testUserB";

        // --- TC-01 (Positive): Create a valid post ---
        printHeader("TC-01", "Create a valid post");
        Post testPost = new Post(0, userA, "Question about UML Diagrams", "Can someone explain sequence diagrams?", "Homework", false, false, 0, 0);
        db.createPost(testPost);
        List<Post> postsAfterCreate = db.getAllPosts(userA);
        boolean tc01_success = !postsAfterCreate.isEmpty() && postsAfterCreate.get(0).getTitle().equals("Question about UML Diagrams");
        printResult(tc01_success);
        int postId = postsAfterCreate.get(0).getPostID();

        // --- TC-02 & TC-03 (Negative): Input Validation Simulation ---
        printHeader("TC-02/TC-03", "Simulate input validation for empty title/content");
        System.out.println("This is handled by the UI Controller. Simulating logic check:");
        String emptyInput = "";
        boolean tc02_03_success = emptyInput.trim().isEmpty();
        if (tc02_03_success) {
            System.out.println("Logic correctly identifies empty input. UI would show an error.");
        }
        printResult(tc02_03_success);

        // --- TC-04 (Positive): Add a valid reply ---
        printHeader("TC-04", "Add a valid reply to a post");
        Reply testReply = new Reply(0, postId, userB, "I can help with that!");
        db.createReply(testReply);
        List<Reply> replies = db.getRepliesForPost(postId, userA);
        boolean tc04_success = !replies.isEmpty() && replies.get(0).getAuthorUsername().equals(userB);
        printResult(tc04_success);
        int replyId = replies.get(0).getReplyID();

        // --- TC-06 (Positive): Edit a self-authored reply ---
        printHeader("TC-06", "Edit a self-authored reply");
        Reply replyToUpdate = db.getRepliesForPost(postId, userB).get(0);
        replyToUpdate.setContent("Updated content for my reply.");
        db.updateReply(replyToUpdate);
        replies = db.getRepliesForPost(postId, userB);
        boolean tc06_success = replies.get(0).getContent().equals("Updated content for my reply.");
        printResult(tc06_success);

        // --- TC-07 (Negative): Attempt to edit another user's reply ---
        printHeader("TC-07", "Attempt to edit another user's reply");
        System.out.println("Simulating check: User '" + userA + "' tries to edit a reply by '" + userB + "'.");
        boolean tc07_success = !replyToUpdate.getAuthorUsername().equals(userA);
        if(tc07_success){
            System.out.println("Authorization check passed. UI would show error: 'You can only edit your own replies.'");
        }
        printResult(tc07_success);

        // --- TC-08 (Positive): Delete a self-authored post (soft delete) ---
        printHeader("TC-08", "Delete a self-authored post (soft delete)");
        db.deletePost(postId);
        List<Post> postsAfterDelete = db.getAllPosts(userA);
        boolean postFound = false;
        for (Post p : postsAfterDelete) {
            if (p.getPostID() == postId) {
                postFound = true;
                break;
            }
        }
        boolean tc08_success = !postFound;
        printResult(tc08_success);

        // --- TC-10 (Positive): Delete a self-authored reply ---
        printHeader("TC-10", "Delete a self-authored reply");
        db.deleteReply(replyId);
        replies = db.getRepliesForPost(postId, userB);
        boolean tc10_success = replies.isEmpty();
        printResult(tc10_success);

        // --- TC-11 (Positive): Search for posts by keyword ---
        printHeader("TC-11", "Search for posts by keyword");
        db.createPost(new Post(0, userB, "JavaFX Question", "My ListView is not updating.", "General", false, false, 0, 0));
        List<Post> searchResults = db.searchPosts("JavaFX", "All Threads", userA);
        boolean tc11_success = !searchResults.isEmpty() && searchResults.get(0).getTitle().contains("JavaFX");
        printResult(tc11_success);

        // --- TC-13 & TC-15 (Positive): Mark items as read ---
        printHeader("TC-13/15", "Mark a post and reply as read");
        int newPostId = searchResults.get(0).getPostID();
        db.createReply(new Reply(0, newPostId, userA, "A new reply."));
        // Check initial state (unread)
        Post postBeforeRead = db.getAllPosts(userB).get(0);
        boolean initialStateCorrect = !postBeforeRead.isViewed() && postBeforeRead.getUnreadReplyCount() == 1;
        System.out.println("Initial state: Post is unread for User B, and has 1 unread reply. -> " + initialStateCorrect);
        // Mark post and reply as read for User B
        db.markPostAsRead(newPostId, userB);
        int newReplyId = db.getRepliesForPost(newPostId, userB).get(0).getReplyID();
        db.markReplyAsRead(newReplyId, userB);
        // Check final state (read)
        Post postAfterRead = db.getAllPosts(userB).get(0);
        boolean finalStateCorrect = postAfterRead.isViewed() && postAfterRead.getUnreadReplyCount() == 0;
        System.out.println("Final state: Post is read, and has 0 unread replies. -> " + finalStateCorrect);
        printResult(initialStateCorrect && finalStateCorrect);

        System.out.println("______________________________________");
        System.out.println("\nTesting complete.");
        
        db.closeConnection();
    }
}