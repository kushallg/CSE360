// BOLD CHANGE:
package entityClasses;

import java.time.LocalDateTime;
import java.util.EnumSet;

import applicationMain.FoundationsMain;
import database.Database;

/**
 * <p> Title: Reply Class </p>
 *
 * <p> Description: This entity class represents a single reply to a discussion post. 
 * It contains the reply's content, author, and a reference to the parent post. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Neha Kanjamala
 *
 * @version 1.01	2025-11-11 Added visibility/moderation
 */
public class Reply extends Post{

	//private attributes
    private int replyID; //to identify the reply
    private String visibility = "public"; // visibility control to implement private feedback from staff
    private String postAuthorUsername; // helper for visibility controls to show private feedback to recipient student on top of staff users
    private boolean viewed; // to know if the reply has been read to help with unread reply counts
    private LocalDateTime timestamp; // to identify the time post was created

    
    /**
     * <p> Method: Reply(int replyID, int postID, String authorUsername, String content)</p>
     * 
     * <p> Description: This constructor is used to create and initialize a new Reply object. </p>
     * 
     * @param replyID         The unique identifier for the reply.
     *
     * @param postID          The unique identifier for the post.
     * 
     * @param authorUsername  The username of the user who wrote the reply.
     * 
     * @param content         The main body content of the reply.
     */
    public Reply(int replyID, int postID, String authorUsername, String content) {
    	super(postID, authorUsername == null ? "" : authorUsername.trim(), "", content == null ? "" : content.trim(), "", false, false, 0,0);
        this.replyID = replyID;
        this.viewed = false; //added
        this.timestamp = LocalDateTime.now(); // Set the timestamp to the current time
        this.visibility = "public";
        this.postAuthorUsername = null;
    }
    
    /**
     * <p> Method: Reply(int replyID, int postID, String authorUsername, String content)</p>
     * 
     * <p> Description: This constructor is used to create and initialize a new Reply object. </p>
     * 
     * @param replyID         The unique identifier for the reply.
     *
     * @param postID          The unique identifier for the post.
     * 
     * @param authorUsername  The username of the user who wrote the reply.
     * 
     * @param content         The main body content of the reply.
     * 
     * @param visibility        The identifier for the reply's visibility settings as "public" or "private"
     *  
     * @param postAuthorUsername  The username of student user given the private feedback
     */
    // Reply constructor for private feedback replies that have possibly private visibility and require postAuthorUsername
    // Used in implementation of staff-only private feedback replies
    public Reply(int replyID, int postID, String authorUsername, String content, String visibility, String recipientUsername) {
    	super(postID, authorUsername == null ? "" : authorUsername.trim(), "", content == null ? "" : content.trim(), "", false, false, 0,0);
        this.replyID = replyID;
        this.viewed = false; //added
        this.timestamp = LocalDateTime.now(); // Set the timestamp to the current time
        
        if ("private".equals(visibility)) {
            this.visibility = "private";
        } else {
            this.visibility = "public";
        }
        
        if (this.visibility.equals("private")) {
            try {
                Database db = FoundationsMain.database; // global instance already connected
                String author = db.getPostAuthor(postID);
                if (author != null && !author.isEmpty()) {
                    this.postAuthorUsername = author.trim();
                } else {
                    this.postAuthorUsername = null;
                }
            } catch (Exception e) {
                // Defensive fallback in case of DB issues
                this.postAuthorUsername = null;
            }
        } else {
            this.postAuthorUsername = null;
        }
    }
    
    /**
     * <p> NEW Constructor: Reply(int replyID, int postID, String authorUsername, String content,
     *     boolean visible, String actionUser, String actionReason, LocalDateTime actionTimestamp) </p>
     *
     * <p> Description: This constructor adds moderation/visibility control. </p>
     *
     * @param replyID          unique id for the reply
     * @param postID           id of the parent post (used as the Post id)
     * @param authorUsername   reply author
     * @param content          reply body
     * @param visible          visibility to students
     * @param actionUser       who performed the moderation action
     * @param actionReason     why the action occurred
     * @param actionTimestamp  when the action occurred
     */
    public Reply(int replyID, int postID, String authorUsername, String content,
                 boolean visible, String actionUser, String actionReason, LocalDateTime actionTimestamp) {
        super(postID,
              authorUsername == null ? "" : authorUsername.trim(),
              "",                                        // title unused for Reply
              content == null ? "" : content.trim(),
              "",                                        // thread unused for Reply
              false, false, 0, 0,                        // deleted, viewed, reply counts
              visible, actionUser, actionReason, actionTimestamp);
        this.replyID = replyID;
        this.viewed = false;
        this.timestamp = LocalDateTime.now();
    }

    
    // Getters
    /*****
     * <p> Method: Integer getReplyID() </p>
     * 
     * <p> Description: This getter returns the reply Id. It implements read in the CRUD functionalities.  </p>
     * 
     * @return an Integer of the reply Id.
     * 
     */
    // Gets the current value of the reply Id.
    public int getReplyID() { return replyID; }
    
    /*****
     * <p> Method: boolean isViewed() </p>
     * 
     * <p> Description: This getter returns the reply's viewed attribute. It implements read in the CRUD functionalities. </p>
     * 
     * @return a Boolean of the reply's viewed attribute.
     * 
     */
    // Returns if the post has been viewed.
    public boolean isViewed() { return viewed; } // Added getter for viewed
    
    /*****
     * <p> Method: LocalDateTime getTimestamp()) </p>
     * 
     * <p> Description: This getter returns the post's timestamp. It implements read in the CRUD functionalities. </p>
     * 
     * @return an LocalDateTime of the post's timestamp.
     * 
     */
    // Gets the timestamp of the post.
    public LocalDateTime getTimestamp() { return timestamp; }

    /*****
     * <p> Method: String getVisibility() </p>
     * 
     * <p> Part of HW3 Aspect Code </p>
     * <p> Description: This getter returns the visibility of the reply. It implements the 
     * private feedback functionality for staff users.  </p>
     * 
     * @return a String of the reply visibility.
     * 
     */
    // Gets the current status of the reply visibility.
    // "public" means the reply is visible to everyone.
    // "private" means the reply is staff private feedback that should only be visible to staff and the post's author
    public String getVisibility() { return visibility; }

    /*****
     * <p> Method: String getPostAuthorUsername() </p>
     * 
     * <p> Part of HW3 Aspect Code </p>
     * <p> Description: This getter returns the reply's recipient, or the owner of the post the reply is linked to. 
     * It helps implement the private feedback functionality for staff users.  </p>
     * 
     * @return a String of the reply recipient.
     * 
     */
    // Gets the current reply recipient, the username of the author to the post being replied to
    // Private reply is constructed and the DB lookup succeeds so postAuthorUsername is created (db.getPostAuthor),
    // For tests / UI code, explicitly inject postAuthorUsername using setPostAuthorUsername.
    public String getPostAuthorUsername() { return postAuthorUsername; }
    
    
    // Setter
    /*****
     * <p> Method: void setReplyID(int replyID) </p>
     * 
     * <p> Description: This setter defines the Reply id. It implements update in the CRUD functionalities. </p>
     * 
     * @param replyID is a Integer that specifies the id of the post.
     * 
     */
    // Sets the current value of the reply Id.
    public void setReplyID(int replyID) { this.replyID = replyID; }
    
    /*****
     * <p> Method: void setViewed(boolean viewed) </p>
     * 
     * <p> Description: This setter defines the Post viewed attribute. It implements update in the CRUD functionalities. </p>
     * 
     * @param viewed is a boolean that specifies if the post has been viewed.
     * 
     */
 // Returns if the has been viewed.
    public void setViewed(boolean viewed) { this.viewed = viewed; } // Added setter for viewed
    
    /*****
     * <p> Method: void setPostAuthorUsername(String postAuthorUsername) </p>
     * 
     * <p> Part of HW3 Aspect Code </p>
     * <p> Description: This setter assigns the username of the post’s author for testing
     * or when the value is already known, allowing visibility checks to happen
     * without a database lookup (used for Junit Test) </p>
     * 
     * @param postAuthorUsername The username of the post’s author.
     */
    // Avoids direct database usage solely for Junit testing and explicitly sets the postAuthorUsername
    public void setPostAuthorUsername(String postAuthorUsername) {
        this.postAuthorUsername = postAuthorUsername;
    }
    
    /*****
     * <p> Method: boolean isVisibleTo(String requesterUsername) </p>
     * 
     * <p> Part of HW3 Aspect Code </p>
     * <p> Description: This method determines whether a particular user is able to view the
     * reply based on its visibility setting and the user’s role. Public replies are visible to
     * everyone. Private replies are considered staff private feedback are restricted staff or to the author
     * of the post that the reply is associated with. This supports privacy
     * and role-based access within the discussion board. </p>
     * 
     * @param requesterUsername The username of the user attempting to view the reply.
     * 
     * @return A boolean value indicating whether the reply is visible to the specified user
     */
    
    public boolean isVisibleTo(String requesterUsername) {
        // Public replies are always visible
        if (this.visibility.equals("public")) {
            return true;
        }

        // Private feedback visible only to post author and staff
        if (requesterUsername == null || requesterUsername.isEmpty()) {
            return false;
        }

        // Allow receiving student (post author) to see private feedback
        if (this.postAuthorUsername != null && requesterUsername.equals(this.postAuthorUsername)) {
            return true;
        }

        // Allow the author/creator of the reply (e.g., staff who posted the private feedback) to see it
        if (requesterUsername.equals(this.getAuthorUsername())) {
            return true;
        }

        // Allow staff users to see private feedback (via database query)
        try {
            int activeRole = FoundationsMain.activeHomePage;
            // If the session is currently acting as Staff, allow view of private feedback.
            if (activeRole == 3) return true;
        } catch (Exception e) {
            // fallback: deny visibility when something goes wrong
            return false;
        }

        return false;
    }
    
    /*****
     * <p> Method: String toString() </p>
     * 
     * <p> Part of HW3 Aspect Code </p>
     * <p> Description: This method returns a formatted string representation of the reply for display.
     * It includes the reply author’s username, the reply’s content, and indicates if the reply is
     * marked as private. </p>
     * 
     * @return a String containing the author’s username and the reply content, with “(private)”
     *         added when the reply’s visibility is set to private.
     */
    // Returns a formatted string representation of the reply for display with privacy settings
    @Override
    public String toString() {
        String base = this.getAuthorUsername() + ": " + this.getContent();
        if ("private".equalsIgnoreCase(this.visibility)) {
            base += " [Private Feedback]";
        }
        return base;
    }



    /*****
     * <p> Method: String getDisplayContent(EnumSet&lt;database.Database.Role&gt; roles) </p>
     * 
     * <p> Description: Convenience instance method for replies that delegates to 
     * Post.getDisplayContent(this, roles) without mutating the underlying content. </p>
     *
     * @param roles The set of roles of the current user.
     * 
     * @return A String representing the appropriate content to display for this reply.
     */
    public String getDisplayContent(EnumSet<database.Database.Role> roles) {
        return Post.getDisplayContent(this, roles);
    }
}