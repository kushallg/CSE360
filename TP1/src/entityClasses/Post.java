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
 * @version 1.00	2025-10-18 Initial version
 */
public class Post {

    private int postID;
    private String authorUsername;
    private String title;
    private String content;
    private LocalDateTime timestamp;
    private List<Reply> replies;

    /**
     * Constructor to initialize a new Post object.
     *
     * @param postID          The unique identifier for the post.
     * @param authorUsername  The username of the user who created the post.
     * @param title           The title of the post.
     * @param content         The main body content of the post.
     */
    public Post(int postID, String authorUsername, String title, String content) {
        this.postID = postID;
        this.authorUsername = authorUsername;
        this.title = title;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // Set the timestamp to the current time
        this.replies = new ArrayList<>();
    }

    // Getters
    public int getPostID() { return postID; }
    public String getAuthorUsername() { return authorUsername; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<Reply> getReplies() { return replies; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }

    /**
     * Adds a reply to this post's list of replies.
     *
     * @param reply The Reply object to be added.
     */
    public void addReply(Reply reply) {
        this.replies.add(reply);
    }
}