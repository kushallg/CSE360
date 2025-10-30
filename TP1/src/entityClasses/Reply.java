// BOLD CHANGE:
package entityClasses;

import java.time.LocalDateTime;

/**
 * <p> Title: Reply Class </p>
 *
 * <p> Description: This entity class represents a single reply to a discussion post. 
 * It contains the reply's content, author, and a reference to the parent post. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Kushal Gadamsetty
 *
 * @version 1.01	2025-10-20 Added viewed attribute
 */
public class Reply extends Post{

    private int replyID;
    //private int postID;
    //private String authorUsername;
    //private String content;
    private boolean viewed; // Added viewed attribute
    private LocalDateTime timestamp;

    /**
     * Constructor to initialize a new Reply object.
     *
     * @param replyID         The unique identifier for the reply.
     * @param postID          The ID of the post this reply belongs to.
     * @param authorUsername  The username of the user who wrote the reply.
     * @param content         The main body content of the reply.
     */
    public Reply(int replyID, int postID, String authorUsername, String content) {
    	super(postID, authorUsername == null ? "" : authorUsername.trim(), "", content == null ? "" : content.trim(), "", false, false, 0,0);
        this.replyID = replyID;
        //this.postID = postID;
        //this.authorUsername = authorUsername;
        //this.content = content;
        this.viewed = false; //added
        this.timestamp = LocalDateTime.now(); // Set the timestamp to the current time
    }

    // Getters
    public int getReplyID() { return replyID; }
    //public int getPostID() { return postID; }
    //public String getAuthorUsername() { return authorUsername; }
    //public String getContent() { return content; }
    public boolean isViewed() { return viewed; } // Added getter for viewed
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setter
    //public void setContent(String content) { this.content = content; }
    public void setReplyID(int replyID) { this.replyID = replyID; }
    public void setViewed(boolean viewed) { this.viewed = viewed; } // Added setter for viewed
}