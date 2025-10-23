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
     * Constructor to initialize a new Post object.
     *
     * @param postID          The unique identifier for the post.
     * @param authorUsername  The username of the user who created the post.
     * @param title           The title of the post.
     * @param content         The main body content of the post.
     * @param thread          The discussion thread the post belongs to.
     * @param deleted         A boolean indicating if the post is deleted.
     * @param viewed          A boolean indicating if the post has been viewed by the current user.
     * @param replyCount      The total number of replies to the post.
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
    public int getPostID() { return postID; }
    public String getAuthorUsername() { return authorUsername; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getThread() { return thread; } 
    public boolean isDeleted() { return deleted; } 
    public boolean isViewed() { return viewed; } 
    public int getReplyCount() { return replyCount; } // Added getter for replyCount
    public int getUnreadReplyCount() { return unreadReplyCount; } // Added getter for unreadReplyCount
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<Reply> getReplies() { return replies; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setThread(String thread) { this.thread = thread; } 
    public void setDeleted(boolean deleted) { this.deleted = deleted; } 
    public void setViewed(boolean viewed) { this.viewed = viewed; } 

    /**
     * Adds a reply to this post's list of replies.
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