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

    private int postID;
    private String authorUsername;
    private String title;
    private String content;
    private String thread; 
    private boolean deleted; 
    private boolean viewed; 
    private int replyCount; // Added replyCount attribute
    private int unreadReplyCount; // Added unreadReplyCount attribute
    private LocalDateTime timestamp;
    private List<Reply> replies;

    /**
     * <p> Method: Post(int postID, String authorUsername, String title, String content, 
     * String thread, boolean deleted, boolean viewed, int replyCount, int unreadReplyCount)</p>
     * 
     * <p> Description: This constructor is used to establish user entity objects. </p>
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
    public Post(int postID, String authorUsername, String title, String content, String thread, boolean deleted, boolean viewed, int replyCount, int unreadReplyCount) {
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
     * <p> Description: This getter returns the post Id. </p>
     * 
     * @return an Integer of the post Id.
     * 
     */
    // Gets the current value of the post Id.
    public int getPostID() { return postID; }
    
    /*****
     * <p> Method: String getAuthorUsername() </p>
     * 
     * <p> Description: This getter returns the author's UserName. </p>
     * 
     * @return a String of the author's UserName.
     * 
     */
    // Gets the current value of the author's UserName.
    public String getAuthorUsername() { return authorUsername; }
    
    /*****
     * <p> Method: String getTitle()) </p>
     * 
     * <p> Description: This getter returns the post's title. </p>
     * 
     * @return a String of the post's title.
     * 
     */
    // Gets the title of the post.
    public String getTitle() { return title; }
    
    /*****
     * <p> Method: String getContent()) </p>
     * 
     * <p> Description: This getter returns the post's content. </p>
     * 
     * @return a String of the post's content.
     * 
     */
    // Gets the content of the post.
    public String getContent() { return content; }
    
    /*****
     * <p> Method: String getThread()) </p>
     * 
     * <p> Description: This getter returns the post's thread. </p>
     * 
     * @return a String of the post's thread.
     * 
     */
    // Gets the thread of the post.
    public String getThread() { return thread; } 
    
    /*****
     * <p> Method: boolean isDeleted()) </p>
     * 
     * <p> Description: This getter returns the post's deleted attribute. </p>
     * 
     * @return a Boolean of the post's deleted attribute.
     * 
     */
    // Returns if the post has been deleted.
    public boolean isDeleted() { return deleted; } 
    
    /*****
     * <p> Method: boolean isViewed()) </p>
     * 
     * <p> Description: This getter returns the post's viewed attribute. </p>
     * 
     * @return a Boolean of the post's viewed attribute.
     * 
     */
    // Returns if the post has been viewed.
    public boolean isViewed() { return viewed; } 
    
    /*****
     * <p> Method: String getReplyCount()) </p>
     * 
     * <p> Description: This getter returns the post's reply count. </p>
     * 
     * @return an Integer of the post's reply count.
     * 
     */
    // Gets the reply count of the post.
    public int getReplyCount() { return replyCount; } // Added getter for replyCount
    
    /*****
     * <p> Method: String getUnreadReplyCount()) </p>
     * 
     * <p> Description: This getter returns the post's unread reply count. </p>
     * 
     * @return an Integer of the post's unread reply count.
     * 
     */
    // Gets the unread reply count of the post.
    public int getUnreadReplyCount() { return unreadReplyCount; } // Added getter for unreadReplyCount
    
    /*****
     * <p> Method: LocalDateTime getTimestamp()) </p>
     * 
     * <p> Description: This getter returns the post's timestamp. </p>
     * 
     * @return an LocalDateTime of the post's timestamp.
     * 
     */
    // Gets the timestamp of the post.
    public LocalDateTime getTimestamp() { return timestamp; }
    
    /*****
     * <p> Method: List<Reply> getReplies()) </p>
     * 
     * <p> Description: This getter returns a list of replies. </p>
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
     * <p> Description: This setter defines the Post title attribute. </p>
     * 
     * @param title is a string that specifies the title of the post.
     * 
     */
    public void setTitle(String title) { this.title = title; }
    
    
    /*****
     * <p> Method: void setContent(String content) </p>
     * 
     * <p> Description: This setter defines the Post content attribute. </p>
     * 
     * @param content is a string that specifies the content of the post.
     * 
     */
    public void setContent(String content) { this.content = content; }
    
    /*****
     * <p> Method: void setThread(String thread) </p>
     * 
     * <p> Description: This setter defines the Post thread attribute. </p>
     * 
     * @param thread is a string that specifies the thread of the post.
     * 
     */
    public void setThread(String thread) { this.thread = thread; } 
    
    /*****
     * <p> Method: void setDeleted(boolean deleted) </p>
     * 
     * <p> Description: This setter defines the Post deleted attribute. </p>
     * 
     * @param deleted is a boolean that specifies if the post has been deleted.
     * 
     */
    public void setDeleted(boolean deleted) { this.deleted = deleted; } 
    
    /*****
     * <p> Method: void setViewed(boolean viewed) </p>
     * 
     * <p> Description: This setter defines the Post viewed attribute. </p>
     * 
     * @param viewed is a boolean that specifies if the post has been viewed.
     * 
     */
    public void setViewed(boolean viewed) { this.viewed = viewed; } 

    /**
     * <p> Method: void addReply(Reply reply) </p>
     * 
     * <p> Description: This method adds a new reply to this post's set of replies. </p>
     *
     * @param reply The Reply object to be added.
     */
    public void addReply(Reply reply) {
        this.replies.add(reply);
    }

    /**
     * Returns a string representation of the Post object, which is used for display in the ListView.
     * @return A string in the format "Title (Thread) - Replies: [replyCount] (Unread: [unreadReplyCount])".
     */
    @Override
    public String toString() {
        return String.format("%s (%s) - Replies: %d (Unread: %d)", title, thread, replyCount, unreadReplyCount);
    }
}