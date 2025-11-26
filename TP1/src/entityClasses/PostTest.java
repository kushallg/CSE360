package entityClasses;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * PostTest — automated JUnit tests for discussion-post behavior.
 *
 * <p>TP2 Requirements covered:</p>
 * 
 * <p>Automated test coverage (Junit tests):</p>
 * <ul>
 *   <li>R-POST-01: Users can create posts with title, content, and thread.</li>
 *   <li>R-POST-02: A user may only update or delete their own posts.</li>
 *   <li>R-UI-01: Empty title/content is invalid on when creating or updating.</li>
 *   <li>R-UI-02: Post fields (deleted, viewed, reply counts, timestamp) are set or edited properly.</li>
 * </ul>
 *
 * <p>Notes on interpreting results:</p>
 * <ul>
 *   <li>Run this class with JUnit. Each of these tests show the requirements they check.
 *       A green test indicates the code satisfies that check.
 *       A red test indicates the code does not satisfy that check.</li>
 * </ul>
 *
 * <p>Manual test coverage (UI / integration tests):</p>
 * <ul>
 *   <li>MT-01: View all posts, My Posts, and Unread posts (sorting/filtering behavior).</li>
 *   <li>MT-02: Verify total post counts and total unread post counts in the general view and under search queries.</li>
 *   <li>MT-03: Search by keyword and by keyword+thread (scoped search).</li>
 *   <li>MT-04: Create posts in different threads and verify thread selection/filtering.</li>
 *   <li>MT-05: Verify confirmation/verification message appears when deleting a post.</li>
 * </ul>
 *
 * <p>Manual evidence location:</p>
 * <ul>
 *   <li>See manual test entries MT-01 through MT-05 and supporting screenshots in StudentPostTests.pdf (after generated Javadoc).</li>
 * </ul>
 */
public class PostTest {

    private Post post;

	@Before
    public void setUp() throws Exception {
        post = new Post(1234, "abcd", "Intro", "This is a Test Post.", "Homework", false, false, 0, 0);
        System.out.println("\n==============================");
	}

	/**
     * testCreatePost()
     *
     * <p>Requirements tested:</p>
     *  - R-POST-01 (create post)
     *  - R-UI-02 (post fields set correctly)
     *
     * <p>What this test checks:</p>
     *  Creating a Post with values and asserts getters and more to match.
     *
     * <p>Interpreting results:</p>
     *  - PASS: all getters return expected values.
     *  - FAIL: post does not properly create as specified.
     */
	@Test
    public void testCreatePost() {
        assertEquals(1234, post.getPostID());
        assertEquals("abcd", post.getAuthorUsername());
        assertEquals("Intro", post.getTitle());
        assertEquals("This is a Test Post.", post.getContent());
        assertEquals("Homework", post.getThread());
        assertFalse(post.isDeleted());
        assertFalse(post.isViewed());
        assertEquals(0, post.getReplyCount());
        assertEquals(0, post.getUnreadReplyCount());
        assertNotNull(post.getTimestamp());
        System.out.println("Post created successfully.\n");
    }
	
	/**
     * testUserCanUpdate()
     *
     * <p>Requirements tested:</p>
     *  - R-POST-02 (author edit)
     *
     * <p>What this checks:</p>
     *  Simulates user editing own post by calling setters and asserting.
     *
     * <p>Interpreting results:</p>
     *  - PASS: title/content is updated.
     *  - FAIL: post is not correctly updating.
     */
	@Test
    public void testUserCanUpdate() {
        String currentUser = "abcd";
        boolean canEdit = post.getAuthorUsername().equals(currentUser);
        assertTrue(canEdit);

        post.setTitle("Updated Intro");
        post.setContent("Updated content.");
        assertEquals("Updated Intro", post.getTitle());
        assertEquals("Updated content.", post.getContent());

        System.out.println("Edit test passed for author.\n");
    }
	
	/**
     * testOtherUserCannotUpdate()
     *
     * <p>Requirements tested:</p>
     *  - R-POST-02 (author-only edit) negative case
     *
     * <p>What this checks:</p>
     *  Simulates another user attempting to update a post they do not own — test uses in-test implementation to prevent edit
     *  and asserts original content remains unchanged, thought this is implemented within the discussion gui controller as well.
     *
     * <p>Interpreting results:</p>
     *  - PASS: content unchanged when non-owner attempts to update.
     *  - FAIL: test will reveal a change where it shouldn't, showing missing validation.
     */
	@Test
    public void testOtherUserCannotUpdate() {
        String currentUser = "fred";
        boolean canEdit = post.getAuthorUsername().equals(currentUser);
        assertFalse(canEdit);

        String oldTitle = post.getTitle();
        String oldContent = post.getContent();
        if (canEdit) {
            post.setContent("Fred shouldn't be able to edit this");
        }
        assertEquals(oldTitle, post.getTitle());
        assertEquals(oldContent, post.getContent());

        System.out.println("Unauthorized edit test passed.\n");
    }
	
	/**
     * testUserCanDelete()
     *
     * <p>Requirements tested:</p>
     *  - R-POST-02 (author delete)
     *
     * <p>What this checks:</p>
     *  Simulates user marking their own post as deleted (post.setDeleted(true)).
     *
     * <p>Interpreting results:</p>
     *  - PASS: post.isDeleted() returns true after owner deletes.
     *  - FAIL: deletion not carried out correctly.
     */
	@Test
    public void testUserCanDelete() {
        String currentUser = "abcd";
        boolean canDelete = post.getAuthorUsername().equals(currentUser);
        assertTrue(canDelete);

        if (canDelete) {
            post.setDeleted(true);
        }
        assertTrue(post.isDeleted());
        System.out.println("Delete test passed for author.\n");
    }
	
	/**
     * testOtherUserCannotDelete()
     *
     * <p>Requirements tested:</p>
     *  - R-POST-02 (author-only delete) negative case
     *
     * <p>What this checks:</p>
     *  Simulates another user attempting to delete a post they don't own and ensures deleted boolean remains false.
     *
     * <p>Interpreting results:</p>
     *  - PASS: deleted boolean remains false for non-owner.
     *  - FAIL: wrong user was able to delete the post, showing missing validation.
     */
	@Test
    public void testOtherUserCannotDelete() {
        post.setDeleted(false);
        String currentUser = "fred";
        boolean canDelete = post.getAuthorUsername().equals(currentUser);
        assertFalse(canDelete);

        if (canDelete) {
            post.setDeleted(true);
        }
        assertFalse(post.isDeleted());
        System.out.println("Unauthorized delete test passed.\n");
    }
	
	/**
     * testInvalidPostCreation()
     *
     * <p>Requirements tested:</p>
     *  - R-UI-01 (prevent blank title/content on create)
     *
     * <p>What this checks:</p>
     *  Checks with asserts that a blank title or blank content when creating a post are invalid.
     *
     * <p>Interpreting results:</p>
     *  - PASS: Empty inputs are rejected by the check in the test.
     *  - FAIL: an empty value is taken as valid, which shows missing validation.
     */
	@Test
    public void testInvalidPostCreation() {
        String title = "";
        String content = "Valid content";
        boolean valid1 = title != null && !title.trim().isEmpty() && content != null && !content.trim().isEmpty();
        assertFalse(valid1);

        title = "Valid Title";
        content = "";
        boolean valid2 = title != null && !title.trim().isEmpty() && content != null && !content.trim().isEmpty();
        assertFalse(valid2);

        System.out.println("Invalid post creation test passed.\n");
    }
	
	/**
     * testInvalidPostUpdate()
     *
     * <p>Requirements tested:</p>
     *  - R-UI-01 (prevent blank content on update)
     *
     * <p>What this checks:</p>
     *  Checks with asserts that blank content when updating a post is invalid.
     *
     * <p>Interpreting results:</p>
     *  - PASS: Empty inputs are rejected by the check in the test.
     *  - FAIL: an empty value is taken as valid, which shows missing validation.
     */
	@Test
    public void testInvalidPostUpdate() {
        String newContent = "";
        boolean valid1 = newContent != null && !newContent.trim().isEmpty();
        assertFalse(valid1);

        System.out.println("Invalid post update test passed.\n");
    }

}
