package staffTesting;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Test;

import database.Database;           // for Database.Role enum
import entityClasses.Post;
import entityClasses.Reply;

/**
 * HidePostTests
 *
 * <p>Automated JUnit 4 tests for verifying post/reply visibility and moderation behavior
 * using role sets directly (no database calls). Tests assert the display text returned
 * by Post.getDisplayContent(...) and Reply.getDisplayContent(...).</p>
 */
public class HidePostTests {

    private LocalDateTime ts;

    // Role sets for tests
    private EnumSet<Database.Role> STUDENT;
    private EnumSet<Database.Role> STAFF;
    private EnumSet<Database.Role> ADMIN;
    private EnumSet<Database.Role> NOROLES;

    @Before
    public void setUp() throws Exception {
        ts = LocalDateTime.of(2025, 11, 11, 10, 0);
        STUDENT = EnumSet.of(Database.Role.STUDENT);
        STAFF   = EnumSet.of(Database.Role.STAFF);
        ADMIN   = EnumSet.of(Database.Role.ADMIN);
        NOROLES = EnumSet.noneOf(Database.Role.class);

        System.out.println("\n======================================");
        System.out.println(" Setting up HidePostTests...");
    }

    // ---------------------------------------------------------------------
    // Post tests
    // ---------------------------------------------------------------------

    /**
     * testVisiblePost_NoChange()
     *
     * <p>Section: Visible posts remain viewable to all users. </p>
     *
     * <p>Checks:A visible post returns its original content for any role. </p> 
     */
    @Test
    public void testVisiblePost_NoChange() {
        Post post = new Post(
            1, "Author1", "Visible Title", "Visible Content", "General",
            false, false, 0, 0,
            true, "staffUser", "routine check", ts
        );

        assertTrue(post.isVisible());
        assertEquals("Visible Content", Post.getDisplayContent(post, STUDENT));
        assertEquals("Visible Content", Post.getDisplayContent(post, STAFF));
        assertEquals("Visible Content", Post.getDisplayContent(post, ADMIN));
        assertEquals("Visible Content", Post.getDisplayContent(post, NOROLES));
        System.out.println("PASS: testVisiblePost_NoChange -> All roles see unmodified content.");
    }

    /**
     * testHiddenPost_StudentView()
     *
     * <p>Section: Hidden posts are masked for Student role. </p>
     */
    @Test
    public void testHiddenPost_StudentView() {
        Post post = new Post(
            2, "Author", "Private Post", "Sensitive Info", "General",
            false, false, 0, 0,
            false, "staffAlice", "policy violation", ts
        );

        assertFalse(post.isVisible());
        String shown = Post.getDisplayContent(post, STUDENT);
        assertEquals("Content Not Available", shown);
        System.out.println("PASS: testHiddenPost_StudentView -> Students see error message.");
    }

    /**
     * testHiddenPost_StaffView()
     *
     * <p>Section: Hidden posts visible to Staff with label. </p>
     */
    @Test
    public void testHiddenPost_StaffView() {
        String body = "Assessment Guidelines";
        Post post = new Post(
            3, "Author", "Rubric", body, "Assignments",
            false, false, 0, 0,
            false, "adminBob", "moderation", ts
        );

        assertFalse(post.isVisible());
        String shown = Post.getDisplayContent(post, STAFF);
        assertEquals(body + " (Hidden by Staff)", shown);
        System.out.println("PASS: testHiddenPost_StaffView -> Staff see labeled hidden content.");
    }

    /**
     * testHiddenPost_AdminView()
     *
     * <p>Section: Hidden posts visible to Admin with label. </p>
     */
    @Test
    public void testHiddenPost_AdminView() {
        String body = "Exam Blueprint";
        Post post = new Post(
            4, "Author", "Confidential", body, "Exams",
            false, true, 0, 0,
            false, "staffAmy", "sensitive info", ts
        );

        assertFalse(post.isVisible());
        String shown = Post.getDisplayContent(post, ADMIN);
        assertEquals(body + " (Hidden by Staff)", shown);
        System.out.println("PASS: testHiddenPost_AdminView -> Admins see labeled hidden content.");
    }


    // ---------------------------------------------------------------------
    // Reply tests: same role-based behavior
    // ---------------------------------------------------------------------

    /**
     * testVisibleReply_NoChange()
     *
     * <p>Section: Visible replies remain viewable to all users. </p>
     */
    @Test
    public void testVisibleReply_NoChange() {
        String body = "Thanks for the clarification!";
        Reply r = new Reply(
            101, 1, "AnyUser", body,
            true, "staffUser", "routine check", ts
        );

        assertTrue(r.isVisible());
        assertEquals(body, r.getDisplayContent(STUDENT));
        assertEquals(body, r.getDisplayContent(STAFF));
        assertEquals(body, r.getDisplayContent(ADMIN));
        assertEquals(body, r.getDisplayContent(NOROLES));
        System.out.println("PASS: testVisibleReply_NoChange -> All roles see visible reply unchanged.");
    }

    /**
     * testHiddenReply_StudentView()
     *
     * <p>Section: Hidden replies are masked for Student role. </p>
     */
    @Test
    public void testHiddenReply_StudentView() {
        Reply r = new Reply(
            102, 2, "Author", "Private reply text",
            false, "modAlice", "policy violation", ts
        );

        assertFalse(r.isVisible());
        String shown = r.getDisplayContent(STUDENT);
        assertEquals("Content Not Available", shown);
        System.out.println("PASS: testHiddenReply_StudentView -> Student sees error message.");
    }

    /**
     * testHiddenReply_StaffView()
     *
     * <p>Section: Hidden replies visible to Staff with label. </p>
     */
    @Test
    public void testHiddenReply_StaffView() {
        String body = "Grading rubric updated.";
        Reply r = new Reply(
            103, 3, "Author", body,
            false, "adminBob", "moderation", ts
        );

        assertFalse(r.isVisible());
        String shown = r.getDisplayContent(STAFF);
        assertEquals(body + " (Hidden by Staff)", shown);
        System.out.println("PASS: testHiddenReply_StaffView -> Staff sees labeled hidden reply.");
    }

    /**
     * testHiddenReply_AdminView()
     *
     * <p>Section: Hidden replies visible to Admin with label. </p> 
     */
    @Test
    public void testHiddenReply_AdminView() {
        String body = "Please review section 2.";
        Reply r = new Reply(
            104, 4, "Author", body,
            false, "staffAmy", "sensitive info", ts
        );

        assertFalse(r.isVisible());
        String shown = r.getDisplayContent(ADMIN);
        assertEquals(body + " (Hidden by Staff)", shown);
        System.out.println("PASS: testHiddenReply_AdminView → Admin sees labeled hidden reply.");
    }

}
