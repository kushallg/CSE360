package staffTesting;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import entityClasses.Post;
import entityClasses.Reply;
import database.Database;

import java.sql.SQLException;
import java.util.List;

/**
 * FeedbackVisibilityTest — automated JUnit tests for staff private feedback visibility behavior.
 * 
 * <p>Testable Portion Covered: Only the staff member(s) and the student receiving the private feedback can view the feedback </p>
 * <ul>
 *   <li>Public replies are visible to all users.</li>
 *   <li>Private replies (staff feedback) are visible only to staff members and the post author.</li>
 *   <li>Private replies remain hidden from unrelated student users.</li>
 * </ul>
 *
 * <p>Notes on interpreting results:</p>
 * <ul>
 *   <li>Run this class using JUnit. The test method documents the specific requirement it checks.</li>
 *   <li>A green (passing) test indicates correct visibility control logic.</li>
 *   <li>A red (failing) test indicates a visibility or role-checking error in the Reply implementation.</li>
 * </ul>
 *
 */
public class FeedbackTesting {

    private Post post;
    private Reply privateFeedback;

    @Before
    public void setUp() {
        post = new Post(1001, "student", "Feedback Post", "This is a simple post message.", "General", false, false, 0, 0);

        privateFeedback = new Reply(2002, post.getPostID(), "staffUser", "This is a private feedback reply.", "private", null);

        // since unit tests don’t load a DB row, set this manually
        privateFeedback.setPostAuthorUsername("student");

        System.out.println("\n==============================");
        System.out.println(" Setting up FeedbackVisibilityTest environment...");
    }

    
    /**
     * testprivateFeedbackVisibleToStaff()
     *
     * <p>Section of the Requirement tested:</p>
     *  - Private replies visible to staff
     *
     * <p>What this test checks:</p>
     *  Ensures that private replies are visible to staff users for moderation and feedback tracking.
     *
     * <p>Interpreting results:</p>
     *  - PASS: staffUser can view private reply.
     *  - FAIL: staffUser cannot view private reply, indicating access control issue.
     */
    @Test
    public void testprivateFeedbackVisibleToStaff() {
        assertTrue(privateFeedback.isVisibleTo("staffUser"));
        System.out.println("Private feedback visibility confirmed for staff user.\n");
        System.out.println("toString() output: " + privateFeedback.toString() + "\n");
    }
    
    /**
     * testprivateFeedbackVisibleToPostAuthor()
     *
     * <p>Section of the Requirement tested:</p>
     *  - Private replies visible to post author
     *
     * <p>What this test checks:</p>
     *  Ensures that private feedback replies created by staff are visible to the student who authored the post.
     *
     * <p>Interpreting results:</p>
     *  - PASS: post author can view private feedback.
     *  - FAIL: post author cannot view private feedback, indicating missing linkage.
     */
    @Test
    public void testprivateFeedbackVisibleToPostAuthor() {
        assertTrue(privateFeedback.isVisibleTo("student"));
        System.out.println("Private feedback visibility confirmed for post author (student).\n");
        System.out.println("toString() output: " + privateFeedback.toString() + "\n");
    }
    
    /**
     * testprivateFeedbackHiddenFromOtherStudents()
     *
     * <p>Section of the Requirement tested:</p>
     *  - Private replies hidden from unrelated users
     *
     * <p>What this test checks:</p>
     *  Ensures that private feedback replies are not visible to unrelated students.
     *
     * <p>Interpreting results:</p>
     *  - PASS: unrelated student cannot view private reply.
     *  - FAIL: unrelated student can see private reply, indicating missing privacy enforcement.
     */
    @Test
    public void testprivateFeedbackHiddenFromOtherStudents() {
        assertFalse(privateFeedback.isVisibleTo("studentB"));
        System.out.println("Private feedback correctly hidden from unrelated student users.\n");
    }


    /**
     * PrivateFeedbackAsStaff()
     *
     * <p>Section of the Requirement tested:</p>
     *  - Staff can create private feedback (backend enforcement)
     *
     * <p>What this test checks:</p>
     *  Verifies that Database.createReply(...) **accepts** private replies
     *  authored by staff users.
     * 
     * <p>Interpreting results:</p>
     *  - PASS: staff cannot create proper private reply.
     *  - FAIL: staff can create private reply.
     */
    @Test
    public void PrivateFeedbackAsStaff() throws Exception {
        Database db = new Database();
        try {
            db.connectToDatabase();
        } catch (SQLException e) {
            org.junit.Assume.assumeTrue("DB not available for backend test", false);
        }

        // Create a post
        db.create(new Post(0, "student", "TestPost2", "Body", "General", false, false, 0, 0));
        Post inserted = db.getAllPosts("student").get(0);
        int postID = inserted.getPostID();

        // Staff creates private feedback
        Reply staffReply = new Reply(0, postID, "staffUser",
                                     "Official private feedback", "private", "student");

        boolean result = db.createReply(staffReply);

        assertTrue("Backend should allow staff to create private feedback", result);
        db.closeConnection();
    }

}
