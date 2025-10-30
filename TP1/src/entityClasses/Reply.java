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

	//private attributes
    private int replyID; //to identify the reply
    //private int postID;
    //private String authorUsername;
    //private String content;
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
     * <p> Method: boolean isViewed()) </p>
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
}