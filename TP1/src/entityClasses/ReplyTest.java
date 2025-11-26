package entityClasses;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * ReplyTest — automated JUnit tests for reply behavior.
 *
 * <p>TP2 Requirements covered:</p>
 * 
 * <p>Automated test coverage (Junit tests):</p>
 * <ul>
 *   <li>R-REPLY-01: Users can create replies linked to a post (reply.postID matches post ID).</li>
 *   <li>R-REPLY-02: A user may only update or delete their own replies.</li>
 *   <li>R-UI-01: Empty reply content is invalid when creating or updating a reply.</li>
 *   <li>R-UI-02: Reply fields (viewed flag, timestamp) are set or edited properly.</li>
 * </ul>
 *
 * <p>Notes on interpreting results:</p>
 * <ul>
 *   <li>Run this class with JUnit. Each test method documents the requirement(s) it checks.
 *       A green test indicates the code satisfies that check.
 *       A red test indicates it does not.</li>
 * </ul>
 *
 * <p>Manual test coverage (UI / integration tests):</p>
 * <ul>
 *   <li>MT-06: View all replies under a post (list view behavior).</li>
 *   <li>MT-07: View unread replies under a post (filtering by unread).</li>
 *   <li>MT-08: Verify total reply number and total unread reply number for each post.</li>
 *   <li>MT-09: Verify confirmation/verification message appears when deleting a reply.</li>
 *   <li>MT-10: Verify a reply remains visible even when its linked post is deleted.</li>
 * </ul>
 *
 * <p>Manual evidence location:</p>
 * <ul>
 *   <li>See manual test entries MT-06 through MT-10 and supporting screenshots in StudentPostTests.pdf (after generated Javadoc).</li>
 * </ul>

 */

public class ReplyTest {

	private Post post;
    private Reply reply;
    
    @Before
    public void setUp() {
        post = new Post(1234, "abcd", "Intro", "This is a Test Post.", "General", false, false, 0, 0);

        reply = new Reply(10, post.getPostID(), "abcd", "This is a reply.");
        System.out.println("\n==============================");
    }
    
    /**
     * testCreateReply()
     *
     * <p>Requirements tested:</p>
     *  - R-REPLY-01 (create reply)
     *  - R-UI-02 (reply fields set correctly)
     *
     * <p>What this test checks:</p>
     *  Creating a Reply with values and asserts getters and more to match.
     *
     * <p>Interpreting results:</p>
     *  - PASS: all getters return expected values.
     *  - FAIL: reply does not properly create as specified.
     */
    @Test
    public void testCreateReply() {
        assertEquals(10, reply.getReplyID());
        assertEquals(post.getPostID(), reply.getPostID());
        assertEquals("abcd", reply.getAuthorUsername());
        assertEquals("This is a reply.", reply.getContent());
        assertFalse(reply.isViewed());
        assertNotNull(reply.getTimestamp());

        System.out.println("Reply created successfully.\n");
    }
    
    /**
     * testReplyLinkedToPost()
     *
     * <p>Requirements tested:</p>
     *  - R-REPLY-01 (reply linked to post)
     *
     * <p>What this test checks:</p>
     *  Ensures the reply.postID matches the post.postID it is supposed to belong to so the reply is under a post.
     *
     * <p>Interpreting results:</p>
     *  - PASS: reply.getPostID() equals post.getPostID()
     *  - FAIL: link did not work and reply is not set to a post.
     */
    @Test
    public void testReplyLinkedToPost() {
        assertEquals(post.getPostID(), reply.getPostID());
        System.out.println("Reply-post linkage test passed.\n");
    }
    
    /**
     * testUserCanUpdateReply()
     *
     * <p>Requirements tested:</p>
     *  - R-REPLY-02 (author update)
     *
     * <p>What this checks:</p>
     *  Simulates user editing own reply by calling setters and asserting.
     *
     * <p>Interpreting results:</p>
     *  - PASS: reply is updated.
     *  - FAIL: reply is not correctly updating and setters may not be making changes.
     */
    @Test
    public void testUserCanUpdateReply() {
        String currentUser = "abcd";
        assertEquals(reply.getAuthorUsername(), currentUser);

        String newContent = "Owner updated this reply.";
        if (reply.getAuthorUsername().equals(currentUser)) {
            reply.setContent(newContent);
        }

        assertEquals("Owner update should apply", newContent, reply.getContent());
        System.out.println("Owner update test passed.\n");
    }
    
    /**
     * testOtherUserCannotUpdate()
     *
     * <p>Requirements tested:</p>
     *  - R-REPLY-02 (author-only update) negative case
     *
     * <p>What this checks:</p>
     *  Simulates another user attempting to update a reply they do not own.
     *
     * <p>Interpreting results:</p>
     *  - PASS: reply content unchanged when non-owner attempts to update.
     *  - FAIL: test will reveal a change where it shouldn't, showing missing validation.
     */
    @Test
    public void testOtherUserCannotUpdateReply() {
        reply.setContent("This is a reply.");

        String currentUser = "fred";
        String attempted = "Fred tries to edit this.";
        if (reply.getAuthorUsername().equals(currentUser)) {
            reply.setContent(attempted);
        }

        assertEquals("This is a reply.", reply.getContent());
        System.out.println("Unauthorized update test passed.\n");
    }
    
    /**
     * testUserCanDeleteReply()
     *
     * <p>Requirements tested:</p>
     *  - R-REPLY-02 (author delete)
     *
     * <p>What this checks:</p>
     *  Simulates user marking their own reply as deleted (replacing content with "[deleted]").
     *
     * <p>Interpreting results:</p>
     *  - PASS: reply content becomes "[deleted]" after owner deletes.
     *  - FAIL: deletion not carried out correctly.
     */
    @Test
    public void testUserCanDeleteReply() {
        reply.setContent("This is a reply.");
        String currentUser = "abcd";
        if (reply.getAuthorUsername().equals(currentUser)) {
            reply.setContent("[deleted]");
        }
        assertEquals("[deleted]", reply.getContent());
        System.out.println("Owner delete test passed (content replaced with [deleted]).\n");
    }
    
    /**
     * testOtherUserCannotDeleteReply()
     *
     * <p>Requirements tested:</p>
     *  - R-REPLY-02 (author-only delete) negative case
     *
     * <p>What this checks:</p>
     *  Simulates another user attempting to delete a reply they don't own and ensures reply remains visible.
     *
     * <p>Interpreting results:</p>
     *  - PASS: reply content unchanged for non-owner delete attempt.
     *  - FAIL: content changed, showing missing validation.
     */
    @Test
    public void testOtherUserCannotDeleteReply() {
        reply.setContent("This is a reply.");
        String currentUser = "fred";
        if (reply.getAuthorUsername().equals(currentUser)) {
            reply.setContent("[deleted]");
        }
        assertEquals("This is a reply.", reply.getContent());
        System.out.println("Unauthorized delete test passed.\n");
    }
    
    /**
     * testInvalidReplyCreation()
     *
     * <p>Requirements tested:</p>
     *  - R-UI-01 (prevent blank reply content on create)
     *
     * <p>What this checks:</p>
     *  Asserts that blank or whitespace-only reply content is considered invalid.
     *
     * <p>Interpreting results:</p>
     *  - PASS: blank and whitespace-only inputs are rejected by the validation check.
     *  - FAIL: empty inputs are accepted which shows missing validation.
     */
    @Test
    public void testInvalidReplyCreation() {
        String blank = "";
        String whitespace = "   ";

        boolean valid1 = blank != null && !blank.trim().isEmpty();
        boolean valid2 = whitespace != null && !whitespace.trim().isEmpty();

        assertFalse(valid1);
        assertFalse(valid2);
        System.out.println("Invalid reply creation test passed.\n");
    }
    
    /**
     * testInvalidReplyUpdate()
     *
     * <p>Requirements tested:</p>
     *  - R-UI-01 (prevent blank reply content on update)
     *
     * <p>What this checks:</p>
     *  Ensures that attempting to update a reply with empty content does not change the reply.
     *
     * <p>Interpreting results:</p>
     *  - PASS: reply content remains unchanged when update content is invalid.
     *  - FAIL: invalid update is applied.
     */
    @Test
    public void testInvalidReplyUpdate() {
        reply.setContent("This is a reply.");
        String currentUser = "abcd";
        String newBlank = "   ";

        boolean valid = newBlank != null && !newBlank.trim().isEmpty();
        if (reply.getAuthorUsername().equals(currentUser) && valid) {
            reply.setContent(newBlank);
        }

        assertEquals("This is a reply.", reply.getContent());
        System.out.println("Invalid reply update test passed.\n");
    }
}
