// BOLD CHANGE:
package entityClasses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p> Title: Post Class </p>
 *
 * <p> Description: This entity class represents a single discussion post in the system. 
 * It holds all information related to a post, such as its author, title, content, 
 * and associated replies. </p>
 *
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 *
 * @author Kushal Gadamsetty
 *
 * @version 1.04    2025-10-20 Added reply counts
 */
public class Post {

	//private attributes
    private int postID; // to identify the post
    private String authorUsername; // to identify who authored post to make sure user can't delete another user's post
    private String title; //to identify the title of post
    private String content; //to identify the content of post
    private String thread; //to identify the thread of post
    private boolean deleted; //to identify if post has been deleted
    private boolean viewed; //to identify if post has been read to help with unread count
    private int replyCount; // Added replyCount attribute to use for replyCount method
    private int unreadReplyCount; // Added unreadReplyCount attribute for unreadReplyCount method
    private LocalDateTime timestamp; // identify when post was posted
    private List<Reply> replies; //to identify the replies attached to a post

    /**
     * <p> Method: Post(int postID, String authorUsername, String title, String content, 
     * String thread, boolean deleted, boolean viewed, int replyCount, int unreadReplyCount)</p>
     * 
     * <p> Description: This constructor is used to create post entity objects. </p>
     *
     * @param postID          The unique identifier for the post.
     * 
     * @param authorUsername  The username of the user who created the post.
     * 
     * @param title           The title of the post.
     * 
     * @param content         The main body content of the post.
     * 
     * @param thread          The discussion thread the post belongs to.
     * 
     * @param deleted         A boolean indicating if the post is deleted.
     * 
     * @param viewed          A boolean indicating if the post has been viewed by the current user.
     * 
     * @param replyCount      The total number of replies to the post.
     * 
     * @param unreadReplyCount The number of unread replies to the post.
     */
    public Post(int postID, String authorUsername, String title, String content, String thread, boolean deleted, boolean viewed,
    		    int replyCount, int unreadReplyCount) {
        this.postID = postID;
        this.authorUsername = authorUsername;
        this.title = title;
        this.content = content;
        this.thread = thread;
        this.deleted = deleted;
        this.viewed = viewed;
        this.replyCount = replyCount;
        this.unreadReplyCount = unreadReplyCount;
        this.timestamp = LocalDateTime.now(); // Set the timestamp to the current time
        this.replies = new ArrayList<>();
    }

    // Getters
    /*****
     * <p> Method: Integer getPostID() </p>
     * 
     * <p> Description: This getter returns the post Id. It implements read in the CRUD functionalities. </p>
     * 
     * @return an Integer of the post Id.
     * 
     */
    // Gets the current value of the post Id.
    public int getPostID() { return postID; }
    
    /*****
     * <p> Method: String getAuthorUsername() </p>
     * 
     * <p> Description: This getter returns the author's UserName. It implements read in the CRUD functionalities. </p>
     * 
     * @return a String of the author's UserName.
     * 
     */
    // Gets the current value of the author's UserName.
    public String getAuthorUsername() { return authorUsername; }
    
    /*****
     * <p> Method: String getTitle()) </p>
     * 
     * <p> Description: This getter returns the post's title.It implements read in the CRUD functionalities.  </p>
     * 
     * @return a String of the post's title.
     * 
     */
    // Gets the title of the post.
    public String getTitle() { return title; }
    
    /*****
     * <p> Method: String getContent()) </p>
     * 
     * <p> Description: This getter returns the post's content. It implements read in the CRUD functionalities. </p>
     * 
     * @return a String of the post's content.
     * 
     */
    // Gets the content of the post.
    public String getContent() { return content; }
    
    /*****
     * <p> Method: String getThread()) </p>
     * 
     * <p> Description: This getter returns the post's thread. It implements read in the CRUD functionalities. </p>
     * 
     * @return a String of the post's thread.
     * 
     */
    // Gets the thread of the post.
    public String getThread() { return thread; } 
    
    /*****
     * <p> Method: boolean isDeleted()) </p>
     * 
     * <p> Description: This getter returns the post's deleted attribute. It implements delete in the CRUD functionalities. </p>
     * 
     * @return a Boolean of the post's deleted attribute.
     * 
     */
    // Returns if the post has been deleted.
    public boolean isDeleted() { return deleted; } 
    
    /*****
     * <p> Method: boolean isViewed()) </p>
     * 
     * <p> Description: This getter returns the post's viewed attribute. It implements read in the CRUD functionalities. </p>
     * 
     * @return a Boolean of the post's viewed attribute.
     * 
     */
    // Returns if the post has been viewed.
    public boolean isViewed() { return viewed; } 
    
    /*****
     * <p> Method: String getReplyCount()) </p>
     * 
     * <p> Description: This getter returns the post's reply count. It implements read in the CRUD functionalities. </p>
     * 
     * @return an Integer of the post's reply count.
     * 
     */
    // Gets the reply count of the post.
    public int getReplyCount() { return replyCount; } // Added getter for replyCount
    
    /*****
     * <p> Method: String getUnreadReplyCount()) </p>
     * 
     * <p> Description: This getter returns the post's unread reply count. It implements read in the CRUD functionalities. </p>
     * 
     * @return an Integer of the post's unread reply count.
     * 
     */
    // Gets the unread reply count of the post.
    public int getUnreadReplyCount() { return unreadReplyCount; } // Added getter for unreadReplyCount
    
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
     * <p> Method: List<Reply> getReplies()) </p>
     * 
     * <p> Description: This getter returns a list of replies. It implements read in the CRUD functionalities. </p>
     * 
     * @return a List<Reply> of replies.
     * 
     */
    // Gets the list of replies.
    public List<Reply> getReplies() { return replies; }

    
    // Setters
    /*****
     * <p> Method: void setTitle(String title) </p>
     * 
     * <p> Description: This setter defines the Post title attribute. It implements read in the CRUD functionalities. </p>
     * 
     * @param title is a string that specifies the title of the post.
     * 
     */
    // Sets the the title of the post.
    public void setTitle(String title) { this.title = title; }
    
    
    /*****
     * <p> Method: void setContent(String content) </p>
     * 
     * <p> Description: This setter defines the Post content attribute. It implements update in the CRUD functionalities. </p>
     * 
     * @param content is a string that specifies the content of the post.
     * 
     */
    // Sets the the content of the post.
    public void setContent(String content) { this.content = content; }
    
    /*****
     * <p> Method: void setThread(String thread) </p>
     * 
     * <p> Description: This setter defines the Post thread attribute. It implements update in the CRUD functionalities. </p>
     * 
     * @param thread is a string that specifies the thread of the post.
     * 
     */
    // Sets the the thread of the post.
    public void setThread(String thread) { this.thread = thread; } 
    
    /*****
     * <p> Method: void setDeleted(boolean deleted) </p>
     * 
     * <p> Description: This setter defines the Post deleted attribute. It implements delete in the CRUD functionalities. </p>
     * 
     * @param deleted is a boolean that specifies if the post has been deleted.
     * 
     */
    // Updates if post was deleted.
    public void setDeleted(boolean deleted) { this.deleted = deleted; } 
    
    /*****
     * <p> Method: void setViewed(boolean viewed) </p>
     * 
     * <p> Description: This setter defines the Post viewed attribute. It implements update in the CRUD functionalities. </p>
     * 
     * @param viewed is a boolean that specifies if the post has been viewed.
     * 
     */
    // Sets if post was viewed.
    public void setViewed(boolean viewed) { this.viewed = viewed; } 

    /**
     * <p> Method: void addReply(Reply reply) </p>
     * 
     * <p> Description: This method adds a new reply to this post's set of replies. It implements update in the CRUD functionalities. </p>
     *
     * @param reply The Reply object to be added.
     */
    // Updates to add reply to a post
    public void addReply(Reply reply) {
        this.replies.add(reply);
    }

    /**
     * Returns a string representation of the Post object, which is used for display in the ListView. It implements update in the CRUD functionalities.
     * @return A string in the format "Title (Thread) - Replies: [replyCount] (Unread: [unreadReplyCount])".
     */
    // Displays the string representation of the Post object
    @Override
    public String toString() {
        return String.format("%s (%s) - Replies: %d (Unread: %d)", title, thread, replyCount, unreadReplyCount);
    }
}