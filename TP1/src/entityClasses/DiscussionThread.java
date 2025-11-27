package entityClasses;

import java.time.LocalDateTime;

/**
 * Represents a discussion thread category.
 */
public class DiscussionThread {
    private String title;
    private boolean visible;
    private LocalDateTime createdAt;

    public DiscussionThread(String title, boolean visible, LocalDateTime createdAt) {
        this.title = title;
        this.visible = visible;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVisible() {
        return visible;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return title + (visible ? "" : " (Hidden)");
    }
}
